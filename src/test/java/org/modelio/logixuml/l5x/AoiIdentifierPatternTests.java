package org.modelio.logixuml.l5x;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for the AOI identifier validation regex.
 */
class AoiIdentifierPatternTests {
    /**
     * Confirm valid identifiers are accepted.
     */
    @ParameterizedTest
    @ValueSource(strings = { //
            "_foo", // Begin with an underscore.
            "sp_am", // Underscore in the middle.
            "foo", // Begin with a lower-case letter.
            "Foo", // Begin with an upper-case letter.
            "fOo", // Upper-case letter in the middle.
            "foO", // End with an upper-case letter.
            "b0r", // Digit in the middle.
            "eggs0", // Ending with a digit.
            "x", // Single character.
            "_123456789012345678901234567890123456789" // Maximum length.
    })
    void validIdentifier(String id) {
        try {
            new AddOnInstruction(id);
        } catch (AddOnInstructionException e) {
            fail(String.format("Valid id not accepted: %s", id));
        }
    }

    /**
     * Confirm invalid identifiers are rejected.
     */
    @ParameterizedTest
    @ValueSource(strings = { //
            "0foo", // Begin with a digit.
            " bar", // Begin with whitespace.
            "foo\tbar", // Whitespace in the middle.
            "spam\n", // Whitespace at the end.
            "__foo", // Start with consecutive underscores.
            "foo__bar", // Consecutive underscores in the middle.
            "foobar__", // End with consecutive underscores.
            "foobar_", // End with a single underscore.
            "_", // Single underscore.
            "", // Empty string.
            "_123456789012345678901234567890123456789X" // Exceeding maximum length.
    })
    void invalidIdentifier(String id) {
        assertThrows(AddOnInstructionException.class, () -> new AddOnInstruction(id),
                String.format("Invalid id not rejected: %s", id));
    }

    /**
     * Confirm an invalid parameter name is rejected.
     */
    @Test
    void invalidParameter() throws AddOnInstructionException {
        AddOnInstruction aoi = new AddOnInstruction("aoi");
        assertThrows(AddOnInstructionException.class, () -> aoi.addParameter("_", "Input", "BOOL", true));
    }

    /**
     * Confirm an invalid local tag name is rejected.
     */
    @Test
    void invalidLocalTag() throws AddOnInstructionException {
        AddOnInstruction aoi = new AddOnInstruction("aoi");
        assertThrows(AddOnInstructionException.class, () -> aoi.addLocalTag("_", "DINT"));
    }
}
