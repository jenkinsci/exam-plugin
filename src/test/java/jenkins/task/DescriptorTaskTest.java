package jenkins.task;

import hudson.model.FreeStyleProject;
import jenkins.model.Jenkins;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.plugins.exam.config.ExamPluginConfig;
import jenkins.task.TestUtil.FakeTask;
import jenkins.task.TestUtil.TUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.JenkinsRule;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Jenkins.class)
@PowerMockIgnore({ "javax.crypto.*" })
public class DescriptorTaskTest {
    
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    
    private Task testObject;
    FakeTask.DescriptorTask testObjectDescriptor;
    private String examName;
    
    @Before
    public void setUp() {
        examName = "EXAM";
        testObject = new FakeTask();
        testObjectDescriptor = testObject.getDescriptor();
    }
    
    @After
    public void tearDown() {
        testObject = null;
    }
    
    @Test
    public void testConstructor() {
        Task.DescriptorTask descriptor = new Task.DescriptorTask(Task.class);
        assertNotNull(descriptor);
    }
    
    @Test
    public void isApplicable() throws IOException {
        FreeStyleProject freeStyleProject = jenkinsRule.createFreeStyleProject();
        assertTrue(testObjectDescriptor.isApplicable(freeStyleProject.getClass()));
    }
    
    @Test
    public void getInstallations() {
        String examHome = "examHome";
        String examRelativePath = "examRelativePath";
        int num = 5;
        ExamTool[] expected = new ExamTool[num];
        for (int i = 0; i < num; i++) {
            expected[i] = TUtil
                    .createAndRegisterExamTool(jenkinsRule, examName + "_" + i, examHome, examRelativePath);
        }
        
        ExamTool[] actual = testObjectDescriptor.getInstallations();
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void getModelConfigs() {
        ExamPluginConfig descriptor = (ExamPluginConfig) jenkinsRule.getInstance()
                .getDescriptor(ExamPluginConfig.class);
        List<ExamModelConfig> modelConfigs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            modelConfigs.add(new ExamModelConfig("EMC_" + i));
        }
        descriptor.setModelConfigs(modelConfigs);
        List<ExamModelConfig> actual = testObjectDescriptor.getModelConfigs();
        assertEquals(modelConfigs, actual);
    }
}
