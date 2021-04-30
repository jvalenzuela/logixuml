package org.modelio.logixuml.command;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
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
        try {
            final StateMachineAoi aoi = new StateMachineAoi((StateMachine) selectedElements.get(0));
            final String path = getTargetPath(context);
            if (path != null) {
                aoi.export(path);
            }
        } catch (ExportException e) {
            selectExceptionObject(e, context);
            showErrorDialog(e);
        } catch (AssertionError e) {
            showAssertionDialog(e);
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
     * Displays a GUI dialog reporting an assertion failure.
     *
     * @param e The assertion error.
     */
    private void showAssertionDialog(final Throwable e) {
        // Build a MultiStatus object containing the stack trace.
        final List<Status> childStatus = new ArrayList<>();
        for (final StackTraceElement stackTraceElement : e.getStackTrace()) {
            childStatus.add(new Status(IStatus.ERROR, "org.modelio.logixuml", stackTraceElement.toString()));
        }
        final MultiStatus ms = new MultiStatus("org.modelio.logixuml", IStatus.ERROR,
                childStatus.toArray(new Status[] {}), e.toString(), e);

        ErrorDialog.openError(null, "Assertion Error",
                "Oops, something didn't work as planned. Please report this as a bug.", ms);
    }

    /**
     * Opens a dialog to select the output directory.
     *
     * @param context LogixUML module context.
     * @return Absolute path to the target directory, or null if the Cancel button
     *         was pressed.
     */
    private String getTargetPath(final IModuleContext context) {
        final Shell parent = DefaultShellProvider.getBestParentShell();
        final DirectoryDialog dialog = new DirectoryDialog(parent);
        dialog.setText("Select AOI Export Directory");

        // Set the starting path to the same as the model project.
        final Path path = context.getProjectStructure().getPath();
        dialog.setFilterPath(path.toString());

        return dialog.open();
    }
}
