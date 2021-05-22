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
import org.modelio.metamodel.uml.behavior.stateMachineModel.InitialPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateVertex;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.vcore.smkernel.mapi.MRef;

/**
 * This object defines transition condition unit tests that are run in each scan
 * mode: single, dual, and sequential. The transition model for each test is the
 * same, and is mocked by this object's test methods; concrete subclasses
 * generate the results as required by each scan mode.
 */
abstract class TransitionConditionTests {
    /**
     * The transition condition generated from the mock transition. This is
     * constructed by a subclass according scan mode.
     */
    protected TransitionConditions result;

    /**
     * Origin point of the test transition.
     */
    protected StateVertex source;

    /**
     * Expected ultimate target of the test transition.
     */
    protected State target;

    /**
     * List of conditions that define the pass criteria for each test.
     */
    protected List<Condition> expectedConditions;

    /**
     * Top-level state machine model object used for all tests.
     */
    protected StateMachine stateMachine;

    /**
     * Top-level state machine region used for all tests.
     */
    protected Region top;

    /**
     * The transition model element that will be passed to the TransitionCondition
     * constructor.
     */
    protected Transition transition;

    @BeforeEach
    void setup() {
        MockModule.init();

        // Initialize root model objects.
        stateMachine = MockModel.stateMachine("sm", null);
        top = MockModel.region(stateMachine);

        expectedConditions = new ArrayList<Condition>();
    }

    /**
     * Generates the result transition conditions based on the scan mode being
     * tested in the concrete subclass.
     */
    abstract protected void generateResult() throws ExportException;

