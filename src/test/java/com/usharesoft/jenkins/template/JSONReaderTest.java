package com.usharesoft.jenkins.template;

import org.json.simple.JSONObject;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.AssertJUnit.assertEquals;

public class JSONReaderTest {
    @Spy
    @InjectMocks
    private JSONReader jsonReader;

    @Mock
    private JSONObject template;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void should_hasAccountSection_read_yaml_file() throws IOException {
        // given
        doReturn(template).when(jsonReader).readJSONFile();
        doReturn(false).when(template).containsKey("builders");
        // when
        boolean result = jsonReader.hasAccountSection();

        //then
        verify(jsonReader).readJSONFile();
        assertEquals(false, result);
    }
}
