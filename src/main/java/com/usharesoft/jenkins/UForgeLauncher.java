package com.usharesoft.jenkins;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jenkinsci.plugins.durabletask.BourneShellScript;
import org.jenkinsci.plugins.durabletask.Controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hudson.AbortException;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;

public class UForgeLauncher {
    private static final String MASKED_PASSWORD = "********";
    private Run<?, ?> run;
    private FilePath workspace;
    private Launcher launcher;
    private TaskListener listener;
    private UForgeEnvironmentVariables uForgeEnvVars;

    public UForgeLauncher(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener, UForgeEnvironmentVariables uForgeEnvVars) {
        this.run = run;
        this.workspace = workspace;
        this.launcher = launcher;
        this.listener = listener;
        this.uForgeEnvVars = uForgeEnvVars;
    }

    public void launchScript(String script) throws InterruptedException, IOException {
        Controller c = createController(script);
        if (awaitCompletion(c) != 0) {
            throw new AbortException(Messages.UForgeBuilderLauncher_errors_scriptFailure());
        }

    }

    Controller createController(String script) throws InterruptedException, IOException {
        return new BourneShellScript(script).launch(run.getEnvironment(listener), workspace, launcher, listener);
    }

    int awaitCompletion(Controller c) throws InterruptedException, IOException {
        Integer exitStatus = null;
        ByteArrayOutputStream newLogs = new ByteArrayOutputStream();

        while (exitStatus == null) {
            Thread.sleep(100);
            exitStatus = c.exitStatus(workspace, launcher, listener);
            c.writeLog(workspace, newLogs);
            printLogs(newLogs);
            newLogs.reset();
        }
        return exitStatus;
    }

    void printLogs(ByteArrayOutputStream byteArr) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(byteArr.toByteArray());
        for(String line : IOUtils.readLines(inputStream, StandardCharsets.UTF_8)) {
            fillApplianceIdEnvVar(line);
            fillImageIdEnvVar(line);
            listener.getLogger().println(sanitizeLine(line));
        }
    }

    private String sanitizeLine(String line) {
        Pattern p = Pattern.compile("(\\+ hammr.* -p )(\".*\")(.*)");
        Matcher m = p.matcher(line);
        if (m.find()) {
            line = m.group(1) + MASKED_PASSWORD + m.group(3);
        }
        return line;
    }

    private void fillApplianceIdEnvVar(String line) {
        if (line.startsWith("Template Id")) {
            String[] splitLine = line.split(" ");
            uForgeEnvVars.addEnvVar("UFORGE_APPLIANCE_ID", splitLine[splitLine.length - 1]);
        }
    }

    private void fillImageIdEnvVar(String line) {
        if (line.startsWith("Image Id")) {
            String[] splitLine = line.split(" ");
            uForgeEnvVars.addEnvVar("UFORGE_IMAGE_ID", splitLine[splitLine.length - 1]);
        }
    }
}
