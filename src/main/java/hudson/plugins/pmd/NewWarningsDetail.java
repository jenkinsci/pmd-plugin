package hudson.plugins.pmd;

import hudson.model.AbstractBuild;
import hudson.plugins.pmd.util.AbstractAnnotationsDetail;
import hudson.plugins.pmd.util.SourceDetail;
import hudson.plugins.pmd.util.model.FileAnnotation;

import java.util.Set;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Result object to visualize the new warnings in a build.
 */
public class NewWarningsDetail extends AbstractAnnotationsDetail {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = 5093487322493056475L;

    /**
     * Creates a new instance of <code>FixedWarningsDetail</code>.
     *
     * @param owner
     *            the current build as owner of this action
     * @param newWarnings
     *            all new warnings in this build
     */
    public NewWarningsDetail(final AbstractBuild<?, ?> owner, final Set<FileAnnotation> newWarnings) {
        super(owner, newWarnings);
    }

    /** {@inheritDoc} */
    public String getDisplayName() {
        return Messages.PMD_NewWarningsDetail_Name();
    }

    /**
     * Returns the dynamic result of the PMD analysis (detail page for a package).
     *
     * @param link the package name to get the result for
     * @param request
     *            Stapler request
     * @param response
     *            Stapler response
     * @return the dynamic result of the PMD analysis (detail page for a package).
     */
    public Object getDynamic(final String link, final StaplerRequest request, final StaplerResponse response) {
        return new SourceDetail(getOwner(), getAnnotation(link));
    }
}

