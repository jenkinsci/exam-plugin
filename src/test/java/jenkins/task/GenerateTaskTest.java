package jenkins.task;

import Utils.Whitebox;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.model.Run;
import jenkins.internal.ClientRequest;
import jenkins.internal.data.ApiVersion;
import jenkins.internal.data.GenerateConfiguration;
import jenkins.internal.data.LegacyGenerateConfiguration;
import jenkins.internal.enumeration.DescriptionSource;
import jenkins.internal.enumeration.ErrorHandling;
import jenkins.internal.enumeration.TestCaseState;
import jenkins.model.Jenkins;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.task.TestUtil.FakeTaskListener;
import jenkins.task.TestUtil.TUtil;
import jenkins.task._exam.Messages;
import org.hamcrest.CoreMatchers;
import org.junit.*;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GenerateTaskTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();

    @Mock
    Run runMock;

    /**
     * Exam properties
     */
    private String examName;
    private String examModel;
    private String examHome;
    private String modelConfiguration;
    private String examRelativePath;

    /**
     * Test objects
     */
    private FreeStyleProject examTestProject;
    private GenerateTask testObject;

    /**
     * Test properties
     */
    private String element;
    private String descriptionSource;
    private boolean documentInReport;
    private String errorHandling;
    private List<String> frameSteps;
    private String mappingList;
    private List<String> testCaseStates;
    private String variant;
    private boolean setStates;
    private String stateForFail;
    private String stateForSuccess;

    List<File> createdFiles = new ArrayList<>();

    @Before
    public void setUp() {
        examName = "examName";
        examModel = "examModel";
        modelConfiguration = "config";
        examRelativePath = "examRelativePath";

        element = "testElement";
        descriptionSource = DescriptionSource.DESCRIPTION.name();
        documentInReport = true;
        errorHandling = ErrorHandling.GENERATE_ERROR_STEP.name();
        frameSteps = new ArrayList<>();
        frameSteps.add("frame");
        mappingList = "mList";
        testCaseStates = new ArrayList<>();
        testCaseStates.add("tCStates");
        variant = "var";
        setStates = true;
        stateForFail = TestCaseState.NOT_YET_SPECIFIED.getName();
        stateForSuccess = TestCaseState.IMPLEMENTED.getName();

        Jenkins instance = jenkinsRule.getInstance();
        examHome = instance == null ? "examHome" : instance.getRootPath().getRemote();
        testObject = new GenerateTask(examModel, examName, modelConfiguration, element, descriptionSource, documentInReport, errorHandling, frameSteps, mappingList, testCaseStates, variant, setStates, stateForFail, stateForSuccess);
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
    public void testGetElement() {
        Whitebox.setInternalState(testObject, "element", element);
        assertEquals(testObject.getElement(), element);
    }

    @Test
    @WithoutJenkins
    public void testSetElement() {
        testObject.setElement(element);
        assertEquals(testObject.getElement(), element);
    }

    @Test
    @WithoutJenkins
    public void testGetDescriptionSource() {
        Whitebox.setInternalState(testObject, "descriptionSource", descriptionSource);
        assertEquals(testObject.getDescriptionSource(), descriptionSource);
    }

    @Test
    @WithoutJenkins
    public void testSetDescriptionSource() {
        testObject.setDescriptionSource(descriptionSource);
        Object internalState = Whitebox.getInternalState(testObject, "descriptionSource");
        assertEquals(descriptionSource, internalState);
    }

    @Test
    @WithoutJenkins
    public void testGetDocumentInReport() throws Exception {
        testGetBoolean("documentInReport", "getDocumentInReport");
    }

    @Test
    @WithoutJenkins
    public void testSetDocumentInReport() throws Exception {
        testSetBoolean("documentInReport", "setDocumentInReport");
    }

    @Test
    @WithoutJenkins
    public void testGetOverwriteDescriptionSource() throws Exception {
        testGetBoolean("overwriteDescriptionSource", "getOverwriteDescriptionSource");
    }

    @Test
    @WithoutJenkins
    public void testSetOverwriteDescriptionSource() throws Exception {
        testSetBoolean("overwriteDescriptionSource", "setOverwriteDescriptionSource");
    }

    @Test
    @WithoutJenkins
    public void testGetOverwriteFrameSteps() throws Exception {
        testGetBoolean("overwriteFrameSteps", "getOverwriteFrameSteps");
    }

    @Test
    @WithoutJenkins
    public void testSetOverwriteFrameSteps() throws Exception {
        testSetBoolean("overwriteFrameSteps", "setOverwriteFrameSteps");
    }

    @Test
    @WithoutJenkins
    public void testGetOverwriteMappingList() throws Exception {
        testGetBoolean("overwriteMappingList", "getOverwriteMappingList");
    }

    @Test
    @WithoutJenkins
    public void testSetOverwriteMappingList() throws Exception {
        testSetBoolean("overwriteMappingList", "setOverwriteMappingList");
    }

    @Test
    @WithoutJenkins
    public void testGetErrorHandling() {
        Whitebox.setInternalState(testObject, "errorHandling", errorHandling);
        assertEquals(testObject.getErrorHandling(), errorHandling);
    }

    @Test
    @WithoutJenkins
    public void testSetErrorHandling() {
        testObject.setErrorHandling(errorHandling);
        Object internalState = Whitebox.getInternalState(testObject, "errorHandling");
        assertEquals(errorHandling, internalState);
    }

    @Test
    @WithoutJenkins
    public void testGetFrameFunctions() {
        Whitebox.setInternalState(testObject, "frameSteps", frameSteps);
        assertArrayEquals(testObject.getFrameSteps().toArray(), frameSteps.toArray());
    }

    @Test
    @WithoutJenkins
    public void testSetFrameFunctions() {
        testObject.setFrameSteps(frameSteps);
        List<String> internalState = Whitebox.getInternalState(testObject, "frameSteps");
        assertArrayEquals(frameSteps.toArray(), internalState.toArray());
    }

    @Test
    @WithoutJenkins
    public void testGetMappingList() {
        Whitebox.setInternalState(testObject, "mappingList", mappingList);
        assertEquals(testObject.getMappingList(), mappingList);
    }

    @Test
    @WithoutJenkins
    public void testSetMappingList() {
        testObject.setMappingList(mappingList);
        Object internalState = Whitebox.getInternalState(testObject, "mappingList");
        assertEquals(mappingList, internalState);
    }

    @Test
    @WithoutJenkins
    public void testGetTestCaseStates() {
        Whitebox.setInternalState(testObject, "testCaseStates", testCaseStates);
        assertArrayEquals(testObject.getTestCaseStates().toArray(), testCaseStates.toArray());
    }

    @Test
    public void testSetTestCaseStates() {
        testObject.setTestCaseStates(testCaseStates);
        List<String> internalState = Whitebox.getInternalState(testObject, "testCaseStates");
        assertArrayEquals(testCaseStates.toArray(), internalState.toArray());

        testObject.setTestCaseStates(new ArrayList<>());
        internalState = Whitebox.getInternalState(testObject, "testCaseStates");
        assertEquals(1, internalState.size());
        assertEquals(TestCaseState.NOT_YET_IMPLEMENTED.toString(), internalState.get(0));
    }

    @Test
    @WithoutJenkins
    public void testGetVariant() {
        Whitebox.setInternalState(testObject, "variant", variant);
        assertEquals(testObject.getVariant(), variant);
    }

    @Test
    @WithoutJenkins
    public void testSetVariant() {
        testObject.setVariant(variant);
        Object internalState = Whitebox.getInternalState(testObject, "variant");
        assertEquals(variant, internalState);
    }


    @Test
    @WithoutJenkins
    public void testGetModelConfiguration() {
        Whitebox.setInternalState(testObject, "modelConfiguration", modelConfiguration);
        assertEquals(modelConfiguration, testObject.getModelConfiguration());
    }

    @Test
    @WithoutJenkins
    public void testSetModelConfiguration() {
        testObject.setModelConfiguration(modelConfiguration);
        Object internalState = Whitebox.getInternalState(testObject, "modelConfiguration");

        assertEquals(modelConfiguration, internalState);
    }

    @Test
    @WithoutJenkins
    public void testGetExamModel() {
        Whitebox.setInternalState(testObject, "examModel", examModel);
        assertEquals(examModel, testObject.getExamModel());
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
        assertEquals(0, jenkinsRule.getInstance().getDescriptorByType(ExamTool.DescriptorImpl.class).getInstallations().length);
        ExamTool newExamTool = TUtil.createAndRegisterExamTool(jenkinsRule, examName, examHome, examRelativePath);
        assertEquals(1, jenkinsRule.getInstance().getDescriptorByType(ExamTool.DescriptorImpl.class).getInstallations().length);

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
        assertEquals(Result.FAILURE, buildResult);

        List<String> log = build.getLog(1000);
        String workspacePath = jenkinsRule.getInstance().getRootPath().getRemote();
        assertThat(log, CoreMatchers.hasItem("ERROR: " + Messages.EXAM_NotExamConfigDirectory(workspacePath + File.separator + "examRelativePath" + File.separator + "configuration" + File.separator + "config.ini")));
    }

    @Test
    public void testPerform_noTool() throws Exception {
        examTestProject = jenkinsRule.createFreeStyleProject();
        examTestProject.getBuildersList().add(testObject);
        FreeStyleBuild build = examTestProject.scheduleBuild2(0).get();
        Result buildResult = build.getResult();
        assertEquals(Result.FAILURE, buildResult);
        List<String> log = build.getLog(1000);
        assertThat(log, CoreMatchers.hasItem("ERROR: examTool is null"));

        TUtil.createAndRegisterExamTool(jenkinsRule, examName, "", examRelativePath);
        build = examTestProject.scheduleBuild2(0).get();
        buildResult = build.getResult();
        assertEquals(Result.FAILURE, buildResult);
        log = build.getLog(1000);
        assertThat(log, CoreMatchers.hasItem("ERROR: " + Messages.EXAM_ExecutableNotFound(examName)));
    }

    @Test
    public void testCreateGenerateConfig() throws Exception {
        LegacyGenerateConfiguration expected = new LegacyGenerateConfiguration();
        expected.setElement(element);
        expected.setDescriptionSource(DescriptionSource.valueOf(descriptionSource).getDisplayString());
        expected.setDocumentInReport(documentInReport);
        expected.setErrorHandling(ErrorHandling.valueOf(errorHandling).displayString());
        expected.setVariant(variant);
        expected.setFrameFunctions(frameSteps);
        expected.setMappingList(Collections.singletonList(mappingList));
        expected.setTestCaseStates(testCaseStates);

        Whitebox.setInternalState(testObject, "element", element);
        Whitebox.setInternalState(testObject, "descriptionSource", descriptionSource);
        Whitebox.setInternalState(testObject, "documentInReport", documentInReport);
        Whitebox.setInternalState(testObject, "errorHandling", errorHandling);
        Whitebox.setInternalState(testObject, "variant", variant);
        Whitebox.setInternalState(testObject, "frameSteps", frameSteps);
        Whitebox.setInternalState(testObject, "mappingList", mappingList);
        Whitebox.setInternalState(testObject, "testCaseStates", testCaseStates);

        LegacyGenerateConfiguration actual = Whitebox.invokeMethod(testObject, "createGenerateConfig");
        TUtil.assertGenerateConfig(expected, actual);

        Whitebox.setInternalState(testObject, "element", "anotherElement");
        Whitebox.setInternalState(testObject, "descriptionSource", DescriptionSource.BESCHREIBUNG.name());
        Whitebox.setInternalState(testObject, "documentInReport", false);
        actual = Whitebox.invokeMethod(testObject, "createGenerateConfig");
        expected.setElement("anotherElement");
        expected.setDescriptionSource(DescriptionSource.BESCHREIBUNG.getDisplayString());
        expected.setDocumentInReport(false);
        TUtil.assertGenerateConfig(expected, actual);
    }

    @Test
    public void testGenerateNewConfig() throws Exception {
        GenerateConfiguration expected = new GenerateConfiguration();
        expected.setElement(element);
        expected.setOverwriteDescriptionSource(true);
        expected.setDescriptionSource(descriptionSource);
        expected.setDocumentInReport(documentInReport);
        expected.setErrorHandling(errorHandling);
        expected.setVariant(variant);
        expected.setOverwriteFrameSteps(true);
        expected.setFrameFunctions(frameSteps);
        expected.setOverwriteMappingList(true);
        expected.setMappingList(Collections.singletonList(mappingList));
        expected.setTestCaseStates(testCaseStates);
        expected.setSetStates(true);
        expected.setStateForSuccess(TestCaseState.NOT_YET_SPECIFIED.getName());
        expected.setStateForFail(TestCaseState.INVALID.getName());

        Whitebox.setInternalState(testObject, "element", element);
        Whitebox.setInternalState(testObject, "overwriteDescriptionSource", true);
        Whitebox.setInternalState(testObject, "descriptionSource", descriptionSource);
        Whitebox.setInternalState(testObject, "documentInReport", documentInReport);
        Whitebox.setInternalState(testObject, "errorHandling", errorHandling);
        Whitebox.setInternalState(testObject, "variant", variant);
        Whitebox.setInternalState(testObject, "overwriteFrameSteps", true);
        Whitebox.setInternalState(testObject, "frameSteps", frameSteps);
        Whitebox.setInternalState(testObject, "overwriteMappingList", true);
        Whitebox.setInternalState(testObject, "mappingList", mappingList);
        Whitebox.setInternalState(testObject, "testCaseStates", testCaseStates);
        Whitebox.setInternalState(testObject, "setStates", true);
        Whitebox.setInternalState(testObject, "stateForSuccess", TestCaseState.NOT_YET_SPECIFIED.getLiteral());
        Whitebox.setInternalState(testObject, "stateForFail", TestCaseState.INVALID.getLiteral());

        GenerateConfiguration actual = Whitebox.invokeMethod(testObject, "generateNewConfig");
        TUtil.assertGenerateConfig(expected, actual);

        Whitebox.setInternalState(testObject, "element", "anotherElement");
        Whitebox.setInternalState(testObject, "descriptionSource", DescriptionSource.BESCHREIBUNG.getDisplayString());
        Whitebox.setInternalState(testObject, "documentInReport", false);
        actual = Whitebox.invokeMethod(testObject, "generateNewConfig");
        expected.setElement("anotherElement");
        expected.setDescriptionSource(DescriptionSource.BESCHREIBUNG.getDisplayString());
        expected.setDocumentInReport(false);
        TUtil.assertGenerateConfig(expected, actual);
    }

    @Test
    public void testExecuteTaskPre203() throws IOException, InterruptedException {
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
        Mockito.when(clientRequestMock.getTCGVersion()).thenReturn(new ApiVersion(2, 0, 2));
        testObject.doExecuteTask(clientRequestMock);
        Mockito.verify(clientRequestMock, Mockito.never()).clearWorkspace(Mockito.any());

        Mockito.when(clientRequestMock.getTCGVersion()).thenReturn(new ApiVersion(2, 0, 2));
        Mockito.when(clientRequestMock.isClientConnected()).thenReturn(Boolean.TRUE);
        testObject.doExecuteTask(clientRequestMock);
        Mockito.verify(clientRequestMock, Mockito.times(1)).createExamProject(Mockito.any());
        Mockito.verify(clientRequestMock, Mockito.times(1)).generateTestcases(Mockito.any());
    }

    @Test
    public void testExecuteTaskPost203() throws IOException, InterruptedException {
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
        Mockito.when(clientRequestMock.getTCGVersion()).thenReturn(new ApiVersion(2, 0, 3));
        testObject.doExecuteTask(clientRequestMock);
        Mockito.verify(clientRequestMock, Mockito.never()).clearWorkspace(Mockito.any());

        Mockito.when(clientRequestMock.getTCGVersion()).thenReturn(new ApiVersion(2, 0, 3));
        Mockito.when(clientRequestMock.isClientConnected()).thenReturn(Boolean.TRUE);
        testObject.setStateForFail(TestCaseState.REVIEWED.getLiteral());
        testObject.setStateForSuccess(TestCaseState.NOT_YET_SPECIFIED.getLiteral());
        testObject.doExecuteTask(clientRequestMock);
        Mockito.verify(clientRequestMock, Mockito.times(1)).createExamProject(Mockito.any());
        Mockito.verify(clientRequestMock, Mockito.times(1)).generateTestcasesPost203(Mockito.any());
    }

    @Test
    @WithoutJenkins
    public void testConvertToList() throws Exception {
        List<String> list = Whitebox.invokeMethod(testObject, "convertToList", "");
        assertTrue(list.isEmpty());

        list = Whitebox.invokeMethod(testObject, "convertToList", "test_1,test_2");
        assertEquals(2, list.size());
        assertTrue(list.contains("test_1"));
        assertTrue(list.contains("test_2"));
    }

    @Test
    @WithoutJenkins
    public void testIsTestCaseStateSelected() {
        assertTrue(testObject.isTestCaseStateSelected(testCaseStates.get(0)));
        assertFalse(testObject.isTestCaseStateSelected("nothing"));
    }

    @Test
    @WithoutJenkins
    public void testIsFrameStepsSelected() {
        assertTrue(testObject.isFrameStepsSelected(frameSteps.get(0)));
        assertFalse(testObject.isFrameStepsSelected("nothing"));
    }

    private void testSetBoolean(String varName, String methodName) throws Exception {
        Whitebox.invokeMethod(testObject, methodName, Boolean.TYPE, true);
        boolean internalState = Whitebox.getInternalState(testObject, varName);
        assertTrue(internalState);

        Whitebox.invokeMethod(testObject, methodName, Boolean.TYPE, false);
        internalState = Whitebox.getInternalState(testObject, varName);
        assertFalse(internalState);
    }

    private void testGetBoolean(String varName, String methodName) throws Exception {
        Whitebox.setInternalState(testObject, varName, true);
        boolean value = Whitebox.invokeMethod(testObject, methodName);
        assertTrue(value);

        Whitebox.setInternalState(testObject, varName, false);
        value = Whitebox.invokeMethod(testObject, methodName);
        assertFalse(value);
    }
}
