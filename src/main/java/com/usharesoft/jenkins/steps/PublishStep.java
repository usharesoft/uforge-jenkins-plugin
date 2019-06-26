package com.usharesoft.jenkins.steps;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;

import com.usharesoft.jenkins.Messages;
import com.usharesoft.jenkins.launcher.UForgeLauncher;

import java.io.IOException;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;

public class PublishStep extends HammrStep {

    public PublishStep(UForgeLauncher launcher, String url, StandardUsernamePasswordCredentials credentials, FilePath templatePath) {
        super(launcher, url, credentials, templatePath);

    }

    @Override
    public void perform() throws InterruptedException, IOException {
        printStep(Messages.Logs_steps_publish());
        launcher.launch(getHammrCommand(), true);
    }

    ArgumentListBuilder getHammrCommand() {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(launcher.getVenvDirectory() + "/bin/hammr");
        args.add("image");
        args.add("publish");
        args.add("--url").add(url);
        args.add("-u").add(credentials.getUsername());
        args.add("-p").add(getPassword());
        args.add("--file").add(templatePath);

        return args;
    }
}
