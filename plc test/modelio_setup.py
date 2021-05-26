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


# Set of all available scan modes.
SCAN_MODES = frozenset(['single', 'dual', 'sequential'])


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
                params['$(name)'] = ''.join((pattern_name, mode))
                pattern_svc.applyPattern(pattern_name, params)

        # All other patterns are applied once as-is.
        else:
            pattern_svc.applyPattern(pattern_name, params)


def apply_stereotypes():
    """Adds the stereotype and sets property values for all state machines."""
    for sm in session.findByClass(StateMachine):
        sm.addStereotype(MODULE_NAME, STEREOTYPE_NAME)
        sm.setProperty(MODULE_NAME, STEREOTYPE_NAME, 'eventQueueSize',
                       str(EVENT_QUEUE_SIZE))

        # Set the scan mode based on the state machine name, or use the default
        # if the name does not define a specific mode.
        suffix = sm.name.split('_')[-1]
        scan_mode = suffix if suffix in SCAN_MODES else DEFAULT_SCAN_MODE
        sm.setProperty(MODULE_NAME, STEREOTYPE_NAME, 'transitionScanMode',
                       scan_mode)
