package org.modelio.logixuml.statemachineaoi;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntSupplier;

import org.modelio.logixuml.l5x.AddOnInstruction;
import org.modelio.logixuml.l5x.DataType;
import org.modelio.logixuml.l5x.ScanModeRoutine;
import org.modelio.logixuml.structuredtext.CaseOf;
import org.modelio.logixuml.structuredtext.Halt;
import org.modelio.metamodel.diagrams.StateMachineDiagram;
import org.modelio.metamodel.uml.behavior.stateMachineModel.InitialPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.metamodel.uml.infrastructure.Note;
import org.modelio.metamodel.uml.infrastructure.properties.TypedPropertyTable;
import org.modelio.vcore.model.CompositionGetter;
import org.modelio.vcore.smkernel.mapi.MObject;
import org.modelio.vcore.smkernel.mapi.MRef;

public class StateMachineAoi {
    /**
     * String added to the state machine model name to form the AOI's name.
     */
    private static String NAME_PREFIX = "stateMachine_";

    /**
     * AOI output object.
     */
    private final AddOnInstruction aoi;

    /**
     * Object handling the event queue implementation.
     */
    private final EventQueue eventQ;

    /**
     * Set of AoiEvent objects keyed by event name.
     */
    private final Map<String, AoiEvent> events;

    /**
     * Mapping of references to state model objects to the object handling the AOI
     * implementation of each state.
     */
    private final Map<MRef, AoiState> states;

    /**
     * Names for local tags that are not derived from model objects.
     */
    private class TagNames {
        /**
         * ID of the condition describing the current output states.
         */
        private final static String CONDITION_VARIABLE = "cv";

        /**
         * ID of the event that has been removed from the event queue and is being
         * evaluated for triggering a transition.
         */
        private final static String CURRENT_EVENT = "e";
    }

    /**
     * Constructor.
     *
     * @param stateMachine Source state machine model.
     * @throws ExportException
     */
    public StateMachineAoi(final MObject stateMachine) throws ExportException {
        aoi = initializeAoi(stateMachine);
        Halt.createTags(aoi);
        final Set<MObject> children = getChildren(stateMachine);
        validateElementTypes(children);
        final StereotypeProperties props = new StereotypeProperties((StateMachine) stateMachine);
        eventQ = new EventQueue(aoi, props.getEventQueueSize());

        events = EventMap.build(children);
        for (final AoiEvent e : events.values()) {
            e.initializeAoi(aoi);
        }

        states = StateMap.build(children);
        for (final AoiState state : states.values()) {
            state.initializeAoi(aoi);
        }

        buildLogicRoutine();
    }

    /**
     * Instantiates the add-on instruction and allocates static resources.
     *
     * @param stateMachine Source state machine model object.
     * @return The created AOI object.
     * @throws ExportException If the state machine name is invalid.
     */
    private AddOnInstruction initializeAoi(final MObject stateMachine) throws ExportException {
        final AddOnInstruction aoi = new AddOnInstruction(getName(stateMachine));

        // Create the condition variable tag and ensure it is reset in prescan and
        // enable-in false.
        aoi.addLocalTag(TagNames.CONDITION_VARIABLE, DataType.DINT);
        final String resetCv = TagNames.CONDITION_VARIABLE + " := 0;";
        aoi.addStructuredTextLine(ScanModeRoutine.Prescan, resetCv);
        aoi.addStructuredTextLine(ScanModeRoutine.EnableInFalse, resetCv);

        // The current event tag does not need to be reset in non-logic scan modes.
        aoi.addLocalTag(TagNames.CURRENT_EVENT, DataType.DINT);

        return aoi;
    }

    /**
     * Builds the AOI name from the state machine name.
     *
     * @param stateMachine Source state machine model.
     * @return The AOI name.
     * @throws ExportException If the state machine name is blank.
     */
    private String getName(final MObject stateMachine) throws ExportException {
        final String objName = stateMachine.getName().trim();

        // The only validation required here is to confirm the name isn't blank before
        // adding the prefix. Further checks for invalid names are applied when the
        // AddOnInstruction object confirms the name is a valid Logix identifier.
        if (objName.isEmpty()) {
            throw new ExportException("State machine cannot have an empty name.");
        }

        return NAME_PREFIX + objName;
    }

