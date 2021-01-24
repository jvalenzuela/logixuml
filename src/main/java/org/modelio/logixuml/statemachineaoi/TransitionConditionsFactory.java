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
