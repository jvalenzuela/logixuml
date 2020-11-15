package org.modelio.logixuml.statemachineaoi;

import java.util.HashSet;
import java.util.Set;

import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.logixuml.impl.LogixUMLModule;
import org.modelio.metamodel.uml.behavior.stateMachineModel.FinalState;

/**
 * This object completely defines the AOI output condition every time it is
 * scanned by maintaining the set of active(energized) action outputs, e.g.
 * entry, do, and exit, for all states, The term <em>state</em> could also be
 * applied to this purpose, however, <em>condition</em> is used to avoid
 * confusion with states defined by the UML state machine.
 * <p>
 * States are stored by their assigned UUID because the collection of states for
 * each action is implemented as a set, and UUIDs are immutable objects(String)
 * well-suited for set membership.
 */
class Condition {
    /**
     * UUIDs of states with an active entry action.
     */
    private final Set<String> actionEntry = new HashSet<String>();

    /**
     * UUIDs of states with an active do action.
     */
    private final Set<String> actionDo = new HashSet<String>();

    /**
     * UUIDs of states with an active exit action.
     */
    private final Set<String> actionExit = new HashSet<String>();

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
     * @param uuid Identifier of the target state.
     */
    public void addEntryAction(final String uuid) {
        assertUuidIsState(uuid);
        actionEntry.add(uuid);
    }

    /**
     * Gets the active entry action states.
     *
     * @return Set of UUIDs of states with an active entry action.
     */
    public Set<String> getEntryActions() {
        return new HashSet<String>(actionEntry);
    }

    /**
     * Adds a state to the set of do actions.
     *
     * @param uuid Identifier of the target state.
     */
    public void addDoAction(final String uuid) {
        assertUuidIsState(uuid);
        actionDo.add(uuid);
    }

    /**
     * Gets the active do action states.
     *
     * @return Set of UUIDs of states with an active do action.
     */
    public Set<String> getDoActions() {
        return new HashSet<String>(actionDo);
    }

    /**
     * Adds a state to the set of exit actions.
     *
     * @param uuid Identifier of the target state.
     */
    public void addExitAction(final String uuid) {
        assertUuidIsState(uuid);
        actionExit.add(uuid);
    }

    /**
     * Gets the active exit action states.
     *
     * @return Set of UUIDs of states with an active exit action.
     */
    public Set<String> getExitActions() {
        return new HashSet<String>(actionExit);
    }

    /**
     * Confirms a given UUID refers to a concrete state, which is the only type of
     * model element that can be added to a condition.
     *
     * @param uuid Identifier to verify.
     */
    private void assertUuidIsState(final String uuid) {
        assert session.findElementById(FinalState.class, uuid) != null;
    }
}
