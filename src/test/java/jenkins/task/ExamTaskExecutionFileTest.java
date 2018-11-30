package jenkins.task;

import hudson.AbortException;
import jenkins.internal.data.TestConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;

public class ExamTaskExecutionFileTest {

    private ExamTaskExecutionFile testObject;
    private String examName;
    private String pythonName;
    private String execFile;
    private String pCode;
    private String examReport;
    private String examSysConfig;

    @Before
    public void setUp() throws Exception {
        examName = "EXAM";
        pythonName = "Python-2.7";
        pCode = "C:\\Python\\path";
        examReport = "examReport";
        execFile = "C:\\EXAM\\execution.xml";
        examSysConfig = "testExamSystemConfig";
        testObject = new ExamTaskExecutionFile(examName, pythonName, examReport, examSysConfig);
        testObject.setPathPCode(pCode);
        testObject.setPathExecutionFile(execFile);
    }

    @Test

    public void getPathExecutionFile() {
        Whitebox.setInternalState(testObject, "pathExecutionFile", execFile);
        String pathExecutionFile = testObject.getPathExecutionFile();

        assertEquals(execFile, pathExecutionFile);
    }

    @Test
    public void setPathExecutionFile() {
        testObject.setPathExecutionFile(execFile);
        String pathExecutionFile = Whitebox.getInternalState(testObject, "pathExecutionFile");

        assertEquals(execFile, pathExecutionFile);
    }

    @Test
    public void getPathPCode() {
        Whitebox.setInternalState(testObject, "pathPCode", pCode);
        String pathPCode = testObject.getPathPCode();

        assertEquals(pCode, pathPCode);
    }

    @Test
    public void setPathPCode() {
        testObject.setPathPCode(pCode);
        String pathPCode = Whitebox.getInternalState(testObject, "pathPCode");

        assertEquals(pCode, pathPCode);
    }

    @Test
    public void addDataToTestConfiguration() throws AbortException {
        TestConfiguration tc = new TestConfiguration();
        tc = testObject.addDataToTestConfiguration(tc);

        assertEquals(pCode, tc.getPathPCode());
        assertEquals(execFile, tc.getTestObject());
    }
}
