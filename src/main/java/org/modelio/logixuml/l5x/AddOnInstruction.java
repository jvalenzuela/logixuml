package org.modelio.logixuml.l5x;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
    private Map<String, Element> Routines = new HashMap<String, Element>();

    /**
     * Constructor.
     *
     * @param name AOI name.
     * @throws AddInstructionException If an invalid name was given.
     */
    public AddOnInstruction(final String name) throws AddOnInstructionException {
        validateIdentifier(name, "AOI");
        Name = name;
        createDoc();
    }

    /**
     * Generates an initial XML document with the necessary AOI structure.
     *
     * @throws AddOnInstructionException If an XML parser configuration error
     *                                   occurs.
     */
    private void createDoc() throws AddOnInstructionException {
        final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder;

        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new AddOnInstructionException("XML parser configuration error.", e);
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
        aoi.setAttribute("name", Name);
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
        routines.appendChild(routineElement("Prescan"));
        routines.appendChild(routineElement("Logic"));
        routines.appendChild(routineElement("EnableInFalse"));
    }

    /**
     * Generates an XML element defining an empty structured text routine.
     *
     * @param name Routine name.
     * @returns The generated XML element.
     */
    private Element routineElement(final String name) {
        final Element routine = Doc.createElement("Routine");
        routine.setAttribute("Name", name);
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
     * @param usage    "Input" or "Output".
     * @param dataType String defining the RSLogix data type, e.g. "DINT".
     * @param visible  True to make the parameter visible in the ladder display.
     * @param desc     Optional parameter description string.
     * @throws AddOnInstructionException If the name is invalid.
     */
    public void addParameter(final String name, final String usage, final String dataType, final boolean visible,
            final String... desc) throws AddOnInstructionException {
        validateIdentifier(name, "parameter");

        final Element e = Doc.createElement("Parameter");
        Parameters.appendChild(e);

        e.setAttribute("Name", name);
        e.setAttribute("Usage", usage);
        e.setAttribute("DataType", dataType);
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
     * @param dataType RSLogix name for the data type, e.g. "DINT".
     * @param dim      Optional array size if creating an array tag.
     * @throws AddOnInstructionException If the name is invalid.
     */
    public void addLocalTag(final String name, final String dataType, final int... dim)
            throws AddOnInstructionException {
        validateIdentifier(name, "local tag");

        final Element e = Doc.createElement("LocalTag");
        LocalTags.appendChild(e);

        e.setAttribute("name", name);
        e.setAttribute("DataType", dataType);

        // Add the optional dimensions attribute for array tags.
        if (dim.length > 0) {
            e.setAttribute("Dimensions", new Integer(dim[0]).toString());
        }
    }

    /**
     * Appends a line of structured text to a routine.
     *
     * @param name   Name of the target routine.
     * @param stLine Structured text line to add.
     */
    public void addStructuredTextLine(final String name, final String stLine) {
        final Element routine = Routines.get(name);

        // Determine the next line number to use.
        final Integer lineNum;
        final NodeList existingLines = routine.getElementsByTagName("Line");
        if (existingLines.getLength() > 0) {
            final Element lastLine = (Element) existingLines.item(existingLines.getLength() - 1);
            lineNum = new Integer(lastLine.getAttribute("Number")) + 1;
        } else {
            lineNum = 0; // Start at 0 if no lines exist.
        }

        final Element line = Doc.createElement("Line");
        routine.appendChild(line);
        line.setAttribute("Number", lineNum.toString());

        // Create the CDATA section with the actual content.
        line.appendChild(Doc.createCDATASection(stLine));
    }

    /**
     * Writes the AOI to an L5X file.
     *
     * @throws AddOnInstructionException If an XML transformation error occurs.
     * @throws IOException               If the L5X file could not be written.
     * @param filename Target L5X file name.
     */
    public void write() throws AddOnInstructionException, IOException {
        final DOMSource src = new DOMSource(Doc);
        final TransformerFactory xfrFactory = TransformerFactory.newInstance();
        final String filename = Name + ".L5X";

        try (final FileOutputStream f = new FileOutputStream(filename)) {
            final Transformer xfr = xfrFactory.newTransformer();
            final StreamResult dst = new StreamResult(f);
            xfr.transform(src, dst);
        } catch (TransformerException e) {
            throw new AddOnInstructionException("XML transformation error.", e);
        }
    }

    /**
     * Confirms a string is valid for use as a Logix identifier.
     *
     * @param id      The identifier to test.
     * @param context The type of identifier begin checked, e.g. local tag,
     *                parameter, etc.
     * @throws AddOnInstructionException If the identifier string is invalid.
     */
    private void validateIdentifier(final String id, final String context) throws AddOnInstructionException {
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
            throw new AddOnInstructionException(String.format("Invalid %1$s identifier: %2$s", context, id));
        }
    }
}
