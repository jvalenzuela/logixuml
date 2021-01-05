package org.modelio.logixuml.statemachineaoi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.modelio.metamodel.diagrams.StateMachineDiagram;
import org.modelio.metamodel.uml.behavior.stateMachineModel.InitialPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.metamodel.uml.infrastructure.Note;
import org.modelio.vcore.model.CompositionGetter;
import org.modelio.vcore.smkernel.mapi.MObject;

public class StateMachineAoi {
    /**
     * Constructor.
     *
     * @param stateMachine Source state machine model.
     * @throws ExportException
     */
    public StateMachineAoi(final MObject stateMachine) throws ExportException {
        final Set<MObject> children = getChildren(stateMachine);
        validateElementTypes(children);
        final Map<String, AoiEvent> events = EventMap.build(children);
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
                break;

            default:
                final String shortName = e.getMClass().getName();
                throw new ExportException(String.format("Unsupported UML element type: %s", shortName), e);
            }
        }
    }
}
