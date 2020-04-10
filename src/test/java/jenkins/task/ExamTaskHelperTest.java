package jenkins.task;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Descriptor;
import hudson.model.Node;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.slaves.DumbSlave;
import hudson.util.ArgumentListBuilder;
import jenkins.internal.Remote;
import jenkins.internal.Util;
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
import org.jvnet.hudson.test.SimpleCommandLauncher;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Remote.class, Util.class, hudson.Util.class })
@PowerMockIgnore({ "javax.crypto.*" })
public class ExamTaskHelperTest {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Mock
    Run runMock;
    
    private ExamTaskHelper testObject;
    private FilePath workspace;
    private TaskListener listener;
    
    @Before
    public void setUp() throws InterruptedException, IOException {
        MockitoAnnotations.initMocks(this);
        workspace = new FilePath(new File("c:\\my\\path\\to\\workspace"));
        Launcher launcher = new Launcher.DummyLauncher(new FakeTaskListener());
        listener = new FakeTaskListener();
        testObject = new ExamTaskHelper(runMock, workspace, launcher, listener);
    }
    
    @After
    public void tearDown() {
        testObject = null;
    }
    
    @Test
    public void copyArtifactsToTarget() throws Exception {
        TestConfiguration tc = new TestConfiguration();
        prepareExamReportConfig(tc);
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("cmd.exe", "/C");
        
        testObject.copyArtifactsToTarget(tc);
    }
    
