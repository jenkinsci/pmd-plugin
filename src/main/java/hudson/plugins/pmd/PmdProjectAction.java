package hudson.plugins.pmd;

import hudson.model.AbstractProject;
import hudson.plugins.analysis.core.ResultAction;
import hudson.plugins.analysis.core.AbstractProjectAction;

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
     * @param project
     *            the project that owns this action
     */
    public PmdProjectAction(final AbstractProject<?, ?> project) {
        this(project, PmdResultAction.class);
    }

    /**
     * Instantiates a new {@link PmdProjectAction}.
     *
     * @param project
     *            the project that owns this action
     * @param type
     *            the result action type
     */
    public PmdProjectAction(final AbstractProject<?, ?> project,
            final Class<? extends ResultAction<PmdResult>> type) {
        super(project, type, Messages._PMD_ProjectAction_Name(), Messages._PMD_Trend_Name(),
                PmdDescriptor.PLUGIN_ID, PmdDescriptor.ICON_URL, PmdDescriptor.RESULT_URL);
    }
}

