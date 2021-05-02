package org.modelio.logixuml.statemachineaoi;

import static java.util.Collections.unmodifiableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelio.logixuml.structuredtext.CaseOf;

/**
 * This object handles the sequence of condition identifiers during the course
 * of a transition by storing the condition ID that follows each condition in a
 * transition, and generating structured text statements to advance the
 * condition variable each scan.
 */
class ConditionIdSequence {
    /**
     * This mapping stores the order of conditions during transitions. Keys are the
     * current condition, which are always transition conditions. Values are the
     * condition that follows, which may be the next condition in the transition or
     * a stable condition of the target state.
     */
    private final Map<Integer, Integer> nextId = new HashMap<>();

    /**
     * Records the identifier of the next condition in a transition.
     *
     * @param current Identifier of the current condition.
     * @param next    Identifier of the next condition.
     */
    void storeNext(final int current, final int next) {
        // An existing relationship should never be overwritten as the IDs for all
        // transition conditions must be unique.
        if (nextId.containsKey(current)) {
            throw new AssertionError(current);
        }

        // If the next ID already exists as a value it must be a stable condition at
        // the completion of a transition, so it must not also be a key.
        if (nextId.containsValue(next) && nextId.containsKey(next)) {
            throw new AssertionError(next);
        }

        nextId.put(current, next);
    }

    /**
     * Generates structured text statements to increment the condition variable
     * through transitional steps.
     *
     * @param tagName Name of the condition variable tag to be evaluated and
     *                updated.
     * @return Structured text statements.
     */
    List<String> advance(final String tagName) {
        // Implemented as a CASE_OF block testing the current condition variable for
        // values representing transitional conditions.
        final CaseOf st = new CaseOf(tagName);

        // Add a case for each transitional condition value to advance the condition
        // variable to the next condition in the transition.
        for (final int currentId : nextId.keySet()) {
            st.addCase(currentId, tagName + " := " + nextId.get(currentId) + ";");
        }

        return unmodifiableList(st.getLines());
    }
}
