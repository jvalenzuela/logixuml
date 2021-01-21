package org.modelio.logixuml.statemachineaoi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;

/**
 * Unit tests for values supplied in the stereotype property table for event
 * queue size.
 */
class StereotypePropertiesEventQueueSizeTests {
    /**
     * Confirm blank values are rejected.
     */
    @ParameterizedTest
    @ValueSource(strings = { "", " \r\t\n" })
    void empty(final String value) {
        StereotypeProperties prop = mockProperties(value);
        assertThrows(ExportException.class, prop::getEventQueueSize, "Failure value: \"" + value + "\"");
    }

    /**
     * Confirm a null value is rejected.
     */
    @Test
    void nullValue() {
        StereotypeProperties prop = mockProperties(null);
        assertThrows(ExportException.class, prop::getEventQueueSize);
    }

    /**
     * Confirm strings that do not represent integers are rejected.
     */
    @ParameterizedTest
    @ValueSource(strings = { "foo", "1.0" })
    void nonInteger(final String value) {
        StereotypeProperties prop = mockProperties(value);
        assertThrows(ExportException.class, prop::getEventQueueSize, "Failure value: " + value);
    }

    /**
     * Confirm leading and trailing whitespace are ignored.
     */
    @Test
    void surroundingWhitespace() {
        final StereotypeProperties prop = mockProperties("  1 \t\r\n");
        try {
            assertEquals(1, prop.getEventQueueSize());
        } catch (ExportException e) {
            fail();
        }
    }

    /**
     * Confirm values at the limits of the allowable range are accepted.
     */
    @ParameterizedTest
    @ValueSource(strings = { "1", "8" })
    void minMax(final String value) {
        final StereotypeProperties prop = mockProperties(value);
        try {
            assertEquals(Integer.parseInt(value), prop.getEventQueueSize());
        } catch (ExportException e) {
            fail("Failure value: " + value);
        }
    }

    /**
     * Confirm values outside the allowable range are rejected.
     */
    @ParameterizedTest
    @ValueSource(strings = { "0", "9" })
    void outsideLimit(final String value) {
        final StereotypeProperties prop = mockProperties(value);
        assertThrows(ExportException.class, prop::getEventQueueSize, "Failure value: " + value);
    }

    /**
     * Creates a mock stereotype property object.
     *
     * @param value Event queue size property value.
     * @return Mock object containing the given event queue size value.
     */
    private StereotypeProperties mockProperties(final String value) {
        MockModule.init();
        final StateMachine sm = MockModel.stateMachine("sm", null);
        MockModel.addProperty(sm, "eventQueueSize", value);
        return new StereotypeProperties(sm);
    }
}
