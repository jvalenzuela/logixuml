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
 * This object generates transition condition unit test results for sequential
 * scan mode transitions.
 */
class TransitionConditionSequentialTests extends TransitionConditionTests {
    @Override
    protected void generateResult() throws ExportException {
        result = new TransitionSequential(transition);
    }

    @Override
    protected void generateExpectedStateMachineInitialTop() {
        final Condition c = new Condition();
        c.addEntryAction(new MRef(target));
        expectedConditions.add(c);
    }

    @Override
    protected void generateExpectedStateMachineInitialSubstate(final State superstate) {
        final MRef superstateRef = new MRef(superstate);

        final Condition c0 = new Condition();
        c0.addEntryAction(superstateRef);
        expectedConditions.add(c0);

        final Condition c1 = new Condition();
        c1.addDoAction(superstateRef);
        c1.addEntryAction(new MRef(target));
        expectedConditions.add(c1);
    }

    @Override
    protected void generateExpectedStateMachineInitialSubstateNested(final State superstate, final State mid) {
        final MRef superstateRef = new MRef(superstate);
        final MRef midRef = new MRef(mid);

        final Condition c0 = new Condition();
        c0.addEntryAction(superstateRef);
        expectedConditions.add(c0);

        final Condition c1 = new Condition();
        c1.addDoAction(superstateRef);
        c1.addEntryAction(midRef);
        expectedConditions.add(c1);

        final Condition c2 = new Condition();
        c2.addDoAction(superstateRef);
        c2.addDoAction(midRef);
        c2.addEntryAction(new MRef(target));
        expectedConditions.add(c2);
    }

    @Override
    protected void generateExpectedStateMachineInitialSuperstateInitial(final State superstate) {
        final MRef superstateRef = new MRef(superstate);

        final Condition c0 = new Condition();
        c0.addEntryAction(superstateRef);
        expectedConditions.add(c0);

        final Condition c1 = new Condition();
        c1.addDoAction(superstateRef);
        c1.addEntryAction(new MRef(target));
        expectedConditions.add(c1);
    }

    @Override
    protected void generateExpectedStateMachineInitialSuperstateInitialNested(final State superstate, final State mid) {
        final MRef superstateRef = new MRef(superstate);
        final MRef midRef = new MRef(mid);

        final Condition c0 = new Condition();
        c0.addEntryAction(superstateRef);
        expectedConditions.add(c0);

        final Condition c1 = new Condition();
        c1.addDoAction(superstateRef);
        c1.addEntryAction(midRef);
        expectedConditions.add(c1);

        final Condition c2 = new Condition();
        c2.addDoAction(superstateRef);
        c2.addDoAction(midRef);
        c2.addEntryAction(new MRef(target));
        expectedConditions.add(c2);

    }

    @Override
    protected void generateExpectedTopSibling() {
        final Condition c0 = new Condition();
        c0.addExitAction(new MRef(source));
        expectedConditions.add(c0);

        final Condition c1 = new Condition();
        c1.addEntryAction(new MRef(target));
        expectedConditions.add(c1);
    }

    @Override
    protected void generateExpectedSubstateSibling(final State superstate) {
        final MRef superstateRef = new MRef(superstate);

        final Condition c0 = new Condition();
        c0.addDoAction(superstateRef);
        c0.addExitAction(new MRef(source));
        expectedConditions.add(c0);

        final Condition c1 = new Condition();
        c1.addDoAction(superstateRef);
        c1.addEntryAction(new MRef(target));
        expectedConditions.add(c1);
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
        final MRef targetRef = new MRef(target);
        final MRef midRef = new MRef(mid);

        final Condition c0 = new Condition();
        c0.addDoAction(targetRef);
        c0.addDoAction(midRef);
        c0.addExitAction(new MRef(source));
        expectedConditions.add(c0);

        final Condition c1 = new Condition();
        c1.addDoAction(targetRef);
        c1.addExitAction(midRef);
        expectedConditions.add(c1);
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
        final MRef sourceRef = new MRef(source);
        final MRef midRef = new MRef(mid);

        final Condition c0 = new Condition();
        c0.addDoAction(sourceRef);
        c0.addEntryAction(midRef);
        expectedConditions.add(c0);

        final Condition c1 = new Condition();
        c1.addDoAction(sourceRef);
        c1.addDoAction(midRef);
        c1.addEntryAction(new MRef(target));
        expectedConditions.add(c1);
    }

