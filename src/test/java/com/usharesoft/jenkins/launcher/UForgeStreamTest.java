package com.usharesoft.jenkins.launcher;

import com.usharesoft.jenkins.UForgeEnvironmentVariables;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.PrintStream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class UForgeStreamTest {
    private UForgeStream uForgeStream;

    @Mock
    private UForgeEnvironmentVariables uForgeEnvironmentVariables;

    @Mock
    private PrintStream jenkinsLogger;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        uForgeStream = spy(new UForgeStream(jenkinsLogger, uForgeEnvironmentVariables));
    }

    @Test
    public void should_write_print_logs_and_fill_env_var() {
        // given
        doNothing().when(uForgeStream).println(anyString());
        doNothing().when(uForgeStream).fillApplianceIdEnvVar(anyString());
        doNothing().when(uForgeStream).fillImageIdEnvVar(anyString());
        byte[] b = "\u001B[1m\u001B[34mINFO: \u001B[0mYou provided a yaml file, checking...".getBytes();
        String expectedLogs = "INFO: You provided a yaml file, checking...";

        // when
        uForgeStream.write(b, 0, b.length);

        //then
        verify(uForgeStream).println(expectedLogs);
        verify(uForgeStream).fillApplianceIdEnvVar(expectedLogs);
        verify(uForgeStream).fillImageIdEnvVar(expectedLogs);
    }

    @Test
    public void should_fillApplianceIdEnvVar_add_env_var() {
        // given
        doNothing().when(uForgeEnvironmentVariables).addEnvVar(anyString(), anyString());

        // when
        uForgeStream.fillApplianceIdEnvVar("Template Id : 1234");

        // then
        verify(uForgeEnvironmentVariables).addEnvVar("UFORGE_APPLIANCE_ID", "1234");
    }

    @Test
    public void should_fillImageIdEnvVar_add_env_var() {
        // given
        doNothing().when(uForgeEnvironmentVariables).addEnvVar(anyString(), anyString());

        // when
        uForgeStream.fillImageIdEnvVar("Image Id : 1234");

        // then
        verify(uForgeEnvironmentVariables).addEnvVar("UFORGE_IMAGE_ID", "1234");
    }

    @Test
    public void should_fillPublishIdEnvVar_add_env_var() {
        // given
        doNothing().when(uForgeEnvironmentVariables).addEnvVar(anyString(), anyString());

        // when
        uForgeStream.fillPublishIdEnvVar("Publish ID : 1234");

        // then
        verify(uForgeEnvironmentVariables).addEnvVar("UFORGE_PUBLISH_ID", "1234");
    }

    @Test
    public void should_fillRegisteringNameEnvVar_add_env_var() {
        // given
        doNothing().when(uForgeEnvironmentVariables).addEnvVar(anyString(), anyString());

        // when
        uForgeStream.fillRegisteringNameEnvVar("| RegisteringName   |  abcd-1234   |");

        // then
        verify(uForgeEnvironmentVariables).addEnvVar("UFORGE_IMAGE_REGISTERING_NAME", "abcd-1234");
    }

    @Test
    public void should_fillCloudIdEnvVar_add_env_var() {
        // given
        doNothing().when(uForgeEnvironmentVariables).addEnvVar(anyString(), anyString());

        // when
        uForgeStream.fillCloudIdEnvVar("Cloud ID : abcd-1234");

        // then
        verify(uForgeEnvironmentVariables).addEnvVar("UFORGE_CLOUD_ID", "abcd-1234");
    }
}