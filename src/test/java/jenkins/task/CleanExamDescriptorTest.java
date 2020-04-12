package jenkins.task;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertNotNull;

public class CleanExamDescriptorTest {
    
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    
    @Test
    public void testConstructor() {
        CleanExam.DescriptorImpl descriptor = new CleanExam.DescriptorImpl(CleanExam.class);
        assertNotNull(descriptor);
    }
}
