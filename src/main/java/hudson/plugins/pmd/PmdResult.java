package hudson.plugins.pmd;

import hudson.model.AbstractBuild;
import hudson.plugins.pmd.parser.Bug;
import hudson.plugins.pmd.util.AnnotationsBuildResult;
import hudson.plugins.pmd.util.model.JavaProject;

/**
 * Represents the results of the PMD analysis. One instance of this class is persisted for
 * each build via an XML file.
 *
 * @author Ulli Hafner
 */
public class PmdResult extends AnnotationsBuildResult {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = 2768250056765266658L;
    static {
        XSTREAM.alias("bug", Bug.class);
    }

    /**
     * Creates a new instance of <code>PmdResult</code>.
     *
     * @param build
     *            the current build as owner of this action
     * @param project
     *            the parsed PMD result
     */
    public PmdResult(final AbstractBuild<?, ?> build, final JavaProject project) {
        super(build, project);
    }

    /**
     * Creates a new instance of <code>PmdResult</code>.
     *
     * @param build
     *            the current build as owner of this action
     * @param project
     *            the parsed PMD result
     * @param previous
     *            the result of the previous build
     */
    public PmdResult(final AbstractBuild<?, ?> build, final JavaProject project, final PmdResult previous) {
        super(build, project, previous);
    }

    /**
     * Returns a summary message for the summary.jelly file.
     *
     * @return the summary message
     */
    public String getSummary() {
        return ResultSummary.createSummary(this);
    }

    /**
     * Returns the detail messages for the summary.jelly file.
     *
     * @return the summary message
     */
    public String getDetails() {
        String message = ResultSummary.createDeltaMessage(this);
        if (getNumberOfAnnotations() == 0 && getDelta() == 0) {
            return message + "<li>" + Messages.PMD_ResultAction_NoWarningsSince(getZeroWarningsSinceBuild()) + "</li>";
        }
        return message;
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
     * Returns the results of the previous build.
     *
     * @return the result of the previous build, or <code>null</code> if no
     *         such build exists
     */
    @Override
    public JavaProject getPreviousResult() {
        PmdResultAction action = getOwner().getAction(PmdResultAction.class);
        if (action.hasPreviousResultAction()) {
            return action.getPreviousResultAction().getResult().getProject();
        }
        else {
            return null;
        }
    }

    /**
     * Returns whether a previous build result exists.
     *
     * @return <code>true</code> if a previous build result exists.
     */
    @Override
    public boolean hasPreviousResult() {
        return getOwner().getAction(PmdResultAction.class).hasPreviousResultAction();
    }
}
