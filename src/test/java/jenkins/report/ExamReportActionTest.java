package jenkins.report;

import hudson.model.Run;
import jenkins.task.ExamTaskModel;
import jenkins.task.FakeTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExamReportActionTest {

    @Mock
    private ExamTaskModel modelTaskMock;

    private FakeTask examFakeTask;

    private ExamReportAction testobject;
    private String examName = "EXAM";
    private String pythonName = "Python-2.7";
    private String examReport = "examReport";
    private String examSysConfig = "testExamSystemConfig";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        examFakeTask = new FakeTask(examName, pythonName, examReport, examSysConfig);
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