package com.usharesoft.jenkins.steps;

import com.usharesoft.jenkins.Messages;
import com.usharesoft.jenkins.launcher.UForgeLauncher;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
        downloadVirtualenv();
        launcher.launchInstall(getExtractVenvCmd(), true);
        launcher.launchInstall(getInitVenvCmd(), true);
        launcher.launchInstall(getInstallHammrCmd(), true);
    }

    void downloadVirtualenv() {
        try {
            FileUtils.copyURLToFile(
                    new URL("https://github.com/pypa/virtualenv/tarball/16.6.1"),
                    new File(launcher.getWorkspace() + "/virtualenv.tar.gz"),
                    1000,
                    1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ArgumentListBuilder getExtractVenvCmd() {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("tar");
        args.add("xvfz");
        args.add("virtualenv.tar.gz");

        return args;
    }

    ArgumentListBuilder getInitVenvCmd() {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("python2.7");
        args.add("pypa-virtualenv-ce9343c/virtualenv.py");
        args.add(launcher.getVenvDirectory());

        return args;
    }

    ArgumentListBuilder getInstallHammrCmd() {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(launcher.getVenvDirectory() + "/bin/python2.7");
        args.add("-m");
        args.add("pip");
        args.add("install");
        args.add("hammr==" + version);

        return args;
    }
}
