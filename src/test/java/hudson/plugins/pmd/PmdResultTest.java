package hudson.plugins.pmd;

import hudson.model.AbstractBuild;
import hudson.plugins.pmd.util.AbstractAnnotationsBuildResultTest;
import hudson.plugins.pmd.util.ParserResult;

/**
 * Tests the class {@link PmdResult}.
 */
public class PmdResultTest extends AbstractAnnotationsBuildResultTest<PmdResult> {
    /** {@inheritDoc} */
    @Override
    protected PmdResult createBuildResult(final AbstractBuild<?, ?> build, final ParserResult project) {
        return new PmdResult(build, project);
    }

    /** {@inheritDoc} */
    @Override
    protected PmdResult createBuildResult(final AbstractBuild<?, ?> build, final ParserResult project, final PmdResult previous) {
        return new PmdResult(build, project, previous);
    }
}

