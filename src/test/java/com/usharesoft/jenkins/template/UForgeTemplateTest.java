package com.usharesoft.jenkins.template;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import hudson.FilePath;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.testng.AssertJUnit.assertEquals;

public class UForgeTemplateTest {
    private static final String FILE = "template.yml";
    private static final FilePath WORKSPACE = new FilePath(new File("/workspace"));
    private static final FilePath ABSOLUTE_PATH = new FilePath(new File("/workspace/template.yml"));

    private UForgeTemplate uForgetemplate;

    @BeforeMethod
    public void setUp() throws IOException, InterruptedException {
        uForgetemplate = spy(new UForgeTemplate(FILE, WORKSPACE));
    }

    @Test
    public void should_getAbsoluteFilePath_create_absolute_path() throws IOException, InterruptedException {
        // given

        // when
        FilePath path = uForgetemplate.getAbsoluteFilePath(FILE, WORKSPACE);

        //then
        assertEquals(ABSOLUTE_PATH, path);
    }

    @Test
    public void should_createTemplateReader_return_YAMLReader_for_yml_file() throws IOException, InterruptedException {
        // given
        doReturn(ABSOLUTE_PATH).when(uForgetemplate).getAbsoluteFilePath(any(), any());

        // when
        TemplateReader newReader = uForgetemplate.createTemplateReader(FILE, WORKSPACE);

        //then
        verify(uForgetemplate).getAbsoluteFilePath(FILE, WORKSPACE);
        assertEquals(YAMLReader.class, newReader.getClass());
    }
}
