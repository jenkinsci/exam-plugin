package jenkins.task;

import hudson.AbortException;
import hudson.model.FreeStyleProject;
import jenkins.internal.data.ModelConfiguration;
import jenkins.internal.data.TestConfiguration;
import jenkins.model.Jenkins;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.task.TestUtil.TUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.crypto.*" })
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
    List<File> createdFiles = new ArrayList<>();
    
    @Before
    public void setUp() {
        examName = "EXAM_name";
        pythonName = "Python-2.7";
        pythonHome = "pythonHome";
        examModel = "EXAM44";
        examReport = "examReport";
        examHome = "examHome";
        examRelativePath = "examRelativePath";
        examExecFile = "EXAM.exe";
        examSysConfig = "testExamSystemConfig";
        
        Jenkins instance = jenkinsRule.getInstance();
        examHome = instance == null ? "examHome" : instance.getRootPath().getRemote();
        testObject = new ExamTaskModel(examName, pythonName, examReport, examExecFile, examSysConfig);
    }
    
    @After
    public void tearDown() {
        TUtil.cleanUpExamTools(jenkinsRule);
        TUtil.cleanUpPythonInstallations(jenkinsRule);
        
        examTestProject = null;
        testObject = null;
        createdFiles.forEach(file -> {
            if (file.exists()) {
                file.delete();
            }
        });
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
        
        ExamModelConfig mod = new ExamModelConfig("nothing");
        mod.setName("nothing");
        testObject.getDescriptor().getModelConfigs().add(mod);
        modelConfig = Whitebox.invokeMethod(testObject, "getModel", examModel);
        assertNull(modelConfig);
        
        ExamModelConfig mod2 = new ExamModelConfig(examModel);
        mod2.setName(examModel);
        testObject.getDescriptor().getModelConfigs().add(mod2);
        
        modelConfig = Whitebox.invokeMethod(testObject, "getModel", examModel);
        assertEquals(mod2, modelConfig);
    }
    
    @Test(expected = AbortException.class)
    public void addDataToTestConfiguration_withException() throws AbortException {
        TestConfiguration tc = new TestConfiguration();
        
        testObject.addDataToTestConfiguration(tc, null);
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
        
        tc = testObject.addDataToTestConfiguration(tc, null);
        
        ModelConfiguration m = tc.getModelProject();
        assertEquals(examModel, m.getProjectName());
        assertEquals(modelConfig, m.getModelConfigUUID());
        assertEquals(examExecFile, tc.getTestObject());
    }
}
