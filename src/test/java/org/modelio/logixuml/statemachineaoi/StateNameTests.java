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
