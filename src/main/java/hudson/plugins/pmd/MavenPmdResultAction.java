package hudson.plugins.pmd;

import hudson.maven.AggregatableAction;
import hudson.maven.MavenAggregatedReport;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.maven.MavenModuleSetBuild;
import hudson.model.Action;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.util.PluginLogger;

import java.util.List;
import java.util.Map;

/**
 * A {@link PmdResultAction} for native maven jobs. This action
 * additionally provides result aggregation for sub-modules and for the main
 * project.
 *
 * @author Ulli Hafner
 */
public class MavenPmdResultAction extends PmdResultAction implements AggregatableAction, MavenAggregatedReport {
    /** The default encoding to be used when reading and parsing files. */
    private final String defaultEncoding;

    /**
     * Creates a new instance of {@link MavenPmdResultAction}. This instance
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
    public MavenPmdResultAction(final MavenModuleSetBuild owner, final HealthDescriptor healthDescriptor,
            final String defaultEncoding) {
        super(owner, healthDescriptor);

        this.defaultEncoding = defaultEncoding;
    }

    /**
     * Creates a new instance of {@link MavenPmdResultAction}.
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
    public MavenPmdResultAction(final MavenBuild owner, final HealthDescriptor healthDescriptor,
            final String defaultEncoding, final PmdResult result) {
        super(owner, healthDescriptor, result);

        this.defaultEncoding = defaultEncoding;
    }

    /** {@inheritDoc} */
    public MavenAggregatedReport createAggregatedAction(final MavenModuleSetBuild build, final Map<MavenModule, List<MavenBuild>> moduleBuilds) {
        return new MavenPmdResultAction(build, getHealthDescriptor(), defaultEncoding);
    }

    /** {@inheritDoc} */
    public Action getProjectAction(final MavenModuleSet moduleSet) {
        return new PmdProjectAction(moduleSet);
    }

    /** {@inheritDoc} */
    public Class<? extends AggregatableAction> getIndividualActionType() {
        return getClass();
    }

    /**
     * Called whenever a new module build is completed, to update the aggregated
     * report. When multiple builds complete simultaneously, Jenkins serializes
     * the execution of this method, so this method needs not be
     * concurrency-safe.
     *
     * @param moduleBuilds
     *            Same as <tt>MavenModuleSet.getModuleBuilds()</tt> but provided
     *            for convenience and efficiency.
     * @param newBuild
     *            Newly completed build.
     */
    public void update(final Map<MavenModule, List<MavenBuild>> moduleBuilds, final MavenBuild newBuild) {
        MavenPmdResultAction additionalAction = newBuild.getAction(MavenPmdResultAction.class);
        if (additionalAction != null) {
            PmdResult existingResult = getResult();
            PmdResult additionalResult = additionalAction.getResult();

            log("Aggregating results of " + newBuild.getProject().getDisplayName());

            if (existingResult == null) {
                setResult(additionalResult);
                getOwner().setResult(additionalResult.getPluginResult());
            }
            else {
                setResult(aggregate(existingResult, additionalResult, getLogger()));
            }
        }
    }

    /**
     * Creates a new instance of {@link BuildResult} that contains the aggregated
     * results of this result and the provided additional result.
     *
     * @param existingResult
     *            the existing result
     * @param additionalResult
     *            the result that will be added to the existing result
     * @param logger
     *            the plug-in logger
     * @return the aggregated result
     */
    public PmdResult aggregate(final PmdResult existingResult, final PmdResult additionalResult, final PluginLogger logger) {
        ParserResult aggregatedAnnotations = new ParserResult();
        aggregatedAnnotations.addAnnotations(existingResult.getAnnotations());
        aggregatedAnnotations.addAnnotations(additionalResult.getAnnotations());

        PmdResult createdResult = new PmdResult(getOwner(), existingResult.getDefaultEncoding(), aggregatedAnnotations);
        createdResult.evaluateStatus(existingResult.getThresholds(), existingResult.canUseDeltaValues(), logger);
        return createdResult;
    }

    /** Backward compatibility. @deprecated */
    @SuppressWarnings("unused")
    @Deprecated
    private transient String height;
}

