package jenkins.task;

import Utils.Whitebox;
import hudson.model.FreeStyleProject;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.plugins.exam.config.ExamPluginConfig;
import jenkins.task.TestUtil.TUtil;
import jenkins.task._exam.Messages;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GroovyTaskDescriptorTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    GroovyTask.DescriptorGroovyTask testObjectDescriptor;
    FreeStyleProject freeStyleProject;
    private GroovyTask testObject;
    private String script;
    private String startElement;
    private String examName;
    private String examModel;
    private String modelConfig;

    @Before
    public void setUp() throws IOException {
        modelConfig = "ITEST";
        script = "test";
        startElement = "";
        examName = "EXAM";
        examModel = "examHome";
        freeStyleProject = jenkinsRule.createFreeStyleProject();
        testObject = new GroovyTask(script, startElement, examName, examModel, modelConfig);
        testObjectDescriptor = (GroovyTask.DescriptorGroovyTask) testObject.getDescriptor();
    }

    @After
    public void tearDown() {
        testObject = null;
    }

    @Test
    public void testGetInstallations() {
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
    public void testGetModelConfigs() {
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

    @Test
    public void testDoFillExamNameItems() {
        String examHome = "examHome";
        String examRelativePath = "examRelativePath";
        int num = 5;
        String[] expected = new String[num];
        for (int i = 0; i < num; i++) {
            expected[i] = examName + "_" + i;
            TUtil.createAndRegisterExamTool(jenkinsRule, examName + "_" + i, examHome, examRelativePath);
        }

        ListBoxModel items = testObjectDescriptor.doFillExamNameItems();

        String[] actual = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            actual[i] = items.get(i).name;
        }
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testDoFillExamModelItems() {
        ExamPluginConfig descriptor = (ExamPluginConfig) jenkinsRule.getInstance()
                .getDescriptor(ExamPluginConfig.class);
        List<ExamModelConfig> modelConfigs = new ArrayList<>();
        int num = 5;
        String[] expected = new String[num];
        for (int i = 0; i < num; i++) {
            String name = "EMC_" + i;
            ExamModelConfig modelConfig = new ExamModelConfig(name);
            modelConfig.setName(name);
            modelConfigs.add(modelConfig);
            expected[i] = name;
        }

        descriptor.setModelConfigs(modelConfigs);
        ListBoxModel items = testObjectDescriptor.doFillExamModelItems();

        String[] actual = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            actual[i] = items.get(i).value;
        }

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testDoCheckModelConfiguration() throws Exception {
        doCheckValid("doCheckModelConfiguration");
    }

    @Test
    public void testDoCheckScript() throws Exception {
        doCheckValid("doCheckScript");
    }

    @Test
    public void testDoCheckStartElement() throws Exception {
        doCheckValid("doCheckStartElement");
    }

    @Test
    public void isApplicable() {
        assertTrue(testObjectDescriptor.isApplicable(freeStyleProject.getClass()));
    }

    // HELP METHOD
    private void doCheckValid(String method) throws Exception {
        String newLine = "\r\n";
        String expectedErrorMsg =
                Messages.EXAM_RegExUuid() + newLine + Messages.EXAM_RegExId() + newLine + Messages.EXAM_RegExFsn()
                        + newLine;

        String invalidString = "#IAmAlsoNoPythonConformName";
        String validString = TUtil.generateValidId();

        FormValidation fv_invalidResult = Whitebox.invokeMethod(testObjectDescriptor, method, invalidString);
        FormValidation fv_validResult = Whitebox.invokeMethod(testObjectDescriptor, method, validString);

        assertEquals(FormValidation.error(expectedErrorMsg).getMessage(), fv_invalidResult.getMessage());
        assertEquals(FormValidation.ok(), fv_validResult);
    }
}
