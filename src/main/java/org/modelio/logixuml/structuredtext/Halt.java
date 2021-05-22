/*
 * Copyright 2021 Jason Valenzuela
 *
 * This file is part of LogixUML.
 *
 * LogixUML is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LogixUML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LogixUML.  If not, see <https://www.gnu.org/licenses/>.
 */

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
            throw new AssertionError(); // These tags should never be invalid.
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
