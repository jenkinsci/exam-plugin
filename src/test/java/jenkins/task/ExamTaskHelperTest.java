package jenkins.task;

import Utils.Mocks;
import Utils.Whitebox;
import hudson.*;
import hudson.model.*;
import hudson.slaves.DumbSlave;
import hudson.util.ArgumentListBuilder;
import jakarta.ws.rs.core.Response;
import jenkins.internal.Util;
import jenkins.internal.*;
import jenkins.internal.data.ApiVersion;
import jenkins.internal.data.ExamStatus;
import jenkins.internal.data.ReportConfiguration;
import jenkins.internal.data.TestConfiguration;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamPluginConfig;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;
import jenkins.task.TestUtil.FakeExamLauncher;
import jenkins.task.TestUtil.FakeTaskListener;
import jenkins.task._exam.Messages;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.SimpleCommandLauncher;
import org.jvnet.hudson.test.WithoutJenkins;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExamTaskHelperTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    Run runMock;

    @Mock
    ExamTool toolMock;

    @Mock
    Task taskMock;

    private ExamTaskHelper testObject;
    private FilePath workspace;
    private TaskListener taskListener;
    private Launcher launcher;
    private String examHome = "C:\\path\\to\\exam";
    private String pathToExe = examHome + "\\exam.exe";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        MockedStatic<Remote> remoteMocked = Mocks.mockStatic(Remote.class);
        remoteMocked.when(() -> Remote.fileExists(Mockito.any(), Mockito.any())).thenReturn(true);

        workspace = new FilePath(new File("c:\\my\\path\\to\\workspace"));
        launcher = new Launcher.DummyLauncher(new FakeTaskListener());
        taskListener = new FakeTaskListener();
        testObject = new ExamTaskHelper();
        testObject.setRun(runMock);
        testObject.setWorkspace(workspace);
        testObject.setLauncher(launcher);
        testObject.setTaskListener(taskListener);
    }

    @After
    public void tearDown() {
        testObject = null;
        Mocks.resetMocks();
    }

    @Test
    @WithoutJenkins
    public void printResult() throws Exception {
        TaskListener taskListenerMock = mock(FakeTaskListener.class);
        when(taskListenerMock.getLogger()).thenCallRealMethod();
        Result resultMock = Mockito.mock(Result.class);
        Whitebox.setInternalState(testObject, "taskListener", taskListenerMock);

        when(runMock.getResult()).thenReturn(null);
        Whitebox.invokeMethod(testObject, "printResult");
        verify(taskListenerMock, never()).getLogger();

        when(runMock.getResult()).thenReturn(resultMock);
        Whitebox.invokeMethod(testObject, "printResult");
        verify(taskListenerMock, times(1)).getLogger();
    }

    @Test
    @WithoutJenkins
    public void failTask() throws Exception {
        String testString = "testString";
        thrown.expect(AbortException.class);
        thrown.expectMessage(testString);
        Whitebox.invokeMethod(testObject, "failTask", testString);
    }

    @Test
    @WithoutJenkins
    public void disconnectAndCloseEXAM() throws Exception {
        Class[] types = new Class[]{ClientRequest.class, Proc.class, Integer.TYPE};
        ClientRequest clientRequestMock = mock(ClientRequest.class);
        Whitebox.invokeMethod(testObject, "disconnectAndCloseEXAM", types, clientRequestMock, null, 1000);
        verify(clientRequestMock).disconnectClient(any(), anyInt());

        Proc procMock = mock(Proc.class);
        when(procMock.isAlive()).thenReturn(Boolean.FALSE);
        Whitebox.invokeMethod(testObject, "disconnectAndCloseEXAM", types, clientRequestMock, procMock, 1000);
        verify(procMock, never()).joinWithTimeout(10, TimeUnit.SECONDS, taskListener);

        when(procMock.isAlive()).thenReturn(Boolean.TRUE);
        Whitebox.invokeMethod(testObject, "disconnectAndCloseEXAM", types, clientRequestMock, procMock, 1000);
        verify(procMock).joinWithTimeout(10, TimeUnit.SECONDS, taskListener);
    }

    @Test
    public void perform_noLicense() throws IOException, InterruptedException, Descriptor.FormException {
        when(taskMock.getExam()).thenReturn(toolMock);
        when(toolMock.getExecutable(any())).thenReturn(pathToExe);
        when(toolMock.forNode(any(), any())).thenReturn(toolMock);
        ExamPluginConfig pluginConfigMock = mock(ExamPluginConfig.class);
        when(pluginConfigMock.getLicenseHost()).thenReturn("");
        when(pluginConfigMock.getLicensePort()).thenReturn(0);

        thrown.expect(AbortException.class);
        thrown.expectMessage(Messages.EXAM_LicenseServerNotConfigured());
        testObject.perform(taskMock, launcher, null);
    }

    @Test
    public void perform_exception() throws IOException, InterruptedException {
        when(taskMock.getExam()).thenReturn(toolMock);
        when(toolMock.getExecutable(any())).thenReturn(pathToExe);
        when(toolMock.forNode(any(), any())).thenReturn(toolMock);
        ExamPluginConfig examPluginConfig = jenkinsRule.getInstance().getDescriptorByType(ExamPluginConfig.class);
        examPluginConfig.setLicenseHost("localhost");
        examPluginConfig.setLicensePort(5054);

        ExamStatus status = new ExamStatus();
        status.setJobRunning(Boolean.FALSE);

        MockedStatic<RemoteService> remoteServiceMocked = Mocks.mockStatic(RemoteService.class);
        remoteServiceMocked.when(() -> RemoteService.getJSON(any(), anyInt(), anyString(), any()))
                .thenReturn(new RemoteServiceResponse(Response.ok().build().getStatus(), status, "")).thenReturn(
                        new RemoteServiceResponse(Response.ok().build().getStatus(), new ApiVersion(1, 0, 0), ""));

        Task.DescriptorTask descriptorTaskMock = mock(Task.DescriptorTask.class);
        when(descriptorTaskMock.getInstallations()).thenReturn(new ExamTool[]{});
        when(taskMock.getDescriptor()).thenReturn(descriptorTaskMock);

        thrown.expect(AbortException.class);
        thrown.expectMessage("ERROR: EXAM is already running");
        testObject.perform(taskMock, launcher, new ApiVersion(0, 0, 0));
    }

    @Test
    public void perform() throws IOException, InterruptedException {
        ExamPluginConfig examPluginConfig = jenkinsRule.getInstance().getDescriptorByType(ExamPluginConfig.class);
        examPluginConfig.setLicenseHost("localhost");
        examPluginConfig.setLicensePort(5054);

        ExamStatus status = new ExamStatus();
        status.setJobRunning(Boolean.FALSE);

        MockedStatic<RemoteService> remoteServiceMocked = Mocks.mockStatic(RemoteService.class);
        remoteServiceMocked.when(() -> RemoteService.getJSON(any(), anyInt(), anyString(), any()))
                .thenReturn(new RemoteServiceResponse(Response.serverError().build().getStatus(), status, ""))
                .thenReturn(new RemoteServiceResponse(Response.ok().build().getStatus(), status, "")).thenReturn(
                        new RemoteServiceResponse(Response.ok().build().getStatus(), new ApiVersion(1, 0, 0), ""));

        PrintStream printStreamMock = mock(PrintStream.class);
        TaskListener taskListenerMock = mock(TaskListener.class);
        when(taskListenerMock.getLogger()).thenReturn(printStreamMock);
        testObject.setTaskListener(taskListenerMock);
        Whitebox.setInternalState(testObject, "env", new EnvVars());

        Executor executorMock = mock(Executor.class);
        when(executorMock.isInterrupted()).thenReturn(false);
        when(runMock.getExecutor()).thenReturn(executorMock);
        when(taskMock.getTimeout()).thenReturn(10);
        when(taskMock.getExam()).thenReturn(toolMock);
        when(toolMock.getExecutable(any())).thenReturn(pathToExe);
        when(toolMock.forNode(any(), any())).thenReturn(toolMock);
        launcher = new FakeExamLauncher(taskListener);
        testObject.perform(taskMock, launcher, new ApiVersion(0, 0, 0));

        verify(taskMock).doExecuteTask(any());
    }

    @Test
    @WithoutJenkins
    public void copyArtifactsToTarget() throws Exception {
        TestConfiguration tc = new TestConfiguration();
        prepareExamReportConfig(tc);
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("cmd.exe", "/C");

        testObject.copyArtifactsToTarget(tc);
    }

    @Test
    @WithoutJenkins
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
    @WithoutJenkins
    public void handleAdditionalArgs_noLicenseConfig() throws Exception {

        ExamPluginConfig pluginConfigMock = mock(ExamPluginConfig.class);
        when(pluginConfigMock.getLicenseHost()).thenReturn("");

        thrown.expect(AbortException.class);
        thrown.expectMessage(Messages.EXAM_LicenseServerNotConfigured());

        testObject.handleAdditionalArgs("", new ArgumentListBuilder(), pluginConfigMock);
    }

    @Test
    @WithoutJenkins
    public void handleAdditionalArgs() throws Exception {
        testObject.setLauncher(launcher);

        ExamPluginConfig pluginConfigMock = mock(ExamPluginConfig.class);
        EnvVars envar = new EnvVars();
        envar.put("testKey", "testValue");
        testObject.setEnv(envar);

        when(pluginConfigMock.getLicenseHost()).thenReturn("localhost");
        when(pluginConfigMock.getLicensePort()).thenReturn(8090);

        ArgumentListBuilder args = new ArgumentListBuilder();
        testObject.handleAdditionalArgs(null, args, pluginConfigMock);
        List<String> argList = args.toList();
        assertTrue(argList.contains("-Dsun.jnu.encoding=UTF-8"));
        assertFalse(envar.containsKey("JAVA_OPTS"));

        String javaOpts = "-Dtest1 -Dtest2=hallo -Denv=${testKey}";
        String javaOptsReplaced = "-Dtest1 -Dtest2=hallo -Denv=testValue";
        args.clear();
        testObject.handleAdditionalArgs(javaOpts, args, pluginConfigMock);
        argList = args.toList();
        assertTrue(argList.contains("-Dsun.jnu.encoding=UTF-8"));
        assertTrue(argList.contains("-Dtest1"));
        assertTrue(argList.contains("-Denv=testValue"));
        assertTrue(envar.containsKey("JAVA_OPTS"));
        assertEquals("JavaOpts not extended", javaOptsReplaced, envar.get("JAVA_OPTS"));

        args.clear();
        testObject.handleAdditionalArgs(javaOpts, args, pluginConfigMock);
        argList = args.toList();
        assertTrue(argList.contains("-Dsun.jnu.encoding=UTF-8"));
        assertTrue(argList.contains("-Dtest1"));
        assertTrue(envar.containsKey("JAVA_OPTS"));
        assertEquals("JavaOpts not extended", javaOptsReplaced, envar.get("JAVA_OPTS"));
    }

    @Test
    @WithoutJenkins
    public void getConfigurationPath() throws IOException, InterruptedException {
        String examHome = "c:\\my\\examHome";
        MockedStatic<Remote> remoteMocked = Mocks.mockStatic(Remote.class);
        remoteMocked.when(() -> Remote.fileExists(Mockito.any(), Mockito.any())).thenReturn(true);

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
        remoteMocked.when(() -> Remote.fileExists(Mockito.any(), Mockito.any())).thenReturn(false);
        testObject.getConfigurationPath(examMock);
    }

    @Test
    @WithoutJenkins
    public void getPythonExePath() throws IOException, InterruptedException, Descriptor.FormException {
        String pythonHome = "c:\\my\\pythonHome";

        Mocks.mockStatic(Util.class);

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
    @WithoutJenkins
    public void getNode() throws Exception {
        Mocks.mockStatic(Util.class);

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
    @WithoutJenkins
    public void handleIOExceptionNormal() throws AbortException {
        Mocks.mockStatic(hudson.Util.class);
        doNothing().when(hudson.Util.class);

        thrown.expect(AbortException.class);
        thrown.expectMessage(Messages.EXAM_ExecFailed());
        testObject.handleIOException(System.currentTimeMillis() - 2000, new AbortException("testException"),
                new ExamTool[]{});
    }

    @Test
    @WithoutJenkins
    public void handleIOExceptionEmptyTools() throws AbortException {
        Mocks.mockStatic(hudson.Util.class);
        doNothing().when(hudson.Util.class);

        thrown.expect(AbortException.class);
        thrown.expectMessage(Messages.EXAM_GlobalConfigNeeded());
        testObject.handleIOException(System.currentTimeMillis(), new AbortException("testException"),
                new ExamTool[]{});
    }

    @Test
    @WithoutJenkins
    public void handleIOExceptionWithTools() throws AbortException {
        Mocks.mockStatic(hudson.Util.class);
        doNothing().when(hudson.Util.class);

        thrown.expect(AbortException.class);
        thrown.expectMessage(Messages.EXAM_ProjectConfigNeeded());
        testObject.handleIOException(System.currentTimeMillis(), new AbortException("testException"),
                new ExamTool[]{null});
    }

    @Test
    @WithoutJenkins
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
    @WithoutJenkins
    public void getRun() {
        Whitebox.setInternalState(testObject, "run", runMock);
        Run testIt = testObject.getRun();
        assertEquals(runMock, testIt);
    }

    @Test
    @WithoutJenkins
    public void getTaskListener() {
        TaskListener listener = new FakeTaskListener();
        Whitebox.setInternalState(testObject, "taskListener", listener);
        TaskListener testIt = testObject.getTaskListener();
        assertEquals(listener, testIt);
    }

    @Test
    @WithoutJenkins
    public void getTool() throws Exception {
        Mocks.mockStatic(Util.class);
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
        MockedStatic<Remote> remoteMocked = Mocks.mockStatic(Remote.class);
        remoteMocked.when(() -> Remote.fileExists(Mockito.any(), Mockito.any())).thenReturn(true);

        when(taskMock.getExam()).thenReturn(toolMock);
        when(toolMock.getHome()).thenReturn(examHome);
        when(toolMock.getExecutable(any())).thenReturn(pathToExe);
        when(toolMock.getRelativeDataPath()).thenReturn("..\\examData");
        when(toolMock.forNode(any(), any())).thenReturn(toolMock);

        FilePath filePath = testObject.prepareWorkspace(taskMock, args);
        List<String> argList = args.toList();
        assertTrue(argList.contains(pathToExe));
        assertEquals("exam", filePath.getBaseName());
    }

    @Test
    public void prepareWorkspaceException() throws IOException, InterruptedException {
        ArgumentListBuilder args = new ArgumentListBuilder();
        when(taskMock.getExam()).thenReturn(toolMock);
        when(toolMock.getExecutable(any())).thenReturn("");
        when(toolMock.forNode(any(), any())).thenReturn(toolMock);

        thrown.expect(AbortException.class);
        thrown.expectMessage(Messages.EXAM_ExecutableNotFound(toolMock.getName()));
        testObject.prepareWorkspace(taskMock, args);
    }

    @Test
    @WithoutJenkins
    public void setLauncher() {
        testObject.setLauncher(launcher);
        Launcher testLauncher = Whitebox.getInternalState(testObject, "launcher");
        assertEquals(launcher, testLauncher);
    }

    @Test
    @WithoutJenkins
    public void setWorkspace() {
        testObject.setWorkspace(workspace);
        FilePath testWorkspace = Whitebox.getInternalState(testObject, "workspace");
        assertEquals(workspace, testWorkspace);
    }

    @Test
    @WithoutJenkins
    public void setRun() throws IOException, InterruptedException {
        testObject.setTaskListener(null);
        testObject.setRun(null);
        EnvVars envVars = new EnvVars();
        envVars.put("test", "test");
        when(runMock.getEnvironment(any())).thenReturn(envVars);
        testObject.setRun(runMock);
        Run testRun = Whitebox.getInternalState(testObject, "run");
        assertEquals(runMock, testRun);
        assertNull(testObject.getEnv());

        testObject.setRun(null);
        testObject.setTaskListener(taskListener);
        testObject.setRun(runMock);
        assertEquals(envVars, testObject.getEnv());
        assertEquals(1, testObject.getEnv().size());

    }

    @Test
    @WithoutJenkins
    public void setTaskListener() throws IOException, InterruptedException {
        testObject.setTaskListener(null);
        testObject.setRun(null);
        EnvVars envVars = new EnvVars();
        envVars.put("test", "test");
        when(runMock.getEnvironment(any())).thenReturn(envVars);
        testObject.setTaskListener(taskListener);
        TaskListener testListener = Whitebox.getInternalState(testObject, "taskListener");
        assertEquals(taskListener, testListener);
        assertNull(testObject.getEnv());

        testObject.setTaskListener(null);
        testObject.setRun(runMock);
        testObject.setTaskListener(taskListener);
        assertEquals(envVars, testObject.getEnv());
        assertEquals(1, testObject.getEnv().size());
    }

    @Test
    public void getClientRequest() throws Exception {
        Class[] types = new Class[]{Launcher.class, ExamPluginConfig.class};
        ExamStatus status = new ExamStatus();
        status.setJobRunning(Boolean.FALSE);

        MockedStatic<RemoteService> remoteServiceMocked = Mocks.mockStatic(RemoteService.class);
        remoteServiceMocked.when(() -> RemoteService.getJSON(any(), anyInt(), any(), any()))
                .thenReturn(new RemoteServiceResponse(Response.serverError().build().getStatus(), status, ""));

        ExamPluginConfig pluginMock = mock(ExamPluginConfig.class);
        when(pluginMock.getPort()).thenReturn(8085);

        ClientRequest clientRequest = Whitebox.invokeMethod(testObject, "getClientRequest", types, launcher, pluginMock);
        assertEquals(8085, clientRequest.getApiPort());

        remoteServiceMocked.when(() -> RemoteService.getJSON(any(), anyInt(), any(), any()))
                .thenReturn(new RemoteServiceResponse(Response.ok().build().getStatus(), status, "")).thenReturn(
                        new RemoteServiceResponse(Response.ok().build().getStatus(), new ApiVersion(1, 0, 0), ""));
        thrown.expect(AbortException.class);
        thrown.expectMessage("ERROR: EXAM is already running");
        Whitebox.invokeMethod(testObject, "getClientRequest", types, launcher, pluginMock);
    }
}
