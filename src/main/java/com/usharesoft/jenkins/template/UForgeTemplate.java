package com.usharesoft.jenkins.template;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

import hudson.FilePath;

public class UForgeTemplate {
    private TemplateReader templateReader;

    public UForgeTemplate(String filePath, FilePath workspace) {
        templateReader = createTemplateReader(filePath, workspace);
    }

    String getAbsolutePath(String filePath, FilePath workspace) {
        File file = new File(filePath);
        if (!file.isAbsolute()) {
            return workspace + "/" + filePath;
        }
        return filePath;
    }

    TemplateReader createTemplateReader(String filePath, FilePath workspace) {
        String absoluteFilePath = getAbsolutePath(filePath, workspace);

        if ("yml".equals(Files.getFileExtension(absoluteFilePath))) {
            return new YAMLReader(absoluteFilePath);
        }
        return new JSONReader(absoluteFilePath);
    }

    public boolean canPublish() throws IOException {
        return templateReader.hasAccountSection();
    }
}
