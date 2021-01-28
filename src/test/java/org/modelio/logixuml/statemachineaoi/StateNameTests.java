package org.modelio.logixuml.statemachineaoi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;

/**
 * Unit tests for the name assigned to a state.
 */
class StateNameTests {
    @BeforeEach
    void setUp() {
        MockModule.init();
    }

    /**
     * Confirm empty names are rejected.
     */
    @ParameterizedTest
    @ValueSource(strings = { "", " \t\r\n" })
    void emptyName(final String name) {
        final State state = MockModel.state(name, null);
        assertThrows(ExportException.class, () -> new AoiState(state));
    }

    /**
     * Confirm whitespace surrounding the name is removed.
     */
    @Test
    void nameWhitespace() {
        final State state = MockModel.state("  foo  ", null);
        AoiState aoiState = null;
        try {
            aoiState = new AoiState(state);
        } catch (ExportException e) {
            fail();
        }
        assertEquals("foo", aoiState.getName());
    }
}
