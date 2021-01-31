package org.modelio.logixuml.structuredtext;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates a structured text WHILE_DO block.
 */
public class WhileDo {
    /**
     * Boolean expression controlling the loop.
     */
    private final String expression;

    /**
     * Structured text statements executed within the loop.
     */
    private final List<String> body;

    /**
     * Constructor.
     *
     * @param exp Boolean expression controlling the loop.
     */
    public WhileDo(final String exp) {
        assert !exp.isEmpty();
        expression = exp;
        body = new ArrayList<>();
    }

    /**
     * Appends structured text statements to the body.
     *
     * @param statements Structured text statements to add.
     */
    public void addStatements(final List<String> statements) {
        body.addAll(statements);
    }

    /**
     * Vararg version of {@link WhileDo#addStatements(List)}.
     *
     * @see IfThen#addCase(String, List)
     */
    public void addStatements(final String... statements) {
        addStatements(Arrays.asList(statements));
    }

    /**
     * Gets the structured text lines of the complete WHILE_DO block.
     *
     * @return Structured text lines.
     */
    public List<String> getLines() {
        final List<String> st = new ArrayList<>();
        st.add("WHILE " + expression + " DO");
        st.addAll(body);
        st.add("END_WHILE;");
        return unmodifiableList(st);
    }
}
