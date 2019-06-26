package com.usharesoft.jenkins.steps;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;

import com.usharesoft.jenkins.Messages;
import com.usharesoft.jenkins.launcher.UForgeLauncher;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.util.Base64;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import hudson.AbortException;
import hudson.util.ArgumentListBuilder;

public class InstallStep extends HammrStep {
    private String version;

    public InstallStep(UForgeLauncher launcher, String url, StandardUsernamePasswordCredentials credentials, String templatePath) {
        super(launcher, url, credentials, templatePath);
    }

    @Override
    public void perform() throws InterruptedException, IOException {
        printStep(Messages.Logs_steps_install());
        version = getUForgeVersion();
        launcher.launchInstall(getInstallVenvCmd(), true);
        launcher.launchInstall(getInstallHammrCmd(), true);
    }

    String getUForgeVersion() throws IOException {
        try {
            URL requestUrl = new URL(url + "/status");
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/xml");

            String credentials = getUsername() + ":" + getPassword();
            String encodedAuthorization = Base64.getEncoder().encodeToString(credentials.getBytes("UTF-8"));
            connection.setRequestProperty("Authorization", "Basic "+ encodedAuthorization);

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            handleStatus(connection.getResponseCode());

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            connection.disconnect();

            return getVersionFromResponse(content);

        } catch (MalformedURLException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
            throw new AbortException(Messages.UForgeInstall_version_errors_unknownError());
        } catch (NoRouteToHostException e) {
            throw new AbortException(Messages.UForgeInstall_version_errors_serverUnreachable());
        }
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

    void handleStatus(int status) throws AbortException {
        if (status == 401) {
            throw new AbortException(Messages.UForgeInstall_version_errors_wrongCredentials());
        } else if (status != 200) {
            throw new AbortException(Messages.UForgeInstall_version_errors_unknownError());
        }
    }

    String getVersionFromResponse(StringBuilder content) throws ParserConfigurationException, IOException, SAXException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new StringReader(content.toString())));

        NodeList errNodes = doc.getElementsByTagName("ns0:serviceStatus");

        Element err = (Element)errNodes.item(0);
        return err.getElementsByTagName("version").item(0).getTextContent();
    }
}
