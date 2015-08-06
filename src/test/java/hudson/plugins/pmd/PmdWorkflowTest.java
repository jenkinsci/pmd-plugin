package hudson.plugins.pmd;

import hudson.FilePath;
import hudson.model.Result;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PmdWorkflowTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    /**
     * Run a workflow job using {@link PmdPublisher} and check for success.
     */
    @Test
    public void pmdPublisherWorkflowStep() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "pmdPublisherWorkflowStep");
        FilePath workspace = jenkinsRule.jenkins.getWorkspaceFor(job);
        FilePath report = workspace.child("target").child("pmd.xml");
        report.copyFrom(PmdWorkflowTest.class.getResourceAsStream("/hudson/plugins/pmd/parser/4-pmd-warnings.xml"));
        job.setDefinition(new CpsFlowDefinition(""
                        + "node {\n"
                        + "  step([$class: 'PmdPublisher'])\n"
                        + "}\n", true)
        );
        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));
        PmdResultAction result = job.getLastBuild().getAction(PmdResultAction.class);
        assertEquals(4, result.getResult().getAnnotations().size());
    }

    /**
     * Run a workflow job using {@link PmdPublisher} with a failing threshold of 0, so the given example file
     * "/hudson/plugins/pmd/parser/4-pmd-warnings.xml" will make the build to fail.
     */
    @Test
    public void pmdPublisherWorkflowStepSetLimits() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "pmdPublisherWorkflowStepSetLimits");
        FilePath workspace = jenkinsRule.jenkins.getWorkspaceFor(job);
        FilePath report = workspace.child("target").child("pmd.xml");
        report.copyFrom(PmdWorkflowTest.class.getResourceAsStream("/hudson/plugins/pmd/parser/4-pmd-warnings.xml"));
        job.setDefinition(new CpsFlowDefinition(""
                        + "node {\n"
                        + "  step([$class: 'PmdPublisher', pattern: '**/pmd.xml', failedTotalAll: '0', usePreviousBuildAsReference: false])\n"
                        + "}\n", true)
        );
        jenkinsRule.assertBuildStatus(Result.FAILURE, job.scheduleBuild2(0).get());
        PmdResultAction result = job.getLastBuild().getAction(PmdResultAction.class);
        assertEquals(4, result.getResult().getAnnotations().size());
    }

    /**
     * Run a workflow job using {@link PmdPublisher} with a unstable threshold of 0, so the given example file
     * "/hudson/plugins/pmd/parser/4-pmd-warnings.xml" will make the build to fail.
     */
    @Test
    public void pmdPublisherWorkflowStepFailure() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "pmdPublisherWorkflowStepFailure");
        FilePath workspace = jenkinsRule.jenkins.getWorkspaceFor(job);
        FilePath report = workspace.child("target").child("pmd.xml");
        report.copyFrom(PmdWorkflowTest.class.getResourceAsStream("/hudson/plugins/pmd/parser/4-pmd-warnings.xml"));
        job.setDefinition(new CpsFlowDefinition(""
                        + "node {\n"
                        + "  step([$class: 'PmdPublisher', pattern: '**/pmd.xml', unstableTotalAll: '0', usePreviousBuildAsReference: false])\n"
                        + "}\n")
        );
        jenkinsRule.assertBuildStatus(Result.UNSTABLE, job.scheduleBuild2(0).get());
        PmdResultAction result = job.getLastBuild().getAction(PmdResultAction.class);
        assertEquals(4, result.getResult().getAnnotations().size());
    }
}

