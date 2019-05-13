package com.usharesoft.jenkins.steps;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;

import com.usharesoft.jenkins.Messages;
import com.usharesoft.jenkins.launcher.UForgeLauncher;

import java.io.IOException;

import hudson.util.ArgumentListBuilder;

public class GenerateStep extends HammrStep {

    public GenerateStep(UForgeLauncher launcher, String url, StandardUsernamePasswordCredentials credentials, String templatePath) {
        super(launcher, url, credentials, templatePath);
    }

    @Override
    public void perform() throws InterruptedException, IOException {
        printStep(Messages.Logs_steps_generate());
        launcher.launch(getHammrCommand(), true);
    }

    ArgumentListBuilder getHammrCommand() {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(launcher.getScriptWorkspace() + "/bin/hammr");
        args.add("template");
        args.add("build");
        args.add("--url").add(url);
        args.add("-u").add(getUsername());
        args.add("-p").add(getPassword());
        args.add("--file").add(templatePath);

        return args;
    }
}
