package jenkins.task;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.util.Secret;
import jenkins.internal.data.TestConfiguration;
import jenkins.internal.enumeration.RestAPILogLevelEnum;
import jenkins.model.Jenkins;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamReportConfig;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;
import jenkins.task.TestUtil.FakeExamTask;
import jenkins.task.TestUtil.TUtil;
import jenkins.task._exam.Messages;
import org.hamcrest.CoreMatchers;
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

import static org.junit.Assert.*;

public class ExamTaskTest {
    
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    
    private ExamTask testObject;
    private String examName;
    private String pythonName;
    private String pythonHome;
    private String examHome;
    private String examReport;
    private String examRelativePath;
    private String examSysConfig;
    List<File> createdFiles = new ArrayList<>();
    
    @Before
    public void setUp() {
        examName = "EXAM_name";
        pythonName = "Python-2.7";
        pythonHome = "pythonHome";
        examReport = "examReport";
        examHome = "examHome";
        examRelativePath = "examRelativePath";
        examSysConfig = "testExamSystemConfig";
        
        Jenkins instance = jenkinsRule.getInstance();
        examHome = instance == null ? "examHome" : instance.getRootPath().getRemote();
        testObject = new FakeExamTask(examName, pythonName, examReport, examSysConfig);
    }
    
    @After
    public void tearDown() {
        testObject = null;
    }
    
    @Test
    @WithoutJenkins
    public void getUseExecutionFile() {
        Whitebox.setInternalState(testObject, "useExecutionFile", true);
        
        assertTrue(testObject.getUseExecutionFile());
    }
    
    @Test
    @WithoutJenkins
    public void setUseExecutionFile() {
        testObject.setUseExecutionFile(true);
        boolean setUseExecutionFile = Whitebox.getInternalState(testObject, "useExecutionFile");
        
        assertTrue(setUseExecutionFile);
    }
    
    @Test
    @WithoutJenkins
    public void getReportPrefix() {
        String reportPrefix = "testReportPrefix";
        Whitebox.setInternalState(testObject, "reportPrefix", reportPrefix);
        String setReportPrefix = testObject.getReportPrefix();
        
        assertEquals(reportPrefix, setReportPrefix);
    }
    
    @Test
    @WithoutJenkins
    public void setReportPrefix() {
        String reportPrefix = "testReportPrefix";
        testObject.setReportPrefix(reportPrefix);
        String setReportPrefix = Whitebox.getInternalState(testObject, "reportPrefix");
        
        assertEquals(reportPrefix, setReportPrefix);
    }
    
    @Test
    @WithoutJenkins
    public void getPdfReport() {
        Whitebox.setInternalState(testObject, "pdfReport", true);
        boolean setPdfReport = testObject.getPdfReport();
        
        assertTrue(setPdfReport);
    }
    
    @Test
    @WithoutJenkins
    public void setPdfReport() {
        testObject.setPdfReport(false);
        boolean setPdfReport = Whitebox.getInternalState(testObject, "pdfReport");
        
        assertFalse(setPdfReport);
    }
    
    @Test
    @WithoutJenkins
    public void getPdfReportTemplate() {
        String testPdfReportTemplate = "testPdfReportTemplate";
        Whitebox.setInternalState(testObject, "pdfReportTemplate", testPdfReportTemplate);
        String setPdfReportTemplate = testObject.getPdfReportTemplate();
        
        assertEquals(testPdfReportTemplate, setPdfReportTemplate);
    }
    
    @Test
    @WithoutJenkins
    public void setPdfReportTemplate() {
        String testPdfReportTemplate = "testPdfReportTemplate";
        testObject.setPdfReportTemplate(testPdfReportTemplate);
        String setPdfReportTemplate = Whitebox.getInternalState(testObject, "pdfReportTemplate");
        
        assertEquals(testPdfReportTemplate, setPdfReportTemplate);
    }
    
    @Test
    @WithoutJenkins
    public void getPdfSelectFilter() {
        String pdfSelectFilter = "pdfSelectFilter";
        Whitebox.setInternalState(testObject, "pdfSelectFilter", pdfSelectFilter);
        String setPdfSelectFilter = testObject.getPdfSelectFilter();
        
        assertEquals(pdfSelectFilter, setPdfSelectFilter);
    }
    
