package hudson.plugins.pmd;

import hudson.model.AbstractBuild;
import hudson.plugins.pmd.parser.Bug;
import hudson.plugins.pmd.util.BuildResult;
import hudson.plugins.pmd.util.ParserResult;
import hudson.plugins.pmd.util.ResultAction;

/**
 * Represents the results of the PMD analysis. One instance of this class is persisted for
 * each build via an XML file.
 *
 * @author Ulli Hafner
 */
public class PmdResult extends BuildResult {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = 2768250056765266658L;
    static {
        XSTREAM.alias("bug", Bug.class);
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
     * @param previous
     *            the result of the previous build
     */
    public PmdResult(final AbstractBuild<?, ?> build, final String defaultEncoding,
            final ParserResult result, final PmdResult previous) {
        super(build, defaultEncoding, result, previous);
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
    public String getDetails() {
        String message = ResultSummary.createDeltaMessage(this);
        if (getNumberOfAnnotations() == 0 && getDelta() == 0) {
            message += "<li>" + Messages.PMD_ResultAction_NoWarningsSince(getZeroWarningsSinceBuild()) + "</li>";
            message += createHighScoreMessage();
        }
        return message;
    }

    /**
     * Creates a highscore message.
     *
     * @return a highscore message
     */
    private String createHighScoreMessage() {
        if (isNewZeroWarningsHighScore()) {
            long days = getDays(getZeroWarningsHighScore());
            if (days == 1) {
                return "<li>" + Messages.PMD_ResultAction_OneHighScore() + "</li>";
            }
            else {
                return "<li>" + Messages.PMD_ResultAction_MultipleHighScore(days) + "</li>";
            }
        }
        else {
            long days = getDays(getHighScoreGap());
            if (days == 1) {
                return "<li>" + Messages.PMD_ResultAction_OneNoHighScore() + "</li>";
            }
            else {
                return "<li>" + Messages.PMD_ResultAction_MultipleNoHighScore(days) + "</li>";
            }
        }
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
