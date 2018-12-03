package jenkins.plugins.exam.config;

import hudson.util.FormValidation;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertEquals;

public class ExamModelConfigDescriptorTest {
    
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    
    ExamModelConfig testObject;
    ExamModelConfig.DescriptorImpl testObjectDescriptor;
    
    @Before
    public void setUp() {
        testObject = new ExamModelConfig("ExamTaskModel");
        testObjectDescriptor = (ExamModelConfig.DescriptorImpl) testObject.getDescriptor();
    }
    
    @After
    public void tearDown() {
        testObject = null;
    }
    
    @Test
    public void doCheckName() {
        FormValidation actual = testObjectDescriptor.doCheckName("test");
        assertEquals(FormValidation.ok(), actual);
        
        actual = testObjectDescriptor.doCheckName("fail test");
        assertEquals(FormValidation.Kind.ERROR, actual.kind);
        String expected = StringEscapeUtils.escapeHtml(Messages.ExamPluginConfig_spacesNotAllowed());
        assertEquals(expected, actual.getMessage());
    }
}
