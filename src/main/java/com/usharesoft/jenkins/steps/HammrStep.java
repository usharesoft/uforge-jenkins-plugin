package com.usharesoft.jenkins.steps;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;

import com.usharesoft.jenkins.launcher.UForgeLauncher;

import hudson.FilePath;

public abstract class HammrStep extends UForgeStep {
    String url;
    StandardUsernamePasswordCredentials credentials;
    FilePath templatePath;

    HammrStep(UForgeLauncher launcher, String url, StandardUsernamePasswordCredentials credentials, FilePath templatePath) {
        super(launcher);
        this.url = url;
        this.credentials = credentials;
        this.templatePath = templatePath;
    }

    String getUsername() {
        return credentials.getUsername();
    }

    String getPassword() {
        return credentials.getPassword().getPlainText();
    }
}
