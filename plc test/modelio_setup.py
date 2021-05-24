"""

"""

import java.nio.file.Paths
from org.modelio.metamodel.uml.statik import Package
import os


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
        pattern_svc.applyPattern(pattern_name, {pattern_name:pkg})
