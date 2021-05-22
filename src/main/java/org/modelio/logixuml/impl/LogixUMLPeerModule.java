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

import org.modelio.vbasic.version.Version;
import org.modelio.api.module.context.configuration.IModuleAPIConfiguration;

import org.modelio.logixuml.api.ILogixUMLPeerModule;

/**
 * Implementation of Module services
 * <br>When a module is built using the MDA Modeler tool, a public interface is generated and accessible for the other module developments.
 * <br>The main class that allows developpers to get specific module services has to implement the current interface.
 * <br>Each mda component brings a specific interface that inherit from this one and gives all the desired module services.
 *
 */
public class LogixUMLPeerModule implements ILogixUMLPeerModule {
    private LogixUMLModule module;
	
    private IModuleAPIConfiguration peerConfiguration;
    
	public LogixUMLPeerModule(LogixUMLModule statModuleModule, IModuleAPIConfiguration peerConfiguration) {
		super();
		this.module = statModuleModule;
		this.peerConfiguration = peerConfiguration;
	}

	/**
	 * @see org.modelio.api.module.IPeerModule#getConfiguration()
	 */
	@Override
	public IModuleAPIConfiguration getConfiguration() {
		return this.peerConfiguration;
	}

	/**
	 * @see org.modelio.api.module.IPeerModule#getDescription()
	 */
	@Override
	public String getDescription() {
	    return this.module.getDescription();
	}

	/**
	 * @see org.modelio.api.module.IPeerModule#getName()
	 */
	@Override
	public String getName() {
	    return this.module.getName();
	}

	/**
	 * @see org.modelio.api.module.IPeerModule#getVersion()
	 */
	@Override
	public Version getVersion() {
	    return this.module.getVersion();
	}

}
