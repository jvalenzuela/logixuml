package org.modelio.logixuml.structuredtext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates a CASE_OF structured text block.
 */
public class CaseOf implements IStructuredTextGenerator {
    /**
     * Expression evaluated in the CASE statement.
     */
    final String expression;

    /**
     * Structured text statements keyed by selector value. Integer objects are used
     * to support a null key, which is used to store the ELSE clause.
     */
    private Map<Integer, List<String>> cases;

    /**
     * Constructor.
     *
     * @param expression Tag or expression that will be evaluated.
     */
    public CaseOf(final String expression) {
        this.expression = expression;
        cases = new HashMap<>();
    }

    /**
     * Assigns a set of structured text statements to a given selector value.
     *
     * @param selector   The value to compare to the CASE expression.
     * @param statements The structured text statements to execute for the given
     *                   selector value.
     */
    public void addCase(final int selector, final List<String> statements) {
        storeCase(selector, statements);
    }

    /**
     * Creates an ELSE case if no selector values match.
     *
     * @param statements The structured text statements to execute in the ELSE
     *                   clause.
     */
    public void addElse(final List<String> statements) {
        storeCase(null, statements);
    }

    /**
     * Validates and records a selector value and associated structured text
     * statements.
     *
     * @param selector   The key selector value; null for the ELSE clause.
     * @param statements The structured text statements to execute for the given
     *                   selector value.
     */
    private void storeCase(final Integer selector, final List<String> statements) {
        assert !cases.containsKey(selector); // All selector values must be unique.
        assert statements != null;
        cases.put(selector, statements);
    }

    @Override
    public List<String> getLines() {
        final List<String> lines = new ArrayList<>();

        lines.add(String.format("CASE %s OF", expression));

        // Append blocks for each non-null selector value.
        for (Integer sel : cases.keySet()) {
            if (sel != null) {
                lines.add(sel + ":");
                lines.addAll(cases.get(sel));
            }
        }

        // Append the else clause if one was defined.
        if (cases.containsKey(null)) {
            lines.add("ELSE");
            lines.addAll(cases.get(null));
        }

        lines.add("END_CASE;");

        return lines;
    }
}
