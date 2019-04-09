package com.usharesoft.jenkins;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Label;
import hudson.model.Result;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class UForgeBuilderTest {

    private static final String URL = "http://my-forge.com/api";
    private static final String VERSION = "3.8.11";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String TEMPLATE_PATH = "./template.yml";

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void should_project_stay_unchanged_and_password_hidden_after_roundtrip_configuration() throws Exception {
        // given
        FreeStyleProject project = jenkins.createFreeStyleProject();
        project.getBuildersList().add(new UForgeBuilder(URL, VERSION, LOGIN, PASSWORD, TEMPLATE_PATH));

        // when
        project = jenkins.configRoundtrip(project);

        // then
        jenkins.assertEqualDataBoundBeans(new UForgeBuilder(URL, VERSION, LOGIN, "", TEMPLATE_PATH), project.getBuildersList().get(0));
    }

    @Test
    public void should_builder_builds_successfully_in_freestyle_project() throws Exception {
        // given
        FreeStyleProject project = jenkins.createFreeStyleProject();
        project.getBuildersList().add(new UForgeBuilder(URL, VERSION, LOGIN, PASSWORD, TEMPLATE_PATH));

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
                + "  uforge url: '" + URL + "', "
                + "version: '" + VERSION + "', "
                + "login: '" + LOGIN + "', "
                + "password: '" + PASSWORD + "', "
                + "templatePath : '" + TEMPLATE_PATH + "'\n"
                + "}";
        job.setDefinition(new CpsFlowDefinition(pipelineScript, true));

        // when
        WorkflowRun completedBuild = jenkins.assertBuildStatusSuccess(job.scheduleBuild2(0));

        // then
        jenkins.assertLogContains("UForge AppCenter plugin", completedBuild);
    }

    @Test
    public void should_build_fails_when_missing_parameters() throws Exception {
        // given
        WorkflowJob job = jenkins.createProject(WorkflowJob.class, "test-scripted-pipeline");
        String pipelineScript
                = "node {\n"
                + "  uforge "
                + "version: '" + VERSION + "', "
                + "login: '" + LOGIN + "', "
                + "password: '" + PASSWORD + "', "
                + "templatePath : '" + TEMPLATE_PATH + "'\n"
                + "}";
        job.setDefinition(new CpsFlowDefinition(pipelineScript, true));

        // when
        WorkflowRun completedBuild = jenkins.assertBuildStatus(Result.FAILURE, job.scheduleBuild2(0));

        // then
        jenkins.assertLogContains("url: UForge API URL is mandatory.", completedBuild);
    }
}
