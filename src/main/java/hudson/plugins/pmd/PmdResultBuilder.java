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
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @return the result action
     */
    public PmdResult build(final AbstractBuild<?, ?> build, final ParserResult result, final String defaultEncoding) {
        Object previous = build.getPreviousBuild();
        while (previous instanceof AbstractBuild<?, ?>) {
            AbstractBuild<?, ?> previousBuild = (AbstractBuild<?, ?>)previous;
            PmdResultAction previousAction = previousBuild.getAction(PmdResultAction.class);
            if (previousAction != null) {
                return new PmdResult(build, defaultEncoding, result, previousAction.getResult());
            }
            previous = previousBuild.getPreviousBuild();
        }
        return new PmdResult(build, defaultEncoding, result);
    }

    /**
     * Creates a result that persists the PMD information for the
     * specified m2 build.
     *
     * @param build
     *            the build to create the action for
     * @param result
     *            the result containing the annotations
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @return the result action
     */
    public PmdMavenResult buildMaven(final AbstractBuild<?, ?> build, final ParserResult result, final String defaultEncoding) {
        Object previous = build.getPreviousBuild();
        while (previous instanceof AbstractBuild<?, ?>) {
            AbstractBuild<?, ?> previousBuild = (AbstractBuild<?, ?>)previous;
            MavenPmdResultAction previousAction = previousBuild.getAction(MavenPmdResultAction.class);
            if (previousAction != null) {
                return new PmdMavenResult(build, defaultEncoding, result, previousAction.getResult());
            }
            previous = previousBuild.getPreviousBuild();
        }
        return new PmdMavenResult(build, defaultEncoding, result);
    }
}

