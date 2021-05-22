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

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntSupplier;

import org.modelio.logixuml.l5x.AddOnInstruction;
import org.modelio.logixuml.l5x.DataType;
import org.modelio.logixuml.l5x.ScanModeRoutine;
import org.modelio.logixuml.structuredtext.CaseOf;
import org.modelio.logixuml.structuredtext.Halt;
import org.modelio.logixuml.structuredtext.IfThen;
import org.modelio.logixuml.structuredtext.WhileDo;
import org.modelio.metamodel.diagrams.StateMachineDiagram;
import org.modelio.metamodel.uml.behavior.stateMachineModel.InitialPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.metamodel.uml.infrastructure.Note;
import org.modelio.metamodel.uml.infrastructure.properties.TypedPropertyTable;
import org.modelio.vcore.model.CompositionGetter;
import org.modelio.vcore.smkernel.mapi.MObject;
import org.modelio.vcore.smkernel.mapi.MRef;

public class StateMachineAoi {
    /**
     * String added to the state machine model name to form the AOI's name.
     */
    private static String NAME_PREFIX = "stateMachine_";

    /**
     * AOI output object.
     */
    private final AddOnInstruction aoi;

    /**
     * Object handling the event queue implementation.
     */
    private final EventQueue eventQ;

    /**
     * Set of AoiEvent objects keyed by event name.
     */
    private final Map<String, AoiEvent> events;

    /**
     * Mapping of references to state model objects to the object handling the AOI
     * implementation of each state.
     */
    private final Map<MRef, AoiState> states;

    /**
     * Container to maintain the sequence of condition identifiers during a
     * transition.
     */
    private final ConditionIdSequence cvSeq;

    /**
     * Object generating transition conditions according to the selected transition
     * scan mode.
     */
    private final TransitionConditionsFactory transitionFactory;

    /**
     * Object generating unique, integer identifiers for every condition.
     */
    private final IntSupplier conditionIdSupplier;

    /**
     * Mapping of the integer identifier assigned to every possible output
     * condition, stable and transition.
     */
    private final Map<Integer, Condition> conditions;

    /**
     * Integer identifier for conditions representing the stable output condition
     * for each state.
     */
    private final Map<MRef, Integer> stableConditions;

    /**
     * Value of condition variable tag when the AOI is scanned true for the first
     * time, following either Prescan or after being scanned false. This value must
     * be zero so it does not collide with dynamically-allocated condition IDs,
     * which are always non-zero. The first condition of the state machine's
     * top-level initial transition will follow this in the condition sequence,
     * although it doesn't necessarily have to be 1.
     */
    private static final int RESET_CONDITION = 0;

    /**
     * Value representing the absence of an event. This must be zero because actual
     * event IDs are non-zero.
     */
    private static final int NO_EVENT = 0;

    /**
     * Names for local tags that are not derived from model objects.
     */
    private class TagNames {
        /**
         * ID of the condition describing the current output states.
         */
        private final static String CONDITION_VARIABLE = "cv";

        /**
         * ID of the event that has been removed from the event queue and is being
         * evaluated for triggering a transition.
         */
        private final static String CURRENT_EVENT = "e";
    }

    /**
     * Constructor.
     *
     * @param stateMachine Source state machine model.
     * @throws ExportException
     */
    public StateMachineAoi(final MObject stateMachine) throws ExportException {
        aoi = initializeAoi(stateMachine);
        Halt.createTags(aoi);
        final Set<MObject> children = getChildren(stateMachine);
        validateElementTypes(children);
        final StereotypeProperties props = new StereotypeProperties((StateMachine) stateMachine);
        eventQ = new EventQueue(aoi, props.getEventQueueSize());
        transitionFactory = new TransitionConditionsFactory(props.getTransitionScanMode());

        events = EventMap.build(children);
        for (final AoiEvent e : events.values()) {
            e.initializeAoi(aoi);
        }

        states = StateMap.build(children);
        for (final AoiState state : states.values()) {
            state.initializeAoi(aoi);
        }

        conditionIdSupplier = new IntegerIdentifier();
        conditions = new HashMap<>();
        stableConditions = new HashMap<>();
        cvSeq = new ConditionIdSequence();

        // Generate identifiers for the stable conditions of every state. Stable
        // conditions must all be allocated before any transitional conditions. See
        // triggerTransitions() for details.
        for (final MRef ref : states.keySet()) {
            final int id = conditionIdSupplier.getAsInt();
            conditions.put(id, states.get(ref).getStableCondition());
            stableConditions.put(ref, id);
        }

        buildInitialTransition(stateMachine);
        buildLogicRoutine();
    }

