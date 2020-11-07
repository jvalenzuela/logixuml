package org.modelio.logixuml.l5x;

/**
 * Exception for problems generating an add-on instruction.
 */
public class AddOnInstructionException extends Exception {
    /**
     * Constructor.
     *
     * @param msg String containing the error description.
     */
    public AddOnInstructionException(final String msg) {
        super(msg);
    }

    /**
     * Constructor with source exception reference.
     *
     * @param msg   String containing the error description.
     * @param error Original exception.
     */
    public AddOnInstructionException(final String msg, Throwable error) {
        super(msg, error);
    }
}
