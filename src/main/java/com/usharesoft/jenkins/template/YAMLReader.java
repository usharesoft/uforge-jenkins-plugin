package com.usharesoft.jenkins.template;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import hudson.FilePath;

public class YAMLReader extends TemplateReader {

    public YAMLReader(FilePath file) {
        super(file);
    }

    Map<String, List<Map<String, Object>>> readYAMLFile() throws InterruptedException, IOException {
        Yaml yaml = new Yaml();

        try (InputStream inputStream = file.read()) {
            return yaml.load(inputStream);
        }
    }

    @Override
    public boolean hasAccountSection() throws IOException, InterruptedException {
        Map<String, List<Map<String, Object>>> template = readYAMLFile();

        if (template != null && template.containsKey("builders")) {
            List<Map<String, Object>> builders = template.get("builders");
            for (Map<String, Object> builder : builders) {
                if (builder.containsKey("account")) {
                    return true;
                }
            }
        }
        return false;
    }
}
