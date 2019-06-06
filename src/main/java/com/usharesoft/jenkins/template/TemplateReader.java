package com.usharesoft.jenkins.template;

import java.io.IOException;

import hudson.FilePath;

public abstract class TemplateReader {
    FilePath file;

    TemplateReader(FilePath file) {
        this.file = file;
    }

    public abstract boolean hasAccountSection() throws IOException, InterruptedException;
}
