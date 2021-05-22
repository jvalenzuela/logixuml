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

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.modelio.metamodel.uml.behavior.stateMachineModel.InitialPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateVertex;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.vcore.smkernel.mapi.MObject;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * Static method to locate the initial transition of a state machine or state.
 */
class InitialTransition {
    /**
     * Acquires the transition originating from an object's initial pseudo state.
     *
     * @param parent Model object to query.
     * @return The transition originating from the initial pseudo state, or null if
     *         the parent object has no initial transition.
     * @throws ExportException
     */
    static Transition getInitialTransition(final MObject parent) throws ExportException {
        Transition transition = null;
        final Region region = getParentRegion(parent);

        if (region != null) {
            final InitialPseudoState initial = findInRegion(region);
            if (initial != null) {
                transition = getTransition(initial);
                validateTransition(parent, transition);
            }
        }

        return transition;
    }

    /**
     * Gets the region that will contain the initial transition. Initial pseudo
     * states are always children of a region.
     *
     * @param parent Model object to query.
     * @return The region model object that may contain an initial transition; null
     *         of no child regions exist.
     * @throws ExportException If a state object has multiple regions.
     */
    static private Region getParentRegion(final MObject parent) throws ExportException {
        final String parentType = parent.getMClass().getQualifiedName();
        final Region region;

        // Acquire the child region based on the type of parent object.
        switch (parentType) {

        // State machines have a single top region.
        case StateMachine.MQNAME:
            region = ((StateMachine) parent).getTop();
            break;

        // States have zero or more child regions.
        case State.MQNAME: {
            final EList<Region> regions = ((State) parent).getOwnedRegion();
            switch (regions.size()) {
            // A state with no regions has no initial transition.
            case 0:
                region = null;
                break;

            case 1:
                region = regions.get(0);
                break;

            default:
                throw new ExportException("Multiple regions are not supported.", parent);
            }
            break;
        }

        // Only state machines and states should be queried for initial transitions.
        default:
            throw new AssertionError(parentType);
        }

        return region;
    }

    /**
     * Locates the initial transition within a region.
     *
     * @param region Source region model element.
     * @return The initial pseudo state, or null if none was found.
     * @throws ExportException If the region contains more than one initial
     *                         transition.
     */
    static private InitialPseudoState findInRegion(final Region region) throws ExportException {
        final InitialPseudoState initial;
        final List<InitialPseudoState> initials = region.getSub(InitialPseudoState.class);

        switch (initials.size()) {
        case 0:
            initial = null;
            break;

        case 1:
            initial = initials.get(0);
            break;

        default:
            throw new ExportException("Multiple initial transitions not supported.", region);
        }

        return initial;
    }

    /**
     * Acquires the outgoing transition from the initial pseudo state.
     *
     * @param initial Source initial pseudo state.
     * @return The outgoing transition.
     * @throws ExportException If the pseudo state does not have a single outgoing
     *                         transition.
     */
    static private Transition getTransition(final InitialPseudoState initial) throws ExportException {
        final EList<Transition> outgoing = initial.getOutGoing();
        final Transition transition;

        if (outgoing.size() == 1) {
            transition = outgoing.get(0);
        } else {
            throw new ExportException("Initial state must have exactly one outgoing transition.", initial);
        }

        return transition;
    }

    /**
     * Confirms a transition is suitable as an initial transition. Only checks
     * conditions unique to initial transitions; validation for conditions common
     * all transitions is implemented elsewhere.
     *
     * @param origin     Model object owning the initial pseudo state where the
     *                   transition originates.
     * @param transition Transition model object to test.
     * @throws ExportException If the transition is not a valid initial transition.
     */
    static private void validateTransition(final MObject origin, final Transition transition) throws ExportException {
        // Confirm no trigger event is defined.
        if (!transition.getReceivedEvents().trim().isEmpty()) {
            throw new ExportException("Initial transitions can not have a trigger event.", transition);
        }

        // If this is an initial transition within a state, and not a state machine, it
        // must target a sub-state.
        if (origin.getMClass().getQualifiedName().equals(State.MQNAME)) {
            final StateVertex target = transition.getTarget();
            final List<MRef> supers = SuperState.getSuperStateRefs(target);
            if (!supers.contains(new MRef(origin))) {
                throw new ExportException("Initial transition must target a substate.", origin);
            }
        }
    }
}
