package org.modelio.logixuml.command;

import java.util.List;

import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.vcore.smkernel.mapi.MObject;


public class ExportAoi extends DefaultModuleCommandHandler {
	public ExportAoi() {
		super();
	}


	@Override
	public void actionPerformed(List<MObject> selectedElements, IModule module) {

	}
}
