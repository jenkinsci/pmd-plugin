package hudson.plugins.pmd;

import static org.junit.Assert.*;
import hudson.plugins.pmd.parser.Bug;
import hudson.plugins.pmd.util.model.FileAnnotation;
import hudson.plugins.pmd.util.model.Priority;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * Tests the class {@link WarningDifferencer}.
 */
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class WarningDifferencerTest {
    /** String for comparison. */
    private static final String STRING = "type1";
    /** Indicates a wrong calculation of warnings. */
    private static final String WARNINGS_COUNT_ERROR = "Wrong warnings count.";

    /**
     * Checks whether equals works for warnings.
     */
    @Test
    public void testWarningEquals() {
        Bug first  = new Bug(Priority.HIGH, STRING, STRING, STRING, 2);
        Bug second = new Bug(Priority.HIGH, STRING, STRING, STRING, 2);

        assertEquals("Warnings are not equal.", first, second);

        first.setFileName(STRING);

        assertFalse("Warnings are equal.", first.equals(second));
    }

    /**
     * Checks whether differencing detects single changes (new and fixed).
     */
    @Test
    public void testDifferencer() {
        Set<FileAnnotation> actual = new HashSet<FileAnnotation>();
        Set<FileAnnotation> previous = new HashSet<FileAnnotation>();

        Bug warning = new Bug(Priority.HIGH, STRING, STRING, STRING, 2);
        actual.add(warning);

        warning = new Bug(Priority.HIGH, STRING, STRING, STRING, 2);
        previous.add(warning);


        assertEquals(WARNINGS_COUNT_ERROR, 0, WarningDifferencer.getFixedWarnings(actual, previous).size());

        warning = new Bug(Priority.HIGH, "type2", STRING, STRING, 2);
        previous.add(warning);

        assertEquals(WARNINGS_COUNT_ERROR, 0, WarningDifferencer.getNewWarnings(actual, previous).size());
        assertEquals(WARNINGS_COUNT_ERROR, 1, WarningDifferencer.getFixedWarnings(actual, previous).size());

        warning = new Bug(Priority.HIGH, "type2", STRING, STRING, 2);
        actual.add(warning);

        assertEquals(WARNINGS_COUNT_ERROR, 0, WarningDifferencer.getNewWarnings(actual, previous).size());
        assertEquals(WARNINGS_COUNT_ERROR, 0, WarningDifferencer.getFixedWarnings(actual, previous).size());

        warning = new Bug(Priority.HIGH, "type3", STRING, STRING, 2);
        actual.add(warning);

        assertEquals(WARNINGS_COUNT_ERROR, 1, WarningDifferencer.getNewWarnings(actual, previous).size());
        assertEquals(WARNINGS_COUNT_ERROR, 0, WarningDifferencer.getFixedWarnings(actual, previous).size());
    }
}

