package io.jenkins.plugins.analysis.pmd;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.IssueBuilder;
import edu.hm.hafner.analysis.Issues;
import static edu.hm.hafner.analysis.Issues.*;
import edu.hm.hafner.analysis.Priority;
import static edu.hm.hafner.analysis.assertj.Assertions.*;
import static edu.hm.hafner.analysis.assertj.SoftAssertions.*;
import io.jenkins.plugins.analysis.pmd.Pmd.PmdLabelProvider;

import hudson.plugins.pmd.parser.PmdMessages;

/**
 * Tests the extraction of PMD analysis results.
 */
class PmdParserTest {
    /**
     * Parses a warning log with 15 warnings.
     *
     * @see <a href="http://issues.jenkins-ci.org/browse/JENKINS-12801">Issue 12801</a>
     */
    @Test
    void issue12801() {
        Issues<Issue> issues = parseFile("issue12801.xml");

        assertThat(issues).hasSize(2);

    }

    /**
     * Checks whether we correctly detect all 669 warnings.
     */
    @Test
    void scanFileWithSeveralWarnings() {
        Issues<Issue> issues = parseFile("pmd.xml");

        assertThat(issues).hasSize(669);
    }

    /**
     * Checks whether we create messages with a single dot.
     */
    @Test
    void verifySingleDot() {
        String fileName = "warning-message-with-dot.xml";
        Issues<Issue> issues = parseFile(fileName);

        assertThat(issues).hasSize(2);
        assertThat(issues.get(0)).hasMessage("Avoid really long parameter lists.");
    }

    /**
     * Checks whether we correctly detect an empty file.
     */
    @Test
    void scanFileWithNoBugs() {
        Issues<Issue> issues = parseFile("empty.xml");

        assertThat(issues).isEmpty();
    }

    /**
     * Checks whether we correctly parse a file with 4 warnings.
     */
    @Test
    void scanFileWith4Warnings() {
        PmdMessages.getInstance().initialize();

        Issues<Issue> issues = parseFile("4-pmd-warnings.xml");

        assertThat(issues).hasSize(4);
        Issues<Issue> actionIssues = issues.filter(byPackageName("com.avaloq.adt.env.internal.ui.actions"));
        assertThat(actionIssues).hasSize(1);
        assertThat(issues.filter(byPackageName("com.avaloq.adt.env.internal.ui.actions"))).hasSize(1);
        assertThat(issues.filter(byPackageName("com.avaloq.adt.env.internal.ui.dialogs"))).hasSize(2);

        assertThat(issues).hasHighPrioritySize(1);
        assertThat(issues).hasNormalPrioritySize(2);
        assertThat(issues).hasLowPrioritySize(1);

        Issue actual = actionIssues.get(0);
        assertSoftly(softly -> {
            softly.assertThat(actual)
                    .hasMessage("These nested if statements could be combined.")
                    .hasPriority(Priority.NORMAL)
                    .hasCategory("Basic")
                    .hasType("CollapsibleIfStatements")
                    .hasLineStart(54)
                    .hasLineEnd(61)
                    .hasPackageName("com.avaloq.adt.env.internal.ui.actions")
                    .hasFileName("C:/Build/Results/jobs/ADT-Base/workspace/com.avaloq.adt.ui/src/main/java/com/avaloq/adt/env/internal/ui/actions/CopyToClipboard.java");
        });

        PmdLabelProvider labelProvider = new PmdLabelProvider();
        assertThat(actual).hasDescription(StringUtils.EMPTY);
        assertThat(labelProvider.getDescription(actual)).isEqualTo("\n"
                + "Sometimes two consecutive 'if' statements can be consolidated by separating their conditions with a boolean short-circuit operator.\n"
                + "      <pre>\n  \nvoid bar() {\n\tif (x) {\t\t\t// original implementation\n\t\tif (y) {\n\t\t\t// do stuff\n\t\t}\n"
                + "\t}\n}\n\nvoid bar() {\n\tif (x && y) {\t\t// optimized implementation\n\t\t// do stuff\n\t}\n}\n \n      </pre>");
    }

    /**
     * Checks whether we correctly parse a file with 4 warnings.
     */
    @Test
    void testEquals() {
        Issues<Issue> issues = parseFile("equals-test.xml");

        int expectedSize = 4;
        assertThat(issues).hasSize(expectedSize);
        assertThat(issues.filter(byPackageName("com.avaloq.adt.env.core.db.plsqlCompletion"))).hasSize(expectedSize);
        assertThat(issues).hasNormalPrioritySize(expectedSize);
    }

    private Issues<Issue> parseFile(final String fileName) {
        InputStream file = hudson.plugins.pmd.parser.PmdParserTest.class.getResourceAsStream(fileName);
        try {
            return new PmdParser().parse(new InputStreamReader(file), new IssueBuilder());
        }
        finally {
            IOUtils.closeQuietly(file);
        }
    }
}
