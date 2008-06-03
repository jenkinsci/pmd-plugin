package hudson.plugins.pmd;

import hudson.plugins.pmd.util.PluginDescriptor;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

/**
 * Descriptor for the class {@link PmdPublisher}. Used as a singleton. The
 * class is marked as public so that it can be accessed from views.
 *
 * @author Ulli Hafner
 */
public final class PmdDescriptor extends PluginDescriptor {
    /** Plug-in name. */
    private static final String PLUGIN_NAME = "pmd";
    /** Icon to use for the result and project action. */
    private static final String ACTION_ICON = "/plugin/pmd/icons/pmd-24x24.gif";

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
    public String getPluginName() {
        return PLUGIN_NAME;
    }

    /** {@inheritDoc} */
    @Override
    public String getIconUrl() {
        return ACTION_ICON;
    }

    /** {@inheritDoc} */
    @Override
    public PmdPublisher newInstance(final StaplerRequest request, final JSONObject formData) throws FormException {
        return request.bindJSON(PmdPublisher.class, formData);
    }
}