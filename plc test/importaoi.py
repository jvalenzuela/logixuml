"""
This script imports all L5X AOI definitions from the current directory into
the unit test PLC project.
"""

from xml.dom.minidom import parse
import os


# File name of the PLC project where AOIs will be imported into.
INPUT_PROJECT = 'unittest.L5X'


# File name of the project to generate after all AOIs have been imported.
OUTPUT_PROJECT = 'out.L5X'


def load_aoi(filename):
    """Parses an L5X file containing an AOI definition."""
    aoi = parse(filename)
    target_type = aoi.documentElement.getAttribute('TargetType')
    if target_type != 'AddOnInstructionDefinition':
        raise TypeError

    # Return a copy of the element defining the AOI.
    elements = aoi.getElementsByTagName('AddOnInstructionDefinition')
    return elements[0].cloneNode(True)


def import_aoi(prj, aoi):
    """Imports an AOI into the PLC project."""
    aois = prj.getElementsByTagName('AddOnInstructionDefinitions')[0]

    # Remove any previous definition.
    name = aoi.getAttribute('Name')
    for e in prj.getElementsByTagName('AddOnInstructionDefinition'):
        if e.getAttribute('Name') == name:
            aois.removeChild(e)
            break

    # Insert the new definition.
    aois.appendChild(aoi)


if __name__ == '__main__':
    prj = parse(INPUT_PROJECT)

    for l5x in [f for f in os.listdir() if f.endswith('.L5X')]:
        try:
            aoi = load_aoi(l5x)
        except TypeError:
            continue
        import_aoi(prj, aoi)

    with open(OUTPUT_PROJECT, 'w') as f:
        prj.writexml(f)
