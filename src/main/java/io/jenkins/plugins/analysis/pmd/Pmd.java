package io.jenkins.plugins.analysis.pmd;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.kohsuke.stapler.DataBoundConstructor;

import io.jenkins.plugins.analysis.core.steps.StaticAnalysisTool;

import hudson.Extension;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.pmd.Messages;
import hudson.plugins.pmd.PmdDescriptor;
import hudson.plugins.pmd.parser.PmdParser;

/**
 * Provides customized messages for PMD.
 *
 * @author Ullrich Hafner
 */
@Extension
public class Pmd extends StaticAnalysisTool {
    /**
     * Creates a new instance of {@link Pmd}.
     */
    @DataBoundConstructor
    public Pmd() {
        super(PmdDescriptor.PLUGIN_ID);
    }

    @Override
    public Collection<FileAnnotation> parse(final File file, final String moduleName) throws InvocationTargetException {
        return new PmdParser().parse(file, moduleName);
    }

    @Override
    protected String getName() {
        return "PMD";
    }

    @Override
    public String getLinkName() {
        return Messages.PMD_ProjectAction_Name();
    }

    @Override
    public String getTrendName() {
        return Messages.PMD_Trend_Name();
    }

    @Override
    public String getSmallIconUrl() {
        return get().getIconUrl();
    }

    private PmdDescriptor get() {
        return new PmdDescriptor();
    }

    @Override
    public String getLargeIconUrl() {
        return get().getSummaryIconUrl();
    }

    /** Descriptor for PMD. */
    @Extension
    public static final class Descriptor extends StaticAnalysisToolDescriptor {
        public Descriptor() {
            super(Pmd.class);
        }
    }
}
