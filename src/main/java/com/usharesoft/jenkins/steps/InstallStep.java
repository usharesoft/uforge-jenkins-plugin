package com.usharesoft.jenkins.steps;

import com.usharesoft.jenkins.Messages;
import com.usharesoft.jenkins.launcher.UForgeLauncher;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
        decompress(launcher.getWorkspace() + "/virtualenv.tar.gz", new File(launcher.getWorkspace().getRemote()));
        launcher.launchInstall(getInitVenvCmd(), true);
        launcher.launchInstall(getInstallHammrCmd(), true);
    }

    void downloadVirtualenv() {
        try {
            FileUtils.copyURLToFile(
                    new URL("https://github.com/pypa/virtualenv/tarball/16.6.1"),
                    new File(launcher.getWorkspace() + "/virtualenv.tar.gz"),
                    5000,
                    5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void decompress(String in, File out) throws IOException {
        try (TarArchiveInputStream fin = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(in)))) {
            TarArchiveEntry entry;
            while ((entry = fin.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                File curfile = new File(out, entry.getName());
                File parent = curfile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                IOUtils.copy(fin, new FileOutputStream(curfile));
            }
        }
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
