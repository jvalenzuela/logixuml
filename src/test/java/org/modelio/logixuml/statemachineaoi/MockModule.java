package org.modelio.logixuml.statemachineaoi;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.Answers;
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
     * Initializes the mock module.
     */
    static void init() {
        final IModuleContext context = mock(IModuleContext.class);
        final LogixUMLModule module = new LogixUMLModule(context);

        // Setup stub methods to return a mock session.
        final IModelingSession session = mock(IModelingSession.class, Answers.RETURNS_DEEP_STUBS);
        when(module.getModuleContext().getModelingSession()).thenReturn(session);
    }

    /**
     * Configures the mock session to associate a given MRef to a given MObject.
     *
     * @param ref    MRef that will point to the target MObject.
     * @param target Referenced MObject.
     */
    static void setSessionMRef(final MRef ref, final MObject target) {
        final IModelingSession session = LogixUMLModule.getInstance().getModuleContext().getModelingSession();
        when(session.findByRef(eq(ref))).thenReturn(target);
    }
}
