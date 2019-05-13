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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.AssertJUnit.assertEquals;

public class InstallStepTest {
    private static final String VERSION = "version";

    @Spy
    @InjectMocks
    private InstallStep installStep;

    @Mock
    private UForgeLauncher launcher;

    @Mock
    private ArgumentListBuilder args;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        FieldSetter.setField(installStep, InstallStep.class.getDeclaredField("version"), VERSION);
        FieldSetter.setField(installStep, UForgeStep.class.getDeclaredField("launcher"), launcher);

        doReturn(new FilePath(new File("workspace"))).when(launcher).getScriptWorkspace();
    }

    @Test
    public void should_perform_launch_install_commands() throws IOException, InterruptedException {
        // given
        doReturn(args).when(installStep).getInstallVenvCmd();
        doReturn(args).when(installStep).getInstallHammrCmd();
        doNothing().when(installStep).printStep(any());

        // when
        installStep.perform();

        //then
        verify(installStep).printStep(any());
        verify(installStep).getInstallVenvCmd();
        verify(installStep).getInstallHammrCmd();
        verify(launcher, times(2)).launchInstall(eq(args), eq(true));
    }

    @Test
    public void should_getInstallVenvCmd_return_good_command() {
        // given
        ArgumentListBuilder expectedArgs = new ArgumentListBuilder();
        expectedArgs.add("virtualenv");
        expectedArgs.add("--python=python2.7");
        expectedArgs.add(launcher.getScriptWorkspace());

        // when
        ArgumentListBuilder args = installStep.getInstallVenvCmd();

        //then
        assertEquals(expectedArgs.toString(), args.toString());
    }

    @Test
    public void should_getInstallHammrCmd_return_good_command() {
        // given
        ArgumentListBuilder expectedArgs = new ArgumentListBuilder();
        expectedArgs.add(launcher.getScriptWorkspace() + "/bin/python2.7");
        expectedArgs.add("-m");
        expectedArgs.add("pip");
        expectedArgs.add("install");
        expectedArgs.add("hammr==" + VERSION);

        // when
        ArgumentListBuilder args = installStep.getInstallHammrCmd();

        //then
        assertEquals(expectedArgs.toString(), args.toString());
    }
}
