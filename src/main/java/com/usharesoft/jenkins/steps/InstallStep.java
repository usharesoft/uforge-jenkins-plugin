package com.usharesoft.jenkins.steps;

import com.usharesoft.jenkins.Messages;
import com.usharesoft.jenkins.launcher.UForgeLauncher;

import java.io.IOException;

import hudson.util.ArgumentListBuilder;

public class InstallStep extends UForgeStep {
    private String version;

    public InstallStep(UForgeLauncher launcher, String version) {
        super(launcher);
        this.version = version;
    }

    @Override
    public void perform() throws InterruptedException, IOException {
        printStep(Messages.Logs_steps_install());
        launcher.launchInstall(getInstallVenvCmd(), true);
        launcher.launchInstall(getInstallHammrCmd(), true);
    }

    ArgumentListBuilder getInstallVenvCmd() {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("virtualenv");
        args.add("--python=python2.7");
        args.add(launcher.getScriptWorkspace());

        return args;
    }

    ArgumentListBuilder getInstallHammrCmd() {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(launcher.getScriptWorkspace() + "/bin/python2.7");
        args.add("-m");
        args.add("pip");
        args.add("install");
        args.add("hammr==" + version);

        return args;
    }
}
