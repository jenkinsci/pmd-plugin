package hudson.plugins.pmd.parser;

import hudson.plugins.analysis.core.IssueDifference;
import hudson.plugins.analysis.test.AnnotationDifferencerTest;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.Priority;

/**
 * Tests the {@link IssueDifference} for bugs.
 */
public class BugsDifferencerTest extends AnnotationDifferencerTest {
    @Override
    public FileAnnotation createAnnotation(final String fileName, final Priority priority, final String message, final String category,
            final String type, final int start, final int end) {
        Bug bug = new Bug(priority, message, message, message, start, end);
        bug.setFileName(fileName);
        return bug;
    }
}

