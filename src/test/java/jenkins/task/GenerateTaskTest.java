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
import jenkins.internal.data.GenerateConfiguration;
import jenkins.internal.data.ModelConfiguration;
import jenkins.model.Jenkins;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.task.TestUtil.FakeTaskListener;
import jenkins.task.TestUtil.TUtil;
import jenkins.task._exam.Messages;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
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

    @Mock Run runMock;

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
    private String[] frameFunctions;
    private String mappingList;
    private String[] testCaseStates;
    private String variant;

    List<File> createdFiles = new ArrayList<>();

    @Before public void setUp() {
        examName = "examName";
        examModel = "examModel";
        modelConfiguration = "config";
        examRelativePath = "examRelativePath";

        element = "testElement";
        descriptionSource = "descSource";
        documentInReport = true;
        errorHandling = "error";
        frameFunctions = new String[]{"frame"};
        mappingList = "mList";
        testCaseStates = new String[]{"tCStates"};
        variant = "var";

        Jenkins instance = jenkinsRule.getInstance();
        examHome = instance == null ? "examHome" : instance.getRootPath().getRemote();
        testObject = new GenerateTask(examModel, examName, modelConfiguration, element, descriptionSource, documentInReport,
                errorHandling, frameFunctions, mappingList, testCaseStates, variant);
    }

    @After public void tearDown() {
        TUtil.cleanUpExamTools(jenkinsRule);
        TUtil.cleanUpPythonInstallations(jenkinsRule);

        testObject = null;
        createdFiles.forEach(file -> {
            if (file.exists()) {
                file.delete();
            }
        });
    }

    @Test @WithoutJenkins public void testGetElement() {
        Whitebox.setInternalState(testObject, "element", element);
        assertEquals(testObject.getElement(), element);
    }

    @Test @WithoutJenkins public void testSetElement() {
        testObject.setElement(element);
        assertEquals(testObject.getElement(), element);
    }

    @Test @WithoutJenkins public void testGetDescriptionSource() {
        Whitebox.setInternalState(testObject, "descriptionSource", descriptionSource);
        assertEquals(testObject.getDescriptionSource(), descriptionSource);
    }

    @Test @WithoutJenkins public void testSetDescriptionSource() {
        testObject.setDescriptionSource(descriptionSource);
        Object internalState = Whitebox.getInternalState(testObject, "descriptionSource");
        assertEquals(descriptionSource, internalState);
    }

    @Test @WithoutJenkins public void testGetDocumentInReport() {
        Whitebox.setInternalState(testObject, "documentInReport", documentInReport);
        assertEquals(testObject.isDocumentInReport(), documentInReport);
    }

    @Test @WithoutJenkins public void testSetDocumentInReport() {
        testObject.setDocumentInReport(documentInReport);
        Object internalState = Whitebox.getInternalState(testObject, "documentInReport");
        assertEquals(documentInReport, internalState);
    }

    @Test @WithoutJenkins public void testGetErrorHandling() {
        Whitebox.setInternalState(testObject, "errorHandling", errorHandling);
        assertEquals(testObject.getErrorHandling(), errorHandling);
    }

    @Test @WithoutJenkins public void testSetErrorHandling() {
        testObject.setErrorHandling(errorHandling);
        Object internalState = Whitebox.getInternalState(testObject, "errorHandling");
        assertEquals(errorHandling, internalState);
    }

    @Test @WithoutJenkins public void testGetFrameFunctions() {
        Whitebox.setInternalState(testObject, "frameSteps", frameFunctions);
        assertArrayEquals(testObject.getFrameSteps(), frameFunctions);
    }

    @Test @WithoutJenkins public void testSetFrameFunctions() {
        testObject.setFrameSteps(frameFunctions);
        Object[] internalState = Whitebox.getInternalState(testObject, "frameSteps");
        assertArrayEquals(frameFunctions, internalState);
    }

    @Test @WithoutJenkins public void testGetMappingList() {
        Whitebox.setInternalState(testObject, "mappingList", mappingList);
        assertEquals(testObject.getMappingList(), mappingList);
    }

    @Test @WithoutJenkins public void testSetMappingList() {
        testObject.setMappingList(mappingList);
        Object internalState = Whitebox.getInternalState(testObject, "mappingList");
        assertEquals(mappingList, internalState);
    }

    @Test @WithoutJenkins public void testGetTestCaseStates() {
        Whitebox.setInternalState(testObject, "testCaseStates", testCaseStates);
        assertArrayEquals(testObject.getTestCaseStates(), testCaseStates);
    }

    @Test @WithoutJenkins public void testSetTestCaseStates() {
        testObject.setTestCaseStates(testCaseStates);
        Object internalState = Whitebox.getInternalState(testObject, "testCaseStates");
        assertEquals(testCaseStates, internalState);
    }

    @Test @WithoutJenkins public void testGetVariant() {
        Whitebox.setInternalState(testObject, "variant", variant);
        assertEquals(testObject.getVariant(), variant);
    }

    @Test @WithoutJenkins public void testSetVariant() {
        testObject.setVariant(variant);
        Object internalState = Whitebox.getInternalState(testObject, "variant");
        assertEquals(variant, internalState);
    }


    @Test @WithoutJenkins public void testGetModelConfiguration() {
        Whitebox.setInternalState(testObject, "modelConfiguration", modelConfiguration);
        assertEquals(modelConfiguration, testObject.getModelConfiguration());
    }
    @Test @WithoutJenkins public void testSetModelConfiguration() {
        testObject.setModelConfiguration(modelConfiguration);
        Object internalState = Whitebox.getInternalState(testObject, "modelConfiguration");

        assertEquals(modelConfiguration, internalState);
    }

    @Test @WithoutJenkins public void testGetExamModel() {
        Whitebox.setInternalState(testObject, "examModel", examModel);
        assertEquals(examModel, testObject.getExamModel());
    }

    @Test @WithoutJenkins public void testSetExamModel() {
        testObject.setExamModel(examModel);
        String element = Whitebox.getInternalState(testObject, "examModel");

        assertEquals(examModel, element);
    }

    @Test public void testGetExam() {
        assertEquals(0, jenkinsRule.getInstance().getDescriptorByType(ExamTool.DescriptorImpl.class)
                .getInstallations().length);
        ExamTool newExamTool = TUtil.createAndRegisterExamTool(jenkinsRule, examName, examHome, examRelativePath);
        assertEquals(1, jenkinsRule.getInstance().getDescriptorByType(ExamTool.DescriptorImpl.class)
                .getInstallations().length);

        ExamTool setTool = testObject.getExam();
        assertEquals(newExamTool, setTool);
    }

    @Test public void testGetExamNoExam() {
        assertNull(testObject.getExam());
    }

    @Test public void testGetModel() {
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

    @Test public void testPerform_noConfig() throws Exception {
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
        assertThat(log, CoreMatchers.hasItem("ERROR: " + Messages.EXAM_NotExamConfigDirectory(
                workspacePath + File.separator + "examRelativePath" + File.separator + "configuration"
                        + File.separator + "config.ini")));
    }

    @Test public void testPerform_noTool() throws Exception {
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

    @Test public void testCreateModelConfig() throws Exception {
        String targetEndpoint = "targetEndpoint";
        ExamModelConfig mod = new ExamModelConfig(examModel);
        mod.setName(examName);
        mod.setModelName(examModel);
        mod.setExamVersion(49);
        mod.setTargetEndpoint(targetEndpoint);
        testObject.getDescriptor().getModelConfigs().add(mod);
        Whitebox.setInternalState(testObject, "examModel", examName);
        Whitebox.setInternalState(testObject, "modelConfiguration", modelConfiguration);
        ModelConfiguration actual = Whitebox.invokeMethod(testObject, "createModelConfig");

        ModelConfiguration expected = new ModelConfiguration();
        expected.setModelName(examModel);
        expected.setProjectName(examName);
        expected.setTargetEndpoint(targetEndpoint);
        expected.setModelConfigUUID(modelConfiguration);

        TUtil.assertModelConfig(expected, actual);

        try {
            testObject.getDescriptor().getModelConfigs().clear();
            Whitebox.invokeMethod(testObject, "createModelConfig");
        } catch (AbortException e) {
            assertEquals("ERROR: no model configured with name: " + examName, e.getMessage());
            return;
        }
        fail();
    }

    @Test public void testCreateGenerateConfig() throws Exception {
        GenerateConfiguration expected = new GenerateConfiguration();
        expected.setElement(element);
        expected.setDescriptionSource(descriptionSource);
        expected.setDocumentInReport(documentInReport);
        expected.setErrorHandling(errorHandling);
        expected.setVariant(variant);
        expected.setFrameFunctions(Arrays.asList(frameFunctions));
        expected.setMappingList(Collections.singletonList(mappingList));
        expected.setTestCaseStates(Arrays.asList(testCaseStates));

        Whitebox.setInternalState(testObject, "element", element);
        Whitebox.setInternalState(testObject, "descriptionSource", descriptionSource);
        Whitebox.setInternalState(testObject, "documentInReport", documentInReport);
        Whitebox.setInternalState(testObject, "errorHandling", errorHandling);
        Whitebox.setInternalState(testObject, "variant", variant);
        Whitebox.setInternalState(testObject, "frameSteps", frameFunctions);
        Whitebox.setInternalState(testObject, "mappingList", mappingList);
        Whitebox.setInternalState(testObject, "testCaseStates", testCaseStates);

        GenerateConfiguration actual = Whitebox.invokeMethod(testObject, "createGenerateConfig");
        TUtil.assertGenerateConfig(expected, actual);

        Whitebox.setInternalState(testObject, "element", "anotherElement");
        Whitebox.setInternalState(testObject, "descriptionSource", "anotherDescriptionSource");
        Whitebox.setInternalState(testObject, "documentInReport", false);
        actual = Whitebox.invokeMethod(testObject, "createGenerateConfig");
        expected.setElement("anotherElement");
        expected.setDescriptionSource("anotherDescriptionSource");
        expected.setDocumentInReport(false);
        TUtil.assertGenerateConfig(expected, actual);
    }

    @Test public void testExecuteTask() throws IOException, InterruptedException {
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
        Mockito.verify(clientRequestMock, Mockito.times(1)).createExamProject(Mockito.any());
        Mockito.verify(clientRequestMock, Mockito.times(1)).generateTestcases(Mockito.any());
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
}
