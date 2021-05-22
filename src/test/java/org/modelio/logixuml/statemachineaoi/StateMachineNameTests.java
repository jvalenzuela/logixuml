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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.modelio.metamodel.uml.behavior.stateMachineModel.InitialPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.vcore.smkernel.mapi.MObject;

/**
 * Tests for invalid state machine names.
 * <p>
 * These tests are limited to invalid patterns, such as a blank name, that would
 * otherwise be accepted once the AOI prefix is attached. Additional testing of
 * invalid patterns is unnecessary because the state machine name is ultimately
 * subject to AOI identifier validation, which is has its own set of unit tests.
 */
class StateMachineNameTests {
    /**
     * Creates a mock state machine with the bare minimum components.
     *
     * @param name Name to assign to the mock state machine.
     * @return The mock state machine model.
     */
    private MObject mockStateMachine(final String name) {
        MockModule.init();
        final StateMachine sm = MockModel.stateMachine(name, null);
        final Region top = MockModel.region(sm);
        final State state = MockModel.state("state", top);
        final InitialPseudoState initial = MockModel.initialPseudoState(top);
        MockModel.transition(initial, state, "");
        return sm;
    }

    /**
     * Confirm empty or blank names are rejected.
     */
    @ParameterizedTest
    @ValueSource(strings = { "", "  " }) // Null is not included because getName() never returns null.
    void emptyName(final String name) {
        final MObject sm = mockStateMachine(name);
        assertThrows(ExportException.class, () -> new StateMachineAoi(sm));
    }

    /**
     * Confirm surrounding whitespace is removed from a name.
     */
    @Test
    void surroundingWhitespace() {
        final MObject sm = mockStateMachine("  foo  ");

        // Validating the name has been trimmed is done by ensuring a state machine with
        // surrounding whitespace does not raise an exception. AOI identifier validation
        // rejects all whitespace, and would throw an exception if surrounding
        // whitespace was not removed.
        try {
            new StateMachineAoi(sm);
        } catch (ExportException e) {
            fail(e);
        }
    }
}