    /**
     * Instantiates the add-on instruction and allocates static resources.
     *
     * @param stateMachine Source state machine model object.
     * @return The created AOI object.
     * @throws ExportException If the state machine name is invalid.
     */
    private AddOnInstruction initializeAoi(final MObject stateMachine) throws ExportException {
        final AddOnInstruction aoi = new AddOnInstruction(getName(stateMachine));

        // Create the condition variable tag and ensure it is reset in prescan and
        // enable-in false.
        aoi.addLocalTag(TagNames.CONDITION_VARIABLE, DataType.DINT);
        final String resetCv = TagNames.CONDITION_VARIABLE + " := " + RESET_CONDITION + ";";
        aoi.addStructuredTextLine(ScanModeRoutine.Prescan, resetCv);
        aoi.addStructuredTextLine(ScanModeRoutine.EnableInFalse, resetCv);

        // The current event tag does not need to be reset in non-logic scan modes.
        aoi.addLocalTag(TagNames.CURRENT_EVENT, DataType.DINT);

        return aoi;
    }

    /**
     * Builds the AOI name from the state machine name.
     *
     * @param stateMachine Source state machine model.
     * @return The AOI name.
     * @throws ExportException If the state machine name is blank.
     */
    private String getName(final MObject stateMachine) throws ExportException {
        final String objName = stateMachine.getName().trim();

        // The only validation required here is to confirm the name isn't blank before
        // adding the prefix. Further checks for invalid names are applied when the
        // AddOnInstruction object confirms the name is a valid Logix identifier.
        if (objName.isEmpty()) {
            throw new ExportException("State machine cannot have an empty name.");
        }

        return NAME_PREFIX + objName;
    }

    /**
     * Gets the set of all model objects that are part of the state machine model.
     *
     * @param stateMachine Source state machine model.
     * @return Set of model objects making up the state machine.
     */
    private Set<MObject> getChildren(final MObject stateMachine) {
        final List<MObject> roots = new ArrayList<MObject>();
        roots.add(stateMachine);
        return CompositionGetter.getAllChildren(roots);
    }

    /**
     * Checks to see if any unsupported UML element types exist within the state
     * machine.
     *
     * @param elements The state machine's child elements.
     * @throws ExportException If an unsupported UML element type was found.
     */
    private void validateElementTypes(final Set<MObject> elements) throws ExportException {
        for (final MObject e : elements) {
            switch (e.getMClass().getQualifiedName()) {

            // List of supported model element types.
            case InitialPseudoState.MQNAME:
            case Note.MQNAME:
            case State.MQNAME:
            case StateMachineDiagram.MQNAME:
            case Region.MQNAME:
            case Transition.MQNAME:
            case TypedPropertyTable.MQNAME:
                break;

            default:
                final String shortName = e.getMClass().getName();
                throw new ExportException(String.format("Unsupported UML element type: %s", shortName), e);
            }
        }
    }

    /**
     * Allocates the conditions required for the state machine's top-level initial
     * transition.
     *
     * @param stateMachine Source state machine model object.
     * @throws ExportException If the state machine's initial transition is absent
     *                         or invalid.
     */
    private void buildInitialTransition(final MObject stateMachine) throws ExportException {
        final Transition initial = InitialTransition.getInitialTransition(stateMachine);
        if (initial == null) {
            throw new ExportException("State machine must have a top-level initial transition.");
        }
        final TransitionConditions txConditions = transitionFactory.build(initial);

        // The reset condition must lead directly to the initial transition's
        // first condition so the initial transition is executed immediately after
        // prescan or scan-false.
        cvSeq.storeNext(RESET_CONDITION, allocateConditionId(txConditions));
    }

    /**
     * Constructs the add-on instruction's logic routine.
     *
     * @throws ExportException If an invalid transition was found.
     */
    private void buildLogicRoutine() throws ExportException {
        // The ST for this block must be generated before the block advancing the
        // condition variable through transitions because the condition IDs for those
        // transitions are allocated here. The generated ST is then added to the routine
        // below.
        final List<String> transitionLoop = triggerTransitions();

        aoi.addStructuredTextLines(ScanModeRoutine.Logic, eventQ.enqueueEvents(events.values()));
        aoi.addStructuredTextLines(ScanModeRoutine.Logic, cvSeq.advance(TagNames.CONDITION_VARIABLE));

        // Append the transition trigger block here.
        aoi.addStructuredTextLines(ScanModeRoutine.Logic, transitionLoop);

        aoi.addStructuredTextLines(ScanModeRoutine.Logic, setStateOutputs(conditions));
    }

