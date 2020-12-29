package org.modelio.logixuml.statemachineaoi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelio.metamodel.uml.behavior.stateMachineModel.InitialPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;

/**
 * Unit tests for the static InitialTransition class.
 */
class InitialTransitionTests {
    // Root model objects mocked for every test.
    private StateMachine stateMachine;
    private Region top;

    /**
     * Builds the root model objects required for each test.
     */
    @BeforeEach
    void constructBaseMockModel() {
        stateMachine = MockModel.stateMachine(null);
        top = MockModel.region(stateMachine);
    }

    /**
     * Confirm the initial transition of a top-level state machine is found.
     */
    @Test
    void stateMachineInitial() throws ExportException {
        final State state = MockModel.state("", top);

        final InitialPseudoState initial = MockModel.initialPseudoState(top);
        MockModel.transition(initial, state, "");

        final Transition result = InitialTransition.getInitialTransition(stateMachine);
        assertEquals(state, result.getTarget());
    }

    /**
     * Confirm null is returned for a state machine with no initial transition.
     */
    @Test
    void stateMachineNone() throws ExportException {
        assertNull(InitialTransition.getInitialTransition(stateMachine));
    }

    /**
     * Confirm a state machine with multiple initial transitions throws an
     * exception.
     */
    @Test
    void stateMachineMultipleInitial() {
        MockModel.initialPseudoState(top);
        MockModel.initialPseudoState(top);

        assertThrows(ExportException.class, () -> InitialTransition.getInitialTransition(stateMachine));
    }

    /**
     * Confirm the initial transition of a state is found.
     */
    @Test
    void stateInitial() throws ExportException {
        final State state = MockModel.state("", top);
        final Region region = MockModel.region(state);
        final State substate = MockModel.state("", region);

        final InitialPseudoState initial = MockModel.initialPseudoState(region);
        MockModel.transition(initial, substate, "");

        final Transition result = InitialTransition.getInitialTransition(state);
        assertEquals(substate, result.getTarget());
    }

    /**
     * Confirm a state with no child region returns null.
     */
    @Test
    void stateNoRegion() throws ExportException {
        final State state = MockModel.state("", top);

        assertNull(InitialTransition.getInitialTransition(state));
    }

    /**
     * Confirm a state with a child region but no initial transition returns null.
     */
    @Test
    void stateRegionNoInitial() throws ExportException {
        final State state = MockModel.state("", top);
        MockModel.region(state);

        assertNull(InitialTransition.getInitialTransition(state));
    }

    /**
     * Confirm a state with more than one region throws an exception.
     */
    @Test
    void stateMultipleRegions() {
        final State state = MockModel.state("", top);
        MockModel.region(state);
        MockModel.region(state);

        assertThrows(ExportException.class, () -> InitialTransition.getInitialTransition(state));
    }

    /**
     * Confirm an initial transition targeting a super-state throws an exception.
     */
    @Test
    void targetSuperState() {
        final State superState = MockModel.state("", top);
        final Region superRegion = MockModel.region(superState);

        final State state = MockModel.state("", superRegion);
        final Region region = MockModel.region(state);

        final InitialPseudoState initial = MockModel.initialPseudoState(region);
        MockModel.transition(initial, superState, "");

        assertThrows(ExportException.class, () -> InitialTransition.getInitialTransition(state));
    }

    /**
     * Confirm an initial transition targeting the source state throws an exception.
     */
    @Test
    void targetSelf() {
        final State state = MockModel.state("", top);
        final Region region = MockModel.region(state);

        final InitialPseudoState initial = MockModel.initialPseudoState(region);
        MockModel.transition(initial, state, "");

        assertThrows(ExportException.class, () -> InitialTransition.getInitialTransition(state));
    }

    /**
     * Confirm an initial transition targeting a sibling state throws an exception.
     */
    @Test
    void targetSibling() {
        final State target = MockModel.state("", top);

        final State source = MockModel.state("", top);
        final Region region = MockModel.region(source);

        final InitialPseudoState initial = MockModel.initialPseudoState(region);
        MockModel.transition(initial, target, "");

        assertThrows(ExportException.class, () -> InitialTransition.getInitialTransition(source));
    }

    /**
     * Confirm an initial transition with a trigger event consisting of only
     * whitespace is accepted.
     */
    @Test
    void blankEvent() throws ExportException {
        final State state = MockModel.state("", top);

        final InitialPseudoState initial = MockModel.initialPseudoState(top);
        MockModel.transition(initial, state, "  ");

        InitialTransition.getInitialTransition(stateMachine);
    }

    /**
     * Confirm an initial transition with a non-blank trigger event throws an
     * exception.
     */
    @Test
    void withEvent() {
        final State state = MockModel.state("", top);

        final InitialPseudoState initial = MockModel.initialPseudoState(top);
        MockModel.transition(initial, state, "event");

        assertThrows(ExportException.class, () -> InitialTransition.getInitialTransition(stateMachine));
    }
}
