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
