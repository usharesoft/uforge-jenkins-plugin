package com.usharesoft.jenkins.steps;

import com.usharesoft.jenkins.launcher.UForgeLauncher;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class UForgeStepTest {

    @Spy
    @InjectMocks
    private CreateStep createStep;

    @Mock
    private UForgeLauncher launcher;


    @BeforeMethod
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void should_printStep_launch_quiet_command() throws IOException, InterruptedException {
        // given
        String message = "message";

        // when
        createStep.printStep(message);

        //then
        verify(launcher).launch(any(), eq(true));
    }
}
