package org.modelio.logixuml.statemachineaoi;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;

/**
 * Unit tests verifying response to the number of regions within a state.
 */
class StateRegionsTests {
    /**
     * Confirm a state containing zero or one region is accepted.
     */
    @ParameterizedTest
    @ValueSource(ints = { 0, 1 })
    void validRegions(final int qty) throws ExportException {
        final State state = mockState(qty);
        new AoiState(state);
    }

    /**
     * Confirm a state with two or more regions is rejected.
     */
    @ParameterizedTest
    @ValueSource(ints = { 2, 4 })
    void invalidRegions(final int qty) {
        final State state = mockState(qty);
        assertThrows(ExportException.class, () -> new AoiState(state));
    }

    /**
     * Creates a mock state containing a given number of regions.
     *
     * @param numRegion Number of child regions.
     * @return The mock state.
     */
    private State mockState(final int numRegion) {
        MockModule.init();

        final State state = MockModel.state("state", null);
        for (int i = 0; i < numRegion; i++) {
            MockModel.region(state);
        }
        return state;
    }
}