    @Test
    public void toWindowsCommand() throws Exception {
        int port = 8085;
        
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("cmd.exe", "/C");
        ArgumentListBuilder converted = Whitebox.invokeMethod(testObject, "toWindowsCommand", args);
        assertEquals(args.toList(), converted.toList());
        
        args.add("--launcher.appendVmargs", "-vmargs", "-DUSE_CONSOLE=true", "-DRESTAPI=true",
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
    
    @Test
    public void handleAdditionalArgs_noLicenseConfig() throws Exception {
        
        ExamPluginConfig pluginConfigMock = mock(ExamPluginConfig.class);
        when(pluginConfigMock.getLicenseHost()).thenReturn("");
        when(pluginConfigMock.getLicensePort()).thenReturn(0);
        
        thrown.expect(AbortException.class);
        thrown.expectMessage(Messages.EXAM_LicenseServerNotConfigured());
        
        testObject.handleAdditionalArgs("", new ArgumentListBuilder(), pluginConfigMock);
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
        
        ArgumentListBuilder argsNew = testObject
                .handleAdditionalArgs(null, new ArgumentListBuilder(), pluginConfigMock);
        List<String> argList = argsNew.toList();
        assertTrue(argList.contains("-Dsun.jnu.encoding=UTF-8"));
        assertFalse(envar.containsKey("JAVA_OPTS"));
        
        String javaOpts = "-Dtest1 -Dtest2=hallo";
        argsNew = testObject.handleAdditionalArgs(javaOpts, new ArgumentListBuilder(), pluginConfigMock);
        argList = argsNew.toList();
        assertTrue(argList.contains("-Dsun.jnu.encoding=UTF-8"));
        assertTrue(argList.contains("-Dtest1"));
        assertTrue(envar.containsKey("JAVA_OPTS"));
        assertEquals("JavaOpts not extended", javaOpts, envar.get("JAVA_OPTS"));
        
        when(launcherMock.isUnix()).thenReturn(true);
        argsNew = testObject.handleAdditionalArgs(javaOpts, new ArgumentListBuilder(), pluginConfigMock);
        argList = argsNew.toList();
        assertTrue(argList.contains("-Dsun.jnu.encoding=UTF-8"));
        assertTrue(argList.contains("-Dtest1"));
        assertTrue(envar.containsKey("JAVA_OPTS"));
        assertEquals("JavaOpts not extended", javaOpts, envar.get("JAVA_OPTS"));
    }
    
    @Test
    public void getConfigurationPath() throws IOException, InterruptedException {
        String examHome = "c:\\my\\examHome";
        PowerMockito.mockStatic(Remote.class);
        PowerMockito.when(Remote.fileExists(Mockito.any(), Mockito.any())).thenReturn(true);
        
        ExamTool examMock = mock(ExamTool.class);
        when(examMock.getHome()).thenReturn(examHome);
        
        when(examMock.getRelativeDataPath()).thenReturn("..\\examData");
        String returnedConfig = testObject.getConfigurationPath(examMock);
        assertEquals(examHome + File.separator + "..\\examData" + File.separator + "configuration", returnedConfig);
        
        when(examMock.getRelativeDataPath()).thenReturn("");
        returnedConfig = testObject.getConfigurationPath(examMock);
        assertEquals(examHome + File.separator + "configuration", returnedConfig);
        
        thrown.expect(AbortException.class);
        thrown.expectMessage(Messages.EXAM_NotExamConfigDirectory(
                examHome + File.separator + "configuration" + File.separator + "config.ini"));
        PowerMockito.when(Remote.fileExists(Mockito.any(), Mockito.any())).thenReturn(false);
        testObject.getConfigurationPath(examMock);
    }
    
    @Test
    public void getPythonExePath() throws IOException, InterruptedException, Descriptor.FormException {
        String pythonHome = "c:\\my\\pythonHome";
        
        mockStatic(Util.class);
        
        DumbSlave slave = new DumbSlave("TestSlave", "", new SimpleCommandLauncher("echo hallo"));
        BDDMockito.given(Util.workspaceToNode(workspace)).willReturn(slave);
        PythonInstallation pyMock = mock(PythonInstallation.class);
        when(pyMock.forNode(Mockito.any(), Mockito.any())).thenReturn(pyMock);
        when(pyMock.getHome()).thenReturn(pythonHome);
        
        String pythonPath = testObject.getPythonExePath(pyMock);
        assertEquals("c:\\my\\pythonHome" + File.separator + "python.exe", pythonPath);
        when(pyMock.getHome()).thenReturn(pythonHome + "\\");
        pythonPath = testObject.getPythonExePath(pyMock);
        assertEquals("c:\\my\\pythonHome\\python.exe", pythonPath);
        
        when(pyMock.getHome()).thenReturn("");
        thrown.expect(AbortException.class);
        thrown.expectMessage("python home not set");
        testObject.getPythonExePath(pyMock);
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
    
    @Test
    public void getNode() throws Exception {
        mockStatic(Util.class);
        
        DumbSlave slave = new DumbSlave("TestSlave", "", new SimpleCommandLauncher("echo hallo"));
        BDDMockito.given(Util.workspaceToNode(workspace)).willReturn(slave);
        
        Node node = testObject.getNode();
        assertEquals(slave, node);
        
        BDDMockito.given(Util.workspaceToNode(workspace)).willReturn(null);
        thrown.expect(AbortException.class);
        thrown.expectMessage(Messages.EXAM_NodeOffline());
        
        testObject.getNode();
    }
    
    @Test
    public void handleIOExceptionNormal() throws AbortException {
        mockStatic(hudson.Util.class);
        doNothing().when(hudson.Util.class);
        
        thrown.expect(AbortException.class);
        thrown.expectMessage(Messages.EXAM_ExecFailed());
        testObject.handleIOException(System.currentTimeMillis() - 2000, null, new ExamTool[] {});
    }
    
    @Test
    public void handleIOExceptionEmptyTools() throws AbortException {
        mockStatic(hudson.Util.class);
        doNothing().when(hudson.Util.class);
        
        thrown.expect(AbortException.class);
        thrown.expectMessage(Messages.EXAM_GlobalConfigNeeded());
        testObject.handleIOException(System.currentTimeMillis(), null, new ExamTool[] {});
    }
    
    @Test
    public void handleIOExceptionWithTools() throws AbortException {
        mockStatic(hudson.Util.class);
        doNothing().when(hudson.Util.class);
        
        thrown.expect(AbortException.class);
        thrown.expectMessage(Messages.EXAM_ProjectConfigNeeded());
        testObject.handleIOException(System.currentTimeMillis(), null, new ExamTool[] { null });
    }
    
    @Test
    public void getEnv() {
        EnvVars envVars = new EnvVars();
        envVars.put("test", "test");
        Whitebox.setInternalState(testObject, "env", envVars);
        EnvVars testIt = testObject.getEnv();
        assertEquals(envVars, testIt);
        assertTrue(envVars.containsKey("test"));
        assertEquals(envVars.get("test"), testIt.get("test"));
    }
    
    @Test
    public void getListener() {
        TaskListener listener = new FakeTaskListener();
        Whitebox.setInternalState(testObject, "listener", listener);
        TaskListener testIt = testObject.getListener();
        assertEquals(listener, testIt);
    }
    
    @Test
    public void getTool() throws Exception {
        mockStatic(Util.class);
        ExamTool tool = mock(ExamTool.class);
        when(tool.forNode(any(), any())).thenReturn(tool);
        
        DumbSlave slave = new DumbSlave("TestSlave", "", new SimpleCommandLauncher("echo hallo"));
        BDDMockito.given(Util.workspaceToNode(workspace)).willReturn(slave);
        
        ExamTool result = testObject.getTool(tool);
        assertEquals(tool, result);
        
        thrown.expect(AbortException.class);
        thrown.expectMessage("examTool is null");
        testObject.getTool(null);
    }
    
    @Test
    public void prepareWorkspace() throws IOException, InterruptedException {
        ArgumentListBuilder args = new ArgumentListBuilder();
        String examHome = "C:\\path\\to\\exam";
        String pathToExe = examHome + "\\exam.exe";
        PowerMockito.mockStatic(Remote.class);
        PowerMockito.when(Remote.fileExists(Mockito.any(), Mockito.any())).thenReturn(true);
        
        ExamTool tool = mock(ExamTool.class);
        when(tool.getHome()).thenReturn(examHome);
        when(tool.getExecutable(any())).thenReturn(pathToExe);
        when(tool.getRelativeDataPath()).thenReturn("..\\examData");
        
        FilePath filePath = testObject.prepareWorkspace(tool, args);
        List<String> argList = args.toList();
        assertTrue(argList.contains(pathToExe));
        assertEquals("exam", filePath.getBaseName());
    }
    
    @Test
    public void prepareWorkspaceException() throws IOException, InterruptedException {
        ArgumentListBuilder args = new ArgumentListBuilder();
        ExamTool tool = mock(ExamTool.class);
        when(tool.getExecutable(any())).thenReturn("");
        when(tool.getName()).thenReturn("test");
        
        thrown.expect(AbortException.class);
        thrown.expectMessage(Messages.EXAM_ExecutableNotFound(tool.getName()));
        testObject.prepareWorkspace(tool, args);
    }
    
}
