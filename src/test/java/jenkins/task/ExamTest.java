package jenkins.task;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;
import jenkins.task.TestUtil.Util;
import org.junit.*;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;
import org.powermock.reflect.Whitebox;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ExamTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();
    private FreeStyleProject examTestProject;
    private Exam testObject;
    private String examName;
    private String pythonName;
    private String pythonHome;
    private String examModel;
    private String examHome;
    private String examReport;
    private String examRelativePath;
    private String examExecFile;
    private String examSysConfig;

    @Before
    public void setUp() throws Exception {
        examName = "EXAM";
        pythonName = "Python-2.7";
        pythonHome = "pythonHome";
        examModel = "EXAM44";
        examReport = "examReport";
        examHome = "examHome";
        examRelativePath = "examRelativePath";
        examExecFile = "EXAM.exe";
        examSysConfig = "testExamSystemConfig";
        examTestProject = jenkinsRule.createFreeStyleProject();
        testObject = new Exam(examName, pythonName, examReport, examExecFile, examSysConfig);
    }

    @After
    public void tearDown() {
        cleanUpExamTools();
        cleanUpPythonInstallations();

        examTestProject = null;
        testObject = null;
    }

    @Test
    public void getReportPrefix() {
        String reportPrefix = "testReportPrefix";
        Whitebox.setInternalState(testObject, "reportPrefix", reportPrefix);
        String setReportPrefix = testObject.getReportPrefix();

        assertEquals(reportPrefix, setReportPrefix);
    }

    @Test
    public void setReportPrefix() {
        String reportPrefix = "testReportPrefix";
        testObject.setReportPrefix(reportPrefix);
        String setReportPrefix = Whitebox.getInternalState(testObject, "reportPrefix");

        assertEquals(reportPrefix, setReportPrefix);
    }

    @Test
    public void getPdfReport() {
        Whitebox.setInternalState(testObject, "pdfReport", true);
        boolean setPdfReport = testObject.getPdfReport();

        assertTrue(setPdfReport);
    }

    @Test
    public void setPdfReport() {
        testObject.setPdfReport(false);
        boolean setPdfReport = Whitebox.getInternalState(testObject, "pdfReport");

        assertFalse(setPdfReport);
    }

    @Test
    public void getPdfReportTemplate() {
        String testPdfReportTemplate = "testPdfReportTemplate";
        Whitebox.setInternalState(testObject, "pdfReportTemplate", testPdfReportTemplate);
        String setPdfReportTemplate = testObject.getPdfReportTemplate();

        assertEquals(testPdfReportTemplate, setPdfReportTemplate);
    }

    @Test
    public void setPdfReportTemplate() {
        String testPdfReportTemplate = "testPdfReportTemplate";
        testObject.setPdfReportTemplate(testPdfReportTemplate);
        String setPdfReportTemplate = Whitebox.getInternalState(testObject, "pdfReportTemplate");

        assertEquals(testPdfReportTemplate, setPdfReportTemplate);
    }

    @Test
    public void getPdfSelectFilter() {
        String pdfSelectFilter = "pdfSelectFilter";
        Whitebox.setInternalState(testObject, "pdfSelectFilter", pdfSelectFilter);
        String setPdfSelectFilter = testObject.getPdfSelectFilter();

        assertEquals(pdfSelectFilter, setPdfSelectFilter);
    }

    @Test
    public void setPdfSelectFilter() {
        String pdfSelectFilter = "testPdfSelectFilter";
        testObject.setPdfSelectFilter(pdfSelectFilter);
        String setPdfSelectFilter = Whitebox.getInternalState(testObject, "pdfSelectFilter");

        assertEquals(pdfSelectFilter, setPdfSelectFilter);
    }

    @Test
    public void getPdfMeasureImages() {
        Whitebox.setInternalState(testObject, "pdfMeasureImages", true);
        boolean setPdfMeasureImages = testObject.getPdfMeasureImages();

        assertTrue(setPdfMeasureImages);
    }

    @Test
    public void setPdfMeasureImages() {
        testObject.setPdfMeasureImages(true);
        boolean setPdfMeasureImages = Whitebox.getInternalState(testObject, "pdfMeasureImages");

        assertTrue(setPdfMeasureImages);
    }

    @Test
    public void getLogging() {
        Whitebox.setInternalState(testObject, "logging", true);
        boolean setLogging = testObject.getLogging();

        assertTrue(setLogging);
    }

    @Test
    public void setLogging() {
        testObject.setLogging(false);
        boolean setLogging = Whitebox.getInternalState(testObject, "logging");

        assertFalse(setLogging);
    }

    @Test
    public void getTestrunFilter() {
        List<TestrunFilter> testrunFilters = Util.createTestrunFilter();
        Whitebox.setInternalState(testObject, "testrunFilter", testrunFilters);
        List<TestrunFilter> setTestrunFilter = testObject.getTestrunFilter();

        assertEquals(testrunFilters, setTestrunFilter);
    }

    @Test
    public void setTestrunFilter() {
        List<TestrunFilter> testrunFilters = Util.createTestrunFilter();
        testObject.setTestrunFilter(testrunFilters);
        List<TestrunFilter> setFilters = Whitebox.getInternalState(testObject, "testrunFilter");

        assertEquals(testrunFilters, setFilters);
    }

    @Test
    public void getLoglevelTestCtrl() {
        String testLoglevel = "testLoglevel";
        Whitebox.setInternalState(testObject, "loglevelTestCtrl", testLoglevel);
        String setLoglevel = testObject.getLoglevelTestCtrl();

        assertEquals(testLoglevel, setLoglevel);
    }

    @Test
    public void setLoglevelTestCtrl() {
        String testLoglevel = "anotherTestLoglevel";
        testObject.setLoglevelTestCtrl(testLoglevel);
        String setLoglevel = Whitebox.getInternalState(testObject, "loglevelTestCtrl");

        assertEquals(testLoglevel, setLoglevel);
    }

    @Test
    public void getLoglevelLibCtrl() {
        String testLoglevel = "testLoglevel";
        Whitebox.setInternalState(testObject, "loglevelLibCtrl", testLoglevel);
        String setLoglevel = testObject.getLoglevelLibCtrl();

        assertEquals(testLoglevel, setLoglevel);
    }

    @Test
    public void setLoglevelLibCtrl() {
        String testLoglevel = "anotherTestLoglevel";
        testObject.setLoglevelTestLogic(testLoglevel);
        String setLoglevel = Whitebox.getInternalState(testObject, "loglevelTestLogic");

        assertEquals(testLoglevel, setLoglevel);
    }

    @Test
    public void getLoglevelTestLogic() {
        String testLoglevel = "testLoglevel";
        Whitebox.setInternalState(testObject, "loglevelTestLogic", testLoglevel);
        String setLoglevel = testObject.getLoglevelTestLogic();

        assertEquals(testLoglevel, setLoglevel);
    }

    @Test
    public void setLoglevelTestLogic() {
        String testLoglevel = "anotherTestLoglevel";
        testObject.setLoglevelLibCtrl(testLoglevel);
        String setLoglevel = Whitebox.getInternalState(testObject, "loglevelLibCtrl");

        assertEquals(testLoglevel, setLoglevel);
    }

    @Test
    public void isClearWorkspace() {
        Whitebox.setInternalState(testObject, "clearWorkspace", true);
        boolean setClearWorkspace = testObject.isClearWorkspace();

        assertTrue(setClearWorkspace);
    }

    @Test
    public void setClearWorkspace() {
        testObject.setClearWorkspace(false);
        boolean setClearWorkspace = Whitebox.getInternalState(testObject, "clearWorkspace");

        assertFalse(setClearWorkspace);
    }

    @Test
    public void getModelConfiguration() {
        String modelConfig = "testModelConfig";
        Whitebox.setInternalState(testObject, "modelConfiguration", modelConfig);
        String setModelConfig = testObject.getModelConfiguration();

        assertEquals(modelConfig, setModelConfig);
    }

    @Test
    public void setModelConfiguration() {
        String modelConfig = "testModelConfig";
        testObject.setModelConfiguration(modelConfig);
        String setModelConfig = Whitebox.getInternalState(testObject, "modelConfiguration");

        assertEquals(modelConfig, setModelConfig);
    }

    @Test
    public void getExamName() {
        String setExamName = testObject.getExamName();
        assertEquals(examName, setExamName);
    }

    @Test
    public void getPythonName() {
        String setPythonName = testObject.getPythonName();
        assertEquals(pythonName, setPythonName);
    }

    @Test
    public void getExamModel() {
        testObject.setExamModel(examModel);
        String setModelName = testObject.getExamModel();
        assertEquals(examModel, setModelName);
    }

    @Test
    public void getExecutionFile() {
        String setExecFile = testObject.getExecutionFile();
        assertEquals(examExecFile, setExecFile);
    }

    @Test
    public void getSystemConfiguration() {
        String setSysConfig = testObject.getSystemConfiguration();
        assertEquals(examSysConfig, setSysConfig);
    }

    @Test
    public void getExamReport() {
        String setExamReport = testObject.getExamReport();
        assertEquals(examReport, setExamReport);
    }

    @Test
    public void getJavaOpts() {
        String javaOptions = "-test -test2";
        Whitebox.setInternalState(testObject, "javaOpts", javaOptions);
        String setOptions = testObject.getJavaOpts();

        assertEquals(javaOptions, setOptions);
    }

    @Test
    public void setJavaOpts() {
        String javaOptions = "-testoption -n";
        testObject.setJavaOpts(javaOptions);
        String setJavaOpts = Whitebox.getInternalState(testObject, "javaOpts");

        assertEquals(javaOptions, setJavaOpts);
    }

    @Test
    public void getPython() {
        assertEquals(0, jenkinsRule.getInstance().getDescriptorByType(PythonInstallation.DescriptorImpl.class).getInstallations().length);
        PythonInstallation newInstallation = createAndRegisterPythonInstallation(pythonName, "testHome");
        assertEquals(1, jenkinsRule.getInstance().getDescriptorByType(PythonInstallation.DescriptorImpl.class).getInstallations().length);

        PythonInstallation setInstallation = testObject.getPython();
        assertEquals(setInstallation, newInstallation);
    }

    @Test
    public void getPython_noPythonRegisterd() {
        assertNull(testObject.getPython());
    }

    @Test
    public void getExam() {
        assertEquals(0, jenkinsRule.getInstance().getDescriptorByType(ExamTool.DescriptorImpl.class).getInstallations().length);
        ExamTool newExamTool = createAndRegisterExamTool(examName, examHome, examRelativePath);
        assertEquals(1, jenkinsRule.getInstance().getDescriptorByType(ExamTool.DescriptorImpl.class).getInstallations().length);

        ExamTool setTool = testObject.getExam();
        assertEquals(newExamTool, setTool);
    }

    @Test
    public void getExam_noExamRegisterd() {
        assertNull(testObject.getExam());
    }

    @Test
    public void perform_throwsAbortException() throws Exception {
        createAndRegisterExamTool(examName, examHome, examRelativePath);
        createAndRegisterPythonInstallation(pythonName, pythonHome);

        examTestProject.getBuildersList().add(testObject);
        FreeStyleBuild build = examTestProject.scheduleBuild2(0).get();
        Result buildResult = build.getResult();
        assertEquals("FAILURE", buildResult.toString());
    }

    private PythonInstallation createAndRegisterPythonInstallation(String name, String home) {
        PythonInstallation[] installations = jenkinsRule.getInstance()
                .getDescriptorByType(PythonInstallation.DescriptorImpl.class)
                .getInstallations();

        PythonInstallation[] newInstallations = new PythonInstallation[installations.length + 1];
        int index = 0;
        for (PythonInstallation installation : installations) {
            newInstallations[index] = installation;
            index++;
        }
        PythonInstallation newPythonInstallation = new PythonInstallation(name, home, Collections.emptyList());
        newInstallations[index] = newPythonInstallation;

        jenkinsRule.getInstance()
                .getDescriptorByType(PythonInstallation.DescriptorImpl.class)
                .setInstallations(newInstallations);

        return newPythonInstallation;
    }

    private void cleanUpPythonInstallations() {
        PythonInstallation[] noInstallations = new PythonInstallation[0];
        jenkinsRule.getInstance().getDescriptorByType(PythonInstallation.DescriptorImpl.class).setInstallations(noInstallations);
    }

    private ExamTool createAndRegisterExamTool(String examName, String examHome, String relativeConfigPath) {
        ExamTool newExamTool;
        ExamTool[] installations = jenkinsRule.getInstance()
                .getDescriptorByType(ExamTool.DescriptorImpl.class)
                .getInstallations();
        ExamTool[] newInstallations = new ExamTool[installations.length + 1];
        int index = 0;
        for (ExamTool tool : installations) {
            newInstallations[index] = tool;
            index++;
        }
        newExamTool = new ExamTool(examName, examHome, relativeConfigPath, Collections.emptyList());
        newInstallations[index] = newExamTool;

        jenkinsRule.getInstance()
                .getDescriptorByType(ExamTool.DescriptorImpl.class)
                .setInstallations(newInstallations);

        return newExamTool;
    }

    private void cleanUpExamTools() {
        ExamTool[] noInstallations = new ExamTool[0];
        jenkinsRule.getInstance().getDescriptorByType(ExamTool.DescriptorImpl.class).setInstallations(noInstallations);
    }
}
