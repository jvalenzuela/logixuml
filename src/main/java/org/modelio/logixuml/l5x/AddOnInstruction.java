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

package org.modelio.logixuml.l5x;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.modelio.logixuml.statemachineaoi.ExportException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class handles constructing an XML document defining an add-on
 * instruction and exporting it to an L5X file.
 */
public class AddOnInstruction {
    /**
     * Name of the add-on instruction.
     */
    private String Name;

    /**
     * Top-level XML document containing L5X content.
     */
    private Document Doc;

    /**
     * Parent XML element containing all parameter definitions.
     */
    private Element Parameters;

    /**
     * Parent XML element containing all local tag definitions.
     */
    private Element LocalTags;

    /**
     * Mapping containing the logic routines, keyed by routine name, with the value
     * as a STContent element containing the structured text.
     */
    private Map<ScanModeRoutine, Element> Routines = new HashMap<>();

    /**
     * Constructor.
     *
     * @param name AOI name.
     * @throws ExportException If an invalid name was given or an XML parser
     *                         configuration error occurs.
     */
    public AddOnInstruction(final String name) throws ExportException {
        try {
            validateIdentifier(name);
        } catch (ExportException e) {
            throw new ExportException(String.format("String is not a valid add-on instruction name: %s", name));
        }
        Name = name;
        createDoc();
    }

    /**
     * Generates an initial XML document with the necessary AOI structure.
     *
     * @throws ExportException If an XML parser configuration error occurs.
     */
    private void createDoc() throws ExportException {
        final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder;

        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new ExportException("XML parser configuration error.", e);
        }

        Doc = builder.newDocument();

        final Element root = Doc.createElement("RSLogix5000Content");
        root.setAttribute("TargetType", "AddOnInstructionDefinition");
        root.setAttribute("SchemaRevision", "1.0");
        root.setAttribute("ContainsContext", "true");
        Doc.appendChild(root);

        final Element controller = Doc.createElement("Controller");
        root.appendChild(controller);

        controller.appendChild(Doc.createElement("DataTypes"));

        final Element aoiDefs = Doc.createElement("AddOnInstructionDefinitions");
        controller.appendChild(aoiDefs);

        final Element aoi = Doc.createElement("AddOnInstructionDefinition");
        aoi.setAttribute("Name", Name);
        aoi.setAttribute("Use", "Target");
        aoi.setAttribute("ExecutePrescan", "true");
        aoi.setAttribute("ExecuteEnableInFalse", "true");
        aoiDefs.appendChild(aoi);

        Parameters = Doc.createElement("Parameters");
        aoi.appendChild(Parameters);

        LocalTags = Doc.createElement("LocalTags");
        aoi.appendChild(LocalTags);

