package hudson.plugins.pmd;

import hudson.plugins.pmd.util.AbstractHealthDescriptor;
import hudson.plugins.pmd.util.HealthDescriptor;
import hudson.plugins.pmd.util.model.AnnotationProvider;

import org.jvnet.localizer.Localizable;

/**
 * A health descriptor for PMD build results.
 *
 * @author Ulli Hafner
 */
public class PmdHealthDescriptor extends AbstractHealthDescriptor {
    /** Unique ID of this class. */
    private static final long serialVersionUID = -3404826986876607396L;

    /**
     * Creates a new instance of {@link PmdHealthDescriptor} based on the
     * values of the specified descriptor.
     *
     * @param healthDescriptor the descriptor to copy the values from
     */
    public PmdHealthDescriptor(final HealthDescriptor healthDescriptor) {
        super(healthDescriptor);
    }

    /** {@inheritDoc} */
    @Override
    protected Localizable createDescription(final AnnotationProvider result) {
        if (result.getNumberOfAnnotations() == 0) {
            return Messages._PMD_ResultAction_HealthReportNoItem();
        }
        else if (result.getNumberOfAnnotations() == 1) {
            return Messages._PMD_ResultAction_HealthReportSingleItem();
        }
        else {
            return Messages._PMD_ResultAction_HealthReportMultipleItem(result.getNumberOfAnnotations());
        }
    }
}

