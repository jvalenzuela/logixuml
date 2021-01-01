package org.modelio.logixuml.structuredtext;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates an IF_THEN structured text block.
 */
public class IfThen implements IStructuredTextGenerator {
    /**
     * Container class to hold components defining a boolean expression and related
     * structured text statements within the IF_THEN block.
     */
    private class Case {
        /**
         * Boolean expression evaluated in the IF THEN statement.
         */
        private final String expression;

        /**
         * Structured text statements executed if the expression is true.
         */
        private final List<String> statements;

        /**
         * Constructor.
         *
         * @param expression Boolean expression evaluated for the IF block.
         * @param statements Structured text statements executed if the expression is
         *                   true.
         */
        Case(final String expression, final List<String> statements) {
            assert expression != null;
            final String trimmedExp = expression.trim();
            assert !trimmedExp.isEmpty();
            this.expression = trimmedExp;

            assert statements != null;
            assert statements.size() > 0;
            this.statements = statements;
        }

        /**
         * Getter method to acquire the case's boolean expression.
         *
         * @return The boolean expression to evaluate.
         */
        String getExpression() {
            return expression;
        }

        /**
         * Getter method to acquire the case's structured text statements.
         *
         * @return The list of structured text statements associated with the case.
         */
        List<String> getStatements() {
            return statements;
        }
    }

    /**
     * The set of non-else cases making up the entire IF_THEN block.
     */
    private final List<Case> cases;

    /**
     * Structured text statements to execute for the ELSE block; null if no ELSE
     * block was defined.
     */
    private List<String> elseStatements;

    /**
     * Constructor.
     */
    public IfThen() {
        cases = new ArrayList<>();
    }

    /**
     * Adds an expression and associated statements to the IF/THEN block.
     *
     * @param exp        Boolean expression to evaluate.
     * @param statements Structured text statements executed when the expression is
     *                   true.
     */
    public void addCase(final String exp, final List<String> statements) {
        cases.add(new Case(exp, statements));
    }

    /**
     * Assigns a set of statements to the ELSE block.
     *
     * @param statements The list of structured text statements to include in the
     *                   ELSE block.
     */
    public void addElse(final List<String> statements) {
        assert elseStatements == null; // Only one else block is allowed.
        assert statements != null;
        assert statements.size() > 0;
        elseStatements = statements;
    }

    @Override
    public List<String> getLines() {
        final List<String> lines = new ArrayList<>();

        // Add an IF/THEN block for each case.
        for (final Case c : cases) {
            // Use "IF" for the first case, "ELSEIF" for all others.
            final String ifKeyword = (cases.indexOf(c) == 0) ? "IF" : "ELSIF";

            lines.add(String.format("%s %s THEN", ifKeyword, c.getExpression()));
            lines.addAll(c.getStatements());
        }

        // Add the else block, if applicable.
        if (elseStatements != null) {
            lines.add("ELSE");
            lines.addAll(elseStatements);
        }

        lines.add("END_IF;");

        return lines;
    }
}
