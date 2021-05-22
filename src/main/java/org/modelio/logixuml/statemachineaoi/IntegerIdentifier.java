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

package org.modelio.logixuml.statemachineaoi;

import java.util.function.IntSupplier;

/**
 * This object is used to allocate unique, non-zero, integer values for
 * identifying AOI resources.
 */
class IntegerIdentifier implements IntSupplier {
    /**
     * Identifier returned from the previous call to getAsInt();
     */
    private int value;

    @Override
    public int getAsInt() {
        // Sanity check to ensure the result will fit into a DINT.
        if (value >= Integer.MAX_VALUE) {
            throw new AssertionError(value);
        }

        value++; // Increment first to ensure non-zero values.

        if (value <= 0) {
            throw new AssertionError(value);
        }

        return value;
    }
}
