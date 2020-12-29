package org.modelio.logixuml.statemachineaoi;

import org.modelio.vcore.smkernel.mapi.MObject;

/**
 * Exception thrown when a problem is encountered when attempting to export an
 * add-on instruction, typically due to invalid UML model content.
 */
@SuppressWarnings("serial")
public class ExportException extends Exception {
    /**
     * The UML model object where the problem occurred.
     */
    private final MObject modelObject;

    /**
     * No-argument constructor.
     */
    public ExportException() {
        modelObject = null;
    }

    /**
     * Constructor with only a message.
     *
     * @param msg The detail message.
     */
    public ExportException(final String msg) {
        super(msg);
        modelObject = null;
    }

    /**
     * Constructor with message and UML model object.
     *
     * @param msg         The detail message.
     * @param modelObject The UML model object that is the source of the problem.
     */
    public ExportException(final String msg, final MObject modelObject) {
        super(msg);
        this.modelObject = modelObject;
    }

    /**
     * Constructor with message and underlying exception.
     *
     * @param msg   The detail message.
     * @param cause The original exception.
     */
    public ExportException(final String msg, final Throwable cause) {
        super(msg, cause);
        modelObject = null;
    }

    /**
     * Getter method for the UML model object.
     *
     * @return The UML model object provided by the constructor.
     */
    public MObject getModelObject() {
        return modelObject;
    }
}
