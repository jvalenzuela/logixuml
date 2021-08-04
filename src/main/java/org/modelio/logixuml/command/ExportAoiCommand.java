/*
 * Copyright 2021 Jason Valenzuela
 *
 * This file is part of LogixUML.
 *
 * LogixUML is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LogixUML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LogixUML.  If not, see <https://www.gnu.org/licenses/>.
 */

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
            // Generate the set of AOIs from the selected state machine model
            // objects.
            final List<StateMachineAoi> aois = new ArrayList<>();
            for (final MObject element: selectedElements) {
                aois.add(new StateMachineAoi((StateMachine) element));
            }

            // Select an output directory and write all AOIs if no state
            // machines raised an exception.
            final String path = getTargetPath(context);
            if (path != null) {
                for (final StateMachineAoi aoi: aois) {
                    aoi.export(path);
                }
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