        // Add parent elements for each routine.
        final Element routines = Doc.createElement("Routines");
        aoi.appendChild(routines);
        routines.appendChild(routineElement(ScanModeRoutine.Prescan));
        routines.appendChild(routineElement(ScanModeRoutine.Logic));
        routines.appendChild(routineElement(ScanModeRoutine.EnableInFalse));
    }

    /**
     * Generates an XML element defining an empty structured text routine.
     *
     * @param name Routine name.
     * @returns The generated XML element.
     */
    private Element routineElement(final ScanModeRoutine name) {
        final Element routine = Doc.createElement("Routine");
        routine.setAttribute("Name", name.name());
        routine.setAttribute("Type", "ST");

        final Element content = Doc.createElement("STContent");
        routine.appendChild(content);

        // Store the STContent element as the routine element as this is
        // where routine content will actually be stored.
        Routines.put(name, content);

        return routine;
    }

    /**
     * Creates an AOI parameter.
     *
     * @param name     Parameter name.
     * @param usage    Parameter direction; input or output.
     * @param dataType RSLogix data type.
     * @param visible  True to make the parameter visible in the ladder display.
     * @param desc     Optional parameter description string.
     * @throws ExportException If the name is invalid.
     */
    public void addParameter(final String name, final ParameterUsage usage, final DataType dataType,
            final boolean visible, final String... desc) throws ExportException {
        validateIdentifier(name);

        final Element e = Doc.createElement("Parameter");
        Parameters.appendChild(e);

        e.setAttribute("Name", name);
        e.setAttribute("Usage", usage.name());
        e.setAttribute("DataType", dataType.name());
        e.setAttribute("Visible", new Boolean(visible).toString());

        // Description is stored in a child element with CDATA content.
        if (desc.length > 0) {
            final Element descElement = Doc.createElement("Description");
            e.appendChild(descElement);
            descElement.appendChild(Doc.createCDATASection(desc[0]));
        }
    }

    /**
     * Creates a local tag.
     *
     * @param name     Tag name.
     * @param dataType RSLogix name for the data type.
     * @param dim      Optional array size if creating an array tag.
     * @throws ExportException If the name is invalid.
     */
    public void addLocalTag(final String name, final DataType dataType, final int... dim) throws ExportException {
        validateIdentifier(name);

        final Element e = Doc.createElement("LocalTag");
        LocalTags.appendChild(e);

        e.setAttribute("name", name);
        e.setAttribute("DataType", dataType.name());

        // Add the optional dimensions attribute for array tags.
        if (dim.length > 0) {
            e.setAttribute("Dimensions", new Integer(dim[0]).toString());
        }
    }

    /**
     * Appends a line of structured text to a routine.
     *
     * @param routine Target routine.
     * @param stLine  Structured text line to add.
     */
    public void addStructuredTextLine(final ScanModeRoutine routine, final String stLine) {
        final Element element = Routines.get(routine);

        // Determine the next line number to use.
        final Integer lineNum;
        final NodeList existingLines = element.getElementsByTagName("Line");
        if (existingLines.getLength() > 0) {
            final Element lastLine = (Element) existingLines.item(existingLines.getLength() - 1);
            lineNum = new Integer(lastLine.getAttribute("Number")) + 1;
        } else {
            lineNum = 0; // Start at 0 if no lines exist.
        }

        final Element line = Doc.createElement("Line");
        element.appendChild(line);
        line.setAttribute("Number", lineNum.toString());

        // Create the CDATA section with the actual content.
        line.appendChild(Doc.createCDATASection(stLine));
    }

    /**
     * Appends a sequence of structured text lines to a routine.
     *
     * @param routine Target routine.
     * @param lines   ST lines to append.
     */
    public void addStructuredTextLines(final ScanModeRoutine routine, final List<String> lines) {
        for (final String l : lines) {
            addStructuredTextLine(routine, l);
        }
    }

    /**
     * Writes the AOI to an L5X file.
     *
     * @param dir Target directory for the L5X file.
     * @throws ExportException If the L5X file could not be written.
     */
    public void write(final String dir) throws ExportException {
        final DOMSource src = new DOMSource(Doc);
        final TransformerFactory xfrFactory = TransformerFactory.newInstance();
        final Path path;

        // Combine the target directory and output file name into a complete, absolute
        // path.
        try {
            path = Paths.get(dir, Name + ".L5X");
        } catch (InvalidPathException e) {
            throw new ExportException("Invalid output path.", e);
        }

        try (final OutputStream f = Files.newOutputStream(path)) {
            final Transformer xfr = xfrFactory.newTransformer();
            final StreamResult dst = new StreamResult(f);
            xfr.transform(src, dst);
        } catch (TransformerException e) {
            throw new ExportException("XML transformation error.", e);
        } catch (IOException e) {
            throw new ExportException("Error writing L5X file.", e);
        }
    }

    /**
     * Confirms a string is valid for use as a Logix identifier.
     *
     * @param id The identifier to test.
     * @throws ExportException If the identifier string is invalid.
     */
    private void validateIdentifier(final String id) throws ExportException {
        // Per RSLogix documentation, which references IEC-1131, Section 2.1.2.
        final String pattern = "" //
                + "(?x)        # Enable in-line regex comments.\n" //
                + "(?i)        # Case-insensitive matching.\n" //
                + "\\A[_a-z]   # Start with an underscore or letter.\n" //
                + "(?:         # Additional characters after the first, if any.\n" //
                + "  [a-z0-9]  # Allow all letters and digits.\n" //
                + "  | (?<!_)_ # Underscore is permitted if it does not follow an underscore.\n" //
                + "){0,39}     # Additional 39 characters are allowed, for a total of 40.\n" //
                + "(?<!_)\\z   # Must not end in an underscore.";

        if (!id.matches(pattern)) {
            throw new ExportException();
        }
    }
}
