package hudson.plugins.pmd.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractResultTokenMacro;
import hudson.plugins.pmd.PmdResultAction;

/**
 * Provides a token that evaluates to the PMD build result.
 *
 * @author Ulli Hafner
 */
@Extension(optional = true)
public class PmdResultTokenMacro extends AbstractResultTokenMacro {
    /**
     * Creates a new instance of {@link PmdResultTokenMacro}.
     */
    public PmdResultTokenMacro() {
        super(PmdResultAction.class, "PMD_RESULT");
    }
}

