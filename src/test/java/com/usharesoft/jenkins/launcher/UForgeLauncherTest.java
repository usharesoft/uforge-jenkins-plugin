package com.usharesoft.jenkins.launcher;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.FieldSetter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import hudson.model.TaskListener;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class UForgeLauncherTest {

    @Spy
    @InjectMocks
    private UForgeLauncher uForgeLauncher;

    @Mock
    private TaskListener listener;

    @Mock
    private UForgeStream logger;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        FieldSetter.setField(uForgeLauncher, UForgeLauncher.class.getDeclaredField("listener"), listener);
        FieldSetter.setField(uForgeLauncher, UForgeLauncher.class.getDeclaredField("logger"), logger);
    }

    @Test
    public void should_init_makes_right_calls() throws IOException, InterruptedException {
        // given
        doNothing().when(uForgeLauncher).initWorkspace();
        doNothing().when(uForgeLauncher).cleanWorkspace();
        doNothing().when(uForgeLauncher).initLogger(any());

        // when
        uForgeLauncher.init(any());

        //then
        verify(uForgeLauncher).initWorkspace();
        verify(uForgeLauncher).cleanWorkspace();
        verify(uForgeLauncher).initLogger(any());
    }
}
