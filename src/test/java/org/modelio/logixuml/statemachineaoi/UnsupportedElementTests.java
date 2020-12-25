package org.modelio.logixuml.statemachineaoi;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelio.metamodel.uml.behavior.stateMachineModel.ChoicePseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.ConnectionPointReference;
import org.modelio.metamodel.uml.behavior.stateMachineModel.DeepHistoryPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.EntryPointPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.ExitPointPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.FinalState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.ForkPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.InternalTransition;
import org.modelio.metamodel.uml.behavior.stateMachineModel.JoinPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.JunctionPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.ShallowHistoryPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.behavior.stateMachineModel.TerminatePseudoState;
import org.modelio.vcore.smkernel.mapi.MObject;

/**
 * Tests to confirm an exception is thrown if the model contains unsupported UML
 * elements.
 */
class UnsupportedElementTests {
    // Top-level model objects created for every test.
    private StateMachine stateMachine;
    private Region top;

    @BeforeEach
    private void createMockModel() {
        stateMachine = MockModel.stateMachine(null);
        top = MockModel.region(stateMachine);
    }

    /**
     * Confirms an internal transition is rejected.
     */
    @Test
    void internalTransition() {
        final State state = MockModel.state("state", top);
        final InternalTransition it = MockModel.internalTransition(state);
        assertException(it);
    }

    /**
     * Confirms a terminate state is rejected.
     */
    @Test
    void terminatePseudoState() {
        final TerminatePseudoState term = MockModel.terminatePseudoState(top);
        assertException(term);
    }

    /**
     * Confirms an entry point is rejected.
     */
    @Test
    void entryPointPseudoState() {
        final EntryPointPseudoState entry = MockModel.entryPointPseudoState(top);
        assertException(entry);
    }

    /**
     * Confirms an exit point is rejected.
     */
    @Test
    void exitPointPseudoState() {
        final ExitPointPseudoState exit = MockModel.exitPointPseudoState(top);
        assertException(exit);
    }

    /**
     * Confirms a fork is rejected.
     */
    @Test
    void forkPseudoState() {
        final ForkPseudoState fork = MockModel.forkPseudoState(top);
        assertException(fork);
    }

    /**
     * Confirms a join is rejected.
     */
    @Test
    void joinPseudoState() {
        final JoinPseudoState join = MockModel.joinPseudoState(top);
        assertException(join);
    }

    /**
     * Confirms a junction is rejected.
     */
    @Test
    void junctionPseudoState() {
        final JunctionPseudoState junction = MockModel.junctionPseudoState(top);
        assertException(junction);
    }

    /**
     * Confirms a choice is rejected.
     */
    @Test
    void choicePseudoState() {
        final ChoicePseudoState choice = MockModel.choicePseudoState(top);
        assertException(choice);
    }

    /**
     * Confirms a deep history is rejected.
     */
    @Test
    void deepHistoryPseudoState() {
        final DeepHistoryPseudoState dh = MockModel.deepHistoryPseudoState(top);
        assertException(dh);
    }

    /**
     * Confirms a shallow history is rejected.
     */
    @Test
    void shallowHistoryPseudoState() {
        final ShallowHistoryPseudoState sh = MockModel.shallowHistoryPseudoState(top);
        assertException(sh);
    }

    /**
     * Confirms a connection point reference is rejected.
     */
    @Test
    void connectionPointReference() {
        final ConnectionPointReference con = MockModel.connectionPointReference(top);
        assertException(con);
    }

    /**
     * Confirms a final state is rejected.
     */
    @Test
    void finalState() {
        final FinalState f = MockModel.finalState(top);
        assertException(f);
    }

    /**
     * Confirms the correct exception is thrown in response to a specific
     * unsupported UML element type.
     *
     * @param obj The target unsupported model object.
     */
    private void assertException(final MObject obj) {
        UnsupportedUmlException e = assertThrows(UnsupportedUmlException.class,
                () -> new StateMachineAoi(stateMachine));

        // Confirm the exception message includes the name of the model object.
        assertTrue(e.getMessage().contains(obj.getMClass().getName()));

        // Confirm the exception references the correct model object.
        assertSame(obj, e.getMObject());
    }
}
