package hudson.plugins.pmd;

import hudson.maven.AbstractMavenProject;
import hudson.model.AbstractProject;
import hudson.plugins.pmd.util.ThresholdValidator;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.FormFieldValidator;

import java.io.IOException;

import javax.servlet.ServletException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Descriptor for the class {@link PmdPublisher}. Used as a singleton. The
 * class is marked as public so that it can be accessed from views.
 *
 * @author Ulli Hafner
 */
public final class PmdDescriptor extends BuildStepDescriptor<Publisher> {
    /** Icon to use for the result and project action. */
    public static final String PMD_ACTION_LOGO = "/plugin/pmd/icons/pmd-24x24.gif";

    /**
     * Instantiates a new find bugs descriptor.
     */
    PmdDescriptor() {
        super(PmdPublisher.class);
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return Messages.PMD_Publisher_Name();
    }

    /** {@inheritDoc} */
    @Override
    public String getHelpFile() {
        return "/plugin/pmd/help.html";
    }

    /**
     * Performs on-the-fly validation on the file mask.
     *
     * @param request
     *            Stapler request
     * @param response
     *            Stapler response
     */
    public void doCheckPattern(final StaplerRequest request, final StaplerResponse response)
            throws IOException, ServletException {
        new FormFieldValidator.WorkspaceFileMask(request, response).process();
    }

    /**
     * Performs on-the-fly validation on the bugs threshold.
     *
     * @param request
     *            Stapler request
     * @param response
     *            Stapler response
     */
    public void doCheckThreshold(final StaplerRequest request, final StaplerResponse response)
            throws IOException, ServletException {
        new ThresholdValidator(request, response).process();
    }

    /** {@inheritDoc} */
    @Override
    public PmdPublisher newInstance(final StaplerRequest request) throws FormException {
        return request.bindParameters(PmdPublisher.class, "pmd_");
    }

    /** {@inheritDoc} */
    @Override
    public boolean isApplicable(final Class<? extends AbstractProject> jobType) {
        return !AbstractMavenProject.class.isAssignableFrom(jobType);
    }
}