package org.modelio.logixuml.statemachineaoi;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;

/**
 * Unit tests for a state machine's top-level initial transition. These tests
 * are limited to specific cases unique to the initial transition of an entire
 * state machine not exercised in {@link InitialTransitionTests}.
 *
 * @see InitialTransitionTests
 */
class StateMachineInitialTransitionTests {
    /**
     * Confirm a state machine with no initial transition is rejected.
     * <p>
     * Similar to {@link InitialTransitionTests#stateMachineNone()}, but this
     * verifies the ultimate exception is thrown as opposed to the null return
     * value.
     */
    @Test
    void noInitial() {
        MockModule.init();
        final StateMachine sm = MockModel.stateMachine("sm", null);
        final Region top = MockModel.region(sm);
        MockModel.state("state", top);
        assertThrows(ExportException.class, () -> new StateMachineAoi(sm));
    }
}
