package org.modelio.logixuml.structuredtext;

import java.util.List;

/**
 * Interface for classes that generate structured text statements.
 */
public interface IStructuredTextGenerator {
    /**
     * Acquires the resulting list of structured text statements.
     *
     * @return The list of structured text statements.
     */
    List<String> getLines();
}
