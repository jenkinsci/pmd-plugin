package hudson.plugins.pmd.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractFixedAnnotationsTokenMacro;
import hudson.plugins.pmd.PmdMavenResultAction;
import hudson.plugins.pmd.PmdResultAction;

/**
 * Provides a token that evaluates to the number of fixed PMD warnings.
 *
 * @author Ulli Hafner
 */
@Extension(optional = true)
public class FixedPmdWarningsTokenMacro extends AbstractFixedAnnotationsTokenMacro {
    /**
     * Creates a new instance of {@link FixedPmdWarningsTokenMacro}.
     */
    @SuppressWarnings("unchecked")
    public FixedPmdWarningsTokenMacro() {
        super("PMD_FIXED", PmdResultAction.class, PmdMavenResultAction.class);
    }
}

