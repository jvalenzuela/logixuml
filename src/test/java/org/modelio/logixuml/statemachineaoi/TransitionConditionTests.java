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
     * Determines the model object representing the lowest-level state active at the
     * start of the test transition. This will be the source vertex except for
     * transitions originating from a state machine's initial transition.
     *
     * @return Starting source state.
     */
    protected State getActiveSource() {
        final Boolean sourceIsState = source.getMClass().getQualifiedName().equals(State.MQNAME);
        return sourceIsState ? (State) source : null;
    }

    /**
     * Generates the result transition conditions based on the scan mode being
     * tested in the concrete subclass.
     */
    abstract protected void generateResult() throws ExportException, IgnoreTransitionException;

    /**
     * Top-level initial transition for the entire state machine that targets a
     * top-level simple state.
     * <p>
     * <img src="TransitionConditionTests.stateMachineInitialTop.png">
     */
    @Test
    void stateMachineInitialTop() throws ExportException, IgnoreTransitionException {
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
    void stateMachineInitialSubstate() throws ExportException, IgnoreTransitionException {
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
    void stateMachineInitialSubstateNested() throws ExportException, IgnoreTransitionException {
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
    void stateMachineInitialSuperstateInitial() throws ExportException, IgnoreTransitionException {
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
    void stateMachineInitialSuperstateInitialNested() throws ExportException, IgnoreTransitionException {
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
    void topSibling() throws ExportException, IgnoreTransitionException {
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
    void substateSibling() throws ExportException, IgnoreTransitionException {
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
    void toSuperstate() throws ExportException, IgnoreTransitionException {
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
    void toSuperstateNested() throws ExportException, IgnoreTransitionException {
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
    void toSubstate() throws ExportException, IgnoreTransitionException {
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
    void toSubstateNested() throws ExportException, IgnoreTransitionException {
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
    void superstateExit() throws ExportException, IgnoreTransitionException {
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
    void superstateExitNested() throws ExportException, IgnoreTransitionException {
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
    void superstateEntry() throws ExportException, IgnoreTransitionException {
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
    void superstateEntryNested() throws ExportException, IgnoreTransitionException {
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
    void superstateInitial() throws ExportException, IgnoreTransitionException {
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
    void superstateInitialNested() throws ExportException, IgnoreTransitionException {
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
    void fromSubstateInitial() throws ExportException, IgnoreTransitionException {
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
    void fromSubstateInitialNested() throws ExportException, IgnoreTransitionException {
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

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for fromSubstateInitialNested().
     */
    abstract protected void generateExpectedFromSubstateInitialNested(final State mid);

    /**
     * Transition resulting from an event defined in a superstate, containing the
     * source but not the target.
     * <p>
     * <img src="TransitionConditionTests.groupTransitionExternal.png">
     */
    @Test
    void groupTransitionExternal() throws ExportException, IgnoreTransitionException {
        final State superstate = MockModel.state("superstate", top);
        final Region superRegion = MockModel.region(superstate);
        source = MockModel.state("source", superRegion);
        target = MockModel.state("target", top);
        transition = MockModel.transition(superstate, target, "");

        generateResult();
        generateExpectedGroupTransitionExternal(superstate);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for groupTransitionExternal().
     */
    abstract protected void generateExpectedGroupTransitionExternal(final State superstate);

    /**
     * Transition resulting from an event defined in a superstate, containing the
     * deeply-nested source but not the target.
     * <p>
     * <img src="TransitionConditionTests.groupTransitionExternalNested.png">
     */
    @Test
    void groupTransitionExternalNested() throws ExportException, IgnoreTransitionException {
        final State superstate = MockModel.state("superstate", top);
        final Region superRegion = MockModel.region(superstate);
        final State mid = MockModel.state("mid", superRegion);
        final Region midRegion = MockModel.region(mid);
        source = MockModel.state("source", midRegion);
        target = MockModel.state("target", top);
        transition = MockModel.transition(superstate, target, "");

        generateResult();
        generateExpectedGroupTransitionExternalNested(superstate, mid);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for
     * groupTransitionExternalNested().
     */
    abstract protected void generateExpectedGroupTransitionExternalNested(final State superstate, final State mid);

    /**
     * Transition resulting from an event defined in a superstate, containing both
     * the source and target.
     * <p>
     * <img src="TransitionConditionTests.groupTransitionLocal.png">
     */
    @Test
    void groupTransitionLocal() throws ExportException, IgnoreTransitionException {
        final State superstate = MockModel.state("superstate", top);
        final Region superRegion = MockModel.region(superstate);
        source = MockModel.state("source", superRegion);
        target = MockModel.state("target", superRegion);
        transition = MockModel.transition(superstate, target, "");

        generateResult();
        generateExpectedGroupTransitionLocal(superstate);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for groupTransitionLocal().
     */
    abstract protected void generateExpectedGroupTransitionLocal(final State superstate);

    /**
     * Transition resulting from an event defined in a superstate, containing both
     * the source and target.
     * <p>
     * <img src="TransitionConditionTests.groupTransitionLocalNested.png">
     */
    @Test
    void groupTransitionLocalNested() throws ExportException, IgnoreTransitionException {
        final State superstate = MockModel.state("superstate", top);
        final Region superRegion = MockModel.region(superstate);
        final State mid = MockModel.state("mid", superRegion);
        final Region midRegion = MockModel.region(mid);
        source = MockModel.state("source", midRegion);
        target = MockModel.state("target", superRegion);
        transition = MockModel.transition(superstate, target, "");

        generateResult();
        generateExpectedGroupTransitionLocalNested(superstate, mid);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for
     * groupTransitionLocalNested().
     */
    abstract protected void generateExpectedGroupTransitionLocalNested(final State superstate, final State mid);

    /**
     * Transition resulting from an event defined in a superstate, targeting the
     * source substate.
     * <p>
     * <img src="TransitionConditionTests.groupTransitionToSelf.png">
     */
    @Test
    void groupTransitionToSelf() throws ExportException {
        final State superstate = MockModel.state("superstate", top);
        final Region superRegion = MockModel.region(superstate);
        target = MockModel.state("source", superRegion);
        source = target;
        transition = MockModel.transition(superstate, target, null);

        assertThrows(IgnoreTransitionException.class, () -> generateResult());
    }

    /**
     * Transition resulting from an event defined in a superstate, targeting the
     * source substate, where the source has it's own initial transition.
     * <p>
     * <img src="TransitionConditionTests.groupTransitionToSelfInitial.png">
     */
    @Test
    void groupTransitionToSelfInitial() throws ExportException {
        final State superstate = MockModel.state("super", top);
        final Region superRegion = MockModel.region(superstate);
        source = MockModel.state("source", superRegion);
        final Region sourceRegion = MockModel.region(source);
        final State subState = MockModel.state("sub", sourceRegion);
        final InitialPseudoState initial = MockModel.initialPseudoState(sourceRegion);
        MockModel.transition(initial, subState, "");
        transition = MockModel.transition(superstate, source, "");

        assertThrows(IgnoreTransitionException.class, () -> generateResult());
    }

    /**
     * Transition resulting from an event defined in a superstate, targeting an
     * enclosing superstate.
     * <p>
     * <img src="TransitionConditionTests.groupTransitionToSuper.png">
     */
    @Test
    void groupTransitionToSuper() throws ExportException, IgnoreTransitionException {
        target = MockModel.state("target", top);
        final Region targetRegion = MockModel.region(target);
        final State mid = MockModel.state("mid", targetRegion);
        final Region midRegion = MockModel.region(mid);
        source = MockModel.state("source", midRegion);
        transition = MockModel.transition(mid, target, "");

        generateResult();
        generateExpectedGroupTransitionToSuper(mid);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for groupTransitionToSuper().
     */
    abstract protected void generateExpectedGroupTransitionToSuper(final State mid);

    /**
     * Transition resulting from an event defined in a superstate, targeting an
     * enclosing superstate that has its own initial transition.
     * <p>
     * <img src="TransitionConditionTests.groupTransitionToSuperInitial.png">
     */
    @Test
    void groupTransitionToSuperInitial() throws ExportException, IgnoreTransitionException {
        target = MockModel.state("target", top);
        final Region targetRegion = MockModel.region(target);
        final State mid = MockModel.state("mid", targetRegion);
        final Region midRegion = MockModel.region(mid);
        source = MockModel.state("source", midRegion);
        final State subState = MockModel.state("sub", targetRegion);
        final InitialPseudoState initial = MockModel.initialPseudoState(targetRegion);
        MockModel.transition(initial, subState, "");
        transition = MockModel.transition(mid, target, "");

        generateResult();
        generateExpectedGroupTransitionToSuperInitial(mid);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for
     * groupTransitionToSuperInitial().
     */
    abstract protected void generateExpectedGroupTransitionToSuperInitial(final State mid);

    /**
     * Transition resulting from an event defined in a superstate, targeting a
     * substate enclosed by the source.
     * <p>
     * <img src="TransitionConditionTests.groupTransitionToSub.png">
     */
    @Test
    void groupTransitionToSub() throws ExportException, IgnoreTransitionException {
        final State superstate = MockModel.state("super", top);
        final Region superRegion = MockModel.region(superstate);
        source = MockModel.state("source", superRegion);
        final Region sourceRegion = MockModel.region(source);
        target = MockModel.state("target", sourceRegion);
        transition = MockModel.transition(superstate, target, "");

        generateResult();
        generateExpectedGroupTransitionToSub(superstate);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for groupTransitionToSuper().
     * Implemented here, as opposed to the scan mode-dependent subclasses because
     * all scan modes will yield the same result.
     */
    private void generateExpectedGroupTransitionToSub(final State superstate) {
        final Condition c = new Condition();
        c.addEntryAction(new MRef(target));
        c.addDoAction(new MRef(source));
        c.addDoAction(new MRef(superstate));
        expectedConditions.add(c);
    }

    /**
     * Transition resulting from an event defined in a superstate, where the target
     * is enclosed by the source and contains its own initial transition.
     * <p>
     * <img src="TransitionConditionTests.groupTransitionToSubInitial.png">
     */
    @Test
    void groupTransitionToSubInitial() throws ExportException, IgnoreTransitionException {
        final State superstate = MockModel.state("super", top);
        final Region superRegion = MockModel.region(superstate);
        source = MockModel.state("source", superRegion);
        final Region sourceRegion = MockModel.region(source);
        final State substate = MockModel.state("sub", sourceRegion);
        final Region subRegion = MockModel.region(substate);
        target = MockModel.state("target", subRegion);
        final InitialPseudoState initial = MockModel.initialPseudoState(subRegion);
        MockModel.transition(initial, target, "");
        transition = MockModel.transition(superstate, substate, "");

        generateResult();
        generateExpectedGroupTransitionToSubInitial(superstate, substate);

        assertCorrectResult();
    }

    /**
     * Generates the expected transition conditions for
     * groupTransitionToSubInitial().
     */
    abstract protected void generateExpectedGroupTransitionToSubInitial(final State superstate, final State substate);

    /**
     * Confirms the transition conditions in the result match those in the expected
     * list.
     */
    protected void assertCorrectResult() {
        // Confirm the target state is the same.
        if (target != null) {
            assertEquals(new MRef(target), result.getTarget(), "Target state differs.");
        } else {
            // An undefined target must be accompanied by an empty list of expected
            // conditions. This assertion is a sanity check for the target and
            // expectedConditions; it is not intended to verify results.
            assertTrue(expectedConditions.isEmpty());
        }

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
