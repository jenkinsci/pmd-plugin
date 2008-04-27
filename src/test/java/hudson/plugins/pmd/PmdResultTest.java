package hudson.plugins.pmd;

import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;
import hudson.model.AbstractBuild;
import hudson.plugins.pmd.parser.Bug;
import hudson.plugins.pmd.util.model.JavaProject;
import hudson.plugins.pmd.util.model.Priority;

import java.io.File;
import java.util.Calendar;

import org.junit.Test;

/**
 * Tests the class {@link PmdResult}.
 */
// TODO: add more tests for the remaining part of the warnings indicator
public class PmdResultTest {
    /** Dummy path. */
    private static final String PATH = "./";
    /** Error message. */
    private static final String ERROR_MESSAGE = "Wrong zero warnings since build #x counter.";

    /**
     * Checks whether the zero warnings indicator correctly is changed if the build contains warnings.
     */
    @Test
    public void testZeroWarningsIndicator()  {
        AbstractBuild<?, ?> build = createMock(AbstractBuild.class);
        expect(build.getTimestamp()).andReturn(Calendar.getInstance()).anyTimes();
        expect(build.getRootDir()).andReturn(new File(PATH)).anyTimes();
        expect(build.getNumber()).andReturn(5);
        replay(build);

        JavaProject project = new JavaProject();
        PmdResult pmdResult = new PmdResult(build, project);

        assertEquals(ERROR_MESSAGE, 0, pmdResult.getZeroWarningsSinceBuild());

        JavaProject withWarnings = new JavaProject();
        Bug bug = new Bug(Priority.HIGH, "", "", "");
        bug.setFileName("");
        withWarnings.addAnnotation(bug);

        pmdResult = new PmdResult(build, withWarnings, project, 0);
        assertEquals(ERROR_MESSAGE, 0, pmdResult.getZeroWarningsSinceBuild());

        pmdResult = new PmdResult(build, project, withWarnings, 0);
        assertEquals(ERROR_MESSAGE, 5, pmdResult.getZeroWarningsSinceBuild());
    }
}

