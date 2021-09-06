/*
 * Copyright 2021 Jason Valenzuela
 *
 * This file is part of LogixUML.
 *
 * LogixUML is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LogixUML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LogixUML.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.modelio.logixuml.statemachineaoi;

import static java.util.Collections.unmodifiableMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.modelio.logixuml.l5x.AddOnInstruction;
import org.modelio.logixuml.l5x.DataType;
import org.modelio.logixuml.l5x.ParameterUsage;
import org.modelio.logixuml.l5x.ScanModeRoutine;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * This object handles converting a single state model object into add-on
 * instruction components, consisting of the state name, the stable condition,
 * and transitions excluding any initial transition. Initial transitions are
 * captured and incorporated into transitions that target states with initial
 * transitions.
 */
class AoiState {
    /**
     * Source model object.
     */
    private final State state;

    /**
     * Sanitized name extracted from the source model object.
     */
    private final String name;

    /**
     * Prefixes added to the state name to form AOI output parameter tag names.
     */
    private class TagNamePrefix {
        private static final String ENTRY = "stateEntry_";
        private static final String EXIT = "stateExit_";
        private static final String DO = "stateDo_";
        private static final String ACTIVE = "stateActive_";
    }

    /**
     * Name of the AOI output parameter signaling state entry.
     */
    private final String entryTagName;

    /**
     * Name of the AOI output parameter signaling state exit.
     */
    private final String exitTagName;

    /**
     * Name of the AOI output parameter signaling the state's stable condition.
     */
    private final String doTagName;

    /**
     * Name of the AOI output parameter signaling any entry, do, or exit action.
     */
    private final String activeTagName;

    /**
     * Constructor.
     *
     * @param state Source model object.
     * @throws ExportException If the state name is invalid or the state contains
     *                         too many regions.
     */
    AoiState(final State state) throws ExportException {
        this.state = state;
        name = validateName();
        validateRegions();

        entryTagName = TagNamePrefix.ENTRY + name;
        exitTagName = TagNamePrefix.EXIT + name;
        doTagName = TagNamePrefix.DO + name;
        activeTagName = TagNamePrefix.ACTIVE + name;
    }

    /**
     * Validates and trims the source model object's name.
     *
     * @return The validated state name.
     * @throws ExportException If the state name is invalid.
     */
    private String validateName() throws ExportException {
        final String name = state.getName().trim();
        if (name.isEmpty()) {
            throw new ExportException("State can not have an empty name.", state);
        }
        return name;
    }

    /**
     * Getter function for the state name.
     *
     * @return State name.
     */
    String getName() {
        return name;
    }

    /**
     * Confirms the states has a valid number of child regions.
     *
     * @throws ExportException If the state contains more than one region.
     */
    private void validateRegions() throws ExportException {
        if (state.getOwnedRegion().size() > 1) {
            throw new ExportException("States may not have more than one region.", state);
        }
    }

    /**
     * Constructs the condition object for this state's stable condition, which
     * consists of do actions for this state and all of its super-states.
     *
     * @return The stable condition object.
     */
    Condition getStableCondition() {
        final Condition c = new Condition();

        // Include the do action for the state itself.
        c.addDoAction(new MRef(state));

        // Include do actions for all super-states containing this state.
        for (MRef ref : SuperState.getSuperStateRefs(state)) {
            c.addDoAction(ref);
        }

        return c;
    }

    /**
     * Builds a map of transitions possible from this state, keyed by event name,
     * including transitions defined in enclosing super-states.
     * 
     * @param txFactory Object that will generate transition conditions.
     * @return Mapping of event name to transition conditions.
     * @throws ExportException If an invalid event name or transition was found.
     */
    Map<String, TransitionConditions> getTransitions(final TransitionConditionsFactory txFactory)
            throws ExportException {
        // Event name keys are evaluated in a case-insensitive manner because event
        // names are ultimately used to construct Logix tag names, which are also
        // case-insensitive.
        final Map<String, TransitionConditions> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        // Build the list of states from which outgoing transitions will be captured,
        // which is this state and all enclosing super-states.
        final List<State> states = SuperState.getSuperStates(state);
        states.add(0, state);

        // Evaluate transitions starting at the top-level super-state. This is important
        // so transitions defined in lower-level states will override transitions from
        // super-states with the same triggering event.
        Collections.reverse(states);

        for (final State s : states) {
            map.putAll(getSingleTransitions(s, txFactory));
        }

        return unmodifiableMap(map);
    }

