package org.modelio.logixuml.statemachineaoi;

import java.util.ArrayList;
import java.util.List;

import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * This object generates a list of conditions for dual-scan transitions.
 */
class TransitionDual extends TransitionConditions {
    TransitionDual(final Transition element) throws ExportException {
        super(element);
    }

    @Override
    protected List<Condition> computeConditions(final List<MRef> exits, final List<MRef> entries) {
        final List<Condition> list = new ArrayList<Condition>();

        // First condition contains all exit actions.
        if (!exits.isEmpty()) {
            final Condition first = new Condition();
            for (MRef r : exits) {
                first.addExitAction(r);
            }
            list.add(first);
        }

        // Last condition contains all entry actions.
        if (!entries.isEmpty()) {
            final Condition last = new Condition();
            for (MRef r : entries) {
                last.addEntryAction(r);
            }
            list.add(last);
        }

        return list;
    }
}
