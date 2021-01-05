package org.modelio.logixuml.statemachineaoi;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import org.modelio.logixuml.l5x.AddOnInstruction;
import org.modelio.logixuml.structuredtext.IfThen;

/**
 * A buffer to store and retrieve events, implemented as a FIFO using a DINT
 * array as storage:
 *
 * <pre>
 *   [0] <- Tail index where the next value will be removed(dequeue).
 *   [1]
 *   [2] <- Head index where the next value will be stored(enqueue).
 *   .
 *   .
 *   .
 *   [capacity - 1]
 * </pre>
 *
 * The storage array is accessed in a circular manner, so the head and tail
 * indices will wrap back to zero when the end of the array is reached.
 */
class EventQueue {
    /**
     * String constants defining the parameter and local tag names.
     */
    private class TagNames {
        /**
         * Local DINT array tag used to store values in the queue.
         */
        private final static String STORAGE = "q";

        /**
         * Local DINT tag storing the index into the storage array where the next value
         * will be placed.
         */
        private final static String HEAD = "qh";

        /**
         * Local DINT tag storing the index into the storage array where the next value
         * will be removed.
         */
        private final static String TAIL = "qt";

        /**
         * Local DINT tag holding the number of items currently in the queue.
         */
        private final static String SIZE = "qs";

        /**
         * Output BOOL parameter that is set if an attempt is made to enqueue more items
         * than the queue can hold.
         */
        private final static String OVERFLOW = "event_queue_overflow";

        /**
         * Output DINT parameter that contains the highest number of events ever stored
         * in the queue.
         */
        private final static String WATERMARK = "event_queue_watermark";
    }

    /**
     * Add-on instruction containing the queue.
     */
    private AddOnInstruction aoi;

    /**
     * Number of items the queue can hold.
     */
    private int capacity;

    /**
     * Constructor.
     *
     * @param aoi      Add-on instruction containing the queue.
     * @param capacity Maximum number of events that queue must hold.
     * @throws ExportException If the AOI resources could not be created.
     */
    public EventQueue(final AddOnInstruction aoi, final int capacity) throws ExportException {
        this.aoi = aoi;
        this.capacity = capacity;
        createTags();

        // Empty the queue in the prescan and enable-in false routines.
        clear("Prescan");
        clear("EnableInFalse");
    }

    /**
     * Defines the parameters and local tags used by the queue.
     *
     * @throws ExportException If the necessary parameters or local tags could not
     *                         be created.
     */
    private void createTags() throws ExportException {
        try {
            aoi.addParameter(TagNames.OVERFLOW, "Output", "BOOL", true, "True if an overflow has occurred.");
            aoi.addParameter(TagNames.WATERMARK, "Output", "DINT", true, "Highest number of events stored.");

            aoi.addLocalTag(TagNames.STORAGE, "DINT", capacity);
            aoi.addLocalTag(TagNames.HEAD, "DINT");
            aoi.addLocalTag(TagNames.TAIL, "DINT");
            aoi.addLocalTag(TagNames.SIZE, "DINT");
        } catch (ExportException e) {
            // These tag names are not derived from UML model names, and should never be
            // invalid.
            throw new ExportException("Failed to create event queue tags.");
        }
    }

    /**
     * Generates a set of structured text commands to empty the queue.
     *
     * @param routine Name of the routine to append the structured text lines to.
     */
    private void clear(final String routine) {
        ArrayList<String> lines = new ArrayList<String>();

        lines.add(String.format("%s := 0;", TagNames.HEAD));
        lines.add(String.format("%s := 0;", TagNames.TAIL));
        lines.add(String.format("%s := 0;", TagNames.SIZE));
        lines.add(String.format("%s := 0;", TagNames.OVERFLOW));

        addLines(routine, lines);
    }

    /**
     * Generates a set of structured text commands in the logic routine to place a
     * value into the queue.
     *
     * @param value Value to add to the queue.
     * @return Structured text lines implementing the enqueue operation.
     */
    public List<String> enqueue(final int value) {
        ArrayList<String> lines = new ArrayList<String>();

        // Set the overflow flag and generate a processor fault if the queue
        // is full. The processor fault is caused by indexing past the end of
        // the storage array.
        final IfThen overflowCheck = new IfThen();
        overflowCheck.addCase( //
                TagNames.SIZE + " = " + capacity, // Overflow when size = capacity.
                TagNames.OVERFLOW + " := 1;", // Set overflow output.
                TagNames.STORAGE + "[" + capacity + "] := 0;" // Generate processor fault.
        );
        lines.addAll(overflowCheck.getLines());

        // Store the value at the head of the array.
        lines.add(String.format("%1$s[%2$d] := %3$d;", TagNames.STORAGE, TagNames.HEAD, value));

        // Increment the head pointer.
        incrementIndex(TagNames.HEAD, lines);

        // Increment the current size.
        lines.add(String.format("%s := %s + 1;", TagNames.SIZE));

        // Update the high watermark output.
        final IfThen watermark = new IfThen();
        watermark.addCase( //
                TagNames.SIZE + " > " + TagNames.WATERMARK, //
                TagNames.WATERMARK + " := " + TagNames.SIZE + ";" //
        );
        lines.addAll(watermark.getLines());

        return unmodifiableList(lines);
    }

    /**
     * Generates a set of structured text commands in the logic routine to remove a
     * value from the queue
     *
     * @param dest Tag name to receive the value.
     * @return Structured text lines implementing the dequeue operation.
     */
    public List<String> dequeue(final String dest) {
        final IfThen st = new IfThen();

        // Add a case to handle removing an event if the queue has one or more events.
        final List<String> removeEvent = new ArrayList<>();
        removeEvent.add(dest + " := " + TagNames.STORAGE + "[" + TagNames.TAIL + "];"); // Remove value from tail index.
        incrementIndex(TagNames.TAIL, removeEvent); // Increment the tail pointer.
        removeEvent.add(TagNames.TAIL + " := " + TagNames.TAIL + " - 1;"); // Reduce the current size.
        st.addCase(TagNames.SIZE + " > 0", removeEvent);

        // Clear the destination tag if the queue is empty.
        st.addElse(dest + " := 0;");

        return unmodifiableList(st.getLines());
    }

    /**
     * Generates a set of structured text lines to increment an array index.
     *
     * @param tag   Tag name to increment.
     * @param lines List of structured text lines to append onto.
     */
    private void incrementIndex(final String tag, final List<String> lines) {
        lines.add(String.format("%s := %s + 1;", tag));

        // Wrap back to the beginning if the index has reached the end of the array.
        final IfThen wrap = new IfThen();
        wrap.addCase(tag + " = " + capacity, tag + " := 0;");
        lines.addAll(wrap.getLines());
    }

    /**
     * Appends a list of structured text lines to an AOI routine.
     *
     * @param routine Name of the target routine.
     * @param lines   List of lines to add.
     */
    private void addLines(final String routine, final List<String> lines) {
        for (final String s : lines) {
            aoi.addStructuredTextLine(routine, s);
        }
    }
}
