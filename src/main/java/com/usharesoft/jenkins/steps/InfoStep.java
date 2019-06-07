package com.usharesoft.jenkins.steps;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;

import com.usharesoft.jenkins.Messages;
import com.usharesoft.jenkins.launcher.UForgeLauncher;

import java.io.IOException;

import hudson.util.ArgumentListBuilder;

public class InfoStep extends HammrStep {

    private String imageId;

    public InfoStep(UForgeLauncher launcher, String url, StandardUsernamePasswordCredentials credentials, String imageId) {
        super(launcher, url, credentials, null);
        this.imageId = imageId;
    }

    @Override
    public void perform() throws InterruptedException, IOException {
        launcher.launchWithoutLogs(getHammrCommand());
    }

    ArgumentListBuilder getHammrCommand() {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(launcher.getScriptWorkspace() + "/bin/hammr");
        args.add("image");
        args.add("info");
        args.add("--url").add(url);
        args.add("-u").add(getUsername());
        args.add("-p").add(getPassword());
        args.add("--id").add(imageId);
        return args;
    }
}
