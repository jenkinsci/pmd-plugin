package hudson.plugins.pmd;

import hudson.model.AbstractBuild;
import hudson.plugins.analysis.core.BuildHistory;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.test.BuildResultTest;

/**
 * Tests the class {@link PmdResult}.
 */
public class PmdResultTest extends BuildResultTest<PmdResult> {
    /** {@inheritDoc} */
    @Override
    protected PmdResult createBuildResult(final AbstractBuild<?, ?> build, final ParserResult project, final BuildHistory history) {
        return new PmdResult(build, null, project, history);
    }
}

