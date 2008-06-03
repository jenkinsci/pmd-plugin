package hudson.plugins.pmd;

import hudson.maven.MavenReporter;
import hudson.plugins.pmd.util.PluginDescriptor;
import hudson.plugins.pmd.util.ReporterDescriptor;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

/**
 * Descriptor for the class {@link PmdReporter}. Used as a singleton. The
 * class is marked as public so that it can be accessed from views.
 *
 * @author Ulli Hafner
 */
public class PmdReporterDescriptor extends ReporterDescriptor {
    /**
     * Creates a new instance of <code>PmdReporterDescriptor</code>.
     *
     * @param pluginDescriptor
     *            the plug-in descriptor of the publisher
     */
    public PmdReporterDescriptor(final PluginDescriptor pluginDescriptor) {
        super(PmdReporter.class, pluginDescriptor);
    }

    /** {@inheritDoc} */
    @Override
    public MavenReporter newInstance(final StaplerRequest request, final JSONObject formData) throws FormException {
        return request.bindJSON(PmdReporter.class, formData);
    }
}

