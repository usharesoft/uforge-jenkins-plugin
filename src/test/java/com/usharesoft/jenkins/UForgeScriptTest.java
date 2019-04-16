package com.usharesoft.jenkins;

import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.FieldSetter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.AssertJUnit.assertEquals;

public class UForgeScriptTest {
    private static final String URL = "URL";
    private static final String VERSION = "VERSION";
    private static final String LOGIN = "LOGIN";
    private static final String PASSWORD = "my-password";
    private static final String TEMPLATE_PATH = "PATH";
    private static final String WORKSPACE = "WORKSPACE";

    @Spy
    @InjectMocks
    private UForgeScript uForgeScript;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        FieldSetter.setField(uForgeScript, UForgeScript.class.getDeclaredField("url"), URL);
        FieldSetter.setField(uForgeScript, UForgeScript.class.getDeclaredField("version"), VERSION);
        FieldSetter.setField(uForgeScript, UForgeScript.class.getDeclaredField("login"), LOGIN);
        FieldSetter.setField(uForgeScript, UForgeScript.class.getDeclaredField("password"), PASSWORD);
        FieldSetter.setField(uForgeScript, UForgeScript.class.getDeclaredField("templatePath"), TEMPLATE_PATH);
        FieldSetter.setField(uForgeScript, UForgeScript.class.getDeclaredField("workspace"), WORKSPACE);
    }

    @Test
    public void should_getScript_makes_right_calls() {
        // given
        String install = "install\n";
        String create = "create\n";
        String build = "build\n";
        String expectedStript = "my-script";
        doReturn(install).when(uForgeScript).cmdInstallHammr();
        doReturn(create).when(uForgeScript).cmdHammrCreate();
        doReturn(build).when(uForgeScript).cmdHammrBuild();
        doReturn(expectedStript).when(uForgeScript).wrapWithVirtualEnv(anyString());

        // when
        String script = uForgeScript.getScript();

        //then
        verify(uForgeScript).wrapWithVirtualEnv(install + create + build);
        assertEquals(script, expectedStript);
    }

    @Test
    public void should_wrapWithVirtualEnv_returns_right_commands() {
        // given
        String commands = "commands\n";
        String expectedScript = "virtualenv --python=python2.7 " + WORKSPACE + "\n"
                + ". " + WORKSPACE + "/bin/activate\n"
                + commands
                + "deactivate";

        // when
        String script = uForgeScript.wrapWithVirtualEnv(commands);

        //then
        assertEquals(script, expectedScript);
    }

    @Test
    public void should_cmdInstallHammr_returns_right_command() {
        // given
        String expectedCommand = "pip install hammr==" + VERSION + "\n";

        // when
        String command = uForgeScript.cmdInstallHammr();

        //then
        assertEquals(command, expectedCommand);
    }

    @Test
    public void should_cmdHammrCreate_returns_right_command() {
        // given
        String expectedCommand = "hammr template create --url " + URL + " -u " + LOGIN + " -p \"" + PASSWORD + "\" --file " + TEMPLATE_PATH + "\n";

        // when
        String command = uForgeScript.cmdHammrCreate();

        //then
        assertEquals(command, expectedCommand);
    }

    @Test
    public void should_cmdHammrBuild_returns_right_command() {
        // given
        String expectedCommand = "hammr template build --url " + URL + " -u " + LOGIN + " -p \"" + PASSWORD + "\" --file " + TEMPLATE_PATH + "\n";

        // when
        String command = uForgeScript.cmdHammrBuild();

        //then
        assertEquals(command, expectedCommand);
    }
}
