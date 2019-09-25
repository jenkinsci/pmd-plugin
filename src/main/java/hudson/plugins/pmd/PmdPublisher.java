package hudson.plugins.pmd;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import net.sf.json.JSONObject;

import hudson.FilePath;
import hudson.Launcher;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.FilesParser;
import hudson.plugins.analysis.core.HealthAwarePublisher;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.util.PluginLogger;
import hudson.plugins.pmd.parser.PmdParser;

import javax.annotation.CheckForNull;

/**
 * Publishes the results of the PMD analysis  (freestyle project type).
 *
 * @author Ulli Hafner
 */
public class PmdPublisher extends HealthAwarePublisher {
    private static final long serialVersionUID = 6711252664481150129L;

    private static final String PLUGIN_NAME = "PMD";

    /** Default PMD pattern. */
    private static final String DEFAULT_PATTERN = "**/pmd.xml";
    /** Ant file-set pattern of files to work with. */
    private String pattern;

    private PmdCutoff pmdCutoff = new PmdCutoff();

    /**
     * Constructor used from methods like {@link StaplerRequest#bindJSON(Class, JSONObject)} (Class, JSONObject)} and
     * {@link StaplerRequest#bindParameters(Class, String)}.
     */
     @DataBoundConstructor
     public PmdPublisher() {
        super(PLUGIN_NAME);
     }

    /**
     * Returns the Ant file-set pattern of files to work with.
     *
     * @return Ant file-set pattern of files to work with
     */
    public String getPattern() {
        return pattern;
    }

   /**
    * Sets the Ant file-set pattern of files to work with.
    *
    * @param pattern the pattern of files
    */
    @DataBoundSetter
    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }

    public PmdCutoff getPmdCutoff() {
        return pmdCutoff;
    }

    @CheckForNull
    public String getCutoffHighPriority() {
        return pmdCutoff.cutoffHighPriority;
    }

    @DataBoundSetter
    public void setCutoffHighPriority(String cutoffHighPriority) {
        pmdCutoff.cutoffHighPriority = cutoffHighPriority;
    }

    @CheckForNull
    public String getCutoffNormalPriority() {
        return pmdCutoff.cutoffNormalPriority;
    }

    @DataBoundSetter
    public void setCutoffNormalPriority(String cutoffNormalPriority) {
        pmdCutoff.cutoffNormalPriority = cutoffNormalPriority;
    }

    @Override
    public BuildResult perform(final Run<?, ?> build, final FilePath workspace, final PluginLogger logger) throws
            InterruptedException, IOException {
        logger.log("Collecting PMD analysis files...");
        FilesParser parser = new FilesParser(PLUGIN_NAME,
                StringUtils.defaultIfEmpty(expandFilePattern(getPattern(), build.getEnvironment(TaskListener.NULL)), DEFAULT_PATTERN),
                new PmdParser(getDefaultEncoding(), pmdCutoff.cutoffHighPriority, pmdCutoff.cutoffNormalPriority), shouldDetectModules(), isMavenBuild(build));
        ParserResult project = workspace.act(parser);
        logger.logLines(project.getLogMessages());

        blame(project.getAnnotations(), build, workspace);

        PmdResult result = new PmdResult(build, getDefaultEncoding(), project,
                usePreviousBuildAsReference(), useOnlyStableBuildsAsReference());
        build.addAction(new PmdResultAction(build, this, result));

        return result;
    }

    @Override
    public PmdDescriptor getDescriptor() {
        return (PmdDescriptor)super.getDescriptor();
    }

    @Override
    public MatrixAggregator createAggregator(final MatrixBuild build, final Launcher launcher,
            final BuildListener listener) {
        return new PmdAnnotationsAggregator(build, launcher, listener, this,
                getDefaultEncoding(), usePreviousBuildAsReference(), useOnlyStableBuildsAsReference());
    }
}
