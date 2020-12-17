package org.modelio.logixuml.statemachineaoi;

import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * This object generates transition condition unit test results for dual scan
 * mode transitions.
 */
class TransitionConditionDualTests extends TransitionConditionTests {
    @Override
    protected void generateResult() throws ExportException {
        result = new TransitionDual(transition);
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
        final Condition c1 = new Condition();
        c1.addExitAction(new MRef(source));
        expectedConditions.add(c1);

        final Condition c2 = new Condition();
        c2.addEntryAction(new MRef(target));
        expectedConditions.add(c2);
    }

    @Override
    protected void generateExpectedSubstateSibling(final State superstate) {
        final Condition c1 = new Condition();
        c1.addDoAction(new MRef(superstate));
        c1.addExitAction(new MRef(source));
        expectedConditions.add(c1);

        final Condition c2 = new Condition();
        c2.addDoAction(new MRef(superstate));
        c2.addEntryAction(new MRef(target));
        expectedConditions.add(c2);
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
        final Condition c1 = new Condition();
        c1.addExitAction(new MRef(source));
        c1.addExitAction(new MRef(superstate));
        expectedConditions.add(c1);

        final Condition c2 = new Condition();
        c2.addEntryAction(new MRef(target));
        expectedConditions.add(c2);
    }

    @Override
    protected void generateExpectedSuperstateExitNested(final State superstate, final State mid) {
        final Condition c1 = new Condition();
        c1.addExitAction(new MRef(source));
        c1.addExitAction(new MRef(mid));
        c1.addExitAction(new MRef(superstate));
        expectedConditions.add(c1);

        final Condition c2 = new Condition();
        c2.addEntryAction(new MRef(target));
        expectedConditions.add(c2);
    }

    @Override
    protected void generateExpectedSuperstateEntry(final State superstate) {
        final Condition c1 = new Condition();
        c1.addExitAction(new MRef(source));
        expectedConditions.add(c1);

        final Condition c2 = new Condition();
        c2.addEntryAction(new MRef(superstate));
        c2.addEntryAction(new MRef(target));
        expectedConditions.add(c2);
    }

    @Override
    protected void generateExpectedSuperstateEntryNested(final State superstate, final State mid) {
        final Condition c1 = new Condition();
        c1.addExitAction(new MRef(source));
        expectedConditions.add(c1);

        final Condition c2 = new Condition();
        c2.addEntryAction(new MRef(superstate));
        c2.addEntryAction(new MRef(mid));
        c2.addEntryAction(new MRef(target));
        expectedConditions.add(c2);
    }

    @Override
    protected void generateExpectedSuperstateInitial(final State superstate) {
        final Condition c1 = new Condition();
        c1.addExitAction(new MRef(source));
        expectedConditions.add(c1);

        final Condition c2 = new Condition();
        c2.addEntryAction(new MRef(superstate));
        c2.addEntryAction(new MRef(target));
        expectedConditions.add(c2);
    }

    @Override
    protected void generateExpectedSuperstateInitialNested(final State superstate, final State mid) {
        final Condition c1 = new Condition();
        c1.addExitAction(new MRef(source));
        expectedConditions.add(c1);

        final Condition c2 = new Condition();
        c2.addEntryAction(new MRef(superstate));
        c2.addEntryAction(new MRef(mid));
        c2.addEntryAction(new MRef(target));
        expectedConditions.add(c2);
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
}
