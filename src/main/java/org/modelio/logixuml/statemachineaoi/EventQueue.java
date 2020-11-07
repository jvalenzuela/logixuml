package org.modelio.logixuml.statemachineaoi;

import java.util.ArrayList;
import java.util.List;

import org.modelio.logixuml.l5x.AddOnInstruction;

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
     */
    public EventQueue(final AddOnInstruction aoi, final int capacity) {
        this.aoi = aoi;
        this.capacity = capacity;
        createTags();

        // Empty the queue in the prescan and enable-in false routines.
        clear("Prescan");
        clear("EnableInFalse");
    }

    /**
     * Defines the parameters and local tags used by the queue.
     */
    private void createTags() {
        aoi.addParameter(TagNames.OVERFLOW, "Output", "BOOL", true, "True if an overflow has occurred.");
        aoi.addParameter(TagNames.WATERMARK, "Output", "DINT", true, "Highest number of events stored.");

        aoi.addLocalTag(TagNames.STORAGE, "DINT", capacity);
        aoi.addLocalTag(TagNames.HEAD, "DINT");
        aoi.addLocalTag(TagNames.TAIL, "DINT");
        aoi.addLocalTag(TagNames.SIZE, "DINT");
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
     */
    public void enqueue(final int value) {
        ArrayList<String> lines = new ArrayList<String>();

        // Set the overflow flag and generate a processor fault if the queue
        // is full. The processor fault is caused by indexing past the end of
        // the storage array.
        lines.add(String.format("IF %1$s = %2$d THEN", TagNames.SIZE, capacity));
        lines.add(String.format("%s := 1;", TagNames.OVERFLOW));
        lines.add(String.format("%1$s[%2$d] := 0;", TagNames.STORAGE, capacity));
        lines.add("END_IF;");

        // Store the value at the head of the array.
        lines.add(String.format("%1$s[%2$d] := %3$d;", TagNames.STORAGE, TagNames.HEAD, value));

        // Increment the head pointer.
        incrementIndex(TagNames.HEAD, lines);

        // Increment the current size.
        lines.add(String.format("%s := %s + 1;", TagNames.SIZE));

        // Update the high watermark output.
        lines.add(String.format("IF %1$s > %2$s THEN", TagNames.SIZE, TagNames.WATERMARK));
        lines.add(String.format("%1$s := %2$s;", TagNames.WATERMARK, TagNames.SIZE));
        lines.add("END_IF;");

        addLines("Logic", lines);
    }

    /**
     * Generates a set of structured text commands in the logic routine to remove a
     * value from the queue
     *
     * @param dest Tag name to receive the value.
     */
    public void dequeue(final String dest) {
        ArrayList<String> lines = new ArrayList<String>();

        // Check to see if any items are in the queue.
        lines.add(String.format("IF %s > 0 THEN", TagNames.SIZE));

        // Remove the next value from the tail index.
        lines.add(String.format("%1$s := %2$s[%3$d];", dest, TagNames.STORAGE, TagNames.TAIL));

        // Increment the tail pointer.
        incrementIndex(TagNames.TAIL, lines);

        // Reduce the current size.
        lines.add(String.format("%s := %s - 1;", TagNames.TAIL));

        // Clear the destination tag if the queue is empty.
        lines.add("ELSE");
        lines.add(String.format("%s := 0;", dest));

        lines.add("END_IF;");

        addLines("Logic", lines);
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
        lines.add(String.format("IF %1$s = %2$d THEN", tag, capacity));
        lines.add(String.format("%s := 0;", tag));
        lines.add("END_IF;");
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
