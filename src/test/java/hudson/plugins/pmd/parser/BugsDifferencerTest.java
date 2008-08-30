package hudson.plugins.pmd.parser;

import hudson.plugins.pmd.util.AnnotationDifferencer;
import hudson.plugins.pmd.util.AnnotationDifferencerTest;
import hudson.plugins.pmd.util.model.FileAnnotation;
import hudson.plugins.pmd.util.model.Priority;

/**
 * Tests the {@link AnnotationDifferencer} for bugs.
 */
public class BugsDifferencerTest extends AnnotationDifferencerTest {
    /** {@inheritDoc} */
    @Override
    public FileAnnotation createAnnotation(final String fileName, final Priority priority, final String message, final String category,
            final String type, final int start, final int end) {
        Bug bug = new Bug(priority, message, message, message, start, end);
        bug.setFileName(fileName);
        return bug;
    }
}

