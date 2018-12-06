package jenkins.task;

import hudson.AbortException;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import jenkins.internal.data.ModelConfiguration;
import jenkins.internal.data.TestConfiguration;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.task.TestUtil.TUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;
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
        testObject = new ExamTaskModel(examName, pythonName, examReport, examExecFile, examSysConfig);
    }
    
    @After
    public void tearDown() {
        TUtil.cleanUpExamTools(jenkinsRule);
        TUtil.cleanUpPythonInstallations(jenkinsRule);
        
        examTestProject = null;
        testObject = null;
    }
    
    @Test
    @WithoutJenkins
    public void getModelConfiguration() {
        String modelConfig = "testModelConfig";
        Whitebox.setInternalState(testObject, "modelConfiguration", modelConfig);
        String setModelConfig = testObject.getModelConfiguration();
        
        assertEquals(modelConfig, setModelConfig);
    }
    
    @Test
    @WithoutJenkins
    public void setModelConfiguration() {
        String modelConfig = "testModelConfig";
        testObject.setModelConfiguration(modelConfig);
        String setModelConfig = Whitebox.getInternalState(testObject, "modelConfiguration");
        
        assertEquals(modelConfig, setModelConfig);
    }
    
    @Test
    @WithoutJenkins
    public void getExamModel() {
        testObject.setExamModel(examModel);
        String setModelName = testObject.getExamModel();
        assertEquals(examModel, setModelName);
    }
    
    @Test
    @WithoutJenkins
    public void getExecutionFile() {
        String setExecFile = testObject.getExecutionFile();
        assertEquals(examExecFile, setExecFile);
    }
    
    @Test
    @WithoutJenkins
    public void setExecutionFile() throws Exception {
        String testString = "testString";
        testObject.setExecutionFile(testString);
        String setExecutionFile = Whitebox.getInternalState(testObject, "executionFile");
        
        assertEquals(testString, setExecutionFile);
    }
    
    @Test
    public void getModel() throws Exception {
        ExamModelConfig modelConfig = Whitebox.invokeMethod(testObject, "getModel", examModel);
        assertNull(modelConfig);
        
        ExamModelConfig mod = new ExamModelConfig(examModel);
        mod.setName(examModel);
        testObject.getDescriptor().getModelConfigs().add(mod);
        
        modelConfig = Whitebox.invokeMethod(testObject, "getModel", examModel);
        assertEquals(mod, modelConfig);
    }
    
    @Test
    public void perform_throwsAbortException() throws Exception {
        TUtil.createAndRegisterExamTool(jenkinsRule, examName, examHome, examRelativePath);
        TUtil.createAndRegisterPythonInstallation(jenkinsRule, pythonName, pythonHome);
        
        examTestProject = jenkinsRule.createFreeStyleProject();
        examTestProject.getBuildersList().add(testObject);
        FreeStyleBuild build = examTestProject.scheduleBuild2(0).get();
        Result buildResult = build.getResult();
        assertEquals("FAILURE", buildResult.toString());
    }
    
    @Test(expected = AbortException.class)
    public void addDataToTestConfiguration_withException() throws AbortException {
        TestConfiguration tc = new TestConfiguration();
        
        testObject.addDataToTestConfiguration(tc);
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