    /**
     * Builds structured text statements to remove events from the event queue one
     * at a time, checking if each will initiate a transition from the currently
     * active state.
     *
     * @return Structured text lines.
     * @throws ExportException If an invalid transition was found.
     */
    private List<String> triggerTransitions() throws ExportException {
        // This entire block is contained within a WHILE_DO loop to continually remove
        // events from the event queue until it is either depleted or an event triggers
        // a transition. Triggering a transition can only occur when the state machine
        // is in a stable condition, i.e. not in the midst of a transition, so the loop
        // condition is based on the condition variable remaining within the range of
        // stable conditions.
        final int lastStableCondition = Collections.max(stableConditions.values());
        final WhileDo loop = new WhileDo(TagNames.CONDITION_VARIABLE + " <= " + lastStableCondition);

        // The loop begins by removing the next event from the event queue.
        loop.addStatements(TagNames.CURRENT_EVENT + " := " + NO_EVENT + ";");
        loop.addStatements(eventQ.dequeue(TagNames.CURRENT_EVENT));

        // Terminate the loop if the event queue is empty.
        final IfThen noEvent = new IfThen();
        noEvent.addCase(TagNames.CURRENT_EVENT + " = " + NO_EVENT, "EXIT;");
        loop.addStatements(noEvent.getLines());

        // Build a CASE_OF block with a case for every stable condition to evaluate the
        // current event for possible transition triggers.
        final CaseOf stateTransitions = new CaseOf(TagNames.CONDITION_VARIABLE);
        for (final MRef ref : states.keySet()) {
            stateTransitions.addCase(stableConditions.get(ref), evaluateEvent(ref));
        }
        stateTransitions.addElse(Halt.getLines());
        loop.addStatements(stateTransitions.getLines());

        return unmodifiableList(loop.getLines());
    }

    /**
     * Generates a list of structured text statements to initiate a transition from
     * a given state based on the current event.
     *
     * @param ref Reference to the source state.
     * @return Structured text statements.
     * @throws ExportException If a problem was found with the transitions leaving
     *                         the source state.
     */
    private List<String> evaluateEvent(final MRef ref) throws ExportException {
        final List<String> st = new ArrayList<>();
        final Map<String, TransitionConditions> transitions = states.get(ref).getTransitions(transitionFactory);

        // Iterate through every event triggering a transition from the source state.
        for (final String event : transitions.keySet()) {
            final TransitionConditions tx = transitions.get(event);
            final int firstConditionId = allocateConditionId(tx);

            // Generate an IF_THEN block to set the condition variable to the transition's
            // first condition ID if this is the current event.
            final IfThen eventActive = new IfThen();
            eventActive.addCase(TagNames.CURRENT_EVENT + " = " + events.get(event).getId(),
                    TagNames.CONDITION_VARIABLE + " := " + firstConditionId + ";");
            st.addAll(eventActive.getLines());
        }

        return unmodifiableList(st);
    }

    /**
     * Allocates identifiers for each condition in a given transition.
     *
     * @param t Source transition object.
     * @return The identifier of the transition's first condition.
     */
    private int allocateConditionId(final TransitionConditions t) {
        int firstId = 0;
        int lastId = 0;
        for (final Condition c : t.getConditions()) {
            final int id = conditionIdSupplier.getAsInt();
            conditions.put(id, c);

            if (firstId == 0) {
                firstId = id;
            }

            // Store the sequence of conditions for those following the first.
            if (lastId != 0) {
                cvSeq.storeNext(lastId, id);
            }

            lastId = id;
        }

        // The stable condition of the target state follows the transition's final
        // condition.
        cvSeq.storeNext(lastId, stableConditions.get(t.getTarget()));

        return firstId;
    }

    /**
     * Generates a set of structured text statements setting the state outputs based
     * on the current condition.
     *
     * @param conditions Integer IDs for all possible conditions.
     * @return Structured text statements.
     */
    private List<String> setStateOutputs(final Map<Integer, Condition> conditions) {
        final List<String> st = new ArrayList<>();

        // Begin by unconditionally clearing all outputs.
        for (final AoiState state : states.values()) {
            st.add(state.setEntryOutput(false));
            st.add(state.setDoOutput(false));
            st.add(state.setExitOutput(false));
        }

        // Evaluate the current condition variable, and energize outputs associated with
        // the current condition.
        final CaseOf cvCases = new CaseOf(TagNames.CONDITION_VARIABLE);
        for (final int cv : conditions.keySet()) {
            cvCases.addCase(cv, conditions.get(cv).setOutputs(states));
        }
        cvCases.addElse(Halt.getLines()); // Fault on undefined condition variable.
        st.addAll(cvCases.getLines());

        // The state active outputs can now be set as they are just a function of the
        // entry, do, and exit outputs.
        for (final AoiState state : states.values()) {
            st.add(state.setActiveOutput());
        }

        return unmodifiableList(st);
    }

    /**
     * Writes the assembled AOI to an L5X file.
     *
     * @param dir Target directory for the L5X file.
     * @throws ExportException If the L5X file could not be written.
     */
    public void export(final String dir) throws ExportException {
        aoi.write(dir);
    }
}
