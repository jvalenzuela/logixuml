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

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.vcore.smkernel.mapi.MObject;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * This class handles constructing a map of AoiState objects from all the states
 * within the source state machine.
 */
class StateMap {
    /**
     * Builds a mapping of AoiState objects keyed by MRefs.
     *
     * @param elements Child model elements of the source state machine.
     * @return Map of AoiState objects.
     * @throws ExportException If a duplicate state name was found or a state was
     *                         otherwise invalid.
     */
    static Map<MRef, AoiState> build(final Set<MObject> elements) throws ExportException {
        final Set<String> stateNames = new HashSet<>();
        final Map<MRef, AoiState> map = new HashMap<>();

        for (final State stateElement : getStateElements(elements)) {
            final AoiState aoiState = new AoiState(stateElement);

            // Verify the name is unique among all other states. Names are treated in a
            // case-insensitive manner because state names are used to create Logix tags,
            // which are not case-sensitive.
            final String normalizedName = aoiState.getName().toLowerCase();
            if (stateNames.contains(normalizedName)) {
                throw new ExportException("Duplicate state name.", stateElement);
            }
            stateNames.add(normalizedName);

            map.put(new MRef(stateElement), aoiState);
        }

        return unmodifiableMap(map);
    }

    /**
     * Extracts states from all the model elements of a state machine.
     *
     * @param elements Child model elements of the source state machine.
     * @return The set of state model elements.
     */
    static private Set<State> getStateElements(final Set<MObject> elements) {
        final Set<State> set = elements.stream() //
                .filter(e -> e.getMClass().getQualifiedName().equals(State.MQNAME)) // Select only state objects
                .map(e -> (State) e) // Cast to State object.
                .collect(Collectors.toSet());
        return unmodifiableSet(set);
    }
}
