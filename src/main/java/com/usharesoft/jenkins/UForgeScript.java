package com.usharesoft.jenkins;

public class UForgeScript {
    private final String url;
    private final String version;
    private final String login;
    private final String password;
    private final String templatePath;
    private final String workspace;

    UForgeScript(String url, String version, String login, String password, String templatePath, String workspace) {
        this.url = url;
        this.version = version;
        this.login = login;
        this.password = password;
        this.templatePath = templatePath;
        this.workspace = workspace;
    }

    String getScript() {
        String cmds =
                cmdInstallHammr()
                + cmdHammrCreate()
                + cmdHammrBuild();

        return wrapWithVirtualEnv(cmds);
    }

    String wrapWithVirtualEnv(String cmds) {
        return "virtualenv --python=python2.7 " + workspace + "\n"
                + ". " + workspace + "/bin/activate\n"
                + cmds
                + "deactivate";
    }

    String cmdInstallHammr() {
        return "pip install hammr==" + version + "\n";
    }

    String cmdHammrCreate() {
        return "hammr template create --url " + url + " -u " + login + " -p \"" + password + "\" --file " + templatePath + "\n";
    }

    String cmdHammrBuild() {
        return "hammr template build --url " + url + " -u " + login + " -p \"" + password + "\" --file " + templatePath + "\n";
    }
}
