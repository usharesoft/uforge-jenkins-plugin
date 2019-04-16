package com.usharesoft.jenkins;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import hudson.EnvVars;
import hudson.model.EnvironmentContributingAction;
import hudson.model.Run;

public class UForgeEnvironmentVariables implements EnvironmentContributingAction {

    private Map<String,String> envVars = new HashMap<>();

    public void addEnvVar(String key, String value) {
        envVars.put(key, value);
    }

    @Override
    public void buildEnvironment(@Nonnull Run<?, ?> run, @Nonnull EnvVars env) {
        if (envVars != null) env.putAll(envVars);
    }

    @CheckForNull
    @Override
    public String getIconFileName() {
        return null;
    }

    @CheckForNull
    @Override
    public String getDisplayName() {
        return null;
    }

    @CheckForNull
    @Override
    public String getUrlName() {
        return null;
    }
}
