package hudson.plugins.pmd;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.kohsuke.stapler.DataBoundConstructor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import hudson.maven.MavenAggregatedReport;
import hudson.maven.MavenBuild;
import hudson.maven.MavenBuildProxy;
import hudson.maven.MavenModule;
import hudson.maven.MojoInfo;
import hudson.plugins.analysis.core.FilesParser;
import hudson.plugins.analysis.core.HealthAwareReporter;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.util.PluginLogger;
import hudson.plugins.pmd.parser.PmdParser;

/**
 * Publishes the results of the PMD analysis  (maven 2 project type).
 *
 * @author Ulli Hafner
 */
public class PmdReporter extends HealthAwareReporter<PmdResult> {
    private static final long serialVersionUID = 2272875032054063497L;
    private static final String PLUGIN_NAME = "PMD";

    /** Default PMD pattern. */
    private static final String PMD_XML_FILE = "pmd.xml";

    private PmdCutoff pmdCutoff = new PmdCutoff();

    /**
     * Creates a new instance of <code>PmdReporter</code>.
     *
     * @param healthy
     *            Report health as 100% when the number of warnings is less than
     *            this value
     * @param unHealthy
     *            Report health as 0% when the number of warnings is greater
     *            than this value
     * @param thresholdLimit
     *            determines which warning priorities should be considered when
     *            evaluating the build stability and health
     * @param useDeltaValues
     *            determines whether the absolute annotations delta or the
     *            actual annotations set difference should be used to evaluate
     *            the build stability
     * @param unstableTotalAll
     *            annotation threshold
     * @param unstableTotalHigh
     *            annotation threshold
     * @param unstableTotalNormal
     *            annotation threshold
     * @param unstableTotalLow
     *            annotation threshold
     * @param unstableNewAll
     *            annotation threshold
     * @param unstableNewHigh
     *            annotation threshold
     * @param unstableNewNormal
     *            annotation threshold
     * @param unstableNewLow
     *            annotation threshold
     * @param failedTotalAll
     *            annotation threshold
     * @param failedTotalHigh
     *            annotation threshold
     * @param failedTotalNormal
     *            annotation threshold
     * @param failedTotalLow
     *            annotation threshold
     * @param failedNewAll
     *            annotation threshold
     * @param failedNewHigh
     *            annotation threshold
     * @param failedNewNormal
     *            annotation threshold
     * @param failedNewLow
     *            annotation threshold
     * @param canRunOnFailed
     *            determines whether the plug-in can run for failed builds, too
     * @param usePreviousBuildAsReference
     *            determines whether to always use the previous build as the reference build
     * @param useStableBuildAsReference
     *            determines whether only stable builds should be used as reference builds or not
     * @param canComputeNew
     *            determines whether new warnings should be computed (with
     *            respect to baseline)
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @DataBoundConstructor
    public PmdReporter(final String healthy, final String unHealthy, final String thresholdLimit, final boolean useDeltaValues,
            final String unstableTotalAll, final String unstableTotalHigh, final String unstableTotalNormal, final String unstableTotalLow,
            final String unstableNewAll, final String unstableNewHigh, final String unstableNewNormal, final String unstableNewLow,
            final String failedTotalAll, final String failedTotalHigh, final String failedTotalNormal, final String failedTotalLow,
            final String failedNewAll, final String failedNewHigh, final String failedNewNormal, final String failedNewLow,
            final boolean canRunOnFailed, final boolean usePreviousBuildAsReference,
            final boolean useStableBuildAsReference, final boolean canComputeNew,
            final String cutoffHighPriority, final String cutoffNormalPriority) {
        super(healthy, unHealthy, thresholdLimit, useDeltaValues,
                unstableTotalAll, unstableTotalHigh, unstableTotalNormal, unstableTotalLow,
                unstableNewAll, unstableNewHigh, unstableNewNormal, unstableNewLow,
                failedTotalAll, failedTotalHigh, failedTotalNormal, failedTotalLow,
                failedNewAll, failedNewHigh, failedNewNormal, failedNewLow,
                canRunOnFailed, usePreviousBuildAsReference, useStableBuildAsReference, canComputeNew, PLUGIN_NAME);

        pmdCutoff.cutoffHighPriority = cutoffHighPriority;
        pmdCutoff.cutoffNormalPriority = cutoffNormalPriority;
    }

    // CHECKSTYLE:ON

    public PmdCutoff getPmdCutoff() {
        return pmdCutoff;
    }

    @Override
    protected boolean acceptGoal(final String goal) {
        return "pmd".equals(goal) || "site".equals(goal) || "report".equals(goal) || "check".equals(goal);
    }

    @Override
    public ParserResult perform(final MavenBuildProxy build, final MavenProject pom, final MojoInfo mojo, final PluginLogger logger) throws InterruptedException, IOException {
        FilesParser pmdCollector = new FilesParser(PLUGIN_NAME, PMD_XML_FILE,
                new PmdParser(getDefaultEncoding(), pmdCutoff.cutoffHighPriority, pmdCutoff.cutoffNormalPriority), getModuleName(pom));

        return getTargetPath(pom).act(pmdCollector);
    }

    @Override
    protected PmdResult createResult(final MavenBuild build, final ParserResult project) {
        return new PmdReporterResult(build, getDefaultEncoding(), project,
                usePreviousBuildAsReference(), useOnlyStableBuildsAsReference());
    }

    @Override
    protected MavenAggregatedReport createMavenAggregatedReport(final MavenBuild build, final PmdResult result) {
        return new PmdMavenResultAction(build, this, getDefaultEncoding(), result);
    }

    @Override
    public List<PmdProjectAction> getProjectActions(final MavenModule module) {
        return Collections.singletonList(new PmdProjectAction(module, getResultActionClass()));
    }

    @Override
    protected Class<PmdMavenResultAction> getResultActionClass() {
        return PmdMavenResultAction.class;
    }

    /** Ant file-set pattern of files to work with. */
    @SuppressWarnings("PMD")
    @SuppressFBWarnings("")
    private transient String pattern; // obsolete since release 2.5

    /**
     * Initializes new fields that are not serialized yet.
     *
     * @return the object
     */
    protected Object readResolve() {
        if (pmdCutoff == null) {
            pmdCutoff = new PmdCutoff();
        }

        return this;
    }
}

