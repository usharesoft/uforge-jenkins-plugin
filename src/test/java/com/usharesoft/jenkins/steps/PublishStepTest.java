package com.usharesoft.jenkins.steps;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;

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

public class PublishStepTest {
    private static final String URL = "url";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final FilePath TEMPLATE = new FilePath(new File("template"));


    @Spy
    @InjectMocks
    private PublishStep publishStep;

    @Mock
    private UForgeLauncher launcher;

    @Mock
    private ArgumentListBuilder args;

    @Mock
    private StandardUsernamePasswordCredentials credentials;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        FieldSetter.setField(publishStep, HammrStep.class.getDeclaredField("url"), URL);
        FieldSetter.setField(publishStep, HammrStep.class.getDeclaredField("credentials"), credentials);
        FieldSetter.setField(publishStep, HammrStep.class.getDeclaredField("templatePath"), TEMPLATE);
    }

    @Test
    public void should_perform_launch_hammr_command() throws IOException, InterruptedException {
        // given
        doReturn(args).when(publishStep).getHammrCommand();
        doNothing().when(publishStep).printStep(any());

        // when
        publishStep.perform();

        //then
        verify(publishStep).printStep(any());
        verify(publishStep).getHammrCommand();
        verify(launcher).launch(eq(args), eq(true));
    }

    @Test
    public void should_getHammrCommand_return_good_command() {
        // given
        doReturn(new FilePath(new File("workspace"))).when(launcher).getVenvDirectory();
        doReturn(LOGIN).when(credentials).getUsername();
        doReturn(PASSWORD).when(publishStep).getPassword();

        ArgumentListBuilder expectedArgs = new ArgumentListBuilder();
        expectedArgs.add("workspace/bin/hammr");
        expectedArgs.add("image");
        expectedArgs.add("publish");
        expectedArgs.add("--url").add(URL);
        expectedArgs.add("-u").add(LOGIN);
        expectedArgs.add("-p").add(PASSWORD);
        expectedArgs.add("--file").add(TEMPLATE);

        // when
        ArgumentListBuilder args = publishStep.getHammrCommand();

        //then
        assertEquals(expectedArgs.toString(), args.toString());
    }
}
