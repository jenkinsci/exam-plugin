package jenkins.plugins.exam;

import Utils.Whitebox;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import jenkins.model.Jenkins;
import jenkins.task.TestUtil.TUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ExamToolDescriptorTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    ExamTool testObject;
    ExamTool.DescriptorImpl testObjectDescriptor;
    String name = "EXAM_4.7";
    String home = "";
    String examHome = "examHome";
    String examRelativePath = "examRelativePath";

    @Before
    public void setUp() {
        Jenkins instance = jenkinsRule.getInstance();
        home = instance == null ? "C:\\EXAM" : instance.getRootPath().getRemote();
        testObject = new ExamTool(name, home, new ArrayList<>(), null);
        testObjectDescriptor = (ExamTool.DescriptorImpl) testObject.getDescriptor();
    }

    @After
    public void tearDown() {
        testObject = null;
        TUtil.cleanUpExamTools(jenkinsRule);
    }

    @Test
    public void getApplicableDescriptors() {
        List<ExamTool.DescriptorImpl> descriptors = testObjectDescriptor.getApplicableDescriptors();
        assertEquals(1, descriptors.size());
        assertEquals("jenkins.plugins.exam.ExamTool", descriptors.get(0).clazz.getName());
    }

    @Test
    public void setInstallations() {
        ExamTool[] examTools = createExamTools();
        testObjectDescriptor.setInstallations(examTools);

        ExamTool[] actual = Whitebox.getInternalState(testObjectDescriptor, "installations");
        assertArrayEquals(examTools, actual);
    }

    @Test
    public void getInstallations() {
        ExamTool[] examTools = createExamTools();
        Whitebox.setInternalState(testObjectDescriptor, "installations", examTools);

        ExamTool[] actual = testObjectDescriptor.getInstallations();
        assertArrayEquals(examTools, actual);
    }

    @Test
    public void configure() throws IOException, SAXException {
        TUtil.createAndRegisterExamTool(jenkinsRule, name, examHome, examRelativePath);

        JenkinsRule.WebClient webClient = jenkinsRule.createWebClient();
        HtmlPage page = webClient.goTo("configureTools");
        TUtil.cleanUpExamTools(jenkinsRule);

        HtmlForm form = page.getFormByName("config");
        HtmlButton button = jenkinsRule.getButtonByCaption(form, "Apply");
        button.click();

        ExamTool[] installations = jenkinsRule.getInstance().getDescriptorByType(ExamTool.DescriptorImpl.class)
                .getInstallations();
        assertEquals(1, installations.length);
        assertEquals(name, installations[0].getName());
    }

    private ExamTool[] createExamTools() {
        int num = 5;
        ExamTool[] examTools = new ExamTool[num];
        for (int i = 0; i < num; i++) {
            examTools[i] = new ExamTool(name + "_" + i, examHome + "_" + i, Collections.emptyList(),
                    examRelativePath + "_" + i);
        }
        return examTools;
    }
}
