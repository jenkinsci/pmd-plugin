package hudson.plugins.pmd;

import hudson.model.Run;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.core.ResultAction;

/**
 * Represents the aggregated results of the PMD analysis in maven jobs.
 *
 * @author Ulli Hafner
 */
public class PmdReporterResult extends PmdResult {
    private static final long serialVersionUID = 498726255763220019L;

    /**
     * Creates a new instance of {@link PmdReporterResult}.
     *
     * @param build
     *            the current build as owner of this action
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param result
     *            the parsed result with all annotations
     * @param usePreviousBuildAsReference
     *            determines whether to use the previous build as the reference
     *            build
     * @param useStableBuildAsReference
     *            determines whether only stable builds should be used as
     *            reference builds or not
     */
    public PmdReporterResult(final Run<?, ?> build, final String defaultEncoding, final ParserResult result,
                             final boolean usePreviousBuildAsReference, final boolean useStableBuildAsReference) {
        super(build, defaultEncoding, result, usePreviousBuildAsReference, useStableBuildAsReference,
                PmdMavenResultAction.class);
    }

    @Override
    protected Class<? extends ResultAction<? extends BuildResult>> getResultActionType() {
        return PmdMavenResultAction.class;
    }
}