    @Override
    protected void generateExpectedSuperstateExit(final State superstate) {
        final MRef superstateRef = new MRef(superstate);

        final Condition c0 = new Condition();
        c0.addDoAction(superstateRef);
        c0.addExitAction(new MRef(source));
        expectedConditions.add(c0);

        final Condition c1 = new Condition();
        c1.addExitAction(superstateRef);
        expectedConditions.add(c1);

        final Condition c2 = new Condition();
        c2.addEntryAction(new MRef(target));
        expectedConditions.add(c2);
    }

    @Override
    protected void generateExpectedSuperstateExitNested(final State superstate, final State mid) {
        final MRef superstateRef = new MRef(superstate);
        final MRef midRef = new MRef(mid);

        final Condition c0 = new Condition();
        c0.addDoAction(superstateRef);
        c0.addDoAction(midRef);
        c0.addExitAction(new MRef(source));
        expectedConditions.add(c0);

        final Condition c1 = new Condition();
        c1.addDoAction(superstateRef);
        c1.addExitAction(midRef);
        expectedConditions.add(c1);

        final Condition c2 = new Condition();
        c2.addExitAction(superstateRef);
        expectedConditions.add(c2);

        final Condition c3 = new Condition();
        c3.addEntryAction(new MRef(target));
        expectedConditions.add(c3);
    }

    @Override
    protected void generateExpectedSuperstateEntry(final State superstate) {
        final MRef superstateRef = new MRef(superstate);

        final Condition c0 = new Condition();
        c0.addExitAction(new MRef(source));
        expectedConditions.add(c0);

        final Condition c1 = new Condition();
        c1.addEntryAction(superstateRef);
        expectedConditions.add(c1);

        final Condition c2 = new Condition();
        c2.addDoAction(superstateRef);
        c2.addEntryAction(new MRef(target));
        expectedConditions.add(c2);
    }

    @Override
    protected void generateExpectedSuperstateEntryNested(final State superstate, final State mid) {
        final MRef superstateRef = new MRef(superstate);
        final MRef midRef = new MRef(mid);

        final Condition c0 = new Condition();
        c0.addExitAction(new MRef(source));
        expectedConditions.add(c0);

        final Condition c1 = new Condition();
        c1.addEntryAction(superstateRef);
        expectedConditions.add(c1);

        final Condition c2 = new Condition();
        c2.addDoAction(superstateRef);
        c2.addEntryAction(midRef);
        expectedConditions.add(c2);

        final Condition c3 = new Condition();
        c3.addDoAction(superstateRef);
        c3.addDoAction(midRef);
        c3.addEntryAction(new MRef(target));
        expectedConditions.add(c3);
    }

    @Override
    protected void generateExpectedSuperstateInitial(final State superstate) {
        final MRef superstateRef = new MRef(superstate);

        final Condition c0 = new Condition();
        c0.addExitAction(new MRef(source));
        expectedConditions.add(c0);

        final Condition c1 = new Condition();
        c1.addEntryAction(superstateRef);
        expectedConditions.add(c1);

        final Condition c2 = new Condition();
        c2.addDoAction(superstateRef);
        c2.addEntryAction(new MRef(target));
        expectedConditions.add(c2);
    }

    @Override
    protected void generateExpectedSuperstateInitialNested(final State superstate, final State mid) {
        final MRef superstateRef = new MRef(superstate);
        final MRef midRef = new MRef(mid);

        final Condition c0 = new Condition();
        c0.addExitAction(new MRef(source));
        expectedConditions.add(c0);

        final Condition c1 = new Condition();
        c1.addEntryAction(superstateRef);
        expectedConditions.add(c1);

        final Condition c2 = new Condition();
        c2.addDoAction(superstateRef);
        c2.addEntryAction(midRef);
        expectedConditions.add(c2);

        final Condition c3 = new Condition();
        c3.addDoAction(superstateRef);
        c3.addDoAction(midRef);
        c3.addEntryAction(new MRef(target));
        expectedConditions.add(c3);
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
        final MRef targetRef = new MRef(target);
        final MRef midRef = new MRef(mid);

        final Condition c0 = new Condition();
        c0.addDoAction(targetRef);
        c0.addDoAction(midRef);
        c0.addExitAction(new MRef(source));
        expectedConditions.add(c0);

        final Condition c1 = new Condition();
        c1.addDoAction(targetRef);
        c1.addExitAction(midRef);
        expectedConditions.add(c1);
    }

