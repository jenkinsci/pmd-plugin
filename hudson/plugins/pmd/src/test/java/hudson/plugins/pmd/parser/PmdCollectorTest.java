package hudson.plugins.pmd.parser;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests the module guessing algorithm of the class {@link PmdCollector}.
 */
public class PmdCollectorTest {
    /** Expected module name for all tests. */
    private static final String EXPECTED_MODULE = "com.avaloq.adt.core.tests";
    /** JUnit Error message. */
    private static final String ERROR_MESSAGE = "Wrong module name detected.";

    /**
     * Checks whether we extract the right module name regardless the path
     * separator conventions.
     */
    @Test
    public void checkModuleGuessing() {
        String input = "workspace/com.avaloq.adt.core.tests/target/findbugs.xml";
        assertEquals(ERROR_MESSAGE, EXPECTED_MODULE, PmdCollector.guessModuleName(input));

        input = "com.avaloq.adt.core.tests/target/findbugs.xml";
        assertEquals(ERROR_MESSAGE, EXPECTED_MODULE, PmdCollector.guessModuleName(input));

        input = "C:\\work\\workspace\\com.avaloq.adt.core.tests\\target\\findbugs.xml";
        assertEquals(ERROR_MESSAGE, EXPECTED_MODULE, PmdCollector.guessModuleName(input));

        input = "com.avaloq.adt.core.tests\\target\\findbugs.xml";
        assertEquals(ERROR_MESSAGE, EXPECTED_MODULE, PmdCollector.guessModuleName(input));

        input = "com.avaloq.adt.core.tests\\findbugs.xml";
        assertEquals(ERROR_MESSAGE, "", PmdCollector.guessModuleName(input));
    }
}

