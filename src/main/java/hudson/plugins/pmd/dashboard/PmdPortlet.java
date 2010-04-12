package hudson.plugins.pmd.dashboard;

import hudson.plugins.analysis.core.AbstractProjectAction;
import hudson.plugins.analysis.dashboard.AbstractWarningsGraphPortlet;
import hudson.plugins.pmd.PmdProjectAction;

/**
 * A base class for portlets of the PMD plug-in.
 *
 * @author Ulli Hafner
 */
public abstract class PmdPortlet extends AbstractWarningsGraphPortlet {
    /**
     * Creates a new instance of {@link PmdPortlet}.
     *
     * @param name
     *            the name of the portlet
     */
    public PmdPortlet(final String name) {
        super(name);
    }

    /** {@inheritDoc} */
    @Override
    protected Class<? extends AbstractProjectAction<?>> getAction() {
        return PmdProjectAction.class;
    }

    /** {@inheritDoc} */
    @Override
    protected String getPluginName() {
        return "pmd";
    }
}
