package hudson.plugins.pmd;

import hudson.model.AbstractBuild;
import hudson.plugins.pmd.util.ParserResult;

/**
 * Creates a new PMD result based on the values of a previous build and the
 * current project.
 *
 * @author Ulli Hafner
 */
public class PmdResultBuilder {
    /**
     * Creates a result that persists the PMD information for the
     * specified build.
     *
     * @param build
     *            the build to create the action for
     * @param result
     *            the result containing the annotations
     * @return the result action
     */
    public PmdResult build(final AbstractBuild<?, ?> build, final ParserResult result) {
        Object previous = build.getPreviousBuild();
        while (previous instanceof AbstractBuild<?, ?> && previous != null) {
            AbstractBuild<?, ?> previousBuild = (AbstractBuild<?, ?>)previous;
            PmdResultAction previousAction = previousBuild.getAction(PmdResultAction.class);
            if (previousAction != null) {
                return new PmdResult(build, result, previousAction.getResult());
            }
            previous = previousBuild.getPreviousBuild();
        }
        return new PmdResult(build, result);
    }
}

