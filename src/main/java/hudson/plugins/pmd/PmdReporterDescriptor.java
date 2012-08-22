package hudson.plugins.pmd;

import hudson.Extension;
import hudson.plugins.analysis.core.ReporterDescriptor;

/**
 * Descriptor for the class {@link PmdReporter}. Used as a singleton. The
 * class is marked as public so that it can be accessed from views.
 *
 * @author Ulli Hafner
 */
@Extension(ordinal = 100, optional = true) // NOCHECKSTYLE
public class PmdReporterDescriptor extends ReporterDescriptor {
    /**
     * Creates a new instance of <code>PmdReporterDescriptor</code>.
     */
    public PmdReporterDescriptor() {
        super(PmdReporter.class, new PmdDescriptor());
    }
}