    @Test
    @WithoutJenkins
    public void setPdfSelectFilter() {
        String pdfSelectFilter = "testPdfSelectFilter";
        testObject.setPdfSelectFilter(pdfSelectFilter);
        String setPdfSelectFilter = Whitebox.getInternalState(testObject, "pdfSelectFilter");
        
        assertEquals(pdfSelectFilter, setPdfSelectFilter);
    }
    
    @Test
    @WithoutJenkins
    public void getPdfMeasureImages() {
        Whitebox.setInternalState(testObject, "pdfMeasureImages", true);
        boolean setPdfMeasureImages = testObject.getPdfMeasureImages();
        
        assertTrue(setPdfMeasureImages);
    }
    
    @Test
    @WithoutJenkins
    public void setPdfMeasureImages() {
        testObject.setPdfMeasureImages(true);
        boolean setPdfMeasureImages = Whitebox.getInternalState(testObject, "pdfMeasureImages");
        
        assertTrue(setPdfMeasureImages);
    }
    
    @Test
    @WithoutJenkins
    public void getLogging() {
        Whitebox.setInternalState(testObject, "logging", true);
        boolean setLogging = testObject.getLogging();
        
        assertTrue(setLogging);
    }
    
    @Test
    @WithoutJenkins
    public void setLogging() {
        testObject.setLogging(false);
        boolean setLogging = Whitebox.getInternalState(testObject, "logging");
        
        assertFalse(setLogging);
    }
    
    @Test
    @WithoutJenkins
    public void getTestrunFilter() {
        List<TestrunFilter> testrunFilters = TUtil.createTestrunFilter();
        Whitebox.setInternalState(testObject, "testrunFilter", testrunFilters);
        List<TestrunFilter> setTestrunFilter = testObject.getTestrunFilter();
        
        assertEquals(testrunFilters, setTestrunFilter);
    }
    
    @Test
    @WithoutJenkins
    public void setTestrunFilter() {
        List<TestrunFilter> testrunFilters = TUtil.createTestrunFilter();
        testObject.setTestrunFilter(testrunFilters);
        List<TestrunFilter> setFilters = Whitebox.getInternalState(testObject, "testrunFilter");
        
        assertEquals(testrunFilters, setFilters);
    }
    
    @Test
    @WithoutJenkins
    public void getLoglevelTestCtrl() {
        String testLoglevel = "testLoglevel";
        Whitebox.setInternalState(testObject, "loglevelTestCtrl", testLoglevel);
        String setLoglevel = testObject.getLoglevelTestCtrl();
        
        assertEquals(testLoglevel, setLoglevel);
    }
    
    @Test
    @WithoutJenkins
    public void setLoglevelTestCtrl() {
        String testLoglevel = "anotherTestLoglevel";
        testObject.setLoglevelTestCtrl(testLoglevel);
        String setLoglevel = Whitebox.getInternalState(testObject, "loglevelTestCtrl");
        
        assertEquals(testLoglevel, setLoglevel);
    }
    
    @Test
    @WithoutJenkins
    public void getLoglevelLibCtrl() {
        String testLoglevel = "testLoglevel";
        Whitebox.setInternalState(testObject, "loglevelLibCtrl", testLoglevel);
        String setLoglevel = testObject.getLoglevelLibCtrl();
        
        assertEquals(testLoglevel, setLoglevel);
    }
    
    @Test
    @WithoutJenkins
    public void setLoglevelLibCtrl() {
        String testLoglevel = "anotherTestLoglevel";
        testObject.setLoglevelTestLogic(testLoglevel);
        String setLoglevel = Whitebox.getInternalState(testObject, "loglevelTestLogic");
        
        assertEquals(testLoglevel, setLoglevel);
    }
    
    @Test
    @WithoutJenkins
    public void getLoglevelTestLogic() {
        String testLoglevel = "testLoglevel";
        Whitebox.setInternalState(testObject, "loglevelTestLogic", testLoglevel);
        String setLoglevel = testObject.getLoglevelTestLogic();
        
        assertEquals(testLoglevel, setLoglevel);
    }
    
