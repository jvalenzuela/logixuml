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

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.vcore.smkernel.mapi.MObject;

/**
 * The class handles the process of extracting events from a state machine, and
 * compiling them into a map keyed by event name.
 */
class EventMap {
    /**
     * Builds a mapping of event names to AoiEvent objects from the set of state
     * machine UML model objects.
     *
     * @param elements Set of all UML model elements contained within the source
     *                 state machine.
     * @return A map containing AoiEvent objects for each event in the UML model,
     *         keyed by event name.
     */
    static Map<String, AoiEvent> build(final Set<MObject> elements) {
        final List<String> names = getEventNames(elements);
        return buildMap(names);
    }

    /**
     * Extracts the triggering event names from all transitions in the UML model.
     *
     * @param elements Set of all UML model elements contained within the source
     *                 state machine.
     * @return List of event names.
     */
    static private List<String> getEventNames(final Set<MObject> elements) {
        final List<String> names = elements.stream() //
                .filter(e -> e.getMClass().getQualifiedName().equals(Transition.MQNAME)) // Remove non-transitions.
                .map(e -> (Transition) e) // Cast to Transition objects.
                .filter(t -> t.getReceivedEvents() != null) // Ignore transitions with no event.
                .map(t -> t.getReceivedEvents().trim()) // Extract event names.
                .filter(name -> !name.isEmpty()) // Ignore transitions with blank event names.
                .collect(Collectors.toList());

        return unmodifiableList(names);
    }

    /**
     * Builds the mapping of AoiEvent objects keyed by event name.
     *
     * @param names List of event names from the UML model.
     * @return Event mapping.
     */
    static private Map<String, AoiEvent> buildMap(final List<String> names) {
        // Events are keyed by name in a case-insensitive manner because the event name
        // is used to generate PLC tag names, which are also case-insensitive. E.g. the
        // names event1 and EVENT1 would be the same tag. The comparator supplied to the
        // TreeMap ensures events used in multiple transitions all refer to the same
        // event object, even if spelled with differing case.
        final Map<String, AoiEvent> events = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        // Object to allocate unique, numeric identifiers for each event.
        final IntSupplier idGenerator = new IntegerIdentifier();

        // Populate the map with AoiEvent objects, ignoring duplicate event names.
        for (final String name : names) {
            if (!events.containsKey(name)) {
                final int id = idGenerator.getAsInt();
                events.put(name, new AoiEvent(name, id));
            }
        }

        return unmodifiableMap(events);
    }
}
