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

import org.junit.jupiter.api.Test;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;

/**
 * Unit tests for a state machine's top-level initial transition. These tests
 * are limited to specific cases unique to the initial transition of an entire
 * state machine not exercised in {@link InitialTransitionTests}.
 *
 * @see InitialTransitionTests
 */
class StateMachineInitialTransitionTests {
    /**
     * Confirm a state machine with no initial transition is rejected.
     * <p>
     * Similar to {@link InitialTransitionTests#stateMachineNone()}, but this
     * verifies the ultimate exception is thrown as opposed to the null return
     * value.
     */
    @Test
    void noInitial() {
        MockModule.init();
        final StateMachine sm = MockModel.stateMachine("sm", null);
        final Region top = MockModel.region(sm);
        MockModel.state("state", top);
        assertThrows(ExportException.class, () -> new StateMachineAoi(sm));
    }
}
