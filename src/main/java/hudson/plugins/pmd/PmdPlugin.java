package hudson.plugins.pmd;

import hudson.Plugin;
import hudson.plugins.pmd.parser.PmdMessages;

/**
 * Initializes the PMD messages and descriptions.
 *
 * @author Ulli Hafner
 */
public class PmdPlugin extends Plugin {
    @Override
    public void start() {
        PmdMessages.getInstance().initialize();
    }
}
