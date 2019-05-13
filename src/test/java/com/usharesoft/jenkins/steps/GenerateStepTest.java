package com.usharesoft.jenkins.steps;

import com.usharesoft.jenkins.launcher.UForgeLauncher;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.FieldSetter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.AssertJUnit.assertEquals;

public class GenerateStepTest {
    private static final String URL = "url";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String TEMPLATE = "template";

    @Spy
    @InjectMocks
    private GenerateStep generateStep;

    @Mock
    private UForgeLauncher launcher;

    @Mock
    private ArgumentListBuilder args;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        FieldSetter.setField(generateStep, HammrStep.class.getDeclaredField("url"), URL);
        FieldSetter.setField(generateStep, HammrStep.class.getDeclaredField("login"), LOGIN);
        FieldSetter.setField(generateStep, HammrStep.class.getDeclaredField("password"), PASSWORD);
        FieldSetter.setField(generateStep, HammrStep.class.getDeclaredField("templatePath"), TEMPLATE);
        FieldSetter.setField(generateStep, UForgeStep.class.getDeclaredField("launcher"), launcher);
    }

    @Test
    public void should_perform_launch_hammr_command() throws IOException, InterruptedException {
        // given
        doReturn(args).when(generateStep).getHammrCommand();
        doNothing().when(generateStep).printStep(any());

        // when
        generateStep.perform();

        //then
        verify(generateStep).printStep(any());
        verify(generateStep).getHammrCommand();
        verify(launcher).launch(eq(args), eq(true));
    }

    @Test
    public void should_getHammrCommand_return_good_command() {
        // given
        doReturn(new FilePath(new File("workspace"))).when(launcher).getScriptWorkspace();

        ArgumentListBuilder expectedArgs = new ArgumentListBuilder();
        expectedArgs.add("workspace/bin/hammr");
        expectedArgs.add("template");
        expectedArgs.add("build");
        expectedArgs.add("--url").add(URL);
        expectedArgs.add("-u").add(LOGIN);
        expectedArgs.add("-p").add(PASSWORD);
        expectedArgs.add("--file").add(TEMPLATE);

        // when
        ArgumentListBuilder args = generateStep.getHammrCommand();

        //then
        assertEquals(expectedArgs.toString(), args.toString());
    }
}
