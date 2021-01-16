package org.modelio.logixuml.statemachineaoi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.vcore.smkernel.mapi.MObject;

/**
 * Unit tests for building a map of AoiEvent objects from a set of state machine
 * model elements.
 */
class EventMapTests {
    /**
     * Set of mock source model elements supplied to EventMap.
     */
    private Set<MObject> modelElements;

    /**
     * Populates the model element set with some boiler-plate elements.
     */
    @BeforeEach
    void initModel() {
        modelElements = new HashSet<>();
        final StateMachine stateMachine = MockModel.stateMachine("sm", null);
        modelElements.add(stateMachine);
        final Region region = MockModel.region(stateMachine);
        modelElements.add(region);
        modelElements.add(MockModel.state("state", region));
    }

    /**
     * Adds a transition to the source model element set.
     *
     * @param eventName Name of the triggering event.
     */
    private void addTransition(final String eventName) {
        final State source = MockModel.state("source", null);
        modelElements.add(source);
        final State target = MockModel.state("target", null);
        modelElements.add(target);
        modelElements.add(MockModel.transition(source, target, eventName));
    }

    /**
     * Ensure transitions with empty event names are not added to the map.
     */
    @ParameterizedTest
    @ValueSource(strings = { "", "   " })
    void ignoreEmptyEvents(final String eventName) {
        addTransition(eventName);
        final Map<String, AoiEvent> map = EventMap.build(modelElements);
        assertEquals(0, map.size());
    }

    /**
     * Ensure transitions with no event are not added to the map.
     */
    @Test
    void ignoreNullEvents() {
        addTransition(null);
        final Map<String, AoiEvent> map = EventMap.build(modelElements);
        assertEquals(0, map.size());
    }

    /**
     * Ensure leading and trailing whitespace is removed from event names.
     */
    @Test
    void trimWhitespace() {
        addTransition("  event  ");
        final Map<String, AoiEvent> map = EventMap.build(modelElements);
        assertTrue(map.containsKey("event"));
    }

    /**
     * Ensure multiple events with the exact same name yield a single map entry.
     */
    @Test
    void duplicateName() {
        addTransition("event");
        addTransition("event");
        final Map<String, AoiEvent> map = EventMap.build(modelElements);
        assertEquals(1, map.size());
    }

    /**
     * Ensure event names differing only in case yield a single map entry.
     */
    @Test
    void caseDifference() {
        addTransition("EVENT");
        addTransition("event");
        final Map<String, AoiEvent> map = EventMap.build(modelElements);
        assertEquals(1, map.size());
    }

    /**
     * Ensure events are assigned positive identifiers.
     */
    @Test
    void positiveId() {
        addTransition("e1");
        addTransition("e2");
        final Map<String, AoiEvent> map = EventMap.build(modelElements);
        for (final AoiEvent e : map.values()) {
            assertTrue(e.getId() > 0);
        }
    }

    /**
     * Ensure events are assigned unique identifiers.
     */
    @Test
    void uniqueId() {
        addTransition("e1");
        addTransition("e2");
        final Map<String, AoiEvent> map = EventMap.build(modelElements);

        // Construct a set of assigned IDs to determine the number of unique values.
        final Set<Integer> ids = map.values().stream().map(e -> e.getId()).collect(Collectors.toSet());
        assertEquals(2, ids.size());
    }

    /**
     * Ensure the generated map cannot be modified.
     */
    @Test
    void immutable() {
        final Map<String, AoiEvent> map = EventMap.build(modelElements);
        assertThrows(UnsupportedOperationException.class, () -> map.put("event", new AoiEvent("event", 1)));
    }
}
