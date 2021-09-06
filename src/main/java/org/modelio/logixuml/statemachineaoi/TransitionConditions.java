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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.logixuml.impl.LogixUMLModule;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateVertex;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.vcore.smkernel.mapi.MObject;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * This object evaluates a transition model object to generate a list of
 * {@link Condition} objects defining the entry/exit/do outputs that are
 * energized in each scan of the transition. Concrete subclasses represent each
 * of the available scan modes, and implement methods which must be unique in
 * each scan mode.
 */
abstract class TransitionConditions {
    /**
     * The sequence of conditions defining each scan in the transition, beginning
     * from the scan where the triggering event is first received, and ending with
     * the last condition before the target state is reached where no entry or exits
     * actions remain active.
     */
    private final List<Condition> conditions;

    /**
     * The ultimate target of the transition, after all intermediary initial
     * transitions are followed.
     */
    private final State target;

    /**
     * Modeling session used to resolve model object references.
     */
    private final IModelingSession session;

    /**
     * Constructor.
     *
     * @param transition   Model element defining the transition.
     * @param activeSource Lowest-level state model element that is currently active
     *                     at the start of this transition. This will differ from
     *                     the transition's source state in cases of a transition
     *                     defined by a superstate, but starting within an enclosed
     *                     substate.
     * @throws ExportException
     * @throws IgnoreTransitionException If the transition should be ignored in the
     *                                   context of the active source state.
     */
    TransitionConditions(final Transition transition, final State activeSource)
            throws ExportException, IgnoreTransitionException {
        // Self-transitions are not permitted.
        if (transition.getSource() == transition.getTarget()) {
            throw new ExportException("Transition must have different source and target states.", transition);
        }

        session = LogixUMLModule.getInstance().getModuleContext().getModelingSession();
        target = getTargetState(transition, activeSource);
        final List<MRef> exits = computeExitStates(activeSource, target);
        final List<MRef> entries = computeEntryStates(activeSource, target);
        conditions = computeConditions(exits, entries);

        for (final Condition c : conditions) {
            addDoStates(c);
        }
    }

    /**
     * Getter method to acquire the resulting list of transition conditions.
     *
     * @return Action conditions for each scan in the transition.
     */
    List<Condition> getConditions() {
        return new ArrayList<Condition>(conditions);
    }

    /**
     * Getter method to acquire the transition's ultimate target state.
     *
     * @return Reference to the transition's ultimate target state.
     */
    MRef getTarget() {
        return new MRef(target);
    }

    /**
     * Finds the ultimate state targeted by the transition, following any initial
     * transitions within target states.
     *
     * @param tx     Model object defining the transition.
     * @param source State where the transition originates. This may not be the
     *               actual source of the transition model object for a group
     *               transition defined in a superstate.
     * @return The ultimate target state.
     * @throws ExportException
     * @throws IgnoreTransitionException If the transition should be ignored in the
     *                                   context of the active source state.
     */
    private State getTargetState(final Transition tx, final State source)
            throws ExportException, IgnoreTransitionException {
        State targetState = null;
        StateVertex targetElement = tx.getTarget();

        // Ignore this transition if it targets the active state, which can happen for
        // transitions defined in a superstate(group transition).
        if (targetElement == source) {
            throw new IgnoreTransitionException();
        }

        final List<State> sourceSupers = new ArrayList<State>();
        if (source != null) {
            sourceSupers.addAll(SuperState.getSuperStates(source));
        }

        // Iteratively follow initial transitions until a target state is found with no
        // initial transition.
        do {
            final String targetType = targetElement.getMClass().getQualifiedName();

            // Transitions must target only state objects.
            if (!targetType.equals(State.MQNAME)) {
                throw new AssertionError(targetType);
            }

            // Check to see if the target state has its own initial transition.
            final Transition targetInitial = InitialTransition.getInitialTransition(targetElement);

            // Final target is reached if no initial transition is found or the target is a
            // super-state containing the source, in which case the initial transition is
            // ignored because a transition from a sub-state to its enclosing super-state
            // does not re-enter the super-state, and therefore the initial transition does
            // not apply.
            if ((targetInitial == null) || (sourceSupers.contains(targetElement))) {
                targetState = (State) targetElement;
            } else {
                targetElement = targetInitial.getTarget();
            }

        } while (targetState == null);

        return targetState;
    }

