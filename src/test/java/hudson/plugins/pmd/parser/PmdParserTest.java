package hudson.plugins.pmd.parser;

import static org.junit.Assert.*;
import hudson.plugins.pmd.util.model.MavenModule;
import hudson.plugins.pmd.util.model.Priority;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

/**
 *  Tests the extraction of PMD analysis results.
 */
public class PmdParserTest {
    /** Error message. */
    private static final String WRONG_WARNING_PROPERTY = "Wrong warning property";
    /** Error message. */
    private static final String ERROR_MESSAGE = "Wrong number of warnings detected.";

    /**
     * Parses the specified file.
     *
     * @param fileName the file to read
     * @return the parsed module
     * @throws InvocationTargetException
     *             in case of an error
     */
    private MavenModule parseFile(final String fileName) throws InvocationTargetException {
        return new PmdParser().parse(PmdParserTest.class.getResourceAsStream(fileName), "module");
    }

    /**
     * Checks whether we correctly detect all 669 warnings.
     */
    @Test
    public void scanFileWithSeveralWarnings() throws InvocationTargetException {
        String fileName = "pmd.xml";
        MavenModule module = parseFile(fileName);

        assertEquals(ERROR_MESSAGE, 669, module.getNumberOfAnnotations());
    }

    /**
     * Checks whether we correctly detect an empty file.
     */
    @Test
    public void scanFileWithNoBugs() throws InvocationTargetException {
        String fileName = "empty.xml";
        MavenModule module = parseFile(fileName);

        assertEquals(ERROR_MESSAGE, 0, module.getNumberOfAnnotations());
    }

    /**
     * Checks whether we correctly parse a file with 4 warnings.
     */
    @Test
    public void scanFileWith4Warnings() throws InvocationTargetException {
        String fileName = "4-pmd-warnings.xml";
        MavenModule module = parseFile(fileName);

        assertEquals(ERROR_MESSAGE, 4, module.getNumberOfAnnotations());

        assertEquals(ERROR_MESSAGE, 1, module.getPackage("com.avaloq.adt.env.internal.ui.actions").getNumberOfAnnotations());
        assertEquals(ERROR_MESSAGE, 1, module.getPackage("com.avaloq.adt.env.internal.ui.actions.change").getNumberOfAnnotations());
        assertEquals(ERROR_MESSAGE, 2, module.getPackage("com.avaloq.adt.env.internal.ui.dialogs").getNumberOfAnnotations());

        assertEquals(ERROR_MESSAGE, 2, module.getNumberOfAnnotations("HIGH"));
        assertEquals(ERROR_MESSAGE, 1, module.getNumberOfAnnotations("NORMAL"));
        assertEquals(ERROR_MESSAGE, 1, module.getNumberOfAnnotations("LOW"));

        Bug warning = (Bug)module.getPackage("com.avaloq.adt.env.internal.ui.actions").getAnnotations().iterator().next();

        assertEquals(WRONG_WARNING_PROPERTY, "These nested if statements could be combined.", warning.getMessage());
        assertEquals(WRONG_WARNING_PROPERTY, Priority.NORMAL, warning.getPriority());
        assertEquals(WRONG_WARNING_PROPERTY, "Basic Rules", warning.getCategory());
        assertEquals(WRONG_WARNING_PROPERTY, "CollapsibleIfStatements", warning.getType());
        assertEquals(WRONG_WARNING_PROPERTY, 54, warning.getPrimaryLineNumber());
        assertEquals(WRONG_WARNING_PROPERTY, "com.avaloq.adt.env.internal.ui.actions", warning.getPackageName());
        assertEquals(WRONG_WARNING_PROPERTY, 1, warning.getLineRanges().size());
        assertEquals(WRONG_WARNING_PROPERTY, 54, warning.getLineRanges().iterator().next().getStart());
        assertEquals(WRONG_WARNING_PROPERTY, 61, warning.getLineRanges().iterator().next().getEnd());
        assertEquals(WRONG_WARNING_PROPERTY, "C:/Build/Results/jobs/ADT-Base/workspace/com.avaloq.adt.ui/src/main/java/com/avaloq/adt/env/internal/ui/actions/CopyToClipboard.java", warning.getFileName());
        assertEquals(WRONG_WARNING_PROPERTY, "\n"
                + "Sometimes two \'if\' statements can be consolidated by separating their conditions with a boolean short-circuit operator.\n"
                + "      <pre>\n"
                + "  \n"
                + "public class Foo {\n"
                + " void bar() {\n"
                + "  if (x) {\n"
                + "   if (y) {\n"
                + "    // do stuff\n"
                + "   }\n"
                + "  }\n"
                + " }\n"
                + "}\n"
                + " \n"
                + "      </pre>", warning.getToolTip());
    }

    /**
     * Checks whether we correctly parse a file with 4 warnings.
     */
    @Test
    public void testEquals() throws InvocationTargetException {
        String fileName = "equals-test.xml";
        MavenModule module = parseFile(fileName);

        assertEquals(ERROR_MESSAGE, 4, module.getNumberOfAnnotations());
        assertEquals(ERROR_MESSAGE, 4, module.getPackage("com.avaloq.adt.env.core.db.plsqlCompletion").getNumberOfAnnotations());
        assertEquals(ERROR_MESSAGE, 4, module.getNumberOfAnnotations(Priority.NORMAL));
    }
}


/* Copyright (c) Avaloq Evolution AG */