package com.usharesoft.jenkins.template;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONReader extends TemplateReader {

    public JSONReader(String file) {
        super(file);
    }

    JSONObject readJSONFile() throws IOException {
        JSONParser jsonParser = new JSONParser();

        try (Reader reader = new InputStreamReader(new FileInputStream(file), "UTF-8")) {
            return (JSONObject) jsonParser.parse(reader);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean hasAccountSection() throws IOException {
        JSONObject template = readJSONFile();
        if (template == null) {
            return false;
        }

        if (template.containsKey("builders")) {
            JSONArray builders = (JSONArray) template.get("builders");
            for (Object builder : builders) {
                if (((JSONObject) builder).containsKey("account")) {
                    return true;
                }
            }
        }
        return false;
    }
}
