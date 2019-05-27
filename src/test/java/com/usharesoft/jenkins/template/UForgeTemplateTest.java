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

    private UForgeTemplate uForgetemplate;

    @BeforeMethod
    public void setUp() throws Exception {
        uForgetemplate = spy(new UForgeTemplate(FILE, WORKSPACE));
    }

    @Test
    public void should_getAbsolutePath_create_absolute_path() throws IOException, InterruptedException {
        // given
        String expectedPath = "/workspace/template.yml";

        // when
        String path = uForgetemplate.getAbsolutePath(FILE, WORKSPACE);

        //then
        assertEquals(expectedPath, path);
    }

    @Test
    public void should_createTemplateReader_return_YAMLReader_for_yml_file() throws IOException, InterruptedException {
        // given
        doReturn(FILE).when(uForgetemplate).getAbsolutePath(any(), any());

        // when
        TemplateReader newReader = uForgetemplate.createTemplateReader(FILE, WORKSPACE);

        //then
        verify(uForgetemplate).getAbsolutePath(FILE, WORKSPACE);
        assertEquals(YAMLReader.class, newReader.getClass());
    }
}
