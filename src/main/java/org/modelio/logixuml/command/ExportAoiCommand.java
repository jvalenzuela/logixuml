package org.modelio.logixuml.command;

import java.nio.file.Path;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.modelio.api.modelio.navigation.INavigationService;
import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.logixuml.statemachineaoi.ExportException;
import org.modelio.logixuml.statemachineaoi.StateMachineAoi;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.ui.swt.DefaultShellProvider;
import org.modelio.vcore.smkernel.mapi.MObject;

public class ExportAoiCommand extends DefaultModuleCommandHandler {
    public ExportAoiCommand() {
        super();
    }

    @Override
    public void actionPerformed(List<MObject> selectedElements, IModule module) {
        final IModuleContext context = module.getModuleContext();
        final StateMachineAoi aoi;
        try {
            aoi = new StateMachineAoi((StateMachine) selectedElements.get(0));
        } catch (ExportException e) {
            selectExceptionObject(e, context);
            showErrorDialog(e);
        }
    }

    /**
     * Changes the Modelio GUI selection to the model object that was the source of
     * an export problem.
     *
     * @param e       Exception raised during the export.
     * @param context LogixUML module context.
     */
    private void selectExceptionObject(final ExportException e, final IModuleContext context) {
        final MObject sourceObject = e.getModelObject();
        if (sourceObject != null) {
            final INavigationService nav = context.getModelioServices().getNavigationService();
            nav.fireNavigate(sourceObject);
        }
    }

    /**
     * Displays a GUI dialog reporting an error during AOI export.
     *
     * @param e Exception raised during the export.
     */
    private void showErrorDialog(final Throwable e) {
        final Status status = new Status(IStatus.ERROR, "org.modelio.logixuml", e.getMessage(), e.getCause());
        ErrorDialog.openError(null, "AOI Export Error", null, status);
    }

    /**
     * Opens a dialog to select the output filename.
     *
     * @param aoiName
     * @param context LogixUML module context.
     * @return Absolute path to the target L5X file, or null if the Cancel button
     *         was pressed.
     */
    private String getTargetFilename(final String aoiName, final IModuleContext context) {
        final Shell parent = DefaultShellProvider.getBestParentShell();
        final FileDialog dialog = new FileDialog(parent);
        dialog.setText("Select AOI Export File Name");
        dialog.setFileName(aoiName + ".L5X");
        dialog.setFilterExtensions(new String[] { "*.L5X" });

        // Set the starting path to the same as the model project.
        final Path path = context.getProjectStructure().getPath();
        dialog.setFilterPath(path.toString());

        return dialog.open();
    }
}
