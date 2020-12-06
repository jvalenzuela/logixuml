package org.modelio.logixuml.statemachineaoi;

import java.util.HashSet;
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
        assert (target != null) && (target.getMClass().getQualifiedName() == State.MQNAME);
    }
}
