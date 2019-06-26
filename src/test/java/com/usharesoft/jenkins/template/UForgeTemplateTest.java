package com.usharesoft.jenkins.template;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import hudson.FilePath;
import static org.mockito.Mockito.spy;
import static org.testng.AssertJUnit.assertEquals;

public class UForgeTemplateTest {
    private static final FilePath ABSOLUTE_PATH = new FilePath(new File("/workspace/template.yml"));

    private UForgeTemplate uForgetemplate;

    @BeforeMethod
    public void setUp() {
        uForgetemplate = spy(new UForgeTemplate(ABSOLUTE_PATH));
    }

    @Test
    public void should_createTemplateReader_return_YAMLReader_for_yml_file() {
        // given

        // when
        TemplateReader newReader = uForgetemplate.createTemplateReader(ABSOLUTE_PATH);

        //then
        assertEquals(YAMLReader.class, newReader.getClass());
    }
}