    @Test
    @WithoutJenkins
    public void setLoglevelTestLogic() {
        String testLoglevel = "anotherTestLoglevel";
        testObject.setLoglevelLibCtrl(testLoglevel);
        String setLoglevel = Whitebox.getInternalState(testObject, "loglevelLibCtrl");
        
        assertEquals(testLoglevel, setLoglevel);
    }
    
    @Test
    @WithoutJenkins
    public void isClearWorkspace() {
        Whitebox.setInternalState(testObject, "clearWorkspace", true);
        boolean setClearWorkspace = testObject.isClearWorkspace();
        
        assertTrue(setClearWorkspace);
    }
    
    @Test
    @WithoutJenkins
    public void setClearWorkspace() {
        testObject.setClearWorkspace(false);
        boolean setClearWorkspace = Whitebox.getInternalState(testObject, "clearWorkspace");
        
        assertFalse(setClearWorkspace);
    }
    
    @Test
    @WithoutJenkins
    public void getSystemConfiguration() {
        String setSysConfig = testObject.getSystemConfiguration();
        assertEquals(examSysConfig, setSysConfig);
    }
    
    @Test
    @WithoutJenkins
    public void setSystemConfiguration() {
        testObject.setSystemConfiguration(examSysConfig);
        String setModelConfig = Whitebox.getInternalState(testObject, "systemConfiguration");
        
        assertEquals(examSysConfig, setModelConfig);
    }
    
    @Test
    @WithoutJenkins
    public void getExamReport() {
        String setExamReport = testObject.getExamReport();
        assertEquals(examReport, setExamReport);
    }
    
    @Test
    public void getReport() throws Exception {
        ExamReportConfig modelConfig = Whitebox.invokeMethod(testObject, "getReport", examReport);
        assertNull(modelConfig);
        
        ExamReportConfig rep = new ExamReportConfig();
        rep.setName("nothing");
        ((ExamTask.DescriptorExamTask) testObject.getDescriptor()).getReportConfigs().add(rep);
        modelConfig = Whitebox.invokeMethod(testObject, "getReport", examReport);
        assertNull(modelConfig);
        
        ExamReportConfig rep2 = new ExamReportConfig();
        rep2.setName(examReport);
        ((ExamTask.DescriptorExamTask) testObject.getDescriptor()).getReportConfigs().add(rep2);
        
        modelConfig = Whitebox.invokeMethod(testObject, "getReport", examReport);
        assertEquals(rep2, modelConfig);
    }
    
    @Test
    @WithoutJenkins
    public void getPythonName() {
        String setPythonName = testObject.getPythonName();
        assertEquals(pythonName, setPythonName);
    }
    
    @Test
    public void addReportToTestConfiguration() throws Exception {
        prepareExamReportConfig();
        
        TestConfiguration tc = new TestConfiguration();
        
        Whitebox.invokeMethod(testObject, "addReportToTestConfiguration", tc);
        
        assertReport(tc);
    }
    
    @Test
    public void addLogLevelToTestConfiguration() throws Exception {
        prepareExamReportConfig();
        
        TestConfiguration tc = new TestConfiguration();
        
        Whitebox.invokeMethod(testObject, "addLogLevelToTestConfiguration", tc);
        assertLogLevels(tc);
    }
    
    @Test
    public void addPdfReportToTestConfiguration() throws Exception {
        testObject.setPdfReportTemplate("template");
        testObject.setPdfReport(false);
        testObject.setPdfSelectFilter("filter");
        testObject.setPdfMeasureImages(true);
        
        TestConfiguration tc = new TestConfiguration();
        Whitebox.invokeMethod(testObject, "addPdfReportToTestConfiguration", tc);
        assertEquals("", tc.getPdfReportTemplate());
        assertNull(tc.getPdfSelectFilter());
        
        testObject.setPdfReportTemplate("");
        Whitebox.invokeMethod(testObject, "addPdfReportToTestConfiguration", tc);
        assertEquals("", tc.getPdfReportTemplate());
        assertNull(tc.getPdfSelectFilter());
        
        testObject.setPdfReportTemplate("template");
        testObject.setPdfReport(true);
        Whitebox.invokeMethod(testObject, "addPdfReportToTestConfiguration", tc);
        assertPdfReport(tc);
        
    }
    