    /**
     * Gets the set of all model objects that are part of the state machine model.
     *
     * @param stateMachine Source state machine model.
     * @return Set of model objects making up the state machine.
     */
    private Set<MObject> getChildren(final MObject stateMachine) {
        final List<MObject> roots = new ArrayList<MObject>();
        roots.add(stateMachine);
        return CompositionGetter.getAllChildren(roots);
    }

    /**
     * Checks to see if any unsupported UML element types exist within the state
     * machine.
     *
     * @param elements The state machine's child elements.
     * @throws ExportException If an unsupported UML element type was found.
     */
    private void validateElementTypes(final Set<MObject> elements) throws ExportException {
        for (final MObject e : elements) {
            switch (e.getMClass().getQualifiedName()) {

            // List of supported model element types.
            case InitialPseudoState.MQNAME:
            case Note.MQNAME:
            case State.MQNAME:
            case StateMachineDiagram.MQNAME:
            case Region.MQNAME:
            case Transition.MQNAME:
            case TypedPropertyTable.MQNAME:
                break;

            default:
                final String shortName = e.getMClass().getName();
                throw new ExportException(String.format("Unsupported UML element type: %s", shortName), e);
            }
        }
    }

    /**
     * Constructs the add-on instruction's logic routine.
     */
    private void buildLogicRoutine() {
        // Object generating unique integer identifiers for every condition.
        final IntSupplier ConditionVarSupplier = new IntegerIdentifier();

        // Mapping to store the integer identifier assigned to every possible condition,
        // stable and transition.
        final Map<Integer, Condition> conditions = new HashMap<>();

        // Integer identifier for conditions representing the stable output condition
        // for each state.
        final Map<MRef, Integer> stableConditions = new HashMap<>();

        for (final MRef ref : states.keySet()) {
            final int id = ConditionVarSupplier.getAsInt();
            conditions.put(id, states.get(ref).getStableCondition());
            stableConditions.put(ref, id);
        }

        aoi.addStructuredTextLines(ScanModeRoutine.Logic, eventQ.enqueueEvents(events.values()));
        aoi.addStructuredTextLines(ScanModeRoutine.Logic, setStateOutputs(conditions));
    }

    /**
     * Generates a set of structured text statements setting the state outputs based
     * on the current condition.
     *
     * @param conditions Integer IDs for all possible conditions.
     * @return Structured text statements.
     */
    private List<String> setStateOutputs(final Map<Integer, Condition> conditions) {
        final List<String> st = new ArrayList<>();

        // Begin by unconditionally clearing all outputs.
        for (final AoiState state : states.values()) {
            st.add(state.setEntryOutput(false));
            st.add(state.setDoOutput(false));
            st.add(state.setExitOutput(false));
        }

        // Evaluate the current condition variable, and energize outputs associated with
        // the current condition.
        final CaseOf cvCases = new CaseOf(TagNames.CONDITION_VARIABLE);
        for (final int cv : conditions.keySet()) {
            cvCases.addCase(cv, conditions.get(cv).setOutputs(states));
        }
        cvCases.addElse(Halt.getLines()); // Fault on undefined condition variable.
        st.addAll(cvCases.getLines());

        // The state active outputs can now be set as they are just a function of the
        // entry, do, and exit outputs.
        for (final AoiState state : states.values()) {
            st.add(state.setActiveOutput());
        }

        return unmodifiableList(st);
    }

    /**
     * Writes the assembled AOI to an L5X file.
     *
     * @param dir Target directory for the L5X file.
     * @throws ExportException If the L5X file could not be written.
     */
    public void export(final String dir) throws ExportException {
        aoi.write(dir);
    }
}