    /**
     * Determines the list of states that will have their exit action triggered.
     *
     * @param source Transition source state.
     * @param target Transition target state.
     * @return List of state references that will be exited, starting with the
     *         source state, which may not be exited in some transition types, and
     *         ending with the highest-level super-state.
     */
    private List<MRef> computeExitStates(final State source, final State target) {
        final List<MRef> exits = new ArrayList<MRef>();

        // Begin by exiting the source and all of its enclosing super-states.
        if (source != null) {
            exits.add(new MRef(source));
            exits.addAll(SuperState.getSuperStateRefs(source));
        }

        // Do not exit the target or any of its enclosing super-states.
        exits.remove(new MRef(target));
        exits.removeAll(SuperState.getSuperStateRefs(target));

        return exits;
    }

    /**
     * Determines the list of states that will have their entry action triggered.
     *
     * @param source Transition source state.
     * @param target Transition target state.
     * @return List of references to states that will be entered, starting with the
     *         highest-level super-state, and ending with the target state.
     */
    private List<MRef> computeEntryStates(final State source, final State target) {
        // Begin with the target state and all of its enclosing super-states.
        final List<MRef> entries = SuperState.getSuperStateRefs(target);
        entries.add(0, new MRef(target));

        // Do not enter the source state or any of its super-states.
        if (source != null) {
            entries.remove(new MRef(source));
            entries.removeAll(SuperState.getSuperStateRefs(source));
        }

        // Entry actions are listed top-down.
        Collections.reverse(entries);

        return entries;
    }

    /**
     * Adds states with active do actions.
     *
     * @param c The condition to add do actions to.
     */
    private void addDoStates(final Condition c) {
        // Super-states common to all active entry actions will have their do action
        // active.
        for (final MRef ref : commonSuperStates(c.getEntryActions())) {
            c.addDoAction(ref);
        }

        // Super-states common to all active exit actions also have active do actions.
        for (final MRef ref : commonSuperStates(c.getExitActions())) {
            c.addDoAction(ref);
        }
    }

    /**
     * Finds the set of super-states that contain a given set of sub-states.
     *
     * @param substates Set of sub-states to evaluate.
     * @return The set of super-states that are common to every sub-state.
     */
    private Set<MRef> commonSuperStates(final Collection<MRef> substates) {
        final Set<MRef> common = new HashSet<MRef>();

        // Assemble the super-states from all given sub-states.
        for (final MRef ref : substates) {
            final MObject state = session.findByRef(ref);
            common.addAll(SuperState.getSuperStateRefs(state));
        }

        // Remove super-states not common to every sub-state.
        for (final MRef ref : substates) {
            final MObject state = session.findByRef(ref);
            common.retainAll(SuperState.getSuperStateRefs(state));
        }

        return common;
    }

    /**
     * Generates the list of conditions, each representing the active exit and entry
     * actions for a single scan, that implement the given transition. This is
     * implemented in each subclass is the actual sequence of conditions is specific
     * to the scan mode in use.
     * <p>
     * Do actions are not generated by this method as they can be computed from the
     * entry and exit actions for all scan modes, and therefore need not be computed
     * separately in each subclass.
     *
     * @param exits   The list of all states that must be exited during the
     *                transition, ordered from inner subclasses outward.
     * @param entries The list of all states that must be entered, ordered from
     *                highest superclass to inner subclass
     * @return The list of conditions implementing the transition.
     */
    abstract protected List<Condition> computeConditions(final List<MRef> exits, final List<MRef> entries);
}
