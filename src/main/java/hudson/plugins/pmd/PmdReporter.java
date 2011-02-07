package hudson.plugins.pmd;

import hudson.maven.MavenBuildProxy;
import hudson.maven.MojoInfo;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.model.Action;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.FilesParser;
import hudson.plugins.analysis.core.HealthAwareMavenReporter;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.util.PluginLogger;
import hudson.plugins.pmd.parser.PmdParser;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Publishes the results of the PMD analysis  (maven 2 project type).
 *
 * @author Ulli Hafner
 */
public class PmdReporter extends HealthAwareMavenReporter {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = 2272875032054063496L;

    /** Default PMD pattern. */
    private static final String PMD_XML_FILE = "pmd.xml";
    /** Ant file-set pattern of files to work with. */
    @SuppressWarnings("unused")
    private String pattern; // obsolete since release 2.5

    /**
     * Creates a new instance of <code>PmdReporter</code>.
     *
     * @param threshold
     *            Annotation threshold to be reached if a build should be considered as
     *            unstable.
     * @param newThreshold
     *            New annotations threshold to be reached if a build should be
     *            considered as unstable.
     * @param failureThreshold
     *            Annotation threshold to be reached if a build should be considered as
     *            failure.
     * @param newFailureThreshold
     *            New annotations threshold to be reached if a build should be
     *            considered as failure.
     * @param healthy
     *            Report health as 100% when the number of warnings is less than
     *            this value
     * @param unHealthy
     *            Report health as 0% when the number of warnings is greater
     *            than this value
     * @param thresholdLimit
     *            determines which warning priorities should be considered when
     *            evaluating the build stability and health
     * @param canRunOnFailed
     *            determines whether the plug-in can run for failed builds, too
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @DataBoundConstructor
    public PmdReporter(final String threshold, final String newThreshold,
            final String failureThreshold, final String newFailureThreshold,
            final String healthy, final String unHealthy, final String thresholdLimit, final boolean canRunOnFailed) {
        super(threshold, newThreshold, failureThreshold, newFailureThreshold,
                healthy, unHealthy, thresholdLimit, canRunOnFailed, "PMD");
    }
    // CHECKSTYLE:ON

    /** {@inheritDoc} */
    @Override
    protected boolean acceptGoal(final String goal) {
        return "pmd".equals(goal) || "site".equals(goal);
    }

    /** {@inheritDoc} */
    @Override
    public ParserResult perform(final MavenBuildProxy build, final MavenProject pom, final MojoInfo mojo, final PluginLogger logger) throws InterruptedException, IOException {
        FilesParser pmdCollector = new FilesParser(logger, PMD_XML_FILE,
                new PmdParser(getDefaultEncoding()), getModuleName(pom));

        return getTargetPath(pom).act(pmdCollector);
    }

    /** {@inheritDoc} */
    @Override
    protected BuildResult persistResult(final ParserResult project, final MavenBuild build) {
        PmdResult result = new PmdResult(build, getDefaultEncoding(), project);
        build.getActions().add(new MavenPmdResultAction(build, this, getDefaultEncoding(), result));
        build.registerAsProjectAction(PmdReporter.this);

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<PmdProjectAction> getProjectActions(final MavenModule module) {
        return Collections.singletonList(new PmdProjectAction(module));
    }

    /** {@inheritDoc} */
    @Override
    protected Class<? extends Action> getResultActionClass() {
        return MavenPmdResultAction.class;
    }
}

