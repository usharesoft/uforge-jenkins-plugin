package com.usharesoft.jenkins.steps;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;

import com.usharesoft.jenkins.Messages;
import com.usharesoft.jenkins.launcher.UForgeLauncher;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
import hudson.FilePath;
import hudson.util.ArgumentListBuilder;

public class InstallStep extends HammrStep {
    private String version;

    public InstallStep(UForgeLauncher launcher, String url, StandardUsernamePasswordCredentials credentials, FilePath templatePath) {
        super(launcher, url, credentials, templatePath);
    }

    @Override
    public void perform() throws InterruptedException, IOException {
        printStep(Messages.Logs_steps_install());
        version = getUForgeVersion();

        FilePath venvSources = new FilePath(launcher.getWorkspace(), launcher.getWorkspace() + "/virtualenv.tar.gz");
        downloadVirtualenv(venvSources);
        decompress(venvSources);

        launcher.launchInstall(getInitVenvCmd(), true);
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

            int status = connection.getResponseCode();
            if (status == 401) {
                throw new AbortException(Messages.UForgeInstall_version_errors_wrongCredentials());
            } else if (status != 200) {
                throw new AbortException(Messages.UForgeInstall_version_errors_unknownError());
            }

            String version = getVersionFromResponse(connection.getInputStream());

            connection.disconnect();

            return version;

        } catch (MalformedURLException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
            throw new AbortException(Messages.UForgeInstall_version_errors_unknownError());
        } catch (NoRouteToHostException e) {
            throw new AbortException(Messages.UForgeInstall_version_errors_serverUnreachable());
        }
    }

    void downloadVirtualenv(FilePath venvSources) throws InterruptedException, IOException {
        try (InputStream in = new URL("https://github.com/pypa/virtualenv/tarball/16.6.1").openStream()) {
            venvSources.copyFrom(in);
        }
    }

    void decompress(FilePath venvSources) throws IOException, InterruptedException {
        venvSources.untar(launcher.getWorkspace(), FilePath.TarCompression.GZIP);
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

    String getVersionFromResponse(InputStream stream) throws ParserConfigurationException, IOException, SAXException {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new StringReader(content.toString())));

        Element status = (Element) doc.getElementsByTagName("ns0:serviceStatus").item(0);

        return status.getElementsByTagName("version").item(0).getTextContent();
    }
}
