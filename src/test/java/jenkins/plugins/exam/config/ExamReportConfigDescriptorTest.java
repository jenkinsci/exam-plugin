package jenkins.plugins.exam.config;

import hudson.util.FormValidation;
import jenkins.internal.enumeration.DbKind;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


public class ExamReportConfigDescriptorTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    ExamReportConfig testObject;
    ExamReportConfig.DescriptorImpl testObjectDescriptor;

    @Before
    public void setUp() {
        testObject = new ExamReportConfig();
        testObjectDescriptor = testObject.getDescriptor();
    }

    @After
    public void tearDown() {
        testObject = null;
    }

    @Test
    public void getDbTypes() {
        DbKind[] types = testObjectDescriptor.getDbTypes();
        assertArrayEquals(DbKind.values(), types);
    }

    @Test
    public void doCheckName() throws UnsupportedEncodingException {
        FormValidation actual = testObjectDescriptor.doCheckName("test");
        assertEquals(FormValidation.ok(), actual);

        actual = testObjectDescriptor.doCheckName("fail test");
        assertEquals(FormValidation.Kind.ERROR, actual.kind);
        String expected = StringEscapeUtils.escapeHtml(Messages.ExamPluginConfig_spacesNotAllowed());
        assertEquals(expected, actual.getMessage());
    }
}