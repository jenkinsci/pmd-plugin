package hudson.plugins.pmd;

import java.util.Collection;

import hudson.model.Action;
import hudson.model.Run;
import hudson.plugins.analysis.core.AbstractResultAction;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.PluginDescriptor;

/**
 * Controls the live cycle of the PMD results. This action persists the
 * results of the PMD analysis of a build and displays the results on the
 * build page. The actual visualization of the results is defined in the
 * matching <code>summary.jelly</code> file.
 * <p>
 * Moreover, this class renders the PMD result trend.
 * </p>
 *
 * @author Ulli Hafner
 */
public class PmdResultAction extends AbstractResultAction<PmdResult> {
    /**
     * Creates a new instance of <code>PmdResultAction</code>.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor to use
     * @param result
     *            the result in this build
     */
    public PmdResultAction(final Run<?, ?> owner, final HealthDescriptor healthDescriptor, final PmdResult result) {
        super(owner, new PmdHealthDescriptor(healthDescriptor), result);
    }

    @Override
    public Collection<? extends Action> getProjectActions() {
        return asSet(new PmdProjectAction(getJob()));
    }

    @Override
    public String getDisplayName() {
        return Messages.PMD_ProjectAction_Name();
    }

    @Override
    protected PluginDescriptor getDescriptor() {
        return new PmdDescriptor();
    }
}
