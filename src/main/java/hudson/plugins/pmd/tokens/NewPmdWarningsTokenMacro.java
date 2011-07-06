package hudson.plugins.pmd.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractNewAnnotationsTokenMacro;
import hudson.plugins.pmd.PmdMavenResultAction;
import hudson.plugins.pmd.PmdResultAction;

/**
 * Provides a token that evaluates to the number of new PMD warnings.
 *
 * @author Ulli Hafner
 */
@Extension(optional = true)
public class NewPmdWarningsTokenMacro extends AbstractNewAnnotationsTokenMacro {
    /**
     * Creates a new instance of {@link NewPmdWarningsTokenMacro}.
     */
    @SuppressWarnings("unchecked")
    public NewPmdWarningsTokenMacro() {
        super("PMD_NEW", PmdResultAction.class, PmdMavenResultAction.class);
    }
}

