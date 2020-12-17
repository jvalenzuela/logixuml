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
