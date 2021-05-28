"""

"""

import java.nio.file.Paths
from org.modelio.metamodel.uml.behavior.stateMachineModel import StateMachine
from org.modelio.metamodel.uml.statik import Package
import os


# Name of the module defining the stereotype.
MODULE_NAME = 'LogixUML'


# Name of the stereotype defined by the module that will be applied to all
# state machines.
STEREOTYPE_NAME = 'StateMachineAoi'


# Event queue size applied to all state machines.
EVENT_QUEUE_SIZE = 2


# Set of all available scan modes and the suffix applied to state machines
# duplicated for each scan mode.
SCAN_MODES = {
    'single':'1',
    'dual':'2',
    'sequential':'s'
}


# Scan mode for state machines that don't specify a scan mode in their name.
DEFAULT_SCAN_MODE = 'single'


def import_patterns():
    """Loads and applies all pattern files found in the workspace path."""
    instance = Modelio.getInstance()
    context = instance.getContext()
    work_path = context.getWorkspacePath().toString()
    pattern_svc = instance.getPatternService()

    # Locate the project package that will contain all imported patterns.
    # This will be the only remaining package aside from the predefined UML
    # Types package.
    packages = session.findByClass(Package)
    pkg = [p for p in packages if p.name != 'UML Types'][0]

    # Mapping of state machine name to scan mode for state machines that
    # require a non-default scan mode.
    sm_modes = {}

    for ptn in [f for f in os.listdir(work_path) if f.endswith('.umlt')]:
        # Load the pattern file into the pattern catalog.
        pattern_path = java.nio.file.Paths.get(work_path, ptn)
        pattern_svc.addPattern(pattern_path)

        # Apply(instantiate) the pattern in the project.
        pattern_name = ptn[:-5] # Strip extension.
        params = {pattern_name:pkg}

        # Pattern names ending with a '_' are duplicated for each scan mode,
        # and require a $(name) parameter so each variant can be assigend
        # a unique name.
        if pattern_name.endswith('_'):
            for mode in SCAN_MODES:
                name = ''.join((pattern_name, SCAN_MODES[mode]))
                params['$(name)'] = name
                pattern_svc.applyPattern(pattern_name, params)

                sm_modes[name] = mode

        # All other patterns are applied once as-is.
        else:
            pattern_svc.applyPattern(pattern_name, params)

    return sm_modes


def apply_stereotypes(sm_modes):
    """Adds the stereotype and sets property values for all state machines."""
    for sm in session.findByClass(StateMachine):
        sm.addStereotype(MODULE_NAME, STEREOTYPE_NAME)
        sm.setProperty(MODULE_NAME, STEREOTYPE_NAME, 'eventQueueSize',
                       str(EVENT_QUEUE_SIZE))

        try:
            scan_mode = sm_modes[sm.name]
        except KeyError:
            scan_mode = DEFAULT_SCAN_MODE
        sm.setProperty(MODULE_NAME, STEREOTYPE_NAME, 'transitionScanMode',
                       scan_mode)


sm_modes = import_patterns()
apply_stereotypes(sm_modes)
