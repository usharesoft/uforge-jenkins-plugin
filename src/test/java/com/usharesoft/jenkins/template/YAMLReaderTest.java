package com.usharesoft.jenkins.template;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.AssertJUnit.assertEquals;

public class YAMLReaderTest {
    @Spy
    @InjectMocks
    private YAMLReader yamlReader;

    @Mock
    private Map<String, List<Map<String, Object>>> template;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void should_hasAccountSection_read_yaml_file() throws IOException, InterruptedException {
        // given
        doReturn(template).when(yamlReader).readYAMLFile();
        doReturn(false).when(template).containsKey("builders");
        // when
        boolean result = yamlReader.hasAccountSection();

        //then
        verify(yamlReader).readYAMLFile();
        assertEquals(false, result);
    }
}
