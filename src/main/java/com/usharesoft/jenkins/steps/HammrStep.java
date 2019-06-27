package com.usharesoft.jenkins.steps;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;

import com.usharesoft.jenkins.launcher.UForgeLauncher;

import java.io.IOException;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;

public abstract class HammrStep {
    UForgeLauncher launcher;
    String url;
    StandardUsernamePasswordCredentials credentials;
    FilePath templatePath;

    public abstract void perform() throws InterruptedException, IOException;

    HammrStep(UForgeLauncher launcher, String url, StandardUsernamePasswordCredentials credentials, FilePath templatePath) {
        this.launcher = launcher;
        this.url = url;
        this.credentials = credentials;
        this.templatePath = templatePath;
    }

    void printStep(String message) throws InterruptedException, IOException {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("echo");
        args.add(UForgeLauncher.CMD_PREFIX);
        args.add(message);

        launcher.launch(args, true);
    }

    String getUsername() {
        return credentials.getUsername();
    }

    String getPassword() {
        return credentials.getPassword().getPlainText();
    }
}
