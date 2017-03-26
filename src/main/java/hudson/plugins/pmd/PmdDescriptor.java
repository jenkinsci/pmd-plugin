package hudson.plugins.pmd;

import org.jenkinsci.Symbol;

import hudson.Extension;
import hudson.plugins.analysis.core.PluginDescriptor;

/**
 * Descriptor for the class {@link PmdPublisher}. Used as a singleton. The
 * class is marked as public so that it can be accessed from views.
 *
 * @author Ulli Hafner
 */
@Extension(ordinal = 100) @Symbol("pmd")
public final class PmdDescriptor extends PluginDescriptor {
    private static final String ICONS_PREFIX = "/plugin/pmd/icons/";
    /** The ID of this plug-in is used as URL. */
    static final String PLUGIN_ID = "pmd";
    /** The URL of the result action. */
    static final String RESULT_URL = PluginDescriptor.createResultUrlName(PLUGIN_ID);
    /** Icon to use for the result and project action. */
    static final String ICON_URL = ICONS_PREFIX + "pmd-24x24.png";

    /**
     * Creates a new instance of {@link PmdDescriptor}.
     */
    public PmdDescriptor() {
        super(PmdPublisher.class);
    }

    @Override
    public String getDisplayName() {
        return Messages.PMD_Publisher_Name();
    }

    @Override
    public String getPluginName() {
        return PLUGIN_ID;
    }

    @Override
    public String getIconUrl() {
        return ICON_URL;
    }

    @Override
    public String getSummaryIconUrl() {
        return ICONS_PREFIX + "pmd-48x48.png";
    }
}