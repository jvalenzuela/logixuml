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
import java.util.List;

import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.vcore.smkernel.mapi.MObject;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * Static methods to compile super-states containing a given sub-state.
 */
class SuperState {
    /**
     * Acquires the list of super-state objects containing a given state.
     *
     * @param state Sub-state to query.
     * @return The list of super-state objects, starting with the state directly
     *         enclosing the target state, and ending with the top-level state.
     */
    static List<State> getSuperStates(MObject state) {
        final List<State> supers = new ArrayList<State>();
        do {
            state = state.getCompositionOwner();

            // Filter out all non-state object types that can contain states, e.g. regions.
            if (state.getMClass().getQualifiedName().equals(State.MQNAME)) {
                supers.add((State) state);
            }

            // Continue traversing up the graph until the the parent state machine is
            // encountered.
        } while (!state.getMClass().getQualifiedName().equals(StateMachine.MQNAME));

        return supers;
    }

    /**
     * Builds a list of references to super-state objects containing a given state.
     *
     * @param state Sub-state to query.
     * @return The list of references to super-state objects, starting with the
     *         state directly enclosing the target state, and ending with the
     *         top-level state.
     */
    static List<MRef> getSuperStateRefs(MObject state) {
        final List<MRef> refs = new ArrayList<MRef>();
        for (State s : getSuperStates(state)) {
            refs.add(new MRef(s));
        }
        return refs;
    }

}
