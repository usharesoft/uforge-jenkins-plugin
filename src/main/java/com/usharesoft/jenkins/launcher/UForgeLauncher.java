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
    private FilePath scriptWorkspace;
    private FilePath jobWorkspace;
    private Launcher launcher;
    private TaskListener listener;
    private UForgeStream logger;

    public UForgeLauncher(Run<?, ?> run, FilePath jobWorkspace, Launcher launcher, TaskListener listener) {
        this.run = run;
        this.jobWorkspace = jobWorkspace;
        this.launcher = launcher;
        this.listener = listener;
    }

    public FilePath getScriptWorkspace() {
        return scriptWorkspace;
    }

    public void launch(ArgumentListBuilder command, boolean quiet) throws InterruptedException, IOException {
        if (0 != launcher.launch().cmds(command).stdout(logger).pwd(jobWorkspace).envs(run.getEnvironment(listener)).quiet(quiet).join()) {
            throw new AbortException(Messages.Logs_errors_scriptFailure());
        }
    }

    public void launchInstall(ArgumentListBuilder command, boolean quiet) throws InterruptedException, IOException {
        if (0 != launcher.launch().cmds(command).pwd(jobWorkspace).envs(run.getEnvironment(listener)).quiet(quiet).join()) {
            throw new AbortException(Messages.Logs_errors_scriptFailure());
        }
    }

    public void init(UForgeEnvironmentVariables uForgeEnvVars) throws InterruptedException, IOException {
        initScriptWorkspace();
        cleanVenv();
        initLogger(uForgeEnvVars);
    }

    void initScriptWorkspace() throws IOException, InterruptedException {
        scriptWorkspace = jobWorkspace.child("uforge");
        if (!scriptWorkspace.exists()) {
            scriptWorkspace.mkdirs();
        }
    }

    void cleanVenv() throws InterruptedException, IOException {
        scriptWorkspace.deleteContents();
    }

    void initLogger(UForgeEnvironmentVariables uForgeEnvVars) {
        logger = new UForgeStream(listener.getLogger(), uForgeEnvVars);
    }
}
