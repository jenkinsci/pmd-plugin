package hudson.plugins.pmd;

import static junit.framework.Assert.*;
import hudson.model.AbstractBuild;
import hudson.plugins.pmd.util.BuildResult;
import hudson.plugins.pmd.util.BuildResultTest;
import hudson.plugins.pmd.util.ParserResult;

/**
 * Tests the class {@link PmdResult}.
 */
public class PmdResultTest extends BuildResultTest<PmdResult> {
    /** {@inheritDoc} */
    @Override
    protected PmdResult createBuildResult(final AbstractBuild<?, ?> build, final ParserResult project) {
        return new PmdResult(build, null, project);
    }

    /** {@inheritDoc} */
    @Override
    protected PmdResult createBuildResult(final AbstractBuild<?, ?> build, final ParserResult project, final PmdResult previous) {
        return new PmdResult(build, null, project, previous);
    }

    /** {@inheritDoc} */
    @Override
    protected void verifyHighScoreMessage(final int expectedZeroWarningsBuildNumber, final boolean expectedIsNewHighScore, final long expectedHighScore, final long gap, final PmdResult result) {
        if (result.hasNoAnnotations() && result.getDelta() == 0) {
            assertTrue(result.getDetails().contains(Messages.PMD_ResultAction_NoWarningsSince(expectedZeroWarningsBuildNumber)));
            if (expectedIsNewHighScore) {
                long days = BuildResult.getDays(expectedHighScore);
                if (days == 1) {
                    assertTrue(result.getDetails().contains(Messages.PMD_ResultAction_OneHighScore()));
                }
                else {
                    assertTrue(result.getDetails().contains(Messages.PMD_ResultAction_MultipleHighScore(days)));
                }
            }
            else {
                long days = BuildResult.getDays(gap);
                if (days == 1) {
                    assertTrue(result.getDetails().contains(Messages.PMD_ResultAction_OneNoHighScore()));
                }
                else {
                    assertTrue(result.getDetails().contains(Messages.PMD_ResultAction_MultipleNoHighScore(days)));
                }
            }
        }
    }
}

