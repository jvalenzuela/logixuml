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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;

/**
 * Unit tests for values supplied in the stereotype property table for
 * transition scan mode.
 */
class StereotypePropertiesTransitionScanModeTests {
    /**
     * Confirm blank values are rejected.
     */
    @ParameterizedTest
    @ValueSource(strings = { "", " \r\t\n" })
    void empty(final String value) {
        final StereotypeProperties prop = mockProperties(value);
        assertThrows(ExportException.class, prop::getTransitionScanMode, "Failure value: \"" + value + "\"");
    }

    /**
     * Confirm a null value is rejected.
     */
    @Test
    void nullValue() {
        final StereotypeProperties prop = mockProperties(null);
        assertThrows(ExportException.class, prop::getTransitionScanMode);
    }

    /**
     * Confirm leading and trailing whitespace are ignored.
     */
    @Test
    void surroundingWhitespace() {
        final StereotypeProperties prop = mockProperties("  SINGLE \t\r\n");
        try {
            assertEquals(TransitionScanMode.SINGLE, prop.getTransitionScanMode());
        } catch (ExportException e) {
            fail();
        }
    }

    /**
     * Confirm values are not case-sensitive.
     */
    @ParameterizedTest
    @ValueSource(strings = { "single", "SINGLE", "sInGlE" })
    void caseInsensitive(final String value) {
        final StereotypeProperties prop = mockProperties(value);
        try {
            assertEquals(TransitionScanMode.SINGLE, prop.getTransitionScanMode());
        } catch (ExportException e) {
            fail("Failure value: " + value);
        }
    }

    /**
     * Confirm strings that are not enumeration members are rejected.
     */
    @Test
    void undefinedValue() {
        final StereotypeProperties prop = mockProperties("foo");
        assertThrows(ExportException.class, prop::getTransitionScanMode);
    }

    /**
     * Confirm valid enumeration names are accepted.
     */
    @ParameterizedTest
    @EnumSource(TransitionScanMode.class)
    void validValue(final TransitionScanMode value) {
        final StereotypeProperties prop = mockProperties(value.toString());
        try {
            assertEquals(value, prop.getTransitionScanMode());
        } catch (ExportException e) {
            fail("Failure value: " + value);
        }
    }

    /**
     * Creates a mock stereotype property object.
     *
     * @param value Transition scan mode property value.
     * @return Mock object containing the given transition scan mode value.
     */
    private StereotypeProperties mockProperties(final String value) {
        MockModule.init();
        final StateMachine sm = MockModel.stateMachine("sm", null);
        MockModel.addProperty(sm, "transitionScanMode", value);
        return new StereotypeProperties(sm);
    }
}