    @Override
    protected void generateExpectedGroupTransitionExternal(final State superstate) {
        {
            final Condition c = new Condition();
            c.addExitAction(new MRef(source));
            c.addDoAction(new MRef(superstate));
            expectedConditions.add(c);
        }

        {
            final Condition c = new Condition();
            c.addExitAction(new MRef(superstate));
            expectedConditions.add(c);
        }

        {
            final Condition c = new Condition();
            c.addEntryAction(new MRef(target));
            expectedConditions.add(c);
        }
    }

    @Override
    protected void generateExpectedGroupTransitionExternalNested(final State superstate, final State mid) {
        {
            final Condition c = new Condition();
            c.addExitAction(new MRef(source));
            c.addDoAction(new MRef(mid));
            c.addDoAction(new MRef(superstate));
            expectedConditions.add(c);
        }

        {
            final Condition c = new Condition();
            c.addExitAction(new MRef(mid));
            c.addDoAction(new MRef(superstate));
            expectedConditions.add(c);
        }

        {
            final Condition c = new Condition();
            c.addExitAction(new MRef(superstate));
            expectedConditions.add(c);
        }

        {
            final Condition c = new Condition();
            c.addEntryAction(new MRef(target));
            expectedConditions.add(c);
        }
    }

    @Override
    protected void generateExpectedGroupTransitionLocal(final State superstate) {
        {
            final Condition c = new Condition();
            c.addExitAction(new MRef(source));
            c.addDoAction(new MRef(superstate));
            expectedConditions.add(c);
        }

        {
            final Condition c = new Condition();
            c.addEntryAction(new MRef(target));
            c.addDoAction(new MRef(superstate));
            expectedConditions.add(c);
        }
    }

    @Override
    protected void generateExpectedGroupTransitionLocalNested(final State superstate, final State mid) {
        {
            final Condition c = new Condition();
            c.addExitAction(new MRef(source));
            c.addDoAction(new MRef(mid));
            c.addDoAction(new MRef(superstate));
            expectedConditions.add(c);
        }

        {
            final Condition c = new Condition();
            c.addExitAction(new MRef(mid));
            c.addDoAction(new MRef(superstate));
            expectedConditions.add(c);
        }

        {
            final Condition c = new Condition();
            c.addEntryAction(new MRef(target));
            c.addDoAction(new MRef(superstate));
            expectedConditions.add(c);
        }
    }

    @Override
    protected void generateExpectedGroupTransitionToSuper(final State mid) {
        {
            final Condition c = new Condition();
            c.addExitAction(new MRef(source));
            c.addDoAction(new MRef(mid));
            c.addDoAction(new MRef(target));
            expectedConditions.add(c);
        }

        {
            final Condition c = new Condition();
            c.addExitAction(new MRef(mid));
            c.addDoAction(new MRef(target));
            expectedConditions.add(c);
        }
    }

    @Override
    protected void generateExpectedGroupTransitionToSuperInitial(State mid) {
        {
            final Condition c = new Condition();
            c.addExitAction(new MRef(source));
            c.addDoAction(new MRef(mid));
            c.addDoAction(new MRef(target));
            expectedConditions.add(c);
        }

        {
            final Condition c = new Condition();
            c.addExitAction(new MRef(mid));
            c.addDoAction(new MRef(target));
            expectedConditions.add(c);
        }
    }

    @Override
    protected void generateExpectedGroupTransitionToSubInitial(State superstate, State substate) {
        {
            final Condition c = new Condition();
            c.addEntryAction(new MRef(substate));
            c.addDoAction(new MRef(source));
            c.addDoAction(new MRef(superstate));
            expectedConditions.add(c);
        }

        {
            final Condition c = new Condition();
            c.addEntryAction(new MRef(target));
            c.addDoAction(new MRef(substate));
            c.addDoAction(new MRef(source));
            c.addDoAction(new MRef(superstate));
            expectedConditions.add(c);
        }
    }
}
