package org.modelio.logixuml.statemachineaoi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.mockito.Answers;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
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
        return modelObject(Region.class, Region.MQNAME, parent);
    }

    /**
     * Generates a mock state model element.
     *
     * @param parent Object that owns the new mock element in a composition graph.
     * @return The mock state element.
     */
    static State state(final MObject parent) {
        return modelObject(State.class, State.MNAME, parent);
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
        when(obj.getMClass().getQualifiedName()).thenReturn(MClassName);

        // A UUID is needed to generate an MRef object pointing to the mock object.
        when(obj.getUuid()).thenReturn(UUID.randomUUID().toString());

        return obj;
    }
}
