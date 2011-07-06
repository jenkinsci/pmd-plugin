package hudson.plugins.pmd.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractAnnotationsCountTokenMacro;
import hudson.plugins.pmd.PmdMavenResultAction;
import hudson.plugins.pmd.PmdResultAction;

/**
 * Provides a token that evaluates to the number of PMD warnings.
 *
 * @author Ulli Hafner
 */
@Extension(optional = true)
public class PmdWarningCountTokenMacro extends AbstractAnnotationsCountTokenMacro {
    /**
     * Creates a new instance of {@link PmdWarningCountTokenMacro}.
     */
    @SuppressWarnings("unchecked")
    public PmdWarningCountTokenMacro() {
        super("PMD_COUNT", PmdResultAction.class, PmdMavenResultAction.class);
    }
}

