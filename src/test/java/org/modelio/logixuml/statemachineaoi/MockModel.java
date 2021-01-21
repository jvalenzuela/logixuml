package org.modelio.logixuml.statemachineaoi;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.emf.common.util.BasicEList;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.modelio.metamodel.uml.behavior.stateMachineModel.ChoicePseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.ConnectionPointReference;
import org.modelio.metamodel.uml.behavior.stateMachineModel.DeepHistoryPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.EntryPointPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.ExitPointPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.FinalState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.ForkPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.InitialPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.InternalTransition;
import org.modelio.metamodel.uml.behavior.stateMachineModel.JoinPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.JunctionPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.ShallowHistoryPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateVertex;
import org.modelio.metamodel.uml.behavior.stateMachineModel.TerminatePseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MClass;
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
     * @param name   String to return from getName().
     * @param parent Object that owns the new mock element in a composition graph.
     * @return The mock state machine object.
     */
    static StateMachine stateMachine(final String name, final MObject parent) {
        return modelObject(StateMachine.class, StateMachine.MQNAME, parent, name);
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
     * Generates a mock internal transition model element.
     *
     * @param parent Model object owning the new mock element.
     * @return The mock internal transition element.
     */
    static InternalTransition internalTransition(final MObject parent) {
        return modelObject(InternalTransition.class, InternalTransition.MQNAME, parent, "");
    }

    /**
     * Generates a terminate state model element.
     *
     * @param parent Model object owning the new mock element.
     * @return The mock terminate state element.
     */
    static TerminatePseudoState terminatePseudoState(final MObject parent) {
        return modelObject(TerminatePseudoState.class, TerminatePseudoState.MQNAME, parent, "");
    }

    /**
     * Generates a mock entry point model element.
     *
     * @param parent Model object owning the new mock element.
     * @return The mock entry point element.
     */
    static EntryPointPseudoState entryPointPseudoState(final MObject parent) {
        return modelObject(EntryPointPseudoState.class, EntryPointPseudoState.MQNAME, parent, "");
    }

    /**
     * Generates a mock exit point model element.
     *
     * @param parent Model object owning the new mock element.
     * @return The mock exit point element.
     */
    static ExitPointPseudoState exitPointPseudoState(final MObject parent) {
        return modelObject(ExitPointPseudoState.class, ExitPointPseudoState.MQNAME, parent, "");
    }

    /**
     * Generates a mock fork model element.
     *
     * @param parent Model object owning the new mock element.
     * @return The mock fork element.
     */
    static ForkPseudoState forkPseudoState(final MObject parent) {
        return modelObject(ForkPseudoState.class, ForkPseudoState.MQNAME, parent, "");
    }

    /**
     * Generates a mock join model element.
     *
     * @param parent Model object owning the new mock element.
     * @return The mock join element.
     */
    static JoinPseudoState joinPseudoState(final MObject parent) {
        return modelObject(JoinPseudoState.class, JoinPseudoState.MQNAME, parent, "");
    }

    /**
     * Generates a mock junction model element.
     *
     * @param parent Model object owning the new mock element.
     * @return The mock junction element.
     */
    static JunctionPseudoState junctionPseudoState(final MObject parent) {
        return modelObject(JunctionPseudoState.class, JunctionPseudoState.MQNAME, parent, "");
    }

    /**
     * Generates a mock choice model element.
     *
     * @param parent Model object owning the new mock element.
     * @return The mock choice element.
     */
    static ChoicePseudoState choicePseudoState(final MObject parent) {
        return modelObject(ChoicePseudoState.class, ChoicePseudoState.MQNAME, parent, "");
    }

    /**
     * Generates a mock deep history model element.
     *
     * @param parent Model object owning the new mock element.
     * @return The mock deep history element.
     */
    static DeepHistoryPseudoState deepHistoryPseudoState(final MObject parent) {
        return modelObject(DeepHistoryPseudoState.class, DeepHistoryPseudoState.MQNAME, parent, "");
    }

    /**
     * Generates a mock shallow history model element.
     *
     * @param parent Model object owning the new mock element.
     * @return The mock shallow history element.
     */
    static ShallowHistoryPseudoState shallowHistoryPseudoState(final MObject parent) {
        return modelObject(ShallowHistoryPseudoState.class, ShallowHistoryPseudoState.MQNAME, parent, "");
    }

    /**
     * Generates a mock connection point reference model element.
     *
     * @param parent Model object owning the new mock element.
     * @return The mock connection point reference element.
     */
    static ConnectionPointReference connectionPointReference(final MObject parent) {
        return modelObject(ConnectionPointReference.class, ConnectionPointReference.MQNAME, parent, "");
    }

    /**
     * Generates a mock final state model element.
     *
     * @param parent Model object owning the new mock element.
     * @return The mock final state element.
     */
    static FinalState finalState(final MObject parent) {
        return modelObject(FinalState.class, FinalState.MQNAME, parent, "");
    }

    /**
     * Adds a mock stereotype property table value.
     *
     * @param element  Element that would normally have the stereotype applied.
     * @param property Property name.
     * @param value    Property value.
     */
    static void addProperty(final ModelElement element, final String property, final String value) {
        when(element.getProperty(anyString(), anyString(), eq(property))).thenReturn(value);
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

        // Update the parent getCompositionChildren() stub to include the new object.
        // This is done by replacing the child list with a new list containing the
        // original children plus the new object, instead of adding the new object to
        // the original list, to accommodate stubs returning generics.
        if (parent != null) {
            final List<? extends MObject> origChildren = parent.getCompositionChildren();
            final List<MObject> newChildren = new ArrayList<MObject>(origChildren);
            newChildren.add(obj);
            Mockito.doReturn(newChildren).when(parent).getCompositionChildren();
        }

        // Create an empty collection for the getCompositionChildren() stub.
        Mockito.doReturn(new ArrayList<MObject>()).when(obj).getCompositionChildren();

        lenient().when(obj.getMClass().getQualifiedName()).thenReturn(MClassName);

        // Generate the simple name by splitting the fully-qualified name.
        final String simpleName = MClassName.split(String.format("\\Q%c\\E", MClass.QUALIFIER_SEP))[1];
        when(obj.getMClass().getName()).thenReturn(simpleName);

        // A UUID is needed to generate an MRef object pointing to the mock object.
        lenient().when(obj.getUuid()).thenReturn(UUID.randomUUID().toString());

        when(obj.getName()).thenReturn(name);

        // Register references to mock objects in the mock modeling session.
        MockModule.setSessionMRef(new MRef(obj), obj);

        return obj;
    }
}