    @Test
    public void createTestConfiguration() throws Exception {
        prepareExamReportConfig();
        testObject.setPdfReportTemplate("template");
        testObject.setPdfReport(true);
        testObject.setPdfSelectFilter("filter");
        testObject.setPdfMeasureImages(true);
        
        TestConfiguration tc = Whitebox.invokeMethod(testObject, "createTestConfiguration", null);
        
        assertReport(tc);
        assertLogLevels(tc);
        assertPdfReport(tc);
        
    }
    
    @Test
    public void getPython() {
        assertEquals(0, jenkinsRule.getInstance().getDescriptorByType(PythonInstallation.DescriptorImpl.class)
                .getInstallations().length);
        PythonInstallation newInstallation = TUtil
                .createAndRegisterPythonInstallation(jenkinsRule, pythonName, "testHome");
        assertEquals(1, jenkinsRule.getInstance().getDescriptorByType(PythonInstallation.DescriptorImpl.class)
                .getInstallations().length);
        
        PythonInstallation setInstallation = testObject.getPython();
        assertEquals(setInstallation, newInstallation);
    }
    
    @Test
    public void getPython_noPythonRegisterd() {
        assertNull(testObject.getPython());
    }
    
    @Test
    public void getToolDescriptor() {
        ExamTool newExamTool = TUtil.createAndRegisterExamTool(jenkinsRule, examName, "examHome", "examRelativePath");
        ExamTool.DescriptorImpl descriptor = testObject.getToolDescriptor();
        ExamTool[] tools = descriptor.getInstallations();
        assertEquals(1, tools.length);
        assertEquals(newExamTool, tools[0]);
    }
    
    @Test
    public void perform_noConfig() throws Exception {
        TUtil.createAndRegisterExamTool(jenkinsRule, examName, examHome, examRelativePath);
        TUtil.createAndRegisterPythonInstallation(jenkinsRule, pythonName, pythonHome);
        
        File file = new File(examHome + File.separator + "EXAM.exe");
        boolean fileCreated = file.createNewFile();
        assertTrue("File not created", fileCreated);
        createdFiles.add(file);
        
        FreeStyleProject examTestProject = jenkinsRule.createFreeStyleProject();
        examTestProject.getBuildersList().add(testObject);
        FreeStyleBuild build = examTestProject.scheduleBuild2(0).get();
        Result buildResult = build.getResult();
        assertEquals("FAILURE", buildResult.toString());
        
        List<String> log = build.getLog(1000);
        String workspacePath = jenkinsRule.getInstance().getRootPath().getRemote();
        assertThat(log, CoreMatchers.hasItem("ERROR: " + Messages.EXAM_NotExamConfigDirectory(
                workspacePath + File.separator + "examRelativePath" + File.separator + "configuration"
                        + File.separator + "config.ini")));
    }
    
    @Test
    public void perform_noLicenseConfig() throws Exception {
        TUtil.createAndRegisterExamTool(jenkinsRule, examName, examHome, "./data");
        TUtil.createAndRegisterPythonInstallation(jenkinsRule, pythonName, pythonHome);
        
        File file = new File(examHome + File.separator + "EXAM.exe");
        boolean fileCreated = file.createNewFile();
        assertTrue("File not created", fileCreated);
        createdFiles.add(file);
        File file2 = new File(examHome + File.separator + "data" + File.separator + "configuration" + File.separator
                + "config.ini");
        fileCreated = file2.getParentFile().mkdirs();
        assertTrue("Folder not created", fileCreated);
        fileCreated = file2.createNewFile();
        assertTrue("File not created", fileCreated);
        createdFiles.add(file2);
        
        FreeStyleProject examTestProject = jenkinsRule.createFreeStyleProject();
        examTestProject.getBuildersList().add(testObject);
        FreeStyleBuild build = examTestProject.scheduleBuild2(0).get();
        Result buildResult = build.getResult();
        assertEquals("FAILURE", buildResult.toString());
        
        List<String> log = build.getLog(1000);
        assertThat(log, CoreMatchers.hasItem("ERROR: " + Messages.EXAM_LicenseServerNotConfigured()));
    }
    
