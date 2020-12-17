package org.modelio.logixuml.statemachineaoi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * Unit tests for Condition objects.
 */
public class ConditionTests {
    /**
     * Dummy state reference objects.
     */
    private final Set<MRef> refs = new HashSet<MRef>();

    /**
     * Reference to a model object type that can not be added to a condition.
     */
    private MRef nonState;

    private Condition condition;

    @BeforeEach
    public void setUp() {
        MockModule.init();
        condition = new Condition();

        final State state1 = MockModel.state("", null);
        final State state2 = MockModel.state("", null);
        refs.add(new MRef(state1));
        refs.add(new MRef(state2));

        nonState = new MRef(MockModel.region(state1));
    }

    /**
     * Confirm valid state references are added to the entry action set.
     */
    @Test
    void validEntryActions() {
        for (MRef ref : refs) {
            condition.addEntryAction(ref);
        }

        assertEquals(refs, condition.getEntryActions());
        assertEquals(Collections.EMPTY_SET, condition.getDoActions());
        assertEquals(Collections.EMPTY_SET, condition.getExitActions());
    }

    /**
     * Confirm adding an entry action for an object that is not a state generates an
     * assertion.
     */
    @Test
    void invalidEntryAction() {
        assertThrows(AssertionError.class, () -> condition.addEntryAction(nonState));
    }

    /**
     * Confirm valid state references are added to the do action set.
     */
    @Test
    void validDoActions() {
        for (MRef ref : refs) {
            condition.addDoAction(ref);
        }

        assertEquals(refs, condition.getDoActions());
        assertEquals(Collections.EMPTY_SET, condition.getEntryActions());
        assertEquals(Collections.EMPTY_SET, condition.getExitActions());
    }

    /**
     * Confirm adding a do action for an object that is not a state generates an
     * assertion.
     */
    @Test
    void invalidDoAction() {
        assertThrows(AssertionError.class, () -> condition.addDoAction(nonState));
    }

    /**
     * Confirm valid state references are added to the exit action set.
     */
    @Test
    void validExitActions() {
        for (MRef ref : refs) {
            condition.addExitAction(ref);
        }

        assertEquals(refs, condition.getExitActions());
        assertEquals(Collections.EMPTY_SET, condition.getEntryActions());
        assertEquals(Collections.EMPTY_SET, condition.getDoActions());
    }

    /**
     * Confirm adding a exit action for an object that is not a state generates an
     * assertion.
     */
    @Test
    void invalidExitAction() {
        assertThrows(AssertionError.class, () -> condition.addExitAction(nonState));
    }
}
