package hudson.plugins.pmd;

import hudson.model.Job;
import hudson.plugins.analysis.core.AbstractProjectAction;
import hudson.plugins.analysis.core.ResultAction;

/**
 * Entry point to visualize the PMD trend graph in the project screen. Drawing
 * of the graph is delegated to the associated {@link ResultAction}.
 *
 * @author Ulli Hafner
 */
public class PmdProjectAction extends AbstractProjectAction<ResultAction<PmdResult>> {
    /**
     * Instantiates a new {@link PmdProjectAction}.
     *
     * @param job
     *            the job that owns this action
     */
    public PmdProjectAction(final Job<?, ?> job) {
        this(job, PmdResultAction.class);
    }

    /**
     * Instantiates a new {@link PmdProjectAction}.
     *
     * @param job
     *            the job that owns this action
     * @param type
     *            the result action type
     */
    public PmdProjectAction(final Job<?, ?> job,
            final Class<? extends ResultAction<PmdResult>> type) {
        super(job, type, Messages._PMD_ProjectAction_Name(), Messages._PMD_Trend_Name(),
                PmdDescriptor.PLUGIN_ID, PmdDescriptor.ICON_URL, PmdDescriptor.RESULT_URL);
    }
}

