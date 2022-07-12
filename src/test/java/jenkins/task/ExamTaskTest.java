package jenkins.task;

import Utils.Mocks;
import Utils.Whitebox;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.util.Secret;
import jenkins.internal.ClientRequest;
import jenkins.internal.Compatibility;
import jenkins.internal.RemoteService;
import jenkins.internal.data.TestConfiguration;
import jenkins.internal.enumeration.RestAPILogLevelEnum;
import jenkins.model.Jenkins;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamReportConfig;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;
import jenkins.task.TestUtil.*;
import jenkins.task._exam.Messages;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class ExamTaskTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    List<File> createdFiles = new ArrayList<>();
    private ExamTask testObject;
    private String examName;
    private String pythonName;
    private String pythonHome;
    private String examHome;
    private String examReport;
    private String examRelativePath;
    private String examSysConfig;

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
        Mocks.resetMocks();
        testObject = null;
        for (File file : createdFiles) {
            if (file.exists()) {
                file.delete();
            }
        }
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

        TestConfiguration tc = Whitebox.invokeMethod(testObject, "createTestConfiguration", EnvVars.class, null);

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
        file2.getParentFile().mkdirs();
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

    @Test
    public void perform() throws Exception {
        TUtil.createAndRegisterPythonInstallation(jenkinsRule, pythonName, pythonHome);
        Executor executor = Mockito.mock(Executor.class);
        Run runMock = Mockito.mock(Run.class);
        Mockito.when(runMock.getExecutor()).thenReturn(executor);

        ExamTaskHelper helperMock = Mockito.mock(ExamTaskHelper.class, "ExamTaskHelperMock");
        FakeTaskListener listener = new FakeTaskListener();
        Launcher launcher = new Launcher.DummyLauncher(listener);
        FilePath filePath = new FilePath(new File(""));

        testObject = new FakeExamTaskWithoutExecute(examName, pythonName, examReport, examSysConfig);
        Whitebox.setInternalState(testObject, "taskHelper", helperMock);
        testObject.perform(runMock, filePath, launcher, listener);

        Mockito.verify(helperMock).perform(Mockito.any(), Mockito.any(), Mockito.any());

        TUtil.cleanUpPythonInstallations(jenkinsRule);
        thrown.expect(AbortException.class);
        thrown.expectMessage("python is null");
        testObject.perform(runMock, filePath, launcher, listener);
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
        List<ExamReportConfig> reportConfigs = ((ExamTask.DescriptorExamTask) testObject.getDescriptor())
                .getReportConfigs();
        for (ExamReportConfig config : reportConfigs) {
            if (config.getName().equalsIgnoreCase(examReport)) {
                return;
            }
        }

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

    private void prepareDoExecuteTask() {

        prepareExamReportConfig();

        testObject.setPdfReportTemplate("template");
        testObject.setPdfReport(false);
        testObject.setPdfSelectFilter("filter");
        testObject.setPdfMeasureImages(true);

        testObject.setPdfReport(false);
        testObject.setClearWorkspace(false);
        testObject.setTestrunFilter(new ArrayList<>());
    }

    @Test
    public void doExecuteTask() throws IOException, InterruptedException {
        prepareDoExecuteTask();
        Executor executor = Mockito.mock(Executor.class);

        Run runMock = mock(Run.class, "runMock");
        Mockito.when(runMock.getExecutor()).thenReturn(executor);

        ExamTaskHelper taskHelperMock = Mockito.mock(ExamTaskHelper.class, "taskHelperMock");
        when(taskHelperMock.getRun()).thenReturn(runMock);
        when(taskHelperMock.getEnv()).thenReturn(new EnvVars());
        when(taskHelperMock.getTaskListener()).thenReturn(new FakeTaskListener());
        Whitebox.setInternalState(testObject, "taskHelper", taskHelperMock);

        Mocks.mockStatic(Compatibility.class);

        ClientRequest clientRequestMock = mock(ClientRequest.class, "clientRequestMock");
        when(clientRequestMock.isClientConnected()).thenReturn(Boolean.FALSE);

        // test no EXAM connected
        testObject.doExecuteTask(clientRequestMock);
        verify(taskHelperMock, never()).getEnv();

        // test minimum configuration
        when(clientRequestMock.isClientConnected()).thenReturn(Boolean.TRUE);

        testObject.doExecuteTask(clientRequestMock);
        verify(taskHelperMock).copyArtifactsToTarget(any());
        verify(clientRequestMock, never()).waitForExportPDFReportJob(any(), anyInt());
        verify(clientRequestMock, never()).setTestrunFilter(any());
        verify(clientRequestMock).clearWorkspace(examReport);

        // test maximum configuration

        testObject = new FakeExamTaskExtended(testObject.getExamName(), testObject.getPythonName(),
                testObject.getExamReport(), testObject.getSystemConfiguration());
        prepareDoExecuteTask();
        Whitebox.setInternalState(testObject, "taskHelper", taskHelperMock);
        testObject.setPdfReport(true);
        testObject.setClearWorkspace(true);
        TestrunFilter filter = new TestrunFilter("name", "regex", false, true);
        testObject.setTestrunFilter(Collections.singletonList(filter));

        testObject.doExecuteTask(clientRequestMock);
        verify(taskHelperMock, times(2)).copyArtifactsToTarget(any());
        verify(clientRequestMock).waitForExportPDFReportJob(any(), anyInt());
        verify(clientRequestMock).setTestrunFilter(any());
        verify(clientRequestMock, times(3)).clearWorkspace(anyString());
        verify(clientRequestMock, times(2)).clearWorkspace(examReport);
        verify(clientRequestMock).clearWorkspace("modelName");
    }
}
