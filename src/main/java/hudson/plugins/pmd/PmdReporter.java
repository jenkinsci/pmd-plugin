package hudson.plugins.pmd;

import hudson.FilePath;
import hudson.maven.MavenBuild;
import hudson.maven.MavenBuildProxy;
import hudson.maven.MavenModule;
import hudson.maven.MavenReporter;
import hudson.maven.MavenReporterDescriptor;
import hudson.maven.MojoInfo;
import hudson.maven.MavenBuildProxy.BuildCallable;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.plugins.pmd.parser.PmdCollector;
import hudson.plugins.pmd.util.HealthReportBuilder;
import hudson.plugins.pmd.util.model.JavaProject;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.project.MavenProject;

// FIXME: this class more or less is a copy of the PmdPublisher, we should find a way to generalize portions of this class
public class PmdReporter extends MavenReporter {
    /** Descriptor of this publisher. */
    public static final PmdReporterDescriptor PMD_SCANNER_DESCRIPTOR = new PmdReporterDescriptor(PmdPublisher.PMD_DESCRIPTOR);
    /** Default PMD pattern. */
    private static final String DEFAULT_PATTERN = "**/pmd.xml";
    /** Ant file-set pattern of files to work with. */
    private final String pattern;
    /** Annotation threshold to be reached if a build should be considered as unstable. */
    private final String threshold;
    /** Determines whether to use the provided threshold to mark a build as unstable. */
    private boolean thresholdEnabled;
    /** Integer threshold to be reached if a build should be considered as unstable. */
    private int minimumAnnotations;
    /** Report health as 100% when the number of warnings is less than this value. */
    private final String healthy;
    /** Report health as 0% when the number of warnings is greater than this value. */
    private final String unHealthy;
    /** Report health as 100% when the number of warnings is less than this value. */
    private int healthyAnnotations;
    /** Report health as 0% when the number of warnings is greater than this value. */
    private int unHealthyAnnotations;
    /** Determines whether to use the provided healthy thresholds. */
    private boolean healthyReportEnabled;

    /**
     * Creates a new instance of <code>PmdReporter</code>.
     *
     * @param pattern
     *            Ant file-set pattern to scan for PMD files
     * @param threshold
     *            Bug threshold to be reached if a build should be considered as
     *            unstable.
     * @param healthy
     *            Report health as 100% when the number of warnings is less than
     *            this value
     * @param unHealthy
     *            Report health as 0% when the number of warnings is greater
     *            than this value
     * @stapler-constructor
     */
    public PmdReporter(final String pattern, final String threshold, final String healthy, final String unHealthy) {
        super();
        this.threshold = threshold;
        this.healthy = healthy;
        this.unHealthy = unHealthy;
        this.pattern = pattern;

        if (!StringUtils.isEmpty(threshold)) {
            try {
                minimumAnnotations = Integer.valueOf(threshold);
                if (minimumAnnotations >= 0) {
                    thresholdEnabled = true;
                }
            }
            catch (NumberFormatException exception) {
                // nothing to do, we use the default value
            }
        }
        if (!StringUtils.isEmpty(healthy) && !StringUtils.isEmpty(unHealthy)) {
            try {
                healthyAnnotations = Integer.valueOf(healthy);
                unHealthyAnnotations = Integer.valueOf(unHealthy);
                if (healthyAnnotations >= 0 && unHealthyAnnotations > healthyAnnotations) {
                    healthyReportEnabled = true;
                }
            }
            catch (NumberFormatException exception) {
                // nothing to do, we use the default value
            }
        }
    }

    /**
     * Returns the Bug threshold to be reached if a build should be considered as unstable.
     *
     * @return the bug threshold
     */
    public String getThreshold() {
        return threshold;
    }

    /**
     * Returns the healthy threshold.
     *
     * @return the healthy
     */
    public String getHealthy() {
        return healthy;
    }

    /**
     * Returns the unhealthy threshold.
     *
     * @return the unHealthy
     */
    public String getUnHealthy() {
        return unHealthy;
    }

    /**
     * Returns the Ant file-set pattern to the workspace files.
     *
     * @return ant file-set pattern to the workspace files.
     */
    public String getPattern() {
        return pattern;
    }

    /** {@inheritDoc} */
    @Override
    public boolean postExecute(final MavenBuildProxy build, final MavenProject pom, final MojoInfo mojo,
            final BuildListener listener, final Throwable error) throws InterruptedException, IOException {
        if (!"pmd".equals(mojo.getGoal())) {
            return true;
        }

        FilePath pomPath = new FilePath(pom.getBasedir());
        PmdCollector pmdCollector = new PmdCollector(listener.getLogger(),
                build.getTimestamp().getTimeInMillis(),
                StringUtils.defaultIfEmpty(getPattern(), DEFAULT_PATTERN));
        final JavaProject project = pomPath.act(pmdCollector);

        build.execute(new BuildCallable<Void, IOException>() {
            public Void call(final MavenBuild build) throws IOException, InterruptedException {
                PmdResult result = new PmdResultBuilder().build(build, project);
                HealthReportBuilder healthReportBuilder = new HealthReportBuilder(thresholdEnabled, minimumAnnotations,
                        healthyReportEnabled, healthyAnnotations, unHealthyAnnotations,
                        Messages.PMD_ResultAction_HealthReportSingleItem(),
                        Messages.PMD_ResultAction_HealthReportMultipleItem("%d"));
                build.getActions().add(new PmdResultAction(build, result, healthReportBuilder));
                build.registerAsProjectAction(PmdReporter.this);

                return null;
            }
        });

        int warnings = project.getNumberOfAnnotations();
        if (warnings > 0) {
            listener.getLogger().println(
                    "A total of " + warnings + " warnings have been found.");
            if (thresholdEnabled && warnings >= minimumAnnotations) {
                build.setResult(Result.UNSTABLE);
            }
        }
        else {
            listener.getLogger().println("No warnings have been found.");
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Action getProjectAction(final MavenModule module) {
        return new PmdProjectAction(module);
    }

    /** {@inheritDoc} */
    @Override
    public MavenReporterDescriptor getDescriptor() {
        return PMD_SCANNER_DESCRIPTOR;
    }
}

