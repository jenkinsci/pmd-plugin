package hudson.plugins.pmd.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractResultTokenMacro;
import hudson.plugins.pmd.PmdResultAction;

/**
 * Provides a token that evaluates to the number of PMD warnings.
 *
 * @author Ulli Hafner
 */
@Extension(optional = true)
public class PmdWarningCountTokenMacro extends AbstractResultTokenMacro {
    /**
     * Creates a new instance of {@link PmdWarningCountTokenMacro}.
     */
    public PmdWarningCountTokenMacro() {
        super(PmdResultAction.class, "PMD_COUNT");
    }
}

