package com.usharesoft.jenkins;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jenkinsci.plugins.durabletask.Controller;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.FieldSetter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.PrintStream;

import hudson.model.TaskListener;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.AssertJUnit.assertEquals;

public class UForgeLauncherTest {

    private static final String MASKED_PASSWORD = "********";

    @Spy
    @InjectMocks
    UForgeLauncher uForgeLauncher;

    @Mock
    private Controller controller;

    @Mock
    private TaskListener listener;

    @Mock
    private PrintStream logger;


    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void should_launchScript_throw_on_script_failure() throws  Exception {
        // given
        String script = "echo test";
        doReturn(controller).when(uForgeLauncher).createController(script);
        doReturn(1).when(uForgeLauncher).awaitCompletion(any(Controller.class));

        // when
        Throwable throwable = catchThrowable(() -> uForgeLauncher.launchScript(script));

        // then
        assertThat(throwable).isNotNull();
    }

    @Test
    public void should_awaitCompletion_return_when_exitStatus_not_null() throws Exception {
        // given
        doReturn(0).when(controller).exitStatus(any(), any(), any());

        // when
        int returnValue = uForgeLauncher.awaitCompletion(controller);
        // then
        assertEquals(0, returnValue);
    }

    @Test
    public void should_printLogs_mask_password_for_hammr_command() throws Exception {
        // given
        String command = "+ hammr template create --url http:/uforge/api -u login -p \"password\" --file template.yml";
        String expectedOutput = "+ hammr template create --url http:/uforge/api -u login -p " + MASKED_PASSWORD + " --file template.yml";
        ByteArrayOutputStream baos = new ByteArrayOutputStream(command.length());
        baos.write(command.getBytes(), 0, command.length());
        doReturn(logger).when(listener).getLogger();

        // when
        uForgeLauncher.printLogs(baos);

        // then
        verify(logger).println(expectedOutput);
    }
}


