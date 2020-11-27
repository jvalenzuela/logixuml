package org.modelio.logixuml.statemachineaoi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
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
     * Mock model objects. The State class is used for all types to simplify
     * mocking, even though an actual model would use various MObject subclasses. It
     * doesn't matter here because the type differentiation is implemented with
     * metaclass qualified names, via getQualifiedName(), which is set in the stub
     * methods.
     */
    private State pkg; // Top level package.
    private State stateMachine; // Enclosing state machine.
    private State top; // State machine's top-level region.
    private State superState; // Top-level super-state.
    private State region; // Intermediate region.
    private State subState; // Mid-level super-state.
    private State subRegion; // Intermediate region.
    private State target; // Bottom-level state to query.

    @BeforeEach
    private void createMockModel() {
        pkg = mockMobject(null, Package.MQNAME);
        stateMachine = mockMobject(pkg, StateMachine.MQNAME);
        top = mockMobject(stateMachine, Region.MQNAME);
        superState = mockMobject(top, State.MQNAME);
        region = mockMobject(superState, Region.MQNAME);
        subState = mockMobject(region, State.MQNAME);
        subRegion = mockMobject(subState, Region.MQNAME);
        target = mockMobject(subRegion, State.MQNAME);
    }

    /**
     * Creates a mock model object with stub methods required by the SuperState
     * methods.
     *
     * @param parent The object owning the mock object in the composition graph.
     * @param MClass Qualified name of the mock object's metaclass.
     * @return The generated mock object.
     */
    private State mockMobject(final State parent, final String MClass) {
        final State state = mock(State.class, Answers.RETURNS_DEEP_STUBS);

        // These stubs are used by the SuperState methods to assemble the result lists.
        when(state.getCompositionOwner()).thenReturn(parent);
        when(state.getMClass().getQualifiedName()).thenReturn(MClass);

        // A UUID is needed to generate an MRef object pointing to the mock object.
        when(state.getUuid()).thenReturn(UUID.randomUUID().toString());

        return state;
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
