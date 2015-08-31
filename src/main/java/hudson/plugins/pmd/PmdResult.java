package hudson.plugins.pmd;

import com.thoughtworks.xstream.XStream;

import hudson.model.Run;
import hudson.plugins.analysis.core.BuildHistory;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.core.ResultAction;
import hudson.plugins.pmd.parser.Bug;

/**
 * Represents the results of the PMD analysis. One instance of this class is persisted for
 * each build via an XML file.
 *
 * @author Ulli Hafner
 */
public class PmdResult extends BuildResult {
    private static final long serialVersionUID = 2768250056765266658L;

    /**
     * Creates a new instance of {@link PmdResult}.
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
    public PmdResult(final Run<?, ?> build, final String defaultEncoding, final ParserResult result,
                     final boolean usePreviousBuildAsReference, final boolean useStableBuildAsReference) {
        this(build, defaultEncoding, result, usePreviousBuildAsReference, useStableBuildAsReference, PmdResultAction.class);
    }

    /**
     * Creates a new instance of {@link PmdResult}.
     *
     * @param build the current build as owner of this action
     * @param defaultEncoding the default encoding to be used when reading and parsing files
     * @param result the parsed result with all annotations
     * @param usePreviousBuildAsReference the value of usePreviousBuildAsReference
     * @param useStableBuildAsReference determines whether only stable builds should be used as reference builds or not
     * @param actionType the type of the result action
     */
    protected PmdResult(final Run<?, ?> build,
                        final String defaultEncoding, final ParserResult result,
                        final boolean usePreviousBuildAsReference,
                        final boolean useStableBuildAsReference,
                        final Class<? extends ResultAction<PmdResult>> actionType) {
        this(build, new BuildHistory(build, actionType, usePreviousBuildAsReference, useStableBuildAsReference), result, defaultEncoding, true);
    }

    PmdResult(final Run<?, ?> build, final BuildHistory history,
              final ParserResult result, final String defaultEncoding, final boolean canSerialize) {
        super(build, history, result, defaultEncoding);

        if (canSerialize) {
            serializeAnnotations(result.getAnnotations());
        }
    }

    @Override
    public String getHeader() {
        return Messages.PMD_ResultAction_Header();
    }

    @Override
    protected void configure(final XStream xstream) {
        xstream.alias("bug", Bug.class);
    }

    @Override
    public String getSummary() {
        return "PMD: " + createDefaultSummary(PmdDescriptor.RESULT_URL, getNumberOfAnnotations(), getNumberOfModules());
    }

    @Override
    protected String createDeltaMessage() {
        return createDefaultDeltaMessage(PmdDescriptor.RESULT_URL, getNumberOfNewWarnings(), getNumberOfFixedWarnings());
    }

    @Override
    protected String getSerializationFileName() {
        return "pmd-warnings.xml";
    }

    @Override
    public String getDisplayName() {
        return Messages.PMD_ProjectAction_Name();
    }

    @Override
    protected Class<? extends ResultAction<? extends BuildResult>> getResultActionType() {
        return PmdResultAction.class;
    }
}
