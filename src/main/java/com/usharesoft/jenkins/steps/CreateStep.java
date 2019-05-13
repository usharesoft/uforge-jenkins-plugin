package com.usharesoft.jenkins.steps;

import com.usharesoft.jenkins.Messages;
import com.usharesoft.jenkins.launcher.UForgeLauncher;

import java.io.IOException;

import hudson.util.ArgumentListBuilder;

public class CreateStep extends HammrStep {

    public CreateStep(UForgeLauncher launcher, String url, String login, String password, String templatePath) {
        super(launcher, url, login, password, templatePath);
    }

    @Override
    public void perform() throws InterruptedException, IOException {
        printStep(Messages.Logs_steps_create());
        launcher.launch(getHammrCommand(), true);
    }

    ArgumentListBuilder getHammrCommand() {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(launcher.getScriptWorkspace() + "/bin/hammr");
        args.add("template");
        args.add("create");
        args.add("--url").add(url);
        args.add("-u").add(login);
        args.add("-p").add(password);
        args.add("--file").add(templatePath);

        return args;
    }
}
