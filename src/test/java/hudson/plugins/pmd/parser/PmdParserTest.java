package hudson.plugins.pmd.parser;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import static org.junit.Assert.*;

import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.MavenModule;
import hudson.plugins.analysis.util.model.Priority;

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
     * @param fileName
     *            the file to read
     * @return the parsed module
     * @throws InvocationTargetException
     *             in case of an error
     */
    private Collection<FileAnnotation> parseFile(final String fileName) throws InvocationTargetException {
        InputStream file = PmdParserTest.class.getResourceAsStream(fileName);
        try {
            return new PmdParser().parse(file, "module");
        }
        finally {
            IOUtils.closeQuietly(file);
        }
    }

    /**
     * Parses a warning log with 15 warnings.
     *
     * @throws InvocationTargetException
     *      if the file could not be read
     * @see <a href="http://issues.jenkins-ci.org/browse/JENKINS-12801">Issue 12801</a>
     */
    @Test
    public void issue12801() throws InvocationTargetException {
        String fileName = "issue12801.xml";
        Collection<FileAnnotation> annotations = parseFile(fileName);

        assertEquals(ERROR_MESSAGE, 2, annotations.size());
        ParserResult result = new ParserResult(annotations);
        assertEquals(ERROR_MESSAGE, 2, result.getNumberOfAnnotations());

    }

    /**
     * Checks whether we correctly detect all 669 warnings.
     *
     * @throws InvocationTargetException
     *             indicates a test failure
     */
    @Test
    public void scanFileWithSeveralWarnings() throws InvocationTargetException {
        String fileName = "pmd.xml";
        Collection<FileAnnotation> annotations = parseFile(fileName);

        assertEquals(ERROR_MESSAGE, 669, annotations.size());
    }

    /**
     * Checks whether we create messages with a single dot.
     *
     * @throws InvocationTargetException
     *             indicates a test failure
     */
    @Test
    public void verifySingleDot() throws InvocationTargetException {
        String fileName = "warning-message-with-dot.xml";
        Collection<FileAnnotation> annotations = parseFile(fileName);

        assertEquals(ERROR_MESSAGE, 2, annotations.size());

        FileAnnotation annotation = annotations.iterator().next();

        assertEquals("Wrong message text: ", "Avoid really long parameter lists.", annotation.getMessage());
    }

    /**
     * Checks whether we correctly detect an empty file.
     *      * @throws InvocationTargetException indicates a test failure

     * @throws InvocationTargetException
     *             indicates a test failure
     */
    @Test
    public void scanFileWithNoBugs() throws InvocationTargetException {
        String fileName = "empty.xml";
        Collection<FileAnnotation> annotations = parseFile(fileName);

        assertEquals(ERROR_MESSAGE, 0, annotations.size());
    }

    /**
     * Checks whether we correctly parse a file with 4 warnings.
     *
     * @throws InvocationTargetException
     *             indicates a test failure
     */
    @Test
    public void scanFileWith4Warnings() throws InvocationTargetException {
        PmdMessages.getInstance().initialize();

        String fileName = "4-pmd-warnings.xml";
        Collection<FileAnnotation> annotations = parseFile(fileName);
        MavenModule module = new MavenModule();
        module.addAnnotations(annotations);

        assertEquals(ERROR_MESSAGE, 4, module.getNumberOfAnnotations());

        assertEquals(ERROR_MESSAGE, 1, module.getPackage("com.avaloq.adt.env.internal.ui.actions")
                .getNumberOfAnnotations());
        assertEquals(ERROR_MESSAGE, 1, module.getPackage(
                "com.avaloq.adt.env.internal.ui.actions.change").getNumberOfAnnotations());
        assertEquals(ERROR_MESSAGE, 2, module.getPackage("com.avaloq.adt.env.internal.ui.dialogs")
                .getNumberOfAnnotations());

        assertEquals(ERROR_MESSAGE, 1, module.getNumberOfAnnotations("HIGH"));
        assertEquals(ERROR_MESSAGE, 2, module.getNumberOfAnnotations("NORMAL"));
        assertEquals(ERROR_MESSAGE, 1, module.getNumberOfAnnotations("LOW"));

        Bug warning = (Bug)module.getPackage("com.avaloq.adt.env.internal.ui.actions")
                .getAnnotations().iterator().next();

        assertEquals(WRONG_WARNING_PROPERTY, "These nested if statements could be combined.",
                warning.getMessage());
        assertEquals(WRONG_WARNING_PROPERTY, Priority.NORMAL, warning.getPriority());
        assertEquals(WRONG_WARNING_PROPERTY, "Basic", warning.getCategory());
        assertEquals(WRONG_WARNING_PROPERTY, "CollapsibleIfStatements", warning.getType());
        assertEquals(WRONG_WARNING_PROPERTY, 54, warning.getPrimaryLineNumber());
        assertEquals(WRONG_WARNING_PROPERTY, "com.avaloq.adt.env.internal.ui.actions", warning
                .getPackageName());
        assertEquals(WRONG_WARNING_PROPERTY, 1, warning.getLineRanges().size());
        assertEquals(WRONG_WARNING_PROPERTY, 54, warning.getLineRanges().iterator().next()
                .getStart());
        assertEquals(WRONG_WARNING_PROPERTY, 61, warning.getLineRanges().iterator().next().getEnd());
        assertEquals(
                WRONG_WARNING_PROPERTY,
                "C:/Build/Results/jobs/ADT-Base/workspace/com.avaloq.adt.ui/src/main/java/com/avaloq/adt/env/internal/ui/actions/CopyToClipboard.java",
                warning.getFileName());
        assertEquals(
                WRONG_WARNING_PROPERTY,
                "\n" +
                        "Sometimes two consecutive 'if' statements can be consolidated by separating their conditions with a boolean short-circuit operator.\n" +
                        "      <pre>\n" +
                        "  \n" +
                        "void bar() {\n" +
                        "\tif (x) {\t\t\t// original implementation\n" +
                        "\t\tif (y) {\n" +
                        "\t\t\t// do stuff\n" +
                        "\t\t}\n" +
                        "\t}\n" +
                        "}\n" +
                        "\n" +
                        "void bar() {\n" +
                        "\tif (x && y) {\t\t// optimized implementation\n" +
                        "\t\t// do stuff\n" +
                        "\t}\n" +
                        "}\n" +
                        " \n" +
                        "      </pre>", warning.getToolTip());
    }

    /**
     * Checks whether we correctly parse a file with 4 warnings.
     *
     * @throws InvocationTargetException
     *             indicates a test failure
     */
    @Test
    public void testEquals() throws InvocationTargetException {
        String fileName = "equals-test.xml";
        Collection<FileAnnotation> annotations = parseFile(fileName);
        MavenModule module = new MavenModule();
        module.addAnnotations(annotations);

        assertEquals(ERROR_MESSAGE, 4, module.getNumberOfAnnotations());
        assertEquals(ERROR_MESSAGE, 4, module.getPackage("com.avaloq.adt.env.core.db.plsqlCompletion").getNumberOfAnnotations());
        assertEquals(ERROR_MESSAGE, 4, module.getNumberOfAnnotations(Priority.NORMAL));
    }
}
