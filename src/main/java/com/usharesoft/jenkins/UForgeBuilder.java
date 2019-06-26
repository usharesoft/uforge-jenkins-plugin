package com.usharesoft.jenkins;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;

import com.usharesoft.jenkins.launcher.UForgeLauncher;
import com.usharesoft.jenkins.steps.InfoStep;
import com.usharesoft.jenkins.steps.InstallStep;
import com.usharesoft.jenkins.steps.CreateStep;
import com.usharesoft.jenkins.steps.GenerateStep;
import com.usharesoft.jenkins.steps.PublishStep;
import com.usharesoft.jenkins.template.UForgeTemplate;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;

import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.security.ACL;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;

public class UForgeBuilder extends Builder implements SimpleBuildStep {
    private final String url;
    private final String credentialsId;
    private final String templatePath;

    @DataBoundConstructor
    public UForgeBuilder(String url, String credentialsId, String templatePath) {
        this.url = Util.fixNull(url);
        this.credentialsId = Util.fixNull(credentialsId);
        this.templatePath = Util.fixNull(templatePath);
    }

    public String getUrl() {
        return url;
    }

    public String getCredentialsId() {
        return credentialsId;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    private void checkParameters(PrintStream logger) throws AbortException {
        boolean missingParameter = false;
        if (url.isEmpty()) {
            logger.println(Messages.UForgeBuilder_errors_missingUrl());
            missingParameter = true;
        }
        if (credentialsId.isEmpty()) {
            logger.println(Messages.UForgeBuilder_errors_missingCredentialsId());
            missingParameter = true;
        }
        if (templatePath.isEmpty()) {
            logger.println(Messages.UForgeBuilder_errors_missingTemplatePath());
            missingParameter = true;
        }
        if (missingParameter) {
            throw new AbortException(Messages.UForgeBuilder_errors_missingParameter());
        }
    }

    StandardUsernamePasswordCredentials retrieveCredentials(Run<?, ?> run) throws AbortException {
        StandardUsernamePasswordCredentials credentials = CredentialsProvider.findCredentialById(credentialsId, StandardUsernamePasswordCredentials.class, run, Collections.<DomainRequirement>emptyList());
        if (credentials == null) {
            throw new AbortException(Messages.UForgeBuilder_errors_credentialsNotFound());
        }
        return credentials;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        checkParameters(listener.getLogger());

        UForgeEnvironmentVariables envAction = new UForgeEnvironmentVariables();
        StandardUsernamePasswordCredentials credentials = retrieveCredentials(run);
        UForgeTemplate uForgeTemplate = new UForgeTemplate(templatePath, workspace);

        UForgeLauncher uForgeLauncher = new UForgeLauncher(run, workspace, launcher, listener);
        uForgeLauncher.init(envAction);

        InstallStep installStep = new InstallStep(uForgeLauncher, url, credentials, templatePath);
        installStep.perform();

        CreateStep createStep = new CreateStep(uForgeLauncher, url, credentials, templatePath);
        createStep.perform();

        GenerateStep generateStep = new GenerateStep(uForgeLauncher, url, credentials, templatePath);
        generateStep.perform();

        String imageId = envAction.getEnvVar("UFORGE_IMAGE_ID");
        if (!imageId.isEmpty()) {
            InfoStep infoStep = new InfoStep(uForgeLauncher, url, credentials, imageId);
            infoStep.perform();
        }

        if (uForgeTemplate.canPublish()) {
            PublishStep publishStep = new PublishStep(uForgeLauncher, url, credentials, templatePath);
            publishStep.perform();
        }

        run.addAction(envAction);
    }

    @Extension @Symbol("uforge")
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        private FormValidation doCheckField(@QueryParameter String value) {
            if (value.isEmpty()) {
                return FormValidation.error(Messages.UForgeBuilder_DescriptorImpl_errors_missingField());
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckUrl(@QueryParameter String value) {
            return doCheckField(value);
        }

        public FormValidation doCheckCredentialsId(@AncestorInPath Item context, @QueryParameter String value) {
            Jenkins jenkins = Jenkins.getInstanceOrNull();
            if (jenkins == null) {
                return FormValidation.ok();
            }
            // comes from the consumer documentation of the credentials plugin
            if (context == null) {
                if (!jenkins.hasPermission(Jenkins.ADMINISTER)) {
                    return FormValidation.ok();
                }
            } else {
                if (!context.hasPermission(Item.EXTENDED_READ)
                        && !context.hasPermission(CredentialsProvider.USE_ITEM)) {
                    return FormValidation.ok();
                }
            }
            if (StringUtils.isBlank(value)) {
                return FormValidation.error(Messages.UForgeBuilder_DescriptorImpl_errors_missingField());
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckTemplatePath(@QueryParameter String value) {
            return doCheckField(value);
        }

        public ListBoxModel doFillCredentialsIdItems(@AncestorInPath Item context, @QueryParameter String credentialsId) {
            StandardListBoxModel result = new StandardListBoxModel();
            Jenkins jenkins = Jenkins.getInstanceOrNull();
            if (jenkins == null) {
                return result;
            }
            // comes from the consumer documentation of the credentials plugin
            if (context == null) {
                if (!jenkins.hasPermission(Jenkins.ADMINISTER)) {
                    return result.includeCurrentValue(credentialsId);
                }
            } else {
                if (!context.hasPermission(Item.EXTENDED_READ)
                        && !context.hasPermission(CredentialsProvider.USE_ITEM)) {
                    return result.includeCurrentValue(credentialsId);
                }
            }
            return result
                    .includeAs(ACL.SYSTEM, jenkins, StandardUsernamePasswordCredentials.class)
                    .includeCurrentValue(credentialsId);
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.UForgeBuilder_DescriptorImpl_displayName();
        }
    }
}
