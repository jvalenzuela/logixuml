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
import org.modelio.vcore.smkernel.mapi.MRef;

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
        return modelObject(Package.class, Package.MQNAME, null, "");
    }

    /**
     * Generates a mock state machine model element.
     *
     * @param parent Object that owns the new mock element in a composition graph.
     * @return The mock state machine object.
     */
    static StateMachine stateMachine(final MObject parent) {
        return modelObject(StateMachine.class, StateMachine.MQNAME, parent, "");
    }

    /**
     * Generates a mock region model element.
     *
     * @param parent Object that owns the new mock element in a composition graph.
     * @return The mock region element.
     */
    static Region region(final MObject parent) {
        final Region region = modelObject(Region.class, Region.MQNAME, parent, "");

        // Initialize the collection of child initial pseudo states.
        when(region.getSub(InitialPseudoState.class)).thenReturn(new ArrayList<InitialPseudoState>());

        // Configure additional relationships based on the type of parent object.
        switch (parent.getMClass().getQualifiedName()) {

        case StateMachine.MQNAME:
            // Set as the state machine's top region.
            when(((StateMachine) parent).getTop()).thenReturn(region);

            // The region itself has no state parent.
            when(region.getParent()).thenReturn(null);
            break;

        case State.MQNAME:
            // Add the region to the state's collection of child regions.
            ((State) parent).getOwnedRegion().add(region);

            // Set the region's parent state.
            when(region.getParent()).thenReturn((State) parent);
            break;

        default:
            break;
        }

        return region;
    }

    /**
     * Generates a mock state model element.
     *
     * @param name   String to assign as the model object's name.
     * @param parent Object that owns the new mock element in a composition graph.
     * @return The mock state element.
     */
    static State state(final String name, final MObject parent) {
        final State s = modelObject(State.class, State.MQNAME, parent, name);

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
        final InitialPseudoState i = modelObject(InitialPseudoState.class, InitialPseudoState.MQNAME, parent, "");
        when(i.getParent()).thenReturn(parent);
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
        final Transition t = modelObject(Transition.class, Transition.MQNAME, null, "");
        when(t.getSource()).thenReturn(source);
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
     * @param name       String to assign as the object's name.
     * @return The generated mock object.
     */
    static <T extends MObject> T modelObject(final Class<T> cls, final String MClassName, final MObject parent,
            final String name) {
        final T obj = mock(cls, Answers.RETURNS_DEEP_STUBS);
        when(obj.getCompositionOwner()).thenReturn(parent);
        lenient().when(obj.getMClass().getQualifiedName()).thenReturn(MClassName);

        // A UUID is needed to generate an MRef object pointing to the mock object.
        lenient().when(obj.getUuid()).thenReturn(UUID.randomUUID().toString());

        if (!name.isEmpty()) {
            when(obj.getName()).thenReturn(name);
        }

        // Register references to mock objects in the mock modeling session.
        MockModule.setSessionMRef(new MRef(obj), obj);

        return obj;
    }
}
