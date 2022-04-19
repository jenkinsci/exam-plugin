package jenkins.task;

import Utils.Whitebox;
import hudson.AbortException;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.model.Run;
import jenkins.internal.ClientRequest;
import jenkins.internal.data.GroovyConfiguration;
import jenkins.internal.data.ModelConfiguration;
import jenkins.model.Jenkins;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.task.TestUtil.FakeTaskListener;
import jenkins.task.TestUtil.TUtil;
import jenkins.task._exam.Messages;
import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GroovyTaskTest {

    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    @Mock
    Run runMock;
    List<File> createdFiles = new ArrayList<>();
    private FreeStyleProject examTestProject;
    private GroovyTask testObject;
    private String script;
    private String startElement;
    private String examName;
    private String examModel;
    private String examHome;
    private String examRelativePath;
    private String modelConfig;
    private String targetEndpoint;
    private int examVersion;

    @Before
    public void setUp() {
        modelConfig = "ITest";
        script = "test";
        startElement = "";
        examName = "examName";
        examModel = "examModel";
        examRelativePath = "examRelativePath";
        targetEndpoint = "testTargetEndpoint";
        examVersion = 48;
        Jenkins instance = jenkinsRule.getInstance();
        examHome = instance == null ? "examHome" : instance.getRootPath().getRemote();
        testObject = new GroovyTask(script, startElement, examName, examModel, modelConfig);
    }

    @After
    public void tearDown() {
        TUtil.cleanUpExamTools(jenkinsRule);
        TUtil.cleanUpPythonInstallations(jenkinsRule);

        testObject = null;
        createdFiles.forEach(file -> {
            if (file.exists()) {
                file.delete();
            }
        });
    }

    @Test
    @WithoutJenkins
    public void testGetJavaOpts() {
        String javaOptions = "-test -test2";
        Whitebox.setInternalState(testObject, "javaOpts", javaOptions);
        String setOptions = testObject.getJavaOpts();

        assertEquals(javaOptions, setOptions);
    }

    @Test
    @WithoutJenkins
    public void testSetJavaOpts() {
        String javaOptions = "-testoption -n";
        testObject.setJavaOpts(javaOptions);
        String setJavaOpts = Whitebox.getInternalState(testObject, "javaOpts");

        assertEquals(javaOptions, setJavaOpts);
    }

    @Test
    @WithoutJenkins
    public void testGetTimeout() {
        int testTimeout = 1234;
        Whitebox.setInternalState(testObject, "timeout", testTimeout);
        int setTimeout = testObject.getTimeout();

        assertEquals(testTimeout, setTimeout);
    }

    @Test
    @WithoutJenkins
    public void testSetTimeout() {
        int testTimeout = 9876;
        testObject.setTimeout(testTimeout);
        int setTimeout = Whitebox.getInternalState(testObject, "timeout");

        assertEquals(testTimeout, setTimeout);
    }

    @Test
    @WithoutJenkins
    public void testGetStartElement() {
        Whitebox.setInternalState(testObject, "startElement", startElement);
        String setStartElement = testObject.getStartElement();

        assertEquals(startElement, setStartElement);
    }

    @Test
    @WithoutJenkins
    public void testSetStartElement() {
        testObject.setStartElement(startElement);
        String element = Whitebox.getInternalState(testObject, "startElement");

        assertEquals(startElement, element);
    }

    @Test
    @WithoutJenkins
    public void testSetUseStartElement() {
        testObject.setUseStartElement(false);
        boolean isUse = Whitebox.getInternalState(testObject, "useStartElement");

        assertFalse(isUse);

        testObject.setUseStartElement(true);
        isUse = Whitebox.getInternalState(testObject, "useStartElement");

        assertTrue(isUse);
    }

    @Test
    @WithoutJenkins
    public void testIsUseStartElement() {
        Whitebox.setInternalState(testObject, "useStartElement", true);
        boolean isUse = testObject.isUseStartElement();

        assertTrue(isUse);

        Whitebox.setInternalState(testObject, "useStartElement", false);
        isUse = testObject.isUseStartElement();

        assertFalse(isUse);
    }

    @Test
    @WithoutJenkins
    public void testGetScript() {
        Whitebox.setInternalState(testObject, "script", script);
        String setStartElement = testObject.getScript();

        assertEquals(script, setStartElement);
    }

    @Test
    @WithoutJenkins
    public void testSetScript() {
        testObject.setScript(script);
        String element = Whitebox.getInternalState(testObject, "script");

        assertEquals(script, element);
    }

    @Test
    @WithoutJenkins
    public void testGetModelConfiguration() {
        Whitebox.setInternalState(testObject, "modelConfiguration", modelConfig);
        String setStartElement = testObject.getModelConfiguration();

        assertEquals(modelConfig, setStartElement);
    }

    @Test
    @WithoutJenkins
    public void testSetModelConfiguration() {
        testObject.setModelConfiguration(modelConfig);
        String element = Whitebox.getInternalState(testObject, "modelConfiguration");

        assertEquals(modelConfig, element);
    }

    @Test
    @WithoutJenkins
    public void testGetExamName() {
        Whitebox.setInternalState(testObject, "examName", examName);
        String setStartElement = testObject.getExamName();

        assertEquals(examName, setStartElement);
    }

    @Test
    @WithoutJenkins
    public void testGetExamModel() {
        Whitebox.setInternalState(testObject, "examModel", examModel);
        String setStartElement = testObject.getExamModel();

        assertEquals(examModel, setStartElement);
    }

    @Test
    @WithoutJenkins
    public void testSetExamModel() {
        testObject.setExamModel(examModel);
        String element = Whitebox.getInternalState(testObject, "examModel");

        assertEquals(examModel, element);
    }

    @Test
    public void testGetExam() {
        assertEquals(0, jenkinsRule.getInstance().getDescriptorByType(ExamTool.DescriptorImpl.class)
                .getInstallations().length);
        ExamTool newExamTool = TUtil.createAndRegisterExamTool(jenkinsRule, examName, examHome, examRelativePath);
        assertEquals(1, jenkinsRule.getInstance().getDescriptorByType(ExamTool.DescriptorImpl.class)
                .getInstallations().length);

        ExamTool setTool = testObject.getExam();
        assertEquals(newExamTool, setTool);
    }

    @Test
    public void testGetExamNoExam() {
        assertNull(testObject.getExam());
    }

    @Test
    public void testGetModel() {
        ExamModelConfig examModelConfig = testObject.getModel(examModel);
        assertNull(examModelConfig);

        ExamModelConfig mod = new ExamModelConfig("nothing");
        mod.setName("nothing");
        testObject.getDescriptor().getModelConfigs().add(mod);
        examModelConfig = testObject.getModel(examModel);
        assertNull(examModelConfig);

        ExamModelConfig mod2 = new ExamModelConfig(examModel);
        mod2.setName(examModel);
        testObject.getDescriptor().getModelConfigs().add(mod2);

        examModelConfig = testObject.getModel(examModel);
        assertEquals(mod2, examModelConfig);
    }

    @Test
    public void testPerform_noConfig() throws Exception {
        TUtil.createAndRegisterExamTool(jenkinsRule, examName, examHome, examRelativePath);

        File file = new File(examHome + File.separator + "EXAM.exe");
        boolean fileCreated = file.createNewFile();
        assertTrue("File not created", fileCreated);
        createdFiles.add(file);

        examTestProject = jenkinsRule.createFreeStyleProject();
        examTestProject.getBuildersList().add(testObject);
        FreeStyleBuild build = examTestProject.scheduleBuild2(0).get();
        Result buildResult = build.getResult();
        assertEquals("FAILURE", buildResult.toString());

        List<String> log = build.getLog(1000);
        String workspacePath = jenkinsRule.getInstance().getRootPath().getRemote();
        assertThat(log, CoreMatchers.hasItem("ERROR: " + Messages.EXAM_NotExamConfigDirectory(
                workspacePath + File.separator + "examRelativePath" + File.separator + "configuration"
                        + File.separator + "config.ini")));
    }

    @Test
    public void testPerform_noLicenseConfig() throws Exception {
        TUtil.createAndRegisterExamTool(jenkinsRule, examName, examHome, "./data");

        File file = new File(examHome + File.separator + "EXAM.exe");
        boolean fileCreated = file.createNewFile();
        assertTrue("File not created", fileCreated);
        createdFiles.add(file);
        File file2 = new File(examHome + File.separator + "data" + File.separator + "configuration" + File.separator
                + "config.ini");
        fileCreated = file2.getParentFile().mkdirs();
        assertTrue("Folder not created", fileCreated);
        fileCreated = file2.createNewFile();
        assertTrue("File not created", fileCreated);
        createdFiles.add(file2);

        examTestProject = jenkinsRule.createFreeStyleProject();
        examTestProject.getBuildersList().add(testObject);
        FreeStyleBuild build = examTestProject.scheduleBuild2(0).get();
        Result buildResult = build.getResult();
        assertEquals("FAILURE", buildResult.toString());

        List<String> log = build.getLog(1000);
        assertThat(log, CoreMatchers.hasItem("ERROR: " + Messages.EXAM_LicenseServerNotConfigured()));
    }

    @Test
    public void testPerform_noTool() throws Exception {
        examTestProject = jenkinsRule.createFreeStyleProject();
        examTestProject.getBuildersList().add(testObject);
        runProjectWithoutTools("ERROR: examTool is null");

        TUtil.createAndRegisterExamTool(jenkinsRule, examName, "", examRelativePath);
        runProjectWithoutTools("ERROR: " + Messages.EXAM_ExecutableNotFound(examName));
    }

    @Test
    public void testCreateModelConfig() throws Exception {
        ExamModelConfig mod = new ExamModelConfig(examModel);
        mod.setName(examName);
        mod.setModelName(examModel);
        mod.setExamVersion(examVersion);
        mod.setTargetEndpoint(targetEndpoint);
        testObject.getDescriptor().getModelConfigs().add(mod);
        Whitebox.setInternalState(testObject, "examModel", examName);
        Whitebox.setInternalState(testObject, "modelConfiguration", modelConfig);
        ModelConfiguration actual = Whitebox.invokeMethod(testObject, "createModelConfig");

        ModelConfiguration expected = new ModelConfiguration();
        expected.setModelName(examModel);
        expected.setProjectName(examName);
        expected.setTargetEndpoint(targetEndpoint);
        expected.setModelConfigUUID(modelConfig);

        assertModelConfig(expected, actual);

        testObject.getDescriptor().getModelConfigs().clear();

        exceptionRule.expect(AbortException.class);
        exceptionRule.expectMessage("ERROR: no model configured with name: " + examName);
        Whitebox.invokeMethod(testObject, "createModelConfig");
    }

    @Test
    public void testCreateGroovyConfig() throws Exception {
        GroovyConfiguration expected = new GroovyConfiguration();
        expected.setStartElement(startElement);
        expected.setScript(script);

        Whitebox.setInternalState(testObject, "script", script);
        Whitebox.setInternalState(testObject, "startElement", startElement);
        Whitebox.setInternalState(testObject, "useStartElement", true);
        GroovyConfiguration actual = Whitebox.invokeMethod(testObject, "createGroovyConfig");
        assertGroovyConfig(expected, actual);

        Whitebox.setInternalState(testObject, "script", "anotherScript");
        Whitebox.setInternalState(testObject, "useStartElement", false);
        Whitebox.setInternalState(testObject, "startElement", "anotherStartElement");
        actual = Whitebox.invokeMethod(testObject, "createGroovyConfig");
        expected.setScript("anotherScript");
        expected.setStartElement("");
        assertGroovyConfig(expected, actual);
    }

    @Test
    public void doExecuteTask() throws IOException, InterruptedException {
        MockitoAnnotations.openMocks(this);

        ExamModelConfig mod = new ExamModelConfig(examModel);
        mod.setName(examModel);
        testObject.getDescriptor().getModelConfigs().add(mod);

        FakeTaskListener taskListener = new FakeTaskListener();
        ExamTaskHelper taskHelper = new ExamTaskHelper();
        taskHelper.setRun(runMock);
        taskHelper.setWorkspace(new FilePath(new File("c:\\my\\path")));
        taskHelper.setLauncher(new Launcher.DummyLauncher(taskListener));
        taskHelper.setTaskListener(taskListener);
        Whitebox.setInternalState(testObject, "taskHelper", taskHelper);

        ClientRequest clientRequestMock = Mockito.mock(ClientRequest.class);
        Mockito.when(clientRequestMock.isClientConnected()).thenReturn(Boolean.FALSE);
        testObject.doExecuteTask(clientRequestMock);
        Mockito.verify(clientRequestMock, Mockito.never()).clearWorkspace(Mockito.any());

        Mockito.when(clientRequestMock.isClientConnected()).thenReturn(Boolean.TRUE);
        testObject.doExecuteTask(clientRequestMock);
        Mockito.verify(clientRequestMock, Mockito.times(1)).clearWorkspace(Mockito.any());
    }

    private void runProjectWithoutTools(String logContains) throws Exception {
        FreeStyleBuild build = examTestProject.scheduleBuild2(0).get();
        Result buildResult = build.getResult();
        assertEquals("FAILURE", buildResult.toString());
        List<String> log = build.getLog(1000);
        assertThat(log, CoreMatchers.hasItem(logContains));
    }

    private void assertGroovyConfig(GroovyConfiguration c1, GroovyConfiguration c2) {
        if (c1 != null && c2 != null) {
            assertEquals(c1.getScript(), c2.getScript());
            assertEquals(c1.getStartElement(), c2.getStartElement());
            return;
        }
        fail();
    }

    private void assertModelConfig(ModelConfiguration mc1, ModelConfiguration mc2) {
        if (mc1 != null & mc2 != null) {
            assertEquals(mc1.getModelName(), mc2.getModelName());
            assertEquals(mc1.getProjectName(), mc2.getProjectName());
            assertEquals(mc1.getTargetEndpoint(), mc2.getTargetEndpoint());
            assertEquals(mc1.getModelConfigUUID(), mc2.getModelConfigUUID());
            return;
        }
        fail();
    }
}
