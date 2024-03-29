package jenkins.report;

import Utils.Whitebox;
import hudson.model.Run;
import jenkins.task.ExamTaskModel;
import jenkins.task.TestUtil.FakeExamTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExamReportActionTest {

    @Mock
    private ExamTaskModel modelTaskMock;

    private FakeExamTask examFakeTask;

    private ExamReportAction testobject;
    private String examName = "EXAM";
    private String pythonName = "Python-2.7";
    private String examReport = "examReport";
    private String examSysConfig = "testExamSystemConfig";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        examFakeTask = new FakeExamTask(examName, pythonName, examReport, examSysConfig);
        testobject = new ExamReportAction(examFakeTask);
    }

    @After
    public void tearDown() {
    }

    private void replaceExamTask() {
        Whitebox.setInternalState(testobject, "examTask", modelTaskMock);
    }

    @Test
    public void getExamTask() {
        assertEquals(examFakeTask, testobject.getExamTask());
    }

    @Test
    public void isModel() {
        assertFalse(testobject.isModel());
        replaceExamTask();
        assertTrue(testobject.isModel());
    }

    @Test
    public void getExamModel() {
        assertEquals("no model configured", testobject.getExamModel());
        when(modelTaskMock.getExamModel()).thenReturn("myExamModel");
        replaceExamTask();
        assertEquals("myExamModel", testobject.getExamModel());
    }

    @Test
    public void getModelConfiguration() {
        assertEquals("no modelConfig configured", testobject.getModelConfiguration());
        when(modelTaskMock.getModelConfiguration()).thenReturn("myExamModelConfig");
        replaceExamTask();
        assertEquals("myExamModelConfig", testobject.getModelConfiguration());
    }

    @Test
    public void getTestObject() {
        assertEquals("no test object configured", testobject.getTestObject());
        when(modelTaskMock.getExecutionFile()).thenReturn("myExecutionFile");
        replaceExamTask();
        assertEquals("myExecutionFile", testobject.getTestObject());
    }

    @Test
    public void getIconFileName() {
        assertEquals("/plugin/exam/images/exam.jpg", testobject.getIconFileName());
    }

    @Test
    public void getDisplayName() {
        assertEquals("EXAM build", testobject.getDisplayName());
    }

    @Test
    public void getUrlName() {
        assertEquals("EXAM_build", testobject.getUrlName());
    }

    @Test
    public void onAttached() {
        Run run = mock(Run.class);
        testobject.onAttached(run);
        assertEquals(run, Whitebox.getInternalState(testobject, "run"));
    }

    @Test
    public void onLoad() {
        Run run = mock(Run.class);
        testobject.onLoad(run);
        assertEquals(run, Whitebox.getInternalState(testobject, "run"));
    }

    @Test
    public void getRun() {
        Run run = mock(Run.class);
        Whitebox.setInternalState(testobject, "run", run);
        assertEquals(run, testobject.getRun());
    }
}
