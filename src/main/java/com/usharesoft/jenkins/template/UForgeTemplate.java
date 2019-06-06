package com.usharesoft.jenkins.template;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

import hudson.FilePath;

public class UForgeTemplate {
    private TemplateReader templateReader;

    public UForgeTemplate(String filePath, FilePath workspace) throws IOException, InterruptedException {
        templateReader = createTemplateReader(filePath, workspace);
    }

    FilePath getAbsoluteFilePath(String filePath, FilePath workspace) throws IOException, InterruptedException {
        File file = new File(filePath);
        String relativeFilePath = filePath;

        if (file.isAbsolute()) {
            relativeFilePath = workspace.toURI().relativize(file.toURI()).getPath();
        }

        return new FilePath(workspace, relativeFilePath.trim());
    }

    TemplateReader createTemplateReader(String filePath, FilePath workspace) throws IOException, InterruptedException {
        FilePath absoluteFilePath = getAbsoluteFilePath(filePath, workspace);

        if ("yml".equals(Files.getFileExtension(absoluteFilePath.getName()))) {
            return new YAMLReader(absoluteFilePath);
        }
        return new JSONReader(absoluteFilePath);
    }

    public boolean canPublish() throws IOException, InterruptedException {
        return templateReader.hasAccountSection();
    }
}
