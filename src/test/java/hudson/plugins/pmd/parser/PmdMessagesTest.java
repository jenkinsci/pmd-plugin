package hudson.plugins.pmd.parser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the class {@link PmdMessages}.
 *
 * @author Ullrich Hafner
 */
public class PmdMessagesTest {
    /**
     * Verifies that the PMD messages could be correctly read.
     */
    @Test
    public void shouldHaveAllMessage() {
        assertEquals("Wrong number of rulesets found: ", 25, PmdMessages.getInstance().initialize());

        assertEquals("\n" +
                "Empty Catch Block finds instances where an exception is caught, but nothing is done.  \n" +
                "In most circumstances, this swallows an exception which should either be acted on \n" +
                "or reported.\n" +
                "      <pre>\n" +
                "  \n" +
                "public void doSomething() {\n" +
                "  try {\n" +
                "    FileInputStream fis = new FileInputStream(\"/tmp/bugger\");\n" +
                "  } catch (IOException ioe) {\n" +
                "      // not good\n" +
                "  }\n" +
                "}\n" +
                " \n" +
                "      </pre>",
                PmdMessages.getInstance().getMessage("Basic", "EmptyCatchBlock"));
    }
}