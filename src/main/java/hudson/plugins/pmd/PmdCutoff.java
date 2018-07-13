package hudson.plugins.pmd;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.Serializable;

/**
 * Holds the cutoff values for high and medium priority.
 */
@ExportedBean
public class PmdCutoff
        implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * PMD priority 0..5 for high jenkins priority.
     * <p>
     * Any PMD message with a priority lower (inclusive) than this value is mapped to "high priority".
     * If this field is set to "0", then no message is mapped to "high priority".
     * If it is set to "5", every message is mapped to "high priority".
     */
    @Exported
    public String cutoffHighPriority = StringUtils.EMPTY;

    /**
     * PMD priority 0..5 for normal jenkins priority.
     * <p>
     * Any PMD message with a priority lower (inclusive) than this value is mapped to "normal priority".
     * If this field is set to the same value as cutoffHighPriority, then no message is mapped to "normal priority".
     * If this field is set to 5, then no message is mapped to "low priority".
     */
    @Exported
    public String cutoffNormalPriority = StringUtils.EMPTY;

    public boolean isValid() {
        return isValidValue(cutoffHighPriority)
                && isValidValue(cutoffNormalPriority)
                //  high and normal can be the same (then no values are mapped to "normal"), but 'high' must be greater than 'normal'.
                && Integer.valueOf(cutoffHighPriority) <= Integer.valueOf(cutoffNormalPriority);
    }

    private static boolean isValidValue(String value) {
        if (StringUtils.isNotBlank(value)) {
            try {
                int x = Integer.valueOf(value);
                return x >= 0 && x <= 5;
            } catch (NumberFormatException ignored) {
            }
        }
        return false;
    }
}
