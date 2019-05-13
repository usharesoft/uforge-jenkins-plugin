package com.usharesoft.jenkins;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class UForgeBuilderTest {

    private static final String URL = "http://my-forge.com/api";
    private static final String VERSION = "3.8.11";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String TEMPLATE_PATH = "./template.yml";

    private UForgeBuilder builder;

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Before
    public void before() {
        this.builder = spy(new UForgeBuilder(URL, VERSION, LOGIN, PASSWORD, TEMPLATE_PATH));
    }

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
        doNothing().when(builder).perform(any(), any(), any(), any());
        FreeStyleProject project = jenkins.createFreeStyleProject();
        project.getBuildersList().add(builder);

        // when // then
        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
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
