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
        assert value < Integer.MAX_VALUE; // Sanity check to ensure the result will fit into a DINT.
        value++; // Increment first to ensure non-zero values.
        assert value > 0;
        return value;
    }
}
