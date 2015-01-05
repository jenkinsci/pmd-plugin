package hudson.plugins.pmd;

import hudson.Launcher;
import hudson.matrix.MatrixRun;
import hudson.matrix.MatrixBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.plugins.analysis.core.AnnotationsAggregator;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.ParserResult;

/**
 * Aggregates {@link PmdResultAction}s of {@link MatrixRun}s into
 * {@link MatrixBuild}.
 *
 * @author Ulli Hafner
 */
public class PmdAnnotationsAggregator extends AnnotationsAggregator {
    /**
     * Creates a new instance of {@link PmdAnnotationsAggregator}.
     *
     * @param build
     *            the matrix build
     * @param launcher
     *            the launcher
     * @param listener
     *            the build listener
     * @param healthDescriptor
     *            health descriptor
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param usePreviousBuildAsReference
     *            determines whether the previous build should be used as the
     *            reference build
     * @param useStableBuildAsReference
     *            determines whether only stable builds should be used as
     *            reference builds or not
     */
    public PmdAnnotationsAggregator(final MatrixBuild build, final Launcher launcher,
            final BuildListener listener, final HealthDescriptor healthDescriptor, final String defaultEncoding,
            final boolean usePreviousBuildAsReference, final boolean useStableBuildAsReference) {
        super(build, launcher, listener, healthDescriptor, defaultEncoding,
                usePreviousBuildAsReference, useStableBuildAsReference);
    }

    @Override
    protected Action createAction(final HealthDescriptor healthDescriptor, final String defaultEncoding,
            final ParserResult aggregatedResult) {
        return new PmdResultAction(build, healthDescriptor,
                new PmdResult(build, defaultEncoding, aggregatedResult,
                        usePreviousBuildAsReference(), useOnlyStableBuildsAsReference()));
    }

    @Override
    protected boolean hasResult(final MatrixRun run) {
        return getAction(run) != null;
    }

    @Override
    protected PmdResult getResult(final MatrixRun run) {
        return getAction(run).getResult();
    }

    private PmdResultAction getAction(final MatrixRun run) {
        return run.getAction(PmdResultAction.class);
    }
}

