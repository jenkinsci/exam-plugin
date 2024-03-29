package jenkins.task;

import Utils.Whitebox;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.internal.enumeration.RestAPILogLevelEnum;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.plugins.exam.config.ExamPluginConfig;
import jenkins.task.TestUtil.TUtil;
import jenkins.task._exam.Messages;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ExamTaskModelDescriptorTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    ExamTaskModel.DescriptorExamTaskModel testObjectDescriptor;
    private ExamTaskModel testObject;
    private String examName;
    private String pythonName;
    private String examReport;
    private String examSysConfig;
    private String examExecFile;

    @Before
    public void setUp() {
        examName = "EXAM";
        pythonName = "Python-2.7";
        examReport = "examReport";
        examExecFile = "EXAM.exe";
        examSysConfig = "testExamSystemConfig";
        testObject = new ExamTaskModel(examName, pythonName, examReport, examExecFile, examSysConfig);
        testObjectDescriptor = testObject.getDescriptor();
    }

    @After
    public void tearDown() {
        testObject = null;
    }

    @Test
    public void getDefaultLogLevel() {
        assertEquals(RestAPILogLevelEnum.INFO.name(), testObjectDescriptor.getDefaultLogLevel());
    }

    @Test
    public void doFillExamModelItems() {
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
    public void doCheckExecutionFile() throws Exception {
        TUtil.doCheckValidSearchElement(testObjectDescriptor, "doCheckExecutionFile");
    }

    @Test
    public void doCheckSystemConfiguration() throws Exception {
        TUtil.doCheckValidSearchElement(testObjectDescriptor, "doCheckSystemConfiguration");
    }

}