    /**
     * Assembles a mapping of events and transition conditions for transitions
     * leaving a state, excluding any transitions defined by enclosing super-states.
     *
     * @param source    Source state.
     * @param txFactory Object that will generate transition conditions.
     * @return Mapping of event name to transition conditions.
     * @throws ExportException If an invalid event name or transition was found.
     */
    private Map<String, TransitionConditions> getSingleTransitions(final State source,
            final TransitionConditionsFactory txFactory) throws ExportException {
        final Map<String, TransitionConditions> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (final Transition tx : source.getOutGoing()) {
            String event = tx.getReceivedEvents();
            event = (event == null) ? "" : event.trim();
            if (event.isEmpty()) {
                throw new ExportException("Missing name for the event triggering a transition.", tx);
            }

            if (map.containsKey(event)) {
                throw new ExportException(
                        "Multiple transitions leaving a state have the same triggering event: \"" + event + "\"",
                        source);
            }

            try {
                map.put(event, txFactory.build(tx, state));
            } catch (IgnoreTransitionException e) {
                // This transition in the context of this state yields a transition to self,
                // i.e. ignore the transition.
            }
        }

        return unmodifiableMap(map);
    }

    /**
     * Allocates output parameters, and generates reset logic in Prescan and
     * Enable-In False for those parameters.
     *
     * @param aoi Target add-on instruction.
     * @throws ExportException If the state name could not be used to create valid
     *                         AOI parameter names.
     */
    void initializeAoi(final AddOnInstruction aoi) throws ExportException {
        try {
            aoi.addParameter(entryTagName, ParameterUsage.Output, DataType.BOOL, false,
                    "True when the state machine enters the " + name + " state.");
            aoi.addParameter(exitTagName, ParameterUsage.Output, DataType.BOOL, false,
                    "True when the state machine exits the " + name + " state.");
            aoi.addParameter(doTagName, ParameterUsage.Output, DataType.BOOL, false,
                    "True when the state machine is stable in the " + name + " state.");
            aoi.addParameter(activeTagName, ParameterUsage.Output, DataType.BOOL, true,
                    "True when the state machine is entering, exiting, or stable in the " + name + " state.");
        } catch (ExportException e) {
            throw new ExportException(
                    "The state name \"" + name + "\" is invalid for use as part of an AOI parameter name.", state);
        }

        // Reset all outputs in prescan and enable-in false.
        final List<String> st = new ArrayList<>();
        st.add(setEntryOutput(false));
        st.add(setExitOutput(false));
        st.add(setDoOutput(false));
        st.add(setOutput(activeTagName, false));
        aoi.addStructuredTextLines(ScanModeRoutine.Prescan, st);
        aoi.addStructuredTextLines(ScanModeRoutine.EnableInFalse, st);
    }

    /**
     * Generates a structured text line to set the entry output.
     *
     * @return The generated structured text line.
     */
    String setEntryOutput(final boolean value) {
        return setOutput(entryTagName, value);
    }

    /**
     * Generates a structured text line to set the exit output.
     *
     * @return The generated structured text line.
     */
    String setExitOutput(final boolean value) {
        return setOutput(exitTagName, value);
    }

    /**
     * Generates a structured text line to set the do output.
     *
     * @return The generated structured text line.
     */
    String setDoOutput(final boolean value) {
        return setOutput(doTagName, value);
    }

    /**
     * Generates a structured text line to drive the active output based on the
     * entry, exit, and do conditions.
     *
     * @return The generated structured text line.
     */
    String setActiveOutput() {
        return activeTagName + " := " + entryTagName + " OR " + exitTagName + " OR " + doTagName + ";";
    }

    /**
     * Generates a structured text line to set one of the boolean outputs to a
     * value.
     *
     * @param tagName Output tag name.
     * @param value   Target value.
     * @return The generated structured text line.
     */
    private String setOutput(final String tagName, final boolean value) {
        final int intValue = value ? 1 : 0;
        return tagName + " := " + intValue + ";";
    }
}
