package org.modelio.logixuml.statemachineaoi;

import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;

/**
 * This class reads and validates parameter values specified in the module's
 * stereotype property table.
 */
class StereotypeProperties {
    /**
     * Source state machine model object.
     */
    private final StateMachine stateMachine;

    /**
     * Lower, inclusive limit for event queue sizes.
     */
    private static final int MIN_EVENT_QUEUE_SIZE = 1;

    /**
     * Upper, inclusive limit for event queue sizes.
     */
    private static final int MAX_EVENT_QUEUE_SIZE = 8;

    /**
     * Constructor.
     *
     * @param stateMachine Source state machine model element.
     * @throws ExportException
     */
    StereotypeProperties(final StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    /**
     * Getter method to acquire the event queue size parameter.
     *
     * @return The selected event queue size.
     * @throws ExportException If the property table contains an invalid event queue
     *                         size.
     */
    int getEventQueueSize() throws ExportException {
        final String rawValue = getTableValue("eventQueueSize");
        final int value;

        try {
            value = Integer.parseInt(rawValue);
            if ((value < MIN_EVENT_QUEUE_SIZE) || (value > MAX_EVENT_QUEUE_SIZE)) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            throw new ExportException("Illegal event queue size. Valid values are between " + MIN_EVENT_QUEUE_SIZE
                    + " and " + MAX_EVENT_QUEUE_SIZE + " inclusive.");
        }

        return value;
    }

    /**
     * Getter method to acquire the transition scan mode parameter.
     *
     * @return The selected transition scan mode.
     * @throws ExportException If the property table contains an invalid transition
     *                         scan mode.
     */
    TransitionScanMode getTransitionScanMode() throws ExportException {
        final TransitionScanMode mode;
        final String rawValue = getTableValue("transitionScanMode");

        try {
            mode = TransitionScanMode.valueOf(rawValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ExportException("Undefined transition scan mode.");
        }

        return mode;
    }

    /**
     * Retrieves a value from the stereotype property table.
     *
     * @param key Name of the property to get.
     * @return The property value.
     */
    private String getTableValue(final String key) {
        String value = stateMachine.getProperty("LogixUML", "StateMachineAoi", key);

        // getProperty() will return null if no value has been entered since the
        // stereotype was applied. Convert this to an empty string, matching the default
        // values defined in the property table
        value = (value == null) ? "" : value;

        return value.trim();
    }
}
