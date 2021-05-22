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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.vcore.model.CompositionGetter;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * Unit tests for the StateMap.build() method.
 */
class StateMapTests {
    // Top-level mock model elements.
    private StateMachine stateMachine;
    private Region top;

    @BeforeEach
    void setUp() {
        MockModule.init();
        stateMachine = MockModel.stateMachine("sm", null);
        top = MockModel.region(stateMachine);
    }

    /**
     * Ensure valid states are included in the map.
     */
    @Test
    void validState() {
        final State superstate = MockModel.state("superstate", top);
        final Region region = MockModel.region(superstate);
        final State state = MockModel.state("state", region);
        try {
            final Map<MRef, AoiState> map = buildMap();
            assertEquals(2, map.size());
            assertTrue(map.containsKey(new MRef(superstate)));
            assertTrue(map.containsKey(new MRef(state)));
        } catch (ExportException e) {
            fail();
        }
    }

    /**
     * Ensure states with duplicate names are rejected.
     */
    @ParameterizedTest
    @ValueSource(strings = { "state", "STATE", "   State\r\n\t" })
    void duplicateStateName(final String dupName) {
        final State state = MockModel.state("state", top);
        final State dup = MockModel.state(dupName, top);
        final ExportException e = assertThrows(ExportException.class, () -> buildMap());
        assertTrue((e.getModelObject() == state) || (e.getModelObject() == dup));
    }

    /**
     * Ensure the result map is read-only.
     */
    @Test
    void readOnly() {
        try {
            Map<MRef, AoiState> map = buildMap();
            final State state = MockModel.state("state", top);
            final MRef ref = new MRef(state);
            assertThrows(UnsupportedOperationException.class, () -> map.put(ref, new AoiState(state)));
        } catch (ExportException e) {
            fail();
        }
    }

    /**
     * Builds a state map from the mock state machine.
     *
     * @return The built state map.
     * @throws ExportException
     */
    private Map<MRef, AoiState> buildMap() throws ExportException {
        return StateMap.build(CompositionGetter.getAllChildren(Arrays.asList(stateMachine)));
    }
}
