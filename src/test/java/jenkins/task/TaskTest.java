package jenkins.task;

import Utils.Whitebox;
import hudson.AbortException;
import jenkins.internal.data.ModelConfiguration;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.plugins.exam.config.ExamPluginConfig;
import jenkins.task.TestUtil.FakeExamTask;
import jenkins.task.TestUtil.TUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;

import static org.junit.Assert.*;

public class TaskTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private Task testObject;
    private String examName;
    private String pythonName;
    private String examReport;
    private String examSysConfig;
    private String examHome;
    private String examRelativePath;
    private String targetEndpoint;
    private int examVersion;
    private String examModel;
    private String modelConfig;

    @Before
    public void setUp() {
        modelConfig = "ITest";
        examModel = "examModel";
        examName = "EXAM";
        pythonName = "Python-2.7";
        examReport = "examReport";
        examSysConfig = "testExamSystemConfig";
        examHome = "examHome";
        examRelativePath = "examRelativePath";
        targetEndpoint = "testTargetEndpoint";
        examVersion = 48;
        testObject = new FakeExamTask(examName, pythonName, examReport, examSysConfig);
    }

    @After
    public void tearDown() {
        testObject = null;
    }

    @Test
    @WithoutJenkins
    public void getTaskHelper() {
        ExamTaskHelper helper = testObject.getTaskHelper();
        assertNotNull(helper);
        ExamTaskHelper helper2 = testObject.getTaskHelper();
        assertEquals(helper, helper2);
    }

    @Test
    @WithoutJenkins
    public void getJavaOpts() {
        String javaOptions = "-test -test2";
        Whitebox.setInternalState(testObject, "javaOpts", javaOptions);
        String setOptions = testObject.getJavaOpts();

        assertEquals(javaOptions, setOptions);
    }

    @Test
    @WithoutJenkins
    public void setJavaOpts() {
        String javaOptions = "-testoption -n";
        testObject.setJavaOpts(javaOptions);
        String setJavaOpts = Whitebox.getInternalState(testObject, "javaOpts");

        assertEquals(javaOptions, setJavaOpts);
    }

    @Test
    public void getTimeout() {
        int testTimeout = 222;
        int testLocalTimeout = 333;
        ExamPluginConfig examPluginConfig = jenkinsRule.getInstance().getDescriptorByType(ExamPluginConfig.class);
        examPluginConfig.setTimeout(testTimeout);

        Whitebox.setInternalState(testObject, "timeout", 0);
        int setTimeout = testObject.getTimeout();
        assertEquals(testTimeout, setTimeout);

        Whitebox.setInternalState(testObject, "timeout", testLocalTimeout);
        setTimeout = testObject.getTimeout();
        assertEquals(testLocalTimeout, setTimeout);

    }

    @Test
    @WithoutJenkins
    public void setTimeout() {
        int testTimeout = 9876;
        testObject.setTimeout(testTimeout);
        int setTimeout = Whitebox.getInternalState(testObject, "timeout");

        assertEquals(testTimeout, setTimeout);
    }

    @Test
    @WithoutJenkins
    public void getExamName() {
        String setExamName = testObject.getExamName();
        assertEquals(examName, setExamName);
    }

    @Test
    public void getExam() {
        assertEquals(0, jenkinsRule.getInstance().getDescriptorByType(ExamTool.DescriptorImpl.class)
                .getInstallations().length);
        ExamTool newExamTool = TUtil.createAndRegisterExamTool(jenkinsRule, examName, examHome, examRelativePath);
        assertEquals(1, jenkinsRule.getInstance().getDescriptorByType(ExamTool.DescriptorImpl.class)
                .getInstallations().length);

        ExamTool setTool = testObject.getExam();
        assertEquals(newExamTool, setTool);
    }

    @Test
    public void getExam_noExamRegisterd() {
        assertNull(testObject.getExam());
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

        TUtil.assertModelConfig(expected, actual);

        testObject.getDescriptor().getModelConfigs().clear();

        exceptionRule.expect(AbortException.class);
        exceptionRule.expectMessage("ERROR: no model configured with name: " + examName);
        Whitebox.invokeMethod(testObject, "createModelConfig");
    }
}
