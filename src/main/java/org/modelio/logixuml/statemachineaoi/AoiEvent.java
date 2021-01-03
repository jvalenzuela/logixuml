package org.modelio.logixuml.statemachineaoi;

import static java.util.Collections.unmodifiableList;

import java.util.List;

import org.modelio.logixuml.l5x.AddOnInstruction;
import org.modelio.logixuml.structuredtext.IfThen;

/**
 * This object handles translating a UML event, which is simply a string
 * containing the event's name, into the parameters, local tags, and logic
 * required to implement the event in the add-on instruction.
 */
class AoiEvent {
    /**
     * String added to the event name to create the AOI input parameter.
     */
    static private final String INPUT_PREFIX = "event_";

    /**
     * Name of the AOI BOOL input parameter.
     */
    private final String inputTagName;

    /**
     * String added to the event name to create the one-shot storage local tag.
     */
    static private final String ONE_SHOT_STORAGE_PREFIX = "ons_";

    /**
     * Name of the one-shot storage local tag.
     */
    private final String onsTagName;

    /**
     * Value stored in the event queue identifying this event.
     */
    private final int id;

    /**
     * Constructor.
     *
     * @param name Event name from the UML model.
     * @param id   Numeric value used to identify this event in the event queue.
     */
    AoiEvent(final String name, final int id) {
        this.id = id;
        inputTagName = INPUT_PREFIX + name;
        onsTagName = ONE_SHOT_STORAGE_PREFIX + name;
    }

    /**
     * Adds the parameters, local tags, and static reset logic to the AOI.
     *
     * @param aoi Target add-on instruction.
     * @throws ExportException If tag names could not be created from the event
     *                         name.
     */
    void initializeAoi(final AddOnInstruction aoi) throws ExportException {
        // Create the input and local tag.
        aoi.addParameter(inputTagName, "Input", "BOOL", false,
                "Rising edge to deliver this event to the state machine.");
        aoi.addLocalTag(onsTagName, "BOOL");

        // Add lines to prescan and enable-in-false to reset the one-shot storage bit.
        // The storage bit is set to one to require a rising edge of the event input
        // during normal logic scan for the event to be detected and queued.
        final String stLine = onsTagName + " := 1;";
        aoi.addStructuredTextLine("Prescan", stLine);
        aoi.addStructuredTextLine("EnableInFalse", stLine);
    }

    /**
     * Generates structured text statements to add this event to the event queue
     * upon a rising edge of the input tag.
     *
     * @param eventQueue The object implementing event queue logic.
     * @return Structured text lines implementing the input tag processing.
     */
    List<String> evalInput(final EventQueue eventQueue) {
        // Begin with an IF/THEN block to add the event ID to the event queue on the
        // input rising edge.
        final IfThen ifBlock = new IfThen();
        final String onsExp = inputTagName + " AND NOT " + onsTagName;
        ifBlock.addCase(onsExp, eventQueue.enqueue(id));
        final List<String> stLines = ifBlock.getLines();

        // Update the ONS storage bit after the IF/THEN block.
        stLines.add(onsTagName + " := " + inputTagName + ";");

        return unmodifiableList(stLines);
    }

    /**
     * Getter method for the event's integer identifier.
     *
     * @return The event's identifier value.
     */
    int getId() {
        return id;
    }
}
