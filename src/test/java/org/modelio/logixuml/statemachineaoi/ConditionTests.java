package org.modelio.logixuml.statemachineaoi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * Unit tests for Condition objects.
 */
@ExtendWith(MockitoExtension.class)
public class ConditionTests {
    /**
     * Dummy state reference objects.
     */
    private final Set<MRef> refs = new HashSet<MRef>();

    /**
     * Mock modeling session injected into the condition object for simulating MRef
     * lookup.
     */
    private IModelingSession session = mock(IModelingSession.class, Answers.RETURNS_DEEP_STUBS);

    @InjectMocks
    private Condition condition;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        refs.add(new MRef(State.MQNAME, "uuid1"));
        refs.add(new MRef(State.MQNAME, "uuid2"));
    }

    /**
     * Confirm valid state references are added to the entry action set.
     */
    @Test
    void validEntryActions() {
        setMockMClassName(State.MQNAME);

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
        setMockMClassName("not a state");

        for (MRef ref : refs) {
            assertThrows(AssertionError.class, () -> condition.addEntryAction(ref));
            break;
        }
    }

    /**
     * Confirm valid state references are added to the do action set.
     */
    @Test
    void validDoActions() {
        setMockMClassName(State.MQNAME);

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
        setMockMClassName("not a state");

        for (MRef ref : refs) {
            assertThrows(AssertionError.class, () -> condition.addDoAction(ref));
            break;
        }
    }

    /**
     * Confirm valid state references are added to the exit action set.
     */
    @Test
    void validExitActions() {
        setMockMClassName(State.MQNAME);

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
        setMockMClassName("not a state");

        for (MRef ref : refs) {
            assertThrows(AssertionError.class, () -> condition.addExitAction(ref));
            break;
        }
    }

    /**
     * Configures the qualified name returned by the mock session.
     *
     * @param name String to be returned by getQualifiedName().
     */
    private void setMockMClassName(final String name) {
        when(session.findByRef(any(MRef.class)).getMClass().getQualifiedName()).thenReturn(name);
    }
}
