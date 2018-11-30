package jenkins.task;

import hudson.AbortException;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import jenkins.internal.data.ModelConfiguration;
import jenkins.internal.data.TestConfiguration;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;
import jenkins.task.TestUtil.Util;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ExamTaskModelTest {
    
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();
    private FreeStyleProject examTestProject;
    private ExamTaskModel testObject;
    private String examName;
    private String pythonName;
    private String pythonHome;
    private String examModel;
    private String examExecFile;
    private String examHome;
    private String examReport;
    private String examRelativePath;
    private String examSysConfig;
    
    @Before
    public void setUp() throws Exception {
        examName = "EXAM";
        pythonName = "Python-2.7";
        pythonHome = "pythonHome";
        examModel = "EXAM44";
        examReport = "examReport";
        examHome = "examHome";
        examRelativePath = "examRelativePath";
        examExecFile = "EXAM.exe";
        examSysConfig = "testExamSystemConfig";
        examTestProject = jenkinsRule.createFreeStyleProject();
        testObject = new ExamTaskModel(examName, pythonName, examReport, examExecFile, examSysConfig);
    }
    
    @After
    public void tearDown() {
        Util.cleanUpExamTools(jenkinsRule);
        Util.cleanUpPythonInstallations(jenkinsRule);
        
        examTestProject = null;
        testObject = null;
    }
    
    @Test
    public void getModelConfiguration() {
        String modelConfig = "testModelConfig";
        Whitebox.setInternalState(testObject, "modelConfiguration", modelConfig);
        String setModelConfig = testObject.getModelConfiguration();
        
        assertEquals(modelConfig, setModelConfig);
    }
    
    @Test
    public void setModelConfiguration() {
        String modelConfig = "testModelConfig";
        testObject.setModelConfiguration(modelConfig);
        String setModelConfig = Whitebox.getInternalState(testObject, "modelConfiguration");
        
        assertEquals(modelConfig, setModelConfig);
    }
    
    @Test
    public void getExamModel() {
        testObject.setExamModel(examModel);
        String setModelName = testObject.getExamModel();
        assertEquals(examModel, setModelName);
    }
    
    @Test
    public void getExecutionFile() {
        String setExecFile = testObject.getExecutionFile();
        assertEquals(examExecFile, setExecFile);
    }
    
    @Test
    public void getPython() {
        assertEquals(0, jenkinsRule.getInstance().getDescriptorByType(PythonInstallation.DescriptorImpl.class)
                .getInstallations().length);
        PythonInstallation newInstallation = Util
                .createAndRegisterPythonInstallation(jenkinsRule, pythonName, "testHome");
        assertEquals(1, jenkinsRule.getInstance().getDescriptorByType(PythonInstallation.DescriptorImpl.class)
                .getInstallations().length);
        
        PythonInstallation setInstallation = testObject.getPython();
        assertEquals(setInstallation, newInstallation);
    }
    
    @Test
    public void getPython_noPythonRegisterd() {
        assertNull(testObject.getPython());
    }
    
    @Test
    public void getExam() {
        assertEquals(0, jenkinsRule.getInstance().getDescriptorByType(ExamTool.DescriptorImpl.class)
                .getInstallations().length);
        ExamTool newExamTool = Util.createAndRegisterExamTool(jenkinsRule, examName, examHome, examRelativePath);
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
    public void perform_throwsAbortException() throws Exception {
        Util.createAndRegisterExamTool(jenkinsRule, examName, examHome, examRelativePath);
        Util.createAndRegisterPythonInstallation(jenkinsRule, pythonName, pythonHome);
        
        examTestProject.getBuildersList().add(testObject);
        FreeStyleBuild build = examTestProject.scheduleBuild2(0).get();
        Result buildResult = build.getResult();
        assertEquals("FAILURE", buildResult.toString());
    }
    
    @Test
    public void addDataToTestConfiguration() throws AbortException {
        
        String modelConfig = "testModelConfig";
        testObject.setModelConfiguration(modelConfig);
        testObject.setExamModel(examModel);
        
        ExamModelConfig mod = new ExamModelConfig(examModel);
        mod.setName(examModel);
        testObject.getDescriptor().getModelConfigs().add(mod);
        
        TestConfiguration tc = new TestConfiguration();
        
        tc = testObject.addDataToTestConfiguration(tc);
        
        ModelConfiguration m = tc.getModelProject();
        assertEquals(examModel, m.getProjectName());
        assertEquals(modelConfig, m.getModelConfigUUID());
        assertEquals(examExecFile, tc.getTestObject());
    }
}
