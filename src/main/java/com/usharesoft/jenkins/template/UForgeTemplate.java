package com.usharesoft.jenkins.template;

import com.google.common.io.Files;

import java.io.IOException;

import hudson.FilePath;

public class UForgeTemplate {
    private TemplateReader templateReader;

    public UForgeTemplate(FilePath filePath) {
        templateReader = createTemplateReader(filePath);
    }

    TemplateReader createTemplateReader(FilePath filePath) {
        if ("yml".equals(Files.getFileExtension(filePath.getName()))) {
            return new YAMLReader(filePath);
        }
        return new JSONReader(filePath);
    }

    public boolean canPublish() throws IOException, InterruptedException {
        return templateReader.hasAccountSection();
    }
}
