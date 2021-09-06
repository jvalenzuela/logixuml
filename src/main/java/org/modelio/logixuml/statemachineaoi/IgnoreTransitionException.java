package org.modelio.logixuml.statemachineaoi;

/**
 * This exception is raised when a transition is found to have no effect given
 * the currently active state configuration, and should therefore leave the
 * state machine unaffected by ignoring the triggering event.
 */
@SuppressWarnings("serial")
class IgnoreTransitionException extends Exception {
}
