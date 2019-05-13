package com.usharesoft.jenkins.steps;

import com.usharesoft.jenkins.launcher.UForgeLauncher;

import java.io.IOException;

import hudson.util.ArgumentListBuilder;

public abstract class UForgeStep {
    UForgeLauncher launcher;

    UForgeStep(UForgeLauncher launcher) {
        this.launcher = launcher;
    }

    public abstract void perform() throws InterruptedException, IOException;

    void printStep(String message) throws InterruptedException, IOException {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("echo");
        args.add(UForgeLauncher.CMD_PREFIX);
        args.add(message);

        launcher.launch(args, true);
    }
}
