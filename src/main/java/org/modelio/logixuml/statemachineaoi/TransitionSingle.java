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

import java.util.ArrayList;
import java.util.List;

import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * This object generates a list of conditions for single-scan transitions.
 */
class TransitionSingle extends TransitionConditions {
    TransitionSingle(final Transition element) throws ExportException {
        super(element);
    }

    @Override
    protected List<Condition> computeConditions(final List<MRef> exits, final List<MRef> entries) {
        // All entry and exit actions are added to a single condition.
        final Condition c = new Condition();
        for (MRef r : exits) {
            c.addExitAction(r);
        }

        for (MRef r : entries) {
            c.addEntryAction(r);
        }

        // Return a list with the single condition.
        final List<Condition> list = new ArrayList<Condition>();
        list.add(c);
        return list;
    }
}
