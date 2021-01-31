package org.modelio.logixuml.statemachineaoi;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.modelio.logixuml.l5x.AddOnInstruction;
import org.modelio.logixuml.l5x.DataType;
import org.modelio.logixuml.l5x.ScanModeRoutine;
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
        aoi.addStructuredTextLines(ScanModeRoutine.Logic, eventQ.enqueueEvents(events.values()));
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
