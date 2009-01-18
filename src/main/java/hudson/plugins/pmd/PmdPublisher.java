package hudson.plugins.pmd;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Descriptor;
import hudson.plugins.pmd.parser.PmdParser;
import hudson.plugins.pmd.util.FilesParser;
import hudson.plugins.pmd.util.HealthAwarePublisher;
import hudson.plugins.pmd.util.ParserResult;
import hudson.tasks.Publisher;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Publishes the results of the PMD analysis  (freestyle project type).
 *
 * @author Ulli Hafner
 */
public class PmdPublisher extends HealthAwarePublisher {
    /** Unique ID of this class. */
    private static final long serialVersionUID = 6711252664481150129L;
    /** Default PMD pattern. */
    private static final String DEFAULT_PATTERN = "**/pmd.xml";
    /** Descriptor of this publisher. */
    public static final PmdDescriptor PMD_DESCRIPTOR = new PmdDescriptor();
    /** Ant file-set pattern of files to work with. */
    private final String pattern;

    /**
     * Creates a new instance of <code>PmdPublisher</code>.
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
     * @param height
     *            the height of the trend graph
     * @param thresholdLimit
     *            determines which warning priorities should be considered when
     *            evaluating the build stability and health
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @DataBoundConstructor
    public PmdPublisher(final String pattern, final String threshold, final String healthy, final String unHealthy,
            final String height, final String thresholdLimit, final String defaultEncoding) {
        super(threshold, healthy, unHealthy, height, thresholdLimit, defaultEncoding, "PMD");
        this.pattern = pattern;
    }
    // CHECKSTYLE:ON

    /**
     * Returns the Ant file-set pattern of files to work with.
     *
     * @return Ant file-set pattern of files to work with
     */
    public String getPattern() {
        return pattern;
    }

    /** {@inheritDoc} */
    @Override
    public Action getProjectAction(final AbstractProject<?, ?> project) {
        return new PmdProjectAction(project, getTrendHeight());
    }

    /** {@inheritDoc} */
    @Override
    public ParserResult perform(final AbstractBuild<?, ?> build, final PrintStream logger) throws InterruptedException, IOException {
        log(logger, "Collecting pmd analysis files...");
        FilesParser pmdCollector = new FilesParser(logger, StringUtils.defaultIfEmpty(getPattern(), DEFAULT_PATTERN), new PmdParser(getDefaultEncoding()),
                isMavenBuild(build), isAntBuild(build));

        ParserResult project = build.getProject().getWorkspace().act(pmdCollector);
        PmdResult result = new PmdResultBuilder().build(build, project, getDefaultEncoding());
        build.getActions().add(new PmdResultAction(build, this, result));

        return project;
    }

    /** {@inheritDoc} */
    public Descriptor<Publisher> getDescriptor() {
        return PMD_DESCRIPTOR;
    }
}
