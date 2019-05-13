package com.usharesoft.jenkins.steps;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;

import com.usharesoft.jenkins.launcher.UForgeLauncher;

public abstract class HammrStep extends UForgeStep {
    String url;
    StandardUsernamePasswordCredentials credentials;
    String templatePath;

    HammrStep(UForgeLauncher launcher, String url, StandardUsernamePasswordCredentials credentials, String templatePath) {
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
