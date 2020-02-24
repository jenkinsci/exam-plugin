package jenkins.task;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ArgumentListBuilder;
import jenkins.internal.Remote;
import jenkins.internal.data.ReportConfiguration;
import jenkins.internal.data.TestConfiguration;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamPluginConfig;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;
import jenkins.task.TestUtil.FakeTaskListener;
import jenkins.task._exam.Messages;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Remote.class)
public class ExamTaskHelperTest {


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    Run runMock;

    private ExamTaskHelper testObject;

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        MockitoAnnotations.initMocks(this);
        testObject = new ExamTaskHelper(runMock, null);
    }

    @After
    public void tearDown() {
        testObject = null;
    }

    @Test
    public void copyArtifactsToTarget() throws Exception {
        FilePath workspace = new FilePath(
                new File("c:\\my\\path\\to\\workspace"));

        TestConfiguration tc = new TestConfiguration();
        prepareExamReportConfig(tc);
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("cmd.exe", "/C");

        testObject.copyArtifactsToTarget(workspace, tc);
    }

    @Test
    public void toWindowsCommand() throws Exception {
        int port = 8085;

        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("cmd.exe", "/C");
        ArgumentListBuilder converted = Whitebox.invokeMethod(testObject,
                "toWindowsCommand",
                args);
        assertEquals(args.toList(), converted.toList());

        args.add("--launcher.appendVmargs", "-vmargs", "-DUSE_CONSOLE=true",
                "-DRESTAPI=true",
                "-DRESTAPI_PORT=" + port);
        converted = Whitebox.invokeMethod(testObject, "toWindowsCommand", args);
        assertEquals(args.toList(), converted.toList());

        args.add("-DPath=\"C:\\this\\is\\my\\path\"");
        converted = Whitebox.invokeMethod(testObject, "toWindowsCommand", args);
        assertEquals(args.toList(), converted.toList());

        args.add("-Dnothing=");
        List<String> expected = args.toList();
        expected.remove("-Dnothing=");
        expected.add("-Dnothing=\"\"");
        converted = Whitebox.invokeMethod(testObject, "toWindowsCommand", args);
        assertEquals(expected, converted.toList());

        args.add("password", true);
        converted = Whitebox.invokeMethod(testObject, "toWindowsCommand", args);
        expected.remove("password");
        expected.add("******");
        String sExpected = expected.toString().replaceAll(",", "");
        sExpected = sExpected.replaceAll("\\]", "");
        sExpected = sExpected.replaceAll("\\[", "");
        String sConverted = converted.toString();
        assertEquals(sExpected, sConverted);
    }

    @Test()
    public void handleAdditionalArgs_noLicenseConfig() throws Exception {

        ExamPluginConfig pluginConfigMock = mock(ExamPluginConfig.class);
        when(pluginConfigMock.getLicenseHost()).thenReturn("");
        when(pluginConfigMock.getLicensePort()).thenReturn(0);

        thrown.expect(AbortException.class);
        thrown.expectMessage(Messages.EXAM_LicenseServerNotConfigured());

        testObject.handleAdditionalArgs("", new ArgumentListBuilder(),
                pluginConfigMock, null);
    }

    @Test
    public void handleAdditionalArgs() throws Exception {

        Launcher launcherMock = mock(Launcher.class);
        when(launcherMock.isUnix()).thenReturn(false);

        ExamPluginConfig pluginConfigMock = mock(ExamPluginConfig.class);
        EnvVars envar = new EnvVars();
        envar.put("testKey", "testValue");
        Whitebox.setInternalState(testObject, "env", envar);

        when(pluginConfigMock.getLicenseHost()).thenReturn("localhost");
        when(pluginConfigMock.getLicensePort()).thenReturn(8090);

        ArgumentListBuilder argsNew = testObject.handleAdditionalArgs(null,
                new ArgumentListBuilder(), pluginConfigMock, launcherMock);
        List<String> argList = argsNew.toList();
        assertTrue(argList.contains("-Dsun.jnu.encoding=UTF-8"));
        assertFalse(envar.containsKey("JAVA_OPTS"));


        String javaOpts = "-Dtest1 -Dtest2=hallo";
        argsNew = testObject.handleAdditionalArgs(javaOpts,
                new ArgumentListBuilder(), pluginConfigMock, launcherMock);
        argList = argsNew.toList();
        assertTrue(argList.contains("-Dsun.jnu.encoding=UTF-8"));
        assertTrue(argList.contains("-Dtest1"));
        assertTrue(envar.containsKey("JAVA_OPTS"));
        assertEquals("JavaOpts not extended", javaOpts, envar.get("JAVA_OPTS"));

        when(launcherMock.isUnix()).thenReturn(true);
        argsNew = testObject.handleAdditionalArgs(javaOpts,
                new ArgumentListBuilder(), pluginConfigMock, launcherMock);
        argList = argsNew.toList();
        assertTrue(argList.contains("-Dsun.jnu.encoding=UTF-8"));
        assertTrue(argList.contains("-Dtest1"));
        assertTrue(envar.containsKey("JAVA_OPTS"));
        assertEquals("JavaOpts not extended", javaOpts, envar.get("JAVA_OPTS"));
    }

    @Test()
    public void getConfigurationPath() throws IOException, InterruptedException {
        String examHome = "c:\\my\\examHome";
        Launcher launcher = new Launcher.DummyLauncher(new FakeTaskListener());
        PowerMockito.mockStatic(Remote.class);
        PowerMockito.when(Remote.fileExists(Mockito.any(), Mockito.any())).thenReturn(true);

        ExamTool examMock = mock(ExamTool.class);
        when(examMock.getHome()).thenReturn(examHome);

        when(examMock.getRelativeDataPath()).thenReturn("..\\examData");
        String returnedConfig = testObject.getConfigurationPath(launcher, examMock);
        assertEquals(examHome + "\\..\\examData\\configuration", returnedConfig);

        when(examMock.getRelativeDataPath()).thenReturn("");
        returnedConfig = testObject.getConfigurationPath(launcher, examMock);
        assertEquals(examHome + "\\configuration", returnedConfig);

        thrown.expect(AbortException.class);
        thrown.expectMessage(
                Messages.EXAM_NotExamConfigDirectory(examHome + "\\configuration\\config.ini"));
        PowerMockito.when(Remote.fileExists(Mockito.any(), Mockito.any())).thenReturn(false);
        returnedConfig = testObject.getConfigurationPath(launcher, examMock);
    }

    @Test()
    public void getPythonExePath() throws IOException, InterruptedException {
        String pythonHome = "c:\\my\\pythonHome";
        TaskListener listener = new FakeTaskListener();

        PythonInstallation pyMock = mock(PythonInstallation.class);
        when(pyMock.forNode(Mockito.any(), Mockito.any())).thenReturn(pyMock);
        when(pyMock.getHome()).thenReturn(pythonHome);

        String pythonPath = testObject.getPythonExePath(listener, pyMock, null);
        assertEquals("c:\\my\\pythonHome\\python.exe", pythonPath);
        when(pyMock.getHome()).thenReturn(pythonHome + "\\");
        pythonPath = testObject.getPythonExePath(listener, pyMock, null);
        assertEquals("c:\\my\\pythonHome\\python.exe", pythonPath);

        when(pyMock.getHome()).thenReturn("");
        thrown.expect(AbortException.class);
        thrown.expectMessage("python home not set");
        testObject.getPythonExePath(listener, pyMock, null);
    }

    private void prepareExamReportConfig(TestConfiguration config) {
        ReportConfiguration rep = new ReportConfiguration();
        rep.setProjectName("examReport");
        rep.setDbHost("host");
        rep.setDbPort(1234);
        rep.setDbSchema("schema");
        rep.setDbPassword("pass");
        rep.setDbType("type");
        rep.setDbUser("user");
        rep.setDbService("service");
        config.setReportProject(rep);
    }

}
