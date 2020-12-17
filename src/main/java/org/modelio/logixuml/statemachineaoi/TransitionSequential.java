package org.modelio.logixuml.statemachineaoi;

import java.util.ArrayList;
import java.util.List;

import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * This object generates a list of conditions for sequential-scan transitions.
 */
class TransitionSequential extends TransitionConditions {
    TransitionSequential(final Transition element) throws ExportException {
        super(element);
    }

    @Override
    protected List<Condition> computeConditions(final List<MRef> exits, final List<MRef> entries) {
        final List<Condition> list = new ArrayList<Condition>();

        // Begin with the exit actions, one per condition.
        for (final MRef r : exits) {
            final Condition c = new Condition();
            c.addExitAction(r);
            list.add(c);
        }

        // End with the entry actions, one per condition.
        for (final MRef r : entries) {
            final Condition c = new Condition();
            c.addEntryAction(r);
            list.add(c);
        }

        return list;
    }
}
