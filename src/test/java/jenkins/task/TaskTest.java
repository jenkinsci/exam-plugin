package jenkins.task;

import jenkins.plugins.exam.ExamTool;
import jenkins.task.TestUtil.FakeExamTask;
import jenkins.task.TestUtil.TUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TaskTest {
    
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    
    private Task testObject;
    private String examName;
    private String pythonName;
    private String examReport;
    private String examSysConfig;
    private String examHome;
    private String examRelativePath;
    
    @Before
    public void setUp() {
        examName = "EXAM";
        pythonName = "Python-2.7";
        examReport = "examReport";
        examSysConfig = "testExamSystemConfig";
        examHome = "examHome";
        examRelativePath = "examRelativePath";
        testObject = new FakeExamTask(examName, pythonName, examReport, examSysConfig);
    }
    
    @After
    public void tearDown() {
        testObject = null;
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
    @WithoutJenkins
    public void getTimeout() {
        int testTimeout = 1234;
        Whitebox.setInternalState(testObject, "timeout", testTimeout);
        int setTimeout = testObject.getTimeout();
        
        assertEquals(testTimeout, setTimeout);
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
}
