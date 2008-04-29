package hudson.plugins.pmd;

import hudson.model.AbstractBuild;
import hudson.plugins.pmd.util.AbstractResultAction;
import hudson.plugins.pmd.util.HealthReportBuilder;

import java.util.NoSuchElementException;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.JFreeChart;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

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
    /** URL to results. */
    private static final String PMD_RESULT_URL = "pmdResult";

    /**
     * Creates a new instance of <code>PmdBuildAction</code>.
     *
     * @param owner
     *            the associated build of this action
     * @param result
     *            the result in this build
     * @param healthReportBuilder
     *            health builder to use
     */
    public PmdResultAction(final AbstractBuild<?, ?> owner, final PmdResult result, final HealthReportBuilder healthReportBuilder) {
        super(owner, healthReportBuilder, result);
    }

    /** {@inheritDoc} */
    public String getDisplayName() {
        return Messages.PMD_ProjectAction_Name();
    }

    /** {@inheritDoc} */
    @Override
    public String getIconUrl() {
        return PmdDescriptor.PMD_ACTION_LOGO;
    }

    /** {@inheritDoc} */
    public String getUrlName() {
        return PMD_RESULT_URL;
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

    /**
     * Creates the chart for this action.
     *
     * @param request
     *            Stapler request
     * @param response
     *            Stapler response
     * @return the chart for this action.
     */
    @Override
    protected JFreeChart createChart(final StaplerRequest request, final StaplerResponse response) {
        String parameter = request.getParameter("useHealthBuilder");
        boolean useHealthBuilder = Boolean.valueOf(StringUtils.defaultIfEmpty(parameter, "true"));
        return getHealthReportBuilder().createGraph(useHealthBuilder, PMD_RESULT_URL, buildDataSet(useHealthBuilder),
                Messages.PMD_ResultAction_OneWarning(),
                Messages.PMD_ResultAction_MultipleWarnings("%d"));
    }
}
