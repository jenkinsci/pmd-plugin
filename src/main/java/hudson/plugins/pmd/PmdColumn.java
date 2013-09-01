package hudson.plugins.pmd;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

import hudson.plugins.analysis.views.WarningsCountColumn;

import hudson.views.ListViewColumnDescriptor;

/**
 * A column that shows the total number of Checkstyle warnings in a job.
 *
 * @author Ulli Hafner
 */
public class PmdColumn extends WarningsCountColumn<PmdProjectAction> {
    /**
     * Creates a new instance of {@link PmdColumn}.
     */
    @DataBoundConstructor
    public PmdColumn() { // NOPMD: data binding
        super();
    }

    @Override
    protected Class<PmdProjectAction> getProjectAction() {
        return PmdProjectAction.class;
    }

    @Override
    public String getColumnCaption() {
        return Messages.PMD_Warnings_ColumnHeader();
    }

    /**
     * Descriptor for the column.
     */
    @Extension
    public static class ColumnDescriptor extends ListViewColumnDescriptor {
        @Override
        public boolean shownByDefault() {
            return false;
        }

        @Override
        public String getDisplayName() {
            return Messages.PMD_Warnings_Column();
        }
    }
}
