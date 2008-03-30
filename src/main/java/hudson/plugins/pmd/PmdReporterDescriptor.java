package hudson.plugins.pmd;

import hudson.maven.MavenReporter;
import hudson.maven.MavenReporterDescriptor;

import org.kohsuke.stapler.StaplerRequest;

/**
 * Descriptor for the class {@link PmdReporter}. Used as a singleton. The
 * class is marked as public so that it can be accessed from views.
 *
 * @author Ulli Hafner
 */
public class PmdReporterDescriptor extends MavenReporterDescriptor {
    /**
     * Creates a new instance of <code>PmdReporterDescriptor</code>.
     */
    public PmdReporterDescriptor() {
        super(PmdReporter.class);
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return Messages.PMD_Publisher_Name();
    }

    /** {@inheritDoc} */
    @Override
    public String getConfigPage() {
        return getViewPage(PmdPublisher.class, "config.jelly");
    }

    /** {@inheritDoc} */
    @Override
    public String getHelpFile() {
        return "/plugin/pmd/help.html";
    }

    /** {@inheritDoc} */
    @Override
    public MavenReporter newInstance(final StaplerRequest request) throws FormException {
        return request.bindParameters(PmdReporter.class, "pmd_");
    }
}

