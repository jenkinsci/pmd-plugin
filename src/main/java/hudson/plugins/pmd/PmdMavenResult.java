package hudson.plugins.pmd;

import hudson.model.AbstractBuild;
import hudson.plugins.pmd.util.BuildResult;
import hudson.plugins.pmd.util.ParserResult;
import hudson.plugins.pmd.util.ResultAction;

/**
 * Represents the aggregated results of the PMD analysis in m2 jobs.
 *
 * @author Ulli Hafner
 */
public class PmdMavenResult extends PmdResult {
    /** Unique ID of this class. */
    private static final long serialVersionUID = -4913938782537266259L;

    /**
     * Creates a new instance of {@link PmdMavenResult}.
     *
     * @param build
     *            the current build as owner of this action
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param result
     *            the parsed result with all annotations
     */
    public PmdMavenResult(final AbstractBuild<?, ?> build, final String defaultEncoding,
            final ParserResult result) {
        super(build, defaultEncoding, result);
    }

    /**
     * Creates a new instance of {@link PmdMavenResult}.
     *
     * @param build
     *            the current build as owner of this action
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param result
     *            the parsed result with all annotations
     * @param previous
     *            the result of the previous build
     */
    public PmdMavenResult(final AbstractBuild<?, ?> build, final String defaultEncoding,
            final ParserResult result, final PmdResult previous) {
        super(build, defaultEncoding, result, previous);
    }

    /**
     * Returns the actual type of the associated result action.
     *
     * @return the actual type of the associated result action
     */
    @Override
    protected Class<? extends ResultAction<? extends BuildResult>> getResultActionType() {
        return MavenPmdResultAction.class;
    }
}

