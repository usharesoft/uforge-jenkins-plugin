package com.usharesoft.jenkins;

import hudson.AbortException;
import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.Util;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;
import java.io.PrintStream;

import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;

public class UForgeBuilder extends Builder implements SimpleBuildStep {
    private final String url;
    private final String version;
    private final String login;
    private final String password;
    private final String templatePath;

    @DataBoundConstructor
    public UForgeBuilder(String url, String version, String login, String password, String templatePath) {
        this.url = Util.fixNull(url);
        this.version = Util.fixNull(version);
        this.login = Util.fixNull(login);
        this.password = Util.fixNull(password);
        this.templatePath = Util.fixNull(templatePath);
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
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
        if (version.isEmpty()) {
            logger.println(Messages.UForgeBuilder_errors_missingVersion());
            missingParameter = true;
        }
        if (login.isEmpty()) {
            logger.println(Messages.UForgeBuilder_errors_missingLogin());
            missingParameter = true;
        }
        if (password.isEmpty()) {
            logger.println(Messages.UForgeBuilder_errors_missingPassword());
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

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        checkParameters(listener.getLogger());

        listener.getLogger().println("UForge AppCenter plugin");
    }

    @Extension @Symbol("uforge")
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public FormValidation doCheckUrl(@QueryParameter String value) {
            return doCheckField(value);
        }

        public FormValidation doCheckVersion(@QueryParameter String value) {
            return doCheckField(value);
        }

        public FormValidation doCheckLogin(@QueryParameter String value) {
            return doCheckField(value);
        }

        public FormValidation doCheckPassword(@QueryParameter String value) {
            return doCheckField(value);
        }

        public FormValidation doCheckTemplatePath(@QueryParameter String value) {
            return doCheckField(value);
        }

        private FormValidation doCheckField(@QueryParameter String value) {
            if (value.isEmpty()) {
                return FormValidation.error(Messages.UForgeBuilder_DescriptorImpl_errors_missingField());
            }
            return FormValidation.ok();
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
