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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.AssertJUnit.assertEquals;

public class InfoStepTest {
    private static final String URL = "url";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String IMAGE_ID = "1234";

    @Spy
    @InjectMocks
    private InfoStep infoStep;

    @Mock
    private UForgeLauncher launcher;

    @Mock
    private ArgumentListBuilder args;

    @Mock
    private StandardUsernamePasswordCredentials credentials;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        FieldSetter.setField(infoStep, HammrStep.class.getDeclaredField("url"), URL);
        FieldSetter.setField(infoStep, HammrStep.class.getDeclaredField("credentials"), credentials);
        FieldSetter.setField(infoStep, InfoStep.class.getDeclaredField("imageId"), IMAGE_ID);
    }

    @Test
    public void should_perform_launch_hammr_command() throws IOException, InterruptedException {
        // given
        doReturn(args).when(infoStep).getHammrCommand();

        // when
        infoStep.perform();

        //then
        verify(infoStep).getHammrCommand();
        verify(launcher).launchWithoutLogs(eq(args));
    }

    @Test
    public void should_getHammrCommand_return_good_command() {
        // given
        doReturn(new FilePath(new File("workspace"))).when(launcher).getVenvDirectory();
        doReturn(LOGIN).when(infoStep).getUsername();
        doReturn(PASSWORD).when(infoStep).getPassword();

        ArgumentListBuilder expectedArgs = new ArgumentListBuilder();
        expectedArgs.add("workspace/bin/hammr");
        expectedArgs.add("image");
        expectedArgs.add("info");
        expectedArgs.add("--url").add(URL);
        expectedArgs.add("-u").add(LOGIN);
        expectedArgs.add("-p").add(PASSWORD);
        expectedArgs.add("--id").add(IMAGE_ID);

        // when
        ArgumentListBuilder args = infoStep.getHammrCommand();

        //then
        assertEquals(expectedArgs.toString(), args.toString());
    }
}
