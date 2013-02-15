package hudson.plugins.pmd;

import hudson.maven.MavenAggregatedReport;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.maven.MavenModuleSetBuild;
import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.MavenResultAction;
import hudson.plugins.analysis.core.ParserResult;

import java.util.List;
import java.util.Map;

/**
 * A {@link PmdResultAction} for native Maven jobs. This action
 * additionally provides result aggregation for sub-modules and for the main
 * project.
 *
 * @author Ulli Hafner
 */
public class PmdMavenResultAction extends MavenResultAction<PmdResult> {
    /**
     * Creates a new instance of {@link PmdMavenResultAction}.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor to use
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param result
     *            the result in this build
     */
    public PmdMavenResultAction(final AbstractBuild<?, ?> owner, final HealthDescriptor healthDescriptor,
            final String defaultEncoding, final PmdResult result) {
        super(new PmdResultAction(owner, healthDescriptor, result), defaultEncoding, "PMD");
    }

    /** {@inheritDoc} */
    public MavenAggregatedReport createAggregatedAction(final MavenModuleSetBuild build, final Map<MavenModule, List<MavenBuild>> moduleBuilds) {
        return new PmdMavenResultAction(build, getHealthDescriptor(), getDefaultEncoding(),
                new PmdResult(build, getDefaultEncoding(), new ParserResult(), false));
    }

    /** {@inheritDoc} */
    public Action getProjectAction(final MavenModuleSet moduleSet) {
        return new PmdProjectAction(moduleSet, PmdMavenResultAction.class);
    }

    @Override
    public Class<? extends MavenResultAction<PmdResult>> getIndividualActionType() {
        return PmdMavenResultAction.class;
    }

    @Override
    protected PmdResult createResult(final PmdResult existingResult, final PmdResult additionalResult) {
        return new PmdReporterResult(getOwner(), additionalResult.getDefaultEncoding(),
                aggregate(existingResult, additionalResult), existingResult.useOnlyStableBuildsAsReference());
    }
}

