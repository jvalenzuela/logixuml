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

package org.modelio.logixuml.statemachineaoi;

import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;

/**
 * Factory object that will generate TransitionCondition instances based on the
 * selected transition scan mode.
 */
public class TransitionConditionsFactory {
    /**
     * The selected transition scan mode.
     */
    final TransitionScanMode scanMode;

    /**
     * Constructor.
     *
     * @param scanMode Scan mode to select the type of transition condition
     *                 generator.
     */
    TransitionConditionsFactory(final TransitionScanMode scanMode) {
        this.scanMode = scanMode;
    }

    /**
     * Constructs an instance of a transition condition object based on the selected
     * scan mode.
     *
     * @param transition Source model object from which conditions will be built.
     * @return The transition condition generator object.
     * @throws ExportException If the source transition was invalid.
     */
    TransitionConditions build(final Transition transition) throws ExportException {
        TransitionConditions t = null;

        switch (scanMode) {
        case SINGLE:
            t = new TransitionSingle(transition);
            break;

        case DUAL:
            t = new TransitionDual(transition);
            break;

        case SEQUENTIAL:
            t = new TransitionSequential(transition);
            break;
        }

        return t;
    }
}
