package com.usharesoft.jenkins.template;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class YAMLReader extends TemplateReader {

    public YAMLReader(String file) {
        super(file);
    }

    Map<String, List<Map<String, Object>>> readYAMLFile() throws IOException {
        Yaml yaml = new Yaml();

        try (InputStream inputStream = new FileInputStream(file)) {
            return yaml.load(inputStream);
        }
    }

    @Override
    public boolean hasAccountSection() throws IOException {
        Map<String, List<Map<String, Object>>> template = readYAMLFile();

        if (template.containsKey("builders")) {
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
