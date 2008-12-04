package hudson.plugins.pmd;

import hudson.model.AbstractBuild;
import hudson.plugins.pmd.util.AbstractResultAction;
import hudson.plugins.pmd.util.HealthDescriptor;
import hudson.plugins.pmd.util.PluginDescriptor;

import java.util.NoSuchElementException;

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
    /** Unique identifier of this class. */
    private static final long serialVersionUID = -5329651349674842873L;

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
    public PmdResultAction(final AbstractBuild<?, ?> owner, final HealthDescriptor healthDescriptor, final PmdResult result) {
        super(owner, new PmdHealthDescriptor(healthDescriptor), result);
    }

    /**
     * Creates a new instance of <code>PmdResultAction</code>.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor to use
     */
    public PmdResultAction(final AbstractBuild<?, ?> owner, final HealthDescriptor healthDescriptor) {
        super(owner, new PmdHealthDescriptor(healthDescriptor));
    }

    /** {@inheritDoc} */
    public String getDisplayName() {
        return Messages.PMD_ProjectAction_Name();
    }

    /** {@inheritDoc} */
    @Override
    protected PluginDescriptor getDescriptor() {
        return PmdPublisher.PMD_DESCRIPTOR;
    }

    /**
     * Gets the PMD result of the previous build.
     *
     * @return the PMD result of the previous build.
     * @throws NoSuchElementException
     *             if there is no previous build for this action
     */
    public PmdResultAction getPreviousResultAction() {
        AbstractResultAction<PmdResult> previousBuild = getPreviousBuild();
        if (previousBuild instanceof PmdResultAction) {
            return (PmdResultAction)previousBuild;
        }
        throw new NoSuchElementException("There is no previous build for action " + this);
    }

    /** {@inheritDoc} */
    public String getMultipleItemsTooltip(final int numberOfItems) {
        return Messages.PMD_ResultAction_MultipleWarnings(numberOfItems);
    }

    /** {@inheritDoc} */
    public String getSingleItemTooltip() {
        return Messages.PMD_ResultAction_OneWarning();
    }
}
