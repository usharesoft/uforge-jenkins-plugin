package com.usharesoft.jenkins.steps;

import com.usharesoft.jenkins.launcher.UForgeLauncher;

public abstract class HammrStep extends UForgeStep {
    String url;
    String login;
    String password;
    String templatePath;

    HammrStep(UForgeLauncher launcher, String url, String login, String password, String templatePath) {
        super(launcher);
        this.url = url;
        this.login = login;
        this.password = password;
        this.templatePath = templatePath;
    }
}
