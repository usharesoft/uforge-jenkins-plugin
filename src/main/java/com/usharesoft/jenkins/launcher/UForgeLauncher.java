package com.usharesoft.jenkins.launcher;

import com.usharesoft.jenkins.Messages;
import com.usharesoft.jenkins.UForgeEnvironmentVariables;

import java.io.IOException;

import hudson.AbortException;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ArgumentListBuilder;

public class UForgeLauncher {
    public static final String CMD_PREFIX = "[" + Messages.Logs_logs_prefix() + "]";

    private Run<?, ?> run;
    private FilePath venvDirectory;
    private FilePath workspace;
    private Launcher launcher;
    private TaskListener listener;
    private UForgeStream logger;

    public UForgeLauncher(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) {
        this.run = run;
        this.workspace = workspace;
        this.launcher = launcher;
        this.listener = listener;
    }

    public FilePath getVenvDirectory() {
        return venvDirectory;
    }

    public void launch(ArgumentListBuilder command, boolean hideCommand) throws InterruptedException, IOException {
        if (0 != launcher.launch().cmds(command).stdout(logger).pwd(workspace).envs(run.getEnvironment(listener)).quiet(hideCommand).join()) {
            throw new AbortException(Messages.Logs_errors_scriptFailure());
        }
    }

    public void launchInstall(ArgumentListBuilder command, boolean hideCommand) throws InterruptedException, IOException {
        if (0 != launcher.launch().cmds(command).pwd(workspace).envs(run.getEnvironment(listener)).quiet(hideCommand).join()) {
            throw new AbortException(Messages.Logs_errors_scriptFailure());
        }
    }

    public void launchWithoutLogs(ArgumentListBuilder command) throws InterruptedException, IOException {
        logger.setQuiet(true);
        launch(command, true);
        logger.setQuiet(false);
    }

    public void init(UForgeEnvironmentVariables uForgeEnvVars) throws InterruptedException, IOException {
        initWorkspace();
        cleanWorkspace();
        initLogger(uForgeEnvVars);
    }

    void initWorkspace() throws IOException, InterruptedException {
        venvDirectory = workspace.child("venv");
        if (!workspace.exists()) {
            workspace.mkdirs();
        }
        if (!venvDirectory.exists()) {
            venvDirectory.mkdirs();
        }
    }

    void cleanWorkspace() throws InterruptedException, IOException {
        workspace.deleteContents();
    }

    void initLogger(UForgeEnvironmentVariables uForgeEnvVars) {
        logger = new UForgeStream(listener.getLogger(), uForgeEnvVars);
    }
}
