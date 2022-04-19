package jenkins.plugins.exam.config;

import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.util.FormValidation;
import jenkins.internal.enumeration.DbKind;
import jenkins.task.TestUtil.TUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.junit.Assert.*;

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
    public void testConstructor() {
        ExamReportConfig.DescriptorImpl descriptor = new ExamReportConfig.DescriptorImpl(ExamReportConfig.class);
        assertNotNull(descriptor);
    }

    @Test
    public void getDbTypes() {
        DbKind[] types = testObjectDescriptor.getDbTypes();
        assertArrayEquals(DbKind.values(), types);
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

    @Test
    public void configure() throws IOException, SAXException {
        TUtil.createAndRegisterExamPluginConfig(jenkinsRule);

        JenkinsRule.WebClient webClient = jenkinsRule.createWebClient();
        HtmlPage page = webClient.goTo("configure");
        ExamPluginConfig examPluginConfig = jenkinsRule.getInstance().getDescriptorByType(ExamPluginConfig.class);
        examPluginConfig.setPort(0);
        examPluginConfig.getReportConfigs().clear();
        examPluginConfig.getModelConfigs().clear();

        HtmlForm form = page.getFormByName("config");
        HtmlButton button = jenkinsRule.getButtonByCaption(form, "Apply");
        button.click();

        assertEquals(TUtil.pluginConfigPort, examPluginConfig.getPort());
        assertEquals(1, examPluginConfig.getModelConfigs().size());
        assertEquals(1, examPluginConfig.getReportConfigs().size());
    }
}
