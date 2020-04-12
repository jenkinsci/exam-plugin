package jenkins.task;

import hudson.model.FreeStyleProject;
import hudson.util.ListBoxModel;
import jenkins.internal.data.ReportConfiguration;
import jenkins.internal.enumeration.RestAPILogLevelEnum;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.plugins.exam.config.ExamPluginConfig;
import jenkins.plugins.exam.config.ExamReportConfig;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;
import jenkins.task.TestUtil.FakeTask;
import jenkins.task.TestUtil.TUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ExamTaskDescriptorTest {
    
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    
    private ExamTask testObject;
    FakeTask.DescriptorExamTask testObjectDescriptor;
    private String examName;
    private String pythonName;
    private String examReport;
    private String examSysConfig;
    FreeStyleProject freeStyleProject;
    
    @Before
    public void setUp() throws IOException {
        examName = "EXAM";
        pythonName = "Python-2.7";
        examReport = "examReport";
        examSysConfig = "testExamSystemConfig";
        freeStyleProject = jenkinsRule.createFreeStyleProject();
        testObject = new FakeTask(examName, pythonName, examReport, examSysConfig);
        testObjectDescriptor = (FakeTask.DescriptorExamTask) testObject.getDescriptor();
    }
    
    @After
    public void tearDown() {
        testObject = null;
    }
    
    @Test
    public void doFillExamReportItems() {
        ExamPluginConfig descriptor = (ExamPluginConfig) jenkinsRule.getInstance()
                .getDescriptor(ExamPluginConfig.class);
        List<ExamReportConfig> reportConfigs = new ArrayList<>();
        int num = 5;
        String[] expected = new String[num + 1];
        for (int i = 0; i < num; i++) {
            ExamReportConfig reportConfig = new ExamReportConfig();
            reportConfig.setName("ERC_" + i);
            reportConfigs.add(reportConfig);
            expected[i] = "ERC_" + i;
        }
        expected[num] = ReportConfiguration.NO_REPORT;
        
        ExamReportConfig noReport = new ExamReportConfig();
        noReport.setName(ReportConfiguration.NO_REPORT);
        noReport.setSchema("");
        noReport.setHost("");
        noReport.setPort("0");
        reportConfigs.add(0, noReport);
        
        descriptor.setReportConfigs(reportConfigs);
        ListBoxModel items = testObjectDescriptor.doFillExamReportItems();
        
        String[] actual = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            actual[i] = items.get(i).value;
        }
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void doFillLoglevelLibCtrlItems() {
        ListBoxModel items = testObjectDescriptor.doFillLoglevelLibCtrlItems();
        checkLogLevel(items);
    }
    
    @Test
    public void doFillLoglevelTestCtrlItems() {
        ListBoxModel items = testObjectDescriptor.doFillLoglevelTestCtrlItems();
        checkLogLevel(items);
    }
    
    @Test
    public void doFillLoglevelTestLogicItems() {
        ListBoxModel items = testObjectDescriptor.doFillLoglevelTestLogicItems();
        checkLogLevel(items);
    }
    
    @Test
    public void doFillPythonNameItems() {
        int num = 5;
        String[] expected = new String[num];
        for (int i = 0; i < num; i++) {
            expected[i] = pythonName + "_" + i;
            TUtil.createAndRegisterPythonInstallation(jenkinsRule, pythonName + "_" + i, "testHome");
        }
        
        ListBoxModel items = testObjectDescriptor.doFillPythonNameItems();
        
        String[] actual = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            actual[i] = items.get(i).name;
        }
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void getDefaultLogLevel() {
        assertEquals(RestAPILogLevelEnum.INFO.name(), testObjectDescriptor.getDefaultLogLevel());
    }
    
    @Test
    public void doFillExamNameItems() {
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
    public void getLoglevelItems() throws Exception {
        ListBoxModel items = Whitebox.invokeMethod(testObjectDescriptor, "getLoglevelItems");
        checkLogLevel(items);
    }
    
    @Test
    public void getLogLevels() {
        assertArrayEquals(RestAPILogLevelEnum.values(), testObjectDescriptor.getLogLevels());
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
    
    @Test
    public void getPythonInstallations() {
        int num = 5;
        PythonInstallation[] expected = new PythonInstallation[num];
        for (int i = 0; i < num; i++) {
            expected[i] = TUtil.createAndRegisterPythonInstallation(jenkinsRule, pythonName + "_" + i, "testHome");
        }
        
        PythonInstallation[] actual = testObjectDescriptor.getPythonInstallations();
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void getReportConfigs() {
        
        ExamPluginConfig descriptor = (ExamPluginConfig) jenkinsRule.getInstance()
                .getDescriptor(ExamPluginConfig.class);
        List<ExamReportConfig> reportConfigs = new ArrayList<>();
        List<ExamReportConfig> expected = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ExamReportConfig reportConfig = new ExamReportConfig();
            reportConfig.setName("ERC_" + i);
            reportConfigs.add(reportConfig);
        }
        descriptor.setReportConfigs(reportConfigs);
        ExamReportConfig noReport = new ExamReportConfig();
        noReport.setName(ReportConfiguration.NO_REPORT);
        noReport.setSchema("");
        noReport.setHost("");
        noReport.setPort("0");
        expected.add(0, noReport);
        expected.addAll(reportConfigs);
        
        List<ExamReportConfig> actual = testObjectDescriptor.getReportConfigs();
        checkNamesEqual(expected, actual);
        
        actual = testObjectDescriptor.getReportConfigs();
        checkNamesEqual(expected, actual);
    }
    
    @Test
    public void isApplicable() {
        assertTrue(testObjectDescriptor.isApplicable(freeStyleProject.getClass()));
    }
    
    //#region Helpermethod
    
    private void checkNamesEqual(List<ExamReportConfig> expected, List<ExamReportConfig> actual) {
        if (expected.size() != actual.size()) {
            throw new ComparisonFailure("Lists differ in size", String.valueOf(expected.size()),
                    String.valueOf(actual.size()));
        }
        List<String> expectedStrings = new ArrayList<>();
        List<String> actualStrings = new ArrayList<>();
        
        expected.forEach(item -> expectedStrings.add(item.getName()));
        actual.forEach(item -> actualStrings.add(item.getName()));
        assertEquals(expectedStrings, actualStrings);
    }
    
    private void checkLogLevel(ListBoxModel items) {
        String[] expected = RestAPILogLevelEnum.getValues();
        String[] actual = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            actual[i] = items.get(i).name;
        }
        assertArrayEquals(expected, actual);
    }
    
    //#endregion
}
