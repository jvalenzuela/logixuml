package org.modelio.logixuml.statemachineaoi;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.UUID;

import org.eclipse.emf.common.util.BasicEList;
import org.mockito.Answers;
import org.modelio.metamodel.uml.behavior.stateMachineModel.InitialPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateVertex;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MObject;

/**
 * Static methods to generate mock model objects.
 */
class MockModel {
    /**
     * Generates a mock top-level package model object.
     *
     * @return The mock package object.
     */
    static Package pkg() {
        return modelObject(Package.class, Package.MQNAME, null);
    }

    /**
     * Generates a mock state machine model element.
     *
     * @param parent Object that owns the new mock element in a composition graph.
     * @return The mock state machine object.
     */
    static StateMachine stateMachine(final MObject parent) {
        return modelObject(StateMachine.class, StateMachine.MQNAME, parent);
    }

    /**
     * Generates a mock region model element.
     *
     * @param parent Object that owns the new mock element in a composition graph.
     * @return The mock region element.
     */
    static Region region(final MObject parent) {
        final Region region = modelObject(Region.class, Region.MQNAME, parent);

        // Initialize the collection of child initial pseudo states.
        when(region.getSub(InitialPseudoState.class)).thenReturn(new ArrayList<InitialPseudoState>());

        // Add the mock region to the parent's getter method return value.
        switch (parent.getMClass().getQualifiedName()) {

        // State machines only have a single child region.
        case StateMachine.MQNAME:
            when(((StateMachine) parent).getTop()).thenReturn(region);
            break;

        // States have a collection of child regions.
        case State.MQNAME:
            ((State) parent).getOwnedRegion().add(region);
            break;

        default:
            break;
        }

        return region;
    }

    /**
     * Generates a mock state model element.
     *
     * @param parent Object that owns the new mock element in a composition graph.
     * @return The mock state element.
     */
    static State state(final MObject parent) {
        final State s = modelObject(State.class, State.MQNAME, parent);

        // Initialize the list of child regions.
        when(s.getOwnedRegion()).thenReturn(new BasicEList<Region>());

        // Initialize the list of outgoing transitions.
        when(s.getOutGoing()).thenReturn(new BasicEList<Transition>());

        return s;
    }

    /**
     * Generates a mock initial pseudo state model element.
     *
     * @param parent Object that owns the new mock element in a composition graph.
     * @return The mock initial pseudo state element.
     */
    static InitialPseudoState initialPseudoState(final Region parent) {
        final InitialPseudoState i = modelObject(InitialPseudoState.class, InitialPseudoState.MQNAME, parent);

        parent.getSub(InitialPseudoState.class).add(i);

        // Initialize the outgoing transition list.
        when(i.getOutGoing()).thenReturn(new BasicEList<Transition>());

        return i;
    }

    /**
     * Generates a mock transition model element.
     *
     * @param source Model object where the transition originates.
     * @param target Model object where the transition terminates.
     * @param event  Triggering event.
     * @return The mock transition element.
     */
    static Transition transition(final StateVertex source, final StateVertex target, final String event) {
        final Transition t = modelObject(Transition.class, Transition.MQNAME, null);
        when(t.getTarget()).thenReturn(target);

        // Add the transition to the source's list of outgoing transitions.
        source.getOutGoing().add(t);

        when(t.getReceivedEvents()).thenReturn(event);

        return t;
    }

    /**
     * Creates a mock model object.
     *
     * @param cls        Class of model object to mock.
     * @param MClassName Qualified name of the mock object's metac-lass.
     * @param parent     The object owning the mock object in the composition graph.
     * @return The generated mock object.
     */
    static <T extends MObject> T modelObject(final Class<T> cls, final String MClassName, final MObject parent) {
        final T obj = mock(cls, Answers.RETURNS_DEEP_STUBS);
        when(obj.getCompositionOwner()).thenReturn(parent);
        lenient().when(obj.getMClass().getQualifiedName()).thenReturn(MClassName);

        // A UUID is needed to generate an MRef object pointing to the mock object.
        lenient().when(obj.getUuid()).thenReturn(UUID.randomUUID().toString());

        return obj;
    }
}
