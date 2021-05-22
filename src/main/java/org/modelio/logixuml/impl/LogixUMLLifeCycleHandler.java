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

package org.modelio.logixuml.impl;

import java.util.Map;

import org.modelio.api.module.context.log.ILogService;
import org.modelio.api.module.lifecycle.DefaultModuleLifeCycleHandler;
import org.modelio.api.module.lifecycle.ModuleException;
import org.modelio.vbasic.version.Version;

/**
 * Implementation of the IModuleLifeCycleHandler interface.
 * <br>This default implementation may be inherited by the module developers in order to simplify the code writing of the module life cycle handler .
 */
public class LogixUMLLifeCycleHandler extends DefaultModuleLifeCycleHandler {

	/**
	 * Constructor.
	 * @param module the Module this life cycle handler is instanciated for.
	 */
	public LogixUMLLifeCycleHandler(LogixUMLModule module) {
		super(module);
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#start()
	 */
	@Override
	public boolean start() throws ModuleException {
		// get the version of the module
		Version moduleVersion = this.module.getVersion();

		// get the Modelio log service
		ILogService logService = this.module.getModuleContext().getLogService();

		String message = "Start of " + this.module.getName() + " " + moduleVersion;
		logService.info(message);
		return super.start();
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#stop()
	 */
	@Override
	public void stop() throws ModuleException {
		super.stop();
	}

	public static boolean install(String modelioPath, String mdaPath) throws ModuleException {
		return DefaultModuleLifeCycleHandler.install(modelioPath, mdaPath);
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#select()
	 */
	@Override
	public boolean select() throws ModuleException {
		return super.select();
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#unselect()
	 */
	@Override
	public void unselect() throws ModuleException {
		super.unselect();
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#upgrade(org.modelio.api.modelio.Version, java.util.Map)
	 */
	@Override
	public void upgrade(Version oldVersion, Map<String, String> oldParameters) throws ModuleException {
		super.upgrade(oldVersion, oldParameters);
	}
}
