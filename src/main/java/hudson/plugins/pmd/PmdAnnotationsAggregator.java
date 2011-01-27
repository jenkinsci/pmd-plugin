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
     */
    public PmdAnnotationsAggregator(final MatrixBuild build, final Launcher launcher,
            final BuildListener listener, final HealthDescriptor healthDescriptor, final String defaultEncoding) {
        super(build, launcher, listener, healthDescriptor, defaultEncoding);
    }

    /** {@inheritDoc} */
    @Override
    protected Action createAction(final HealthDescriptor healthDescriptor, final String defaultEncoding, final ParserResult aggregatedResult) {
        return new PmdResultAction(build, healthDescriptor,
                new PmdResult(build, defaultEncoding, aggregatedResult));
    }

    /** {@inheritDoc} */
    @Override
    protected PmdResult getResult(final MatrixRun run) {
        PmdResultAction action = run.getAction(PmdResultAction.class);

        return action.getResult();
    }
}

