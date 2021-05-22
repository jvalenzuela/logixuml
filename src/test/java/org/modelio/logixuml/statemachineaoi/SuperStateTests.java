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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * Unit tests for SuperState static methods.
 */
class SuperStateTests {

    /*
     * Mock model objects.
     */
    private Package pkg; // Top level package.
    private StateMachine stateMachine; // Enclosing state machine.
    private Region top; // State machine's top-level region.
    private State superState; // Top-level super-state.
    private Region region; // Intermediate region.
    private State subState; // Mid-level super-state.
    private Region subRegion; // Intermediate region.
    private State target; // Bottom-level state to query.

    @BeforeEach
    private void createMockModel() {
        pkg = MockModel.pkg();
        stateMachine = MockModel.stateMachine("sm", pkg);
        top = MockModel.region(stateMachine);
        superState = MockModel.state("", top);
        region = MockModel.region(superState);
        subState = MockModel.state("", region);
        subRegion = MockModel.region(subState);
        target = MockModel.state("", subRegion);
    }

    /**
     * Confirms the correct list of super-state objects.
     */
    @Test
    void stateList() {
        final List<State> result = SuperState.getSuperStates(target);
        final List<State> expected = new ArrayList<State>();
        expected.add(subState);
        expected.add(superState);
        assertEquals(expected, result);
    }

    /**
     * Confirms the correct list of references to super-state objects.
     */
    @Test
    void referenceList() {
        final List<MRef> result = SuperState.getSuperStateRefs(target);
        final List<MRef> expected = new ArrayList<MRef>();
        expected.add(new MRef(subState));
        expected.add(new MRef(superState));
        assertEquals(expected, result);
    }
}
