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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.logixuml.impl.LogixUMLModule;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.vcore.smkernel.mapi.MObject;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * This object completely defines the AOI output condition every time it is
 * scanned by maintaining the set of active(energized) action outputs, e.g.
 * entry, do, and exit, for all states, The term <em>state</em> could also be
 * applied to this purpose, however, <em>condition</em> is used to avoid
 * confusion with states defined by the UML state machine.
 * <p>
 * States are stored by reference objects because the collection of states for
 * each action is implemented as a set, and MRefs are immutable objects
 * well-suited for set membership.
 */
class Condition {
    /**
     * State references with an active entry action.
     */
    private final Set<MRef> actionEntry = new HashSet<MRef>();

    /**
     * State references with an active do action.
     */
    private final Set<MRef> actionDo = new HashSet<MRef>();

    /**
     * State references with an active exit action.
     */
    private final Set<MRef> actionExit = new HashSet<MRef>();

    /**
     * Modeling session for searching UUIDs.
     */
    private final IModelingSession session;

    /**
     * Constructor.
     */
    public Condition() {
        session = LogixUMLModule.getInstance().getModuleContext().getModelingSession();
    }

    /**
     * Adds a state to the set of entry actions.
     *
     * @param ref Reference to the target state.
     */
    public void addEntryAction(final MRef ref) {
        assertRefIsState(ref);
        actionEntry.add(ref);
    }

    /**
     * Gets the active entry action states.
     *
     * @return Set of references to states with an active entry action.
     */
    public Set<MRef> getEntryActions() {
        return new HashSet<MRef>(actionEntry);
    }

    /**
     * Adds a state to the set of do actions.
     *
     * @param ref Reference to the target state.
     */
    public void addDoAction(final MRef ref) {
        assertRefIsState(ref);
        actionDo.add(ref);
    }

    /**
     * Gets the active do action states.
     *
     * @return Set of references to states with an active do action.
     */
    public Set<MRef> getDoActions() {
        return new HashSet<MRef>(actionDo);
    }

    /**
     * Adds a state to the set of exit actions.
     *
     * @param ref Reference to the target state.
     */
    public void addExitAction(final MRef ref) {
        assertRefIsState(ref);
        actionExit.add(ref);
    }

    /**
     * Gets the active exit action states.
     *
     * @return Set of references to states with an active exit action.
     */
    public Set<MRef> getExitActions() {
        return new HashSet<MRef>(actionExit);
    }

    /**
     * Confirms a given reference points to a state, which is the only type of model
     * element that can be added to a condition.
     *
     * @param ref Model object reference to verify.
     */
    private void assertRefIsState(final MRef ref) {
        final MObject target = session.findByRef(ref);
        if (target == null) {
            throw new AssertionError();
        }
        final String name = target.getMClass().getQualifiedName();
        if (!name.equals(State.MQNAME)) {
            throw new AssertionError(name);
        }
    }

    /**
     * Generates a structured text statements to energize the state action outputs
     * active in this condition.
     *
     * @param stateMap Mapping to resolve MRefs to objects handling the state output
     *                 tags.
     * @return List of structured text statements.
     */
    List<String> setOutputs(final Map<MRef, AoiState> stateMap) {
        final List<String> st = new ArrayList<>();

        for (final MRef ref : actionEntry) {
            final AoiState state = stateMap.get(ref);
            st.add(state.setEntryOutput(true));
        }
        for (final MRef ref : actionDo) {
            final AoiState state = stateMap.get(ref);
            st.add(state.setDoOutput(true));
        }
        for (final MRef ref : actionExit) {
            final AoiState state = stateMap.get(ref);
            st.add(state.setExitOutput(true));
        }

        return unmodifiableList(st);
    }
}
