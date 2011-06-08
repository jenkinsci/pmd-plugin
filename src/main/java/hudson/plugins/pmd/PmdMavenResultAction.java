package hudson.plugins.pmd;

import hudson.maven.MavenAggregatedReport;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.maven.MavenModuleSetBuild;
import hudson.model.Action;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.MavenResultAction;

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
     * Creates a new instance of {@link PmdMavenResultAction}. This instance
     * will have no result set in the beginning. The result will be set
     * successively after each of the modules are build.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor to use
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     */
    public PmdMavenResultAction(final MavenModuleSetBuild owner, final HealthDescriptor healthDescriptor,
            final String defaultEncoding) {
        super(new PmdResultAction(owner, healthDescriptor), defaultEncoding, "PMD");
    }

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
    public PmdMavenResultAction(final MavenBuild owner, final HealthDescriptor healthDescriptor,
            final String defaultEncoding, final PmdResult result) {
        super(new PmdResultAction(owner, healthDescriptor, result), defaultEncoding, "PMD");
    }

    /** {@inheritDoc} */
    public MavenAggregatedReport createAggregatedAction(final MavenModuleSetBuild build, final Map<MavenModule, List<MavenBuild>> moduleBuilds) {
        return new PmdMavenResultAction(build, getHealthDescriptor(), getDisplayName());
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
        return new PmdReporterResult(getOwner(), additionalResult.getDefaultEncoding(), aggregate(existingResult, additionalResult));
    }
}

