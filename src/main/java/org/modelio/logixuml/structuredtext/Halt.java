package org.modelio.logixuml.structuredtext;

import static java.util.Collections.unmodifiableList;

import java.util.Arrays;
import java.util.List;

import org.modelio.logixuml.l5x.AddOnInstruction;
import org.modelio.logixuml.l5x.DataType;
import org.modelio.logixuml.statemachineaoi.ExportException;

/**
 * Implementation of a method to programmatically halt the PLC processor by
 * causing a major fault in response to an invalid array index.
 */
public class Halt {
    /**
     * Local tag names.
     */
    private class TagNames {
        private final static String ARRAY = "pf"; // DINT array.
        private final static String INDEX = "pfi"; // Array index.
    }

    /**
     * Creates the required local tags.
     *
     * @param aoi Target add-on instruction.
     */
    public static void createTags(final AddOnInstruction aoi) {
        try {
            aoi.addLocalTag(TagNames.ARRAY, DataType.DINT, 1);
            aoi.addLocalTag(TagNames.INDEX, DataType.DINT);
        } catch (ExportException e) {
            assert false; // These tags should never be invalid.
        }
    }

    /**
     * Generates the structured text lines that will halt processor execution.
     *
     * @return List of structured text lines.
     */
    public static List<String> getLines() {
        final String lines[] = { //
                TagNames.INDEX + " := -1;", //
                TagNames.ARRAY + "[" + TagNames.INDEX + "] := 0;" //
        };
        return unmodifiableList(Arrays.asList(lines));
    }
}
