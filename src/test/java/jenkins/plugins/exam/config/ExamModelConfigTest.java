package jenkins.plugins.exam.config;

import hudson.model.Descriptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;

public class ExamModelConfigTest {

    ExamModelConfig testObject;

    @Before
    public void setUp() {
        testObject = new ExamModelConfig("ExamTaskModel");
    }

    @After
    public void tearDown() {
        testObject = null;
    }

    @Test
    public void setName() {
        String testName = "TestName";
        testObject.setName(testName);
        String setName = Whitebox.getInternalState(testObject, "name");

        assertEquals(testName, setName);
    }

    @Test
    public void getName() {
        String testName = "testName";
        Whitebox.setInternalState(testObject, "name", testName);
        String setName = testObject.getName();

        assertEquals(testName, setName);
    }

    @Test
    public void setModelName() {
        String testModelName = "testModelName";
        testObject.setModelName(testModelName);
        String setName = Whitebox.getInternalState(testObject, "modelName");

        assertEquals(testModelName, setName);
    }

    @Test
    public void getModelName() {
        String testModelName = "testModelName";
        Whitebox.setInternalState(testObject, "modelName", testModelName);
        String setModelName = testObject.getModelName();

        assertEquals(testModelName, setModelName);
    }

    @Test
    public void getExamVersion() {
        int examVersion = 44;
        Whitebox.setInternalState(testObject, "examVersion", examVersion);
        int setExamVersion = testObject.getExamVersion();

        assertEquals(examVersion, setExamVersion);
    }

    @Test
    public void setExamVersion() {
        int examVersion = 46;
        testObject.setExamVersion(examVersion);
        int setExamVersion = Whitebox.getInternalState(testObject, "examVersion");

        assertEquals(examVersion, setExamVersion);
    }

    @Test
    public void setTargetEndpoint() {
        // test usual set operation
        String targetEndpoint = "testTargetEndpoint";
        testObject.setTargetEndpoint(targetEndpoint);
        String setTargetEndpoint = Whitebox.getInternalState(testObject, "targetEndpoint");

        assertEquals(targetEndpoint, setTargetEndpoint);

        // set without empty string
        String targetEndpointErrorTest = "";
        testObject = new ExamModelConfig("ExamTaskModel");
        testObject.setTargetEndpoint(targetEndpointErrorTest);

        String result = Whitebox.getInternalState(testObject, "targetEndpoint");

        // should be default => empty argument
        assertEquals(ExamModelConfig.TARGET_ENDPOINT, result);
    }

    @Test
    public void getTargetEndpoint() {
        String targetEndpoint = "testTargetEndpoint";
        Whitebox.setInternalState(testObject, "targetEndpoint", targetEndpoint);
        String setTartgetEndpoint = testObject.getTargetEndpoint();

        assertEquals(targetEndpoint, setTartgetEndpoint);
    }

    @Test
    public void getDisplayName() {
        String name = "testName";
        String modelName = "testModelName";
        String targetEndpoint = "testTargetEndpoint";
        String exprectedDisplayName = name + " -- ( " + modelName + "@" + targetEndpoint + " )";

        Whitebox.setInternalState(testObject, "name", name);
        Whitebox.setInternalState(testObject, "modelName", modelName);
        Whitebox.setInternalState(testObject, "targetEndpoint", targetEndpoint);

        String displayName = testObject.getDisplayName();
        assertEquals(exprectedDisplayName, displayName);
    }
}
