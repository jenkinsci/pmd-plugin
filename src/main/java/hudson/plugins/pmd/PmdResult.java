package hudson.plugins.pmd;

import hudson.model.AbstractBuild;
import hudson.plugins.analysis.core.BuildHistory;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.core.ResultAction;
import hudson.plugins.pmd.parser.Bug;

import com.thoughtworks.xstream.XStream;

/**
 * Represents the results of the PMD analysis. One instance of this class is persisted for
 * each build via an XML file.
 *
 * @author Ulli Hafner
 */
public class PmdResult extends BuildResult {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = 2768250056765266658L;

    /**
     * Creates a new instance of {@link PmdResult}.
     *
     * @param build
     *            the current build as owner of this action
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param result
     *            the parsed result with all annotations
     */
    public PmdResult(final AbstractBuild<?, ?> build, final String defaultEncoding,
            final ParserResult result) {
        super(build, defaultEncoding, result);
    }

    /**
     * Creates a new instance of {@link PmdResult}.
     *
     * @param build
     *            the current build as owner of this action
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param result
     *            the parsed result with all annotations
     * @param history
     *            the history of build results of the associated plug-in
     */
    public PmdResult(final AbstractBuild<?, ?> build, final String defaultEncoding,
            final ParserResult result, final BuildHistory history) {
        super(build, defaultEncoding, result, history);
    }

    /** {@inheritDoc} */
    @Override
    protected void configure(final XStream xstream) {
        xstream.alias("bug", Bug.class);
    }

    /**
     * Returns a summary message for the summary.jelly file.
     *
     * @return the summary message
     */
    public String getSummary() {
        return ResultSummary.createSummary(this);
    }

    /** {@inheritDoc} */
    @Override
    protected String createDeltaMessage() {
        return ResultSummary.createDeltaMessage(this);
    }

    /** {@inheritDoc} */
    @Override
    protected String getSerializationFileName() {
        return "pmd-warnings.xml";
    }

    /** {@inheritDoc} */
    public String getDisplayName() {
        return Messages.PMD_ProjectAction_Name();
    }

    /**
     * Returns the actual type of the associated result action.
     *
     * @return the actual type of the associated result action
     */
    @Override
    protected Class<? extends ResultAction<? extends BuildResult>> getResultActionType() {
        return PmdResultAction.class;
    }
}
