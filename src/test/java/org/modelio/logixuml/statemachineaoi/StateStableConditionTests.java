package org.modelio.logixuml.statemachineaoi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * Unit tests for the content of the stable condition computed for a given
 * state.
 */
class StateStableConditionTests {
    /**
     * Reference to the mock super-state containing the source state.
     */
    private MRef superstateRef;

    /**
     * Reference to the mock source state.
     */
    private MRef stateRef;

    /**
     * Reference to a mock sub-state of the source state.
     */
    private MRef substateRef;

    /**
     * Result condition.
     */
    private Condition condition;

    /**
     * Builds the mock model and result condition.
     */
    @BeforeEach
    private void setUp() {
        MockModule.init();

        // Build mock model elements.
        final StateMachine stateMachine = MockModel.stateMachine("sm", null);
        final Region top = MockModel.region(stateMachine);
        final State superstate = MockModel.state("super", top);
        final State state = MockModel.state("state", superstate);
        final State substate = MockModel.state("sub", state);

        // Create references to mock model elements.
        superstateRef = new MRef(superstate);
        stateRef = new MRef(state);
        substateRef = new MRef(substate);

        AoiState aoiState = null;
        try {
            aoiState = new AoiState(state);
        } catch (ExportException e) {
            fail();
        }
        condition = aoiState.getStableCondition();
    }

    /**
     * Confirm the stable condition includes the do action for the source state.
     */
    @Test
    void containsSourceDo() {
        assertTrue(condition.getDoActions().contains(stateRef));
    }

    /**
     * Confirm the stable condition include the do action for enclosing
     * super-states.
     */
    @Test
    void containsSuperDo() {
        assertTrue(condition.getDoActions().contains(superstateRef));
    }

    /**
     * Confirm the stable condition excludes the do action for child sub-states.
     */
    @Test
    void excludeSubDo() {
        assertFalse(condition.getDoActions().contains(substateRef));
    }

    /**
     * Confirm the stable condition contains no entry actions.
     */
    @Test
    void excludeEntry() {
        assertEquals(0, condition.getEntryActions().size());
    }

    /**
     * Confirm the stable condition contains no exit actions.
     */
    @Test
    void excludeExit() {
        assertEquals(0, condition.getExitActions().size());
    }
}
