package com.usharesoft.jenkins.template;

import java.io.IOException;

public abstract class TemplateReader {
    String file;

    TemplateReader(String file) {
        this.file = file;
    }

    public abstract boolean hasAccountSection() throws IOException;
}
