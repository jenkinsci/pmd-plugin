package hudson.plugins.pmd;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import hudson.plugins.pmd.util.AbstractEnglishLocaleTest;
import hudson.plugins.pmd.util.NullHealthDescriptor;
import hudson.plugins.pmd.util.model.AnnotationProvider;

import org.junit.Test;
import org.jvnet.localizer.Localizable;

/**
 * Tests the class {@link PmdHealthDescriptor}.
 *
 * @author Ulli Hafner
 */
public class PmdHealthDescriptorTest extends AbstractEnglishLocaleTest {
    /**
     * Verifies the different messages if the number of items are 0, 1, and 2.
     */
    @Test
    public void verifyNumberOfItems() {
        AnnotationProvider provider = mock(AnnotationProvider.class);
        PmdHealthDescriptor healthDescriptor = new PmdHealthDescriptor(NullHealthDescriptor.NULL_HEALTH_DESCRIPTOR);

        Localizable description = healthDescriptor.createDescription(provider);
        assertEquals(Messages.PMD_ResultAction_HealthReportNoItem(), description.toString());

        when(provider.getNumberOfAnnotations()).thenReturn(1);
        description = healthDescriptor.createDescription(provider);
        assertEquals(Messages.PMD_ResultAction_HealthReportSingleItem(), description.toString());

        when(provider.getNumberOfAnnotations()).thenReturn(2);
        description = healthDescriptor.createDescription(provider);
        assertEquals(Messages.PMD_ResultAction_HealthReportMultipleItem(2), description.toString());
    }
}

