package com.usharesoft.jenkins;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Label;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class UForgeBuilderTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void should_project_stay_unchanged_after_roundtrip_configuration() throws Exception {
        // given
        FreeStyleProject project = jenkins.createFreeStyleProject();
        project.getBuildersList().add(new UForgeBuilder());

        // when
        project = jenkins.configRoundtrip(project);

        // then
        jenkins.assertEqualDataBoundBeans(new UForgeBuilder(), project.getBuildersList().get(0));
    }

    @Test
    public void should_builder_builds_successfully_in_freestyle_project() throws Exception {
        // given
        FreeStyleProject project = jenkins.createFreeStyleProject();
        project.getBuildersList().add(new UForgeBuilder());

        // when
        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);

        // then
        jenkins.assertLogContains("UForge AppCenter plugin", build);
    }

    @Test
    public void should_builder_builds_successfully_in_scripted_pipeline() throws Exception {
        // given
        WorkflowJob job = jenkins.createProject(WorkflowJob.class, "test-scripted-pipeline");
        String pipelineScript
                = "node {\n"
                + "  uforge()\n"
                + "}";
        job.setDefinition(new CpsFlowDefinition(pipelineScript, true));

        // when
        WorkflowRun completedBuild = jenkins.assertBuildStatusSuccess(job.scheduleBuild2(0));

        // then
        jenkins.assertLogContains("UForge AppCenter plugin", completedBuild);
    }
}
