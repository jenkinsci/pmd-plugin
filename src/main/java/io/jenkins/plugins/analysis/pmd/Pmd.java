package io.jenkins.plugins.analysis.pmd;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.kohsuke.stapler.DataBoundConstructor;

import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.IssueBuilder;
import edu.hm.hafner.analysis.Issues;
import io.jenkins.plugins.analysis.core.steps.DefaultLabelProvider;
import io.jenkins.plugins.analysis.core.steps.StaticAnalysisTool;

import hudson.Extension;
import hudson.plugins.pmd.Messages;
import hudson.plugins.pmd.PmdDescriptor;
import hudson.plugins.pmd.parser.PmdMessages;

/**
 * Provides a parser and customized messages for PMD.
 *
 * @author Ullrich Hafner
 */
public class Pmd extends StaticAnalysisTool {
    /**
     * Creates a new instance of {@link Pmd}.
     */
    @DataBoundConstructor
    public Pmd() {
        // empty constructor required for stapler
    }

    @Override
    public Issues<Issue> parse(final File file, final IssueBuilder builder) throws InvocationTargetException {
        return new PmdParser().parse(file, builder);
    }

    /** Registers this tool as extension point implementation. */
    @Extension
    public static final class Descriptor extends StaticAnalysisToolDescriptor {
        public Descriptor() {
            super(new PmdLabelProvider());
        }

    }

    static class PmdLabelProvider extends DefaultLabelProvider {
        PmdLabelProvider() {
            super(PmdDescriptor.PLUGIN_ID);
        }

        @Override
        public String getName() {
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
        public String getDescription(final Issue issue) {
            return PmdMessages.getInstance().getMessage(issue.getCategory(), issue.getType());
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
    }
}
