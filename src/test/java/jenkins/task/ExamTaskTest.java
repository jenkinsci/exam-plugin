package jenkins.task;

import jenkins.internal.data.TestConfiguration;
import jenkins.task.TestUtil.Util;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.powermock.reflect.Whitebox;

import java.util.List;

import static org.junit.Assert.*;

public class ExamTaskTest {


    private class TestObject extends ExamTask{

        TestObject(String examName, String pythonName, String examReport, String systemConfiguration) {
            super(examName, pythonName, examReport, systemConfiguration);
        }

        @Override
        TestConfiguration addDataToTestConfiguration(TestConfiguration testConfiguration) {
            return null;
        }
    }

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    private ExamTask testObject;
    private String examName;
    private String pythonName;
    private String examReport;
    private String examSysConfig;


    @Before
    public void setUp() {
        examName = "EXAM";
        pythonName = "Python-2.7";
        examReport = "examReport";
        examSysConfig = "testExamSystemConfig";
        testObject = new TestObject(examName, pythonName, examReport, examSysConfig);
    }

    @After
    public void tearDown() {
        Util.cleanUpExamTools(jenkinsRule);
        Util.cleanUpPythonInstallations(jenkinsRule);

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
    public void getExamName() {
        String setExamName = testObject.getExamName();
        assertEquals(examName, setExamName);
    }

    @Test
    public void getPythonName() {
        String setPythonName = testObject.getPythonName();
        assertEquals(pythonName, setPythonName);
    }
}