    /**
     * Top-level initial transition for the entire state machine that targets a
     * top-level simple state.
     * <p>
     * <img src="TransitionConditionTests.stateMachineInitialTop.png">
     */
    @Test
    void stateMachineInitialTop() throws ExportException {
        source = MockModel.initialPseudoState(top);
        target = MockModel.state("target", top);
        transition = MockModel.transition(source, target, "");

        generateResult();
        generateExpectedStateMachineInitialTop();

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for stateMachineInitialTop().
     */
    abstract protected void generateExpectedStateMachineInitialTop();

    /**
     * Top-level initial transition for the entire state machine that targets a
     * sub-state.
     * <p>
     * <img src="TransitionConditionTests.stateMachineInitialSubstate.png">
     */
    @Test
    void stateMachineInitialSubstate() throws ExportException {
        source = MockModel.initialPseudoState(top);
        final State superstate = MockModel.state("superstate", top);
        final Region region = MockModel.region(superstate);
        target = MockModel.state("target", region);
        transition = MockModel.transition(source, target, "");

        generateResult();
        generateExpectedStateMachineInitialSubstate(superstate);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for
     * stateMachineInitialSubstate().
     */
    abstract protected void generateExpectedStateMachineInitialSubstate(final State superstate);

    /**
     * Top-level initial transition for the entire state machine that targets a
     * deeply-nested sub-state.
     * <p>
     * <img src="TransitionConditionTests.stateMachineInitialSubstateNested.png">
     */
    @Test
    void stateMachineInitialSubstateNested() throws ExportException {
        source = MockModel.initialPseudoState(top);

        final State superstate = MockModel.state("superstate", top);
        final Region superRegion = MockModel.region(superstate);

        final State mid = MockModel.state("mid", superRegion);
        final Region midRegion = MockModel.region(mid);

        target = MockModel.state("target", midRegion);

        transition = MockModel.transition(source, target, "");

        generateResult();
        generateExpectedStateMachineInitialSubstateNested(superstate, mid);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for
     * stateMachineInitialSubstateNested().
     */
    abstract protected void generateExpectedStateMachineInitialSubstateNested(final State superstate, final State mid);

    /**
     * Top-level initial transition for the entire state machine that targets a
     * super-state that has its own initial transition.
     * <p>
     * <img src="TransitionConditionTests.stateMachineInitialSuperstateInitial.png">
     */
    @Test
    void stateMachineInitialSuperstateInitial() throws ExportException {
        source = MockModel.initialPseudoState(top);
        final State superstate = MockModel.state("superstate", top);
        final Region region = MockModel.region(superstate);
        target = MockModel.state("target", region);
        final InitialPseudoState superInitialOrigin = MockModel.initialPseudoState(region);
        MockModel.transition(superInitialOrigin, target, "");
        transition = MockModel.transition(source, superstate, "");

        generateResult();
        generateExpectedStateMachineInitialSuperstateInitial(superstate);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for
     * stateMachineInitialSuperstateInitial().
     */
    abstract protected void generateExpectedStateMachineInitialSuperstateInitial(final State superstate);

    /**
     * Top-level initial transition for the entire state machine targeting a
     * super-state that has its own initial transition to a deeply-nested sub-state.
     * <p>
     * <img src=
     * "TransitionConditionTests.stateMachineInitialSuperstateInitialNested.png">
     */
    @Test
    void stateMachineInitialSuperstateInitialNested() throws ExportException {
        source = MockModel.initialPseudoState(top);

        final State superstate = MockModel.state("superstate", top);
        final Region superRegion = MockModel.region(superstate);

        final State mid = MockModel.state("mid", superRegion);
        final Region midRegion = MockModel.region(mid);

        target = MockModel.state("target", midRegion);

        final InitialPseudoState superInitial = MockModel.initialPseudoState(superRegion);
        MockModel.transition(superInitial, target, "");

        transition = MockModel.transition(source, superstate, "");

        generateResult();
        generateExpectedStateMachineInitialSuperstateInitialNested(superstate, mid);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for
     * stateMachineInitialSuperstateInitialNested().
     */
    abstract protected void generateExpectedStateMachineInitialSuperstateInitialNested(final State superstate,
            final State mid);

    /**
     * Transition between two, top-level states.
     * <p>
     * <img src="TransitionConditionTests.topSibling.png">
     */
    @Test
    void topSibling() throws ExportException {
        source = MockModel.state("source", top);
        target = MockModel.state("target", top);
        transition = MockModel.transition(source, target, "");

        generateResult();
        generateExpectedTopSibling();

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for topSibling().
     */
    abstract protected void generateExpectedTopSibling();

    /**
     * Transition to a state within the same super-state.
     * <p>
     * <img src="TransitionConditionTests.substateSibling.png">
     */
    @Test
    void substateSibling() throws ExportException {
        final State superstate = MockModel.state("superstate", top);
        final Region region = MockModel.region(superstate);
        source = MockModel.state("source", region);
        target = MockModel.state("target", region);
        transition = MockModel.transition(source, target, "");

        generateResult();
        generateExpectedSubstateSibling(superstate);

        assertCorrectResult();
    }

    abstract protected void generateExpectedSubstateSibling(final State superstate);

    /**
     * Transition from a sub-state to its enclosing super-state.
     * <p>
     * <img src="TransitionConditionTests.toSuperstate.png">
     */
    @Test
    void toSuperstate() throws ExportException {
        target = MockModel.state("target", top);
        final Region region = MockModel.region(target);
        source = MockModel.state("source", region);
        transition = MockModel.transition(source, target, "");

        generateResult();
        generateExpectedToSuperstate();

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for toSuperstate().
     */
    abstract protected void generateExpectedToSuperstate();

    /**
     * Transition from a deeply-nested sub-state to its top-level enclosing
     * super-state
     * <p>
     * <img src="TransitionConditionTests.toSuperstateNested.png">
     */
    @Test
    void toSuperstateNested() throws ExportException {
        target = MockModel.state("target", top);
        final Region targetRegion = MockModel.region(target);
        final State mid = MockModel.state("mid", targetRegion);
        final Region midRegion = MockModel.region(mid);
        source = MockModel.state("source", midRegion);
        transition = MockModel.transition(source, target, "");

        generateResult();
        generateExpectedToSuperstateNested(mid);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for toSuperstateNested().
     */
    abstract protected void generateExpectedToSuperstateNested(final State mid);

    /**
     * Transition from a super-state to one of its sub-states.
     * <p>
     * <img src="TransitionConditionTests.toSubstate.png">
     */
    @Test
    void toSubstate() throws ExportException {
        source = MockModel.state("source", top);
        final Region region = MockModel.region(source);
        target = MockModel.state("target", region);
        transition = MockModel.transition(source, target, "");

        generateResult();
        generateExpectedToSubstate();

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for toSubstate().
     */
    abstract protected void generateExpectedToSubstate();

    /**
     * Transition from a super-state to a deeply-nested sub-state.
     * <p>
     * <img src="TransitionConditionTests.toSubstateNested.png">
     */
    @Test
    void toSubstateNested() throws ExportException {
        source = MockModel.state("source", top);
        final Region sourceRegion = MockModel.region(source);
        final State mid = MockModel.state("mid", sourceRegion);
        final Region midRegion = MockModel.region(mid);
        target = MockModel.state("target", midRegion);
        transition = MockModel.transition(source, target, "");

        generateResult();
        generateExpectedToSubstateNested(mid);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for topSubstateNested().
     */
    abstract protected void generateExpectedToSubstateNested(final State mid);

    /**
     * Transition leaving the super-state containing the source sub-state.
     * <p>
     * <img src="TransitionConditionTests.superstateExit.png">
     */
    @Test
    void superstateExit() throws ExportException {
        final State superstate = MockModel.state("superstate", top);
        final Region region = MockModel.region(superstate);
        source = MockModel.state("source", region);
        target = MockModel.state("target", top);
        transition = MockModel.transition(source, target, "");

        generateResult();
        generateExpectedSuperstateExit(superstate);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for superstateExit().
     */
    abstract protected void generateExpectedSuperstateExit(final State superstate);

    /**
     * Transition leaving a super-state from a deeply-nested sub-state.
     * <p>
     * <img src="TransitionConditionTests.superstateExitNested.png">
     */
    @Test
    void superstateExitNested() throws ExportException {
        final State superstate = MockModel.state("superstate", top);
        final Region superRegion = MockModel.region(superstate);

        final State mid = MockModel.state("mid", superRegion);
        final Region midRegion = MockModel.region(mid);

        source = MockModel.state("source", midRegion);

        target = MockModel.state("target", top);

        transition = MockModel.transition(source, target, "");

        generateResult();
        generateExpectedSuperstateExitNested(superstate, mid);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for superstateExitNested().
     */
    abstract protected void generateExpectedSuperstateExitNested(final State superstate, final State mid);

    /**
     * Transition targeting a sub-state within a super-state that does not contain
     * the source state.
     * <p>
     * <img src="TransitionConditionTests.superstateEntry.png">
     */
    @Test
    void superstateEntry() throws ExportException {
        source = MockModel.state("source", top);
        final State superstate = MockModel.state("superstate", top);
        final Region region = MockModel.region(superstate);
        target = MockModel.state("target", region);
        transition = MockModel.transition(source, target, "");

        generateResult();
        generateExpectedSuperstateEntry(superstate);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for superstateEntry().
     */
    abstract protected void generateExpectedSuperstateEntry(final State superstate);

    /**
     * Transition to a deeply-nested sub-state within a top-level super-state.
     * <p>
     * <img src="TransitionConditionTests.superstateEntryNested.png">
     */
    @Test
    void superstateEntryNested() throws ExportException {
        source = MockModel.state("source", top);

        final State superstate = MockModel.state("superstate", top);
        final Region superRegion = MockModel.region(superstate);

        final State mid = MockModel.state("mid", superRegion);
        final Region midRegion = MockModel.region(mid);

        target = MockModel.state("target", midRegion);

        transition = MockModel.transition(source, target, "");

        generateResult();
        generateExpectedSuperstateEntryNested(superstate, mid);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for superstateEntryNested().
     */
    abstract protected void generateExpectedSuperstateEntryNested(final State superstate, final State mid);

    /**
     * Transition to a super-state with its own initial transition.
     * <p>
     * <img src="TransitionConditionTests.superstateInitial.png">
     */
    @Test
    void superstateInitial() throws ExportException {
        source = MockModel.state("source", top);
        final State superstate = MockModel.state("superstate", top);
        final Region region = MockModel.region(superstate);
        target = MockModel.state("target", region);
        final InitialPseudoState superstateInitial = MockModel.initialPseudoState(region);
        MockModel.transition(superstateInitial, target, "");
        transition = MockModel.transition(source, superstate, "");

        generateResult();
        generateExpectedSuperstateInitial(superstate);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for superstateInitial().
     */
    abstract protected void generateExpectedSuperstateInitial(final State superstate);

    /**
     * Transition to a super-state with its own initial transition targeting a
     * deeply-nested sub-state.
     * <p>
     * <img src="TransitionConditionTests.superstateInitialNested.png">
     */
    @Test
    void superstateInitialNested() throws ExportException {
        source = MockModel.state("source", top);

        final State superstate = MockModel.state("superstate", top);
        final Region superRegion = MockModel.region(superstate);

        final State mid = MockModel.state("mid", superRegion);
        final Region midRegion = MockModel.region(mid);

        target = MockModel.state("target", midRegion);

        final InitialPseudoState superInitial = MockModel.initialPseudoState(superRegion);
        MockModel.transition(superInitial, target, "");

        transition = MockModel.transition(source, superstate, "");

        generateResult();
        generateExpectedSuperstateInitialNested(superstate, mid);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for superstateInitialNested().
     */
    abstract protected void generateExpectedSuperstateInitialNested(final State superstate, final State mid);

    /**
     * Transition from a sub-state to its enclosing super-state where the
     * super-state has its own initial transition.
     * <p>
     * <img src="TransitionConditionTests.fromSubstateInitial.png">
     */
    @Test
    void fromSubstateInitial() throws ExportException {
        target = MockModel.state("target", top);
        final Region region = MockModel.region(target);

        source = MockModel.state("source", region);

        final State substate = MockModel.state("substate", region);

        final InitialPseudoState initial = MockModel.initialPseudoState(region);
        MockModel.transition(initial, substate, "");

        transition = MockModel.transition(source, target, "");

        generateResult();
        generateExpectedFromSubstateInitial();

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for fromSubstateInitial().
     */
    abstract protected void generateExpectedFromSubstateInitial();

    /**
     * Transition from a deeply-nested sub-state to the top-level super-state where
     * the super-state has its own initial transition.
     * <p>
     * <img src="TransitionConditionTests.fromSubstateInitialNested.png">
     */
    @Test
    void fromSubstateInitialNested() throws ExportException {
        target = MockModel.state("target", top);
        final Region targetRegion = MockModel.region(target);

        final State mid = MockModel.state("mid", targetRegion);
        final Region midRegion = MockModel.region(mid);

        source = MockModel.state("source", midRegion);

        final State substate = MockModel.state("substate", targetRegion);
        final InitialPseudoState initial = MockModel.initialPseudoState(targetRegion);
        MockModel.transition(initial, substate, "");

        transition = MockModel.transition(source, target, "");

        generateResult();
        generateExpectedFromSubstateInitialNested(mid);
    }

    /**
     * Generates the expected transition conditions for fromSubstateInitialNested().
     */
    abstract protected void generateExpectedFromSubstateInitialNested(final State mid);

    /**
     * Confirms the transition conditions in the result match those in the expected
     * list.
     */
    protected void assertCorrectResult() {
        // Confirm the target state is the same.
        assertEquals(new MRef(target), result.getTarget(), "Target state differs.");

        final List<Condition> resultConditions = result.getConditions();

        // Confirm the same quantity of conditions.
        assertEquals(expectedConditions.size(), resultConditions.size(), "Number of conditions differ.");

        // Confirm the actions defined in each condition are the same.
        for (int i = 0; i < expectedConditions.size(); i++) {
            assertConditionEqual(i, expectedConditions.get(i), resultConditions.get(i));
        }
    }

    /**
     * Confirms a pair of conditions contain the same set of exit, do, and entry
     * actions.
     *
     * @param index    Position in the transition's condition array being checked.
     * @param expected Condition defining the correct result.
     * @param result   Condition generated by the test case.
     */
    private void assertConditionEqual(final int index, final Condition expected, final Condition result) {
        assertEquals(expected.getExitActions(), result.getExitActions(),
                String.format("Exit actions differ, condition index %d.", index));
        assertEquals(expected.getDoActions(), result.getDoActions(),
                String.format("Do actions differ, condition index %d.", index));
        assertEquals(expected.getEntryActions(), result.getEntryActions(),
                String.format("Entry actions differ, condition index %d.", index));
    }

}
