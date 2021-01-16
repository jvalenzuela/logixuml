package org.modelio.logixuml.statemachineaoi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * Unit tests for SuperState static methods.
 */
class SuperStateTests {

    /*
     * Mock model objects.
     */
    private Package pkg; // Top level package.
    private StateMachine stateMachine; // Enclosing state machine.
    private Region top; // State machine's top-level region.
    private State superState; // Top-level super-state.
    private Region region; // Intermediate region.
    private State subState; // Mid-level super-state.
    private Region subRegion; // Intermediate region.
    private State target; // Bottom-level state to query.

    @BeforeEach
    private void createMockModel() {
        pkg = MockModel.pkg();
        stateMachine = MockModel.stateMachine("sm", pkg);
        top = MockModel.region(stateMachine);
        superState = MockModel.state("", top);
        region = MockModel.region(superState);
        subState = MockModel.state("", region);
        subRegion = MockModel.region(subState);
        target = MockModel.state("", subRegion);
    }

    /**
     * Confirms the correct list of super-state objects.
     */
    @Test
    void stateList() {
        final List<State> result = SuperState.getSuperStates(target);
        final List<State> expected = new ArrayList<State>();
        expected.add(subState);
        expected.add(superState);
        assertEquals(expected, result);
    }

    /**
     * Confirms the correct list of references to super-state objects.
     */
    @Test
    void referenceList() {
        final List<MRef> result = SuperState.getSuperStateRefs(target);
        final List<MRef> expected = new ArrayList<MRef>();
        expected.add(new MRef(subState));
        expected.add(new MRef(superState));
        assertEquals(expected, result);
    }
}
