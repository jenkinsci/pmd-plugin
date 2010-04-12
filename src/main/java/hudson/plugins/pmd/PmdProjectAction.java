package hudson.plugins.pmd;

import hudson.model.AbstractProject;
import hudson.plugins.analysis.core.AbstractProjectAction;

/**
 * Entry point to visualize the PMD trend graph in the project screen.
 * Drawing of the graph is delegated to the associated
 * {@link PmdResultAction}.
 *
 * @author Ulli Hafner
 */
public class PmdProjectAction extends AbstractProjectAction<PmdResultAction> {
    /**
     * Instantiates a new find bugs project action.
     *
     * @param project
     *            the project that owns this action
     */
    public PmdProjectAction(final AbstractProject<?, ?> project) {
        super(project, PmdResultAction.class, new PmdDescriptor());
    }

    /** {@inheritDoc} */
    public String getDisplayName() {
        return Messages.PMD_ProjectAction_Name();
    }

    /** {@inheritDoc} */
    @Override
    public String getTrendName() {
        return Messages.PMD_Trend_Name();
    }
}

