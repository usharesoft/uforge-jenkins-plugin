package com.usharesoft.jenkins;

import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.FieldSetter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.AssertJUnit.assertEquals;

public class UForgeEnvironmentVariablesTest {

    private Map<String,String> envVars = new HashMap<String,String>();

    @Spy
    @InjectMocks
    private UForgeEnvironmentVariables uForgeEnvVars;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        FieldSetter.setField(uForgeEnvVars, UForgeEnvironmentVariables.class.getDeclaredField("envVars"),  envVars);
    }

    @Test
    public void should_addEnvVar_add_env_var() {
        // given
        String key = "key";
        String value = "value";

        // when
        uForgeEnvVars.addEnvVar(key, value);

        // then
        assertEquals(value,  envVars.get(key));
    }
}