    private void runProjectWithoutTools(FreeStyleProject examTestProject, String logContains) throws Exception {
        FreeStyleBuild build = examTestProject.scheduleBuild2(0).get();
        Result buildResult = build.getResult();
        assertEquals("FAILURE", buildResult.toString());
        List<String> log = build.getLog(1000);
        assertThat(log, CoreMatchers.hasItem(logContains));
    }
    
    @Test
    public void perform_noTool() throws Exception {
        FreeStyleProject examTestProject = jenkinsRule.createFreeStyleProject();
        examTestProject.getBuildersList().add(testObject);
        runProjectWithoutTools(examTestProject, "ERROR: python is null");
        
        TUtil.createAndRegisterExamTool(jenkinsRule, examName, examHome, examRelativePath);
        runProjectWithoutTools(examTestProject, "ERROR: python is null");
        TUtil.cleanUpExamTools(jenkinsRule);
        
        TUtil.createAndRegisterPythonInstallation(jenkinsRule, pythonName, pythonHome);
        runProjectWithoutTools(examTestProject, "ERROR: examTool is null");
        TUtil.cleanUpPythonInstallations(jenkinsRule);
        
        TUtil.createAndRegisterExamTool(jenkinsRule, examName, examHome, examRelativePath);
        TUtil.createAndRegisterPythonInstallation(jenkinsRule, pythonName, "");
        runProjectWithoutTools(examTestProject, "ERROR: python home not set");
        TUtil.cleanUpExamTools(jenkinsRule);
        TUtil.cleanUpPythonInstallations(jenkinsRule);
        
        TUtil.createAndRegisterExamTool(jenkinsRule, examName, "", examRelativePath);
        TUtil.createAndRegisterPythonInstallation(jenkinsRule, pythonName, pythonHome);
        runProjectWithoutTools(examTestProject, "ERROR: " + Messages.EXAM_ExecutableNotFound(examName));
        
    }
    
    private void assertPdfReport(TestConfiguration tc) {
        assertEquals("template", tc.getPdfReportTemplate());
        assertEquals("filter", tc.getPdfSelectFilter());
        assertTrue(tc.getPdfMeasureImages());
    }
    
    private void assertLogLevels(TestConfiguration tc) {
        assertEquals(RestAPILogLevelEnum.ERROR, tc.getLogLevelLC());
        assertEquals(RestAPILogLevelEnum.WARNING, tc.getLogLevelTC());
        assertEquals(RestAPILogLevelEnum.INTERNAL, tc.getLogLevelTL());
    }
    
    private void assertReport(TestConfiguration tc) {
        assertEquals(examReport, tc.getReportProject().getProjectName());
        assertEquals("host", tc.getReportProject().getDbHost());
        assertEquals(Integer.valueOf(1234), tc.getReportProject().getDbPort());
        assertEquals("schema", tc.getReportProject().getDbSchema());
        assertEquals("pass", tc.getReportProject().getDbPassword());
        assertEquals("type", tc.getReportProject().getDbType());
        assertEquals("user", tc.getReportProject().getDbUser());
        assertEquals("service", tc.getReportProject().getDbService());
    }
    
    private void prepareExamReportConfig() {
        ExamReportConfig rep = new ExamReportConfig();
        rep.setName(examReport);
        rep.setHost("host");
        rep.setPort("1234");
        rep.setSchema("schema");
        rep.setDbPass(Secret.fromString("pass"));
        rep.setDbType("type");
        rep.setDbUser("user");
        rep.setServiceOrSid("service");
        
        ((ExamTask.DescriptorExamTask) testObject.getDescriptor()).getReportConfigs().add(rep);
        testObject.setUseExecutionFile(true);
        testObject.setLoglevelLibCtrl(RestAPILogLevelEnum.ERROR.name());
        testObject.setLoglevelTestCtrl(RestAPILogLevelEnum.WARNING.name());
        testObject.setLoglevelTestLogic(RestAPILogLevelEnum.INTERNAL.name());
    }
}
