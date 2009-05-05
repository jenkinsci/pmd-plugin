package hudson.plugins.pmd;

import hudson.maven.MavenBuild;
import hudson.maven.MavenBuildProxy;
import hudson.maven.MavenModule;
import hudson.maven.MavenReporterDescriptor;
import hudson.maven.MojoInfo;
import hudson.model.Action;
import hudson.plugins.pmd.parser.PmdParser;
import hudson.plugins.pmd.util.BuildResult;
import hudson.plugins.pmd.util.FilesParser;
import hudson.plugins.pmd.util.HealthAwareMavenReporter;
import hudson.plugins.pmd.util.ParserResult;
import hudson.plugins.pmd.util.PluginLogger;

import java.io.IOException;

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
    /** Descriptor of this publisher. */
    public static final PmdReporterDescriptor PMD_SCANNER_DESCRIPTOR = new PmdReporterDescriptor(PmdPublisher.PMD_DESCRIPTOR);
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
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @DataBoundConstructor
    public PmdReporter(final String threshold, final String newThreshold,
            final String failureThreshold, final String newFailureThreshold,
            final String healthy, final String unHealthy, final String thresholdLimit) {
        super(threshold, newThreshold, failureThreshold, newFailureThreshold,
                healthy, unHealthy, thresholdLimit, "PMD");
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
        FilesParser pmdCollector = new FilesParser(logger, PMD_XML_FILE, new PmdParser(getDefaultEncoding()), true, false);

        return getTargetPath(pom).act(pmdCollector);
    }

    /** {@inheritDoc} */
    @Override
    protected BuildResult persistResult(final ParserResult project, final MavenBuild build) {
        PmdResult result = new PmdResultBuilder().build(build, project, getDefaultEncoding());
        build.getActions().add(new MavenPmdResultAction(build, this, getDefaultEncoding(), result));
        build.registerAsProjectAction(PmdReporter.this);

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public Action getProjectAction(final MavenModule module) {
        return new PmdProjectAction(module);
    }

    /** {@inheritDoc} */
    @Override
    protected Class<? extends Action> getResultActionClass() {
        return MavenPmdResultAction.class;
    }

    /** {@inheritDoc} */
    @Override
    public MavenReporterDescriptor getDescriptor() {
        return PMD_SCANNER_DESCRIPTOR;
    }
}

