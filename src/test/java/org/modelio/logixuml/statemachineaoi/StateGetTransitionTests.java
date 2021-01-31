package org.modelio.logixuml.statemachineaoi;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;

/**
 * Unit tests for transitions generated from a single state.
 */
class StateGetTransitionTests {
    // Mock transition source states.
    private State topSuperstate;
    private State midSuperstate;
    private State source;

    // Mock transition target states.
    private State expectedTarget;
    private State wrongTarget;

    // Required argument for getTransitions(); actual transition scan mode is
    // irrelevant.
    private TransitionConditionsFactory txFactory;

    @BeforeEach
    void setUp() {
        MockModule.init();
        final StateMachine sm = MockModel.stateMachine("sm", null);
        final Region top = MockModel.region(sm);
        topSuperstate = MockModel.state("superstate1", top);
        midSuperstate = MockModel.state("superstate2", topSuperstate);
        source = MockModel.state("source", midSuperstate);
        expectedTarget = MockModel.state("target1", top);
        wrongTarget = MockModel.state("target2", top);

        txFactory = new TransitionConditionsFactory(TransitionScanMode.SINGLE);
    }

    /**
     * Ensure transitions with no trigger event are rejected.
     */
    @ParameterizedTest
    @ValueSource(strings = { "", "   \n\t\r" })
    void emptyEvent(final String event) {
        final Transition tx = MockModel.transition(source, expectedTarget, event);
        try {
            final AoiState aoiState = new AoiState(source);
            ExportException e = assertThrows(ExportException.class, () -> aoiState.getTransitions(txFactory));
            assertSame(tx, e.getModelObject());
        } catch (ExportException e) {
            fail();
        }
    }

    /**
     * Ensure a transition with a null trigger event is rejected.
     */
    @Test
    void nullEvent() {
        final Transition tx = MockModel.transition(source, expectedTarget, null);
        try {
            final AoiState aoiState = new AoiState(source);
            ExportException e = assertThrows(ExportException.class, () -> aoiState.getTransitions(txFactory));
            assertSame(tx, e.getModelObject());
        } catch (ExportException e) {
            fail();
        }
    }

    /**
     * Ensure multiple transitions originating from the same state using the same
     * event name are rejected.
     */
    @ParameterizedTest
    @ValueSource(strings = { "event", "EVENT", "  Event \t\n\r" })
    void duplicateEvent(final String dupEvent) {
        MockModel.transition(source, expectedTarget, "event");
        MockModel.transition(source, expectedTarget, dupEvent);
        try {
            final AoiState aoiState = new AoiState(source);
            ExportException e = assertThrows(ExportException.class, () -> aoiState.getTransitions(txFactory));
            assertSame(source, e.getModelObject());
        } catch (ExportException e) {
            fail();
        }
    }

    /**
     * Ensure triggering events are case-insensitive.
     */
    @Test
    void eventCase() {
        MockModel.transition(source, expectedTarget, "event");
        Map<String, TransitionConditions> map = null;
        try {
            final AoiState aoiState = new AoiState(source);
            map = aoiState.getTransitions(txFactory);
        } catch (ExportException e) {
            fail();
        }
        assertTrue(map.containsKey("EVENT"));
    }

    /**
     * Ensure surrounding whitespace is removed from events.
     */
    @Test
    void eventWhitespace() {
        MockModel.transition(source, expectedTarget, "  event \n\t\r");
        Map<String, TransitionConditions> map = null;
        try {
            final AoiState aoiState = new AoiState(source);
            map = aoiState.getTransitions(txFactory);
        } catch (ExportException e) {
            fail();
        }
        assertTrue(map.containsKey("event"));
    }

    /**
     * Ensure a transition defined in an enclosing super-state with a unique event
     * is included in the set of possible transitions.
     */
    @Test
    void superstateTransition() {
        MockModel.transition(topSuperstate, expectedTarget, "superEvent");
        MockModel.transition(source, expectedTarget, "event");
        Map<String, TransitionConditions> map = null;
        try {
            final AoiState aoiState = new AoiState(source);
            map = aoiState.getTransitions(txFactory);
        } catch (ExportException e) {
            fail();
        }
        assertTrue(map.containsKey("superEvent"));
    }

    /**
     * Ensure a transition from the immediate state overrides a transition with the
     * same triggering event from an enclosing super-state.
     */
    @ParameterizedTest
    @ValueSource(strings = { "event", "EVENT", "  Event\r\t\n" })
    void overrideSuperstateTransition(final String superEvent) {
        // Generate the transition from the immediate state.
        MockModel.transition(source, expectedTarget, "event");

        // Generate duplicate transitions from super-states.
        MockModel.transition(midSuperstate, wrongTarget, superEvent);
        MockModel.transition(topSuperstate, wrongTarget, superEvent);

        Map<String, TransitionConditions> map = null;
        try {
            final AoiState aoiState = new AoiState(source);
            map = aoiState.getTransitions(txFactory);
        } catch (ExportException e) {
            fail();
        }
        assertSame(expectedTarget, map.get("event").getTarget());
    }

    /**
     * Ensure a transition targeting the source state is rejected.
     */
    @Test
    void transitionToSelf() {
        final Transition tx = MockModel.transition(source, source, "event");
        try {
            final AoiState aoiState = new AoiState(source);
            ExportException e = assertThrows(ExportException.class, () -> aoiState.getTransitions(txFactory));
            assertSame(tx, e.getModelObject());
        } catch (ExportException e) {
            fail();
        }
    }
}
