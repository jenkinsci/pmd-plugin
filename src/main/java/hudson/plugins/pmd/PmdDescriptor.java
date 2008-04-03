package hudson.plugins.pmd;

import hudson.plugins.pmd.util.PluginDescriptor;

import org.kohsuke.stapler.StaplerRequest;

/**
 * Descriptor for the class {@link PmdPublisher}. Used as a singleton. The
 * class is marked as public so that it can be accessed from views.
 *
 * @author Ulli Hafner
 */
public final class PmdDescriptor extends PluginDescriptor {
    /** Plug-in name. */
    static final String PLUGIN_NAME = "pmd";
    /** Icon to use for the result and project action. */
    static final String PMD_ACTION_LOGO = "/plugin/pmd/icons/pmd-24x24.gif";

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
    protected String getPluginName() {
        return PLUGIN_NAME;
    }

    /** {@inheritDoc} */
    @Override
    public PmdPublisher newInstance(final StaplerRequest request) throws FormException {
        return request.bindParameters(PmdPublisher.class, PLUGIN_NAME + "_");
    }
}