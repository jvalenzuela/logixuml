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

import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * This object generates transition condition unit test results for single scan
 * mode transitions.
 */
class TransitionConditionSingleTests extends TransitionConditionTests {
    @Override
    protected void generateResult() throws ExportException {
        result = new TransitionSingle(transition);
    }

    @Override
    protected void generateExpectedStateMachineInitialTop() {
        final Condition c = new Condition();
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedStateMachineInitialSubstate(final State superstate) {
        final Condition c = new Condition();
        c.addEntryAction(new MRef(superstate));
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedStateMachineInitialSubstateNested(final State superstate, final State mid) {
        final Condition c = new Condition();
        c.addEntryAction(new MRef(superstate));
        c.addEntryAction(new MRef(mid));
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedStateMachineInitialSuperstateInitial(final State superstate) {
        final Condition c = new Condition();
        c.addEntryAction(new MRef(superstate));
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedStateMachineInitialSuperstateInitialNested(final State superstate, final State mid) {
        final Condition c = new Condition();
        c.addEntryAction(new MRef(superstate));
        c.addEntryAction(new MRef(mid));
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedTopSibling() {
        final Condition c = new Condition();
        c.addExitAction(new MRef(source));
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedSubstateSibling(final State superstate) {
        final Condition c = new Condition();
        c.addDoAction(new MRef(superstate));
        c.addExitAction(new MRef(source));
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedToSuperstate() {
        final Condition c = new Condition();
        c.addDoAction(new MRef(target));
        c.addExitAction(new MRef(source));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedToSuperstateNested(final State mid) {
        final Condition c = new Condition();
        c.addDoAction(new MRef(target));
        c.addExitAction(new MRef(source));
        c.addExitAction(new MRef(mid));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedToSubstate() {
        final Condition c = new Condition();
        c.addDoAction(new MRef(source));
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedToSubstateNested(final State mid) {
        final Condition c = new Condition();
        c.addDoAction(new MRef(source));
        c.addEntryAction(new MRef(mid));
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedSuperstateExit(final State superstate) {
        final Condition c = new Condition();
        c.addExitAction(new MRef(source));
        c.addExitAction(new MRef(superstate));
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedSuperstateExitNested(final State superstate, final State mid) {
        final Condition c = new Condition();
        c.addExitAction(new MRef(source));
        c.addExitAction(new MRef(mid));
        c.addExitAction(new MRef(superstate));
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedSuperstateEntry(final State superstate) {
        final Condition c = new Condition();
        c.addExitAction(new MRef(source));
        c.addEntryAction(new MRef(superstate));
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedSuperstateEntryNested(final State superstate, final State mid) {
        final Condition c = new Condition();
        c.addExitAction(new MRef(source));
        c.addEntryAction(new MRef(superstate));
        c.addEntryAction(new MRef(mid));
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedSuperstateInitial(final State superstate) {
        final Condition c = new Condition();
        c.addExitAction(new MRef(source));
        c.addEntryAction(new MRef(superstate));
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedSuperstateInitialNested(final State superstate, final State mid) {
        final Condition c = new Condition();
        c.addExitAction(new MRef(source));
        c.addEntryAction(new MRef(superstate));
        c.addEntryAction(new MRef(mid));
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedFromSubstateInitial() {
        final Condition c = new Condition();
        c.addDoAction(new MRef(target));
        c.addExitAction(new MRef(source));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedFromSubstateInitialNested(final State mid) {
        final Condition c = new Condition();
        c.addDoAction(new MRef(target));
        c.addExitAction(new MRef(source));
        c.addExitAction(new MRef(mid));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedGroupTransitionExternal(final State superstate) {
        final Condition c = new Condition();
        c.addExitAction(new MRef(source));
        c.addExitAction(new MRef(superstate));
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedGroupTransitionExternalNested(final State superstate, final State mid) {
        final Condition c = new Condition();
        c.addExitAction(new MRef(source));
        c.addExitAction(new MRef(mid));
        c.addExitAction(new MRef(superstate));
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedGroupTransitionLocal(final State superstate) {
        final Condition c = new Condition();
        c.addDoAction(new MRef(superstate));
        c.addExitAction(new MRef(source));
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedGroupTransitionLocalNested(final State superstate, final State mid) {
        final Condition c = new Condition();
        c.addDoAction(new MRef(superstate));
        c.addExitAction(new MRef(source));
        c.addExitAction(new MRef(mid));
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }
}