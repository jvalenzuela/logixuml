package org.modelio.logixuml.statemachineaoi;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.logixuml.impl.LogixUMLModule;
import org.modelio.vcore.smkernel.mapi.MObject;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * Creates and configures a mock Modelio module. This object is not a module
 * instance, but rather instantiates the module singleton as would normally be
 * done by Modelio, along with setting up additional module services required
 * for unit tests.
 */
class MockModule {
    /**
     * Mock context injected into the module constructor.
     */
    @Mock
    private IModuleContext context;

    /**
     * Mock module instance.
     */
    @InjectMocks
    private LogixUMLModule module;

    /**
     * Mock session that is used to resolve MRefs via findByRef().
     */
    private IModelingSession session = mock(IModelingSession.class, Answers.RETURNS_DEEP_STUBS);

    /**
     * Constructor.
     */
    MockModule() {
        MockitoAnnotations.openMocks(this);

        // Setup stub methods to return the mock session.
        when(module.getModuleContext().getModelingSession()).thenReturn(session);
    }

    /**
     * Configures the mock session to associate a given MRef to a given MObject.
     *
     * @param ref    MRef that will point to the target MObject.
     * @param target Referenced MObject.
     */
    void setSessionMRef(final MRef ref, final MObject target) {
        when(session.findByRef(eq(ref))).thenReturn(target);
    }
}
