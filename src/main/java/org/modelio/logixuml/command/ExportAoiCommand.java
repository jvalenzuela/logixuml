package org.modelio.logixuml.command;

import java.util.List;

import org.modelio.api.modelio.navigation.INavigationService;
import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.logixuml.impl.LogixUMLModule;
import org.modelio.logixuml.statemachineaoi.ExportException;
import org.modelio.logixuml.statemachineaoi.StateMachineAoi;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.vcore.smkernel.mapi.MObject;

public class ExportAoiCommand extends DefaultModuleCommandHandler {
    public ExportAoiCommand() {
        super();
    }

    @Override
    public void actionPerformed(List<MObject> selectedElements, IModule module) {
        final StateMachineAoi aoi;
        try {
            aoi = new StateMachineAoi((StateMachine) selectedElements.get(0));
        } catch (ExportException e) {
            selectExceptionObject(e);
        }
    }

    /**
     * Changes the Modelio GUI selection to the model object that was the source of
     * an export problem.
     *
     * @param e Exception raised during the export.
     */
    private void selectExceptionObject(final ExportException e) {
        final MObject sourceObject = e.getModelObject();
        if (sourceObject != null) {
            final IModuleContext context = LogixUMLModule.getInstance().getModuleContext();
            final INavigationService nav = context.getModelioServices().getNavigationService();
            nav.fireNavigate(sourceObject);
        }
    }

    }
}
