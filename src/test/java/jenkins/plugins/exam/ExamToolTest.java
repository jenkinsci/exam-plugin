package jenkins.plugins.exam;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.slaves.DumbSlave;
import hudson.tools.ToolLocationNodeProperty;
import hudson.util.StreamTaskListener;
import jenkins.model.Jenkins;
import jenkins.task.TestUtil.TUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;
import org.powermock.reflect.Whitebox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExamToolTest {
    
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    
    ExamTool testObject;
    String name = "EXAM_4.7";
    String home = "";
    String examHome = "examHome";
    String examRelativePath = "examRelativePath";
    List<File> createdFiles = new ArrayList<>();
    
    @Before
    public void setUp() {
        Jenkins instance = jenkinsRule.getInstance();
        home = instance == null ? "C:\\EXAM" : instance.getRootPath().getRemote();
        testObject = new ExamTool(name, home, new ArrayList<>(), null);
    }
    
    @After
    public void tearDown() {
        testObject = null;
        createdFiles.forEach(file -> {
            if (file.exists()) {
                file.delete();
            }
        });
    }
    
    @Test
    @WithoutJenkins
    public void setRelativeDataPath() {
        String testPath = "../userData";
        Whitebox.setInternalState(testObject, "relativeDataPath", testPath);
        String setPath = Whitebox.getInternalState(testObject, "relativeDataPath");
        
        assertEquals(testPath, setPath);
    }
    
    @Test
    @WithoutJenkins
    public void getRelativeConfigPath() {
        String testPath = "../userData";
        Whitebox.setInternalState(testObject, "relativeDataPath", testPath);
        String setPath = testObject.getRelativeDataPath();
        
        assertEquals(testPath, setPath);
    }
    
    @Test
    public void forNode() throws Exception {
        DumbSlave slave = createSlave();
        ExamTool examTool = testObject.forNode(slave, null);
        
        assertEquals(examHome + "_slave", examTool.getHome());
    }
    
    @Test
    @WithoutJenkins
    public void forEnvironment() {
        EnvVars envVars = new EnvVars();
        envVars.put("first", "first");
        envVars.put("second", "second");
        String testPath = "../userData";
        Whitebox.setInternalState(testObject, "relativeDataPath", testPath);
        ExamTool testTool = testObject.forEnvironment(envVars);
        
        assertEquals(testPath, testTool.getRelativeDataPath());
        assertEquals(name, testTool.getName());
        assertEquals(home, testTool.getHome());
    }
    
    @Test
    public void getExecutable() throws Exception {
        
        Launcher launcher = new Launcher.LocalLauncher(StreamTaskListener.fromStdout(), null);
        String actual = testObject.getExecutable(launcher);
        assertEquals("", actual);
        
        launcher = jenkinsRule.createLocalLauncher();
        actual = testObject.getExecutable(launcher);
        assertEquals("", actual);
        
        File file = new File(home + File.separator + "EXAM.exe");
        boolean fileCreated = file.createNewFile();
        assertTrue("File not created", fileCreated);
        createdFiles.add(file);
        
        actual = testObject.getExecutable(launcher);
        assertEquals(home + File.separator + "EXAM.exe", actual);
        
    }
    
    private DumbSlave createSlave() throws Exception {
        
        DumbSlave slave = jenkinsRule.createOnlineSlave();
        slave.setLabelString("exam");
        ExamTool.DescriptorImpl descriptor = jenkinsRule.getInstance()
                .getDescriptorByType(ExamTool.DescriptorImpl.class);
        
        TUtil.createAndRegisterExamTool(jenkinsRule, name, examHome, examRelativePath);
        slave.getNodeProperties().add(new ToolLocationNodeProperty(
                new ToolLocationNodeProperty.ToolLocation(descriptor, name, examHome + "_slave")));
        
        slave.save();
        return slave;
    }
}
