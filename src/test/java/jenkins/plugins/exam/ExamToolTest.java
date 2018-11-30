package jenkins.plugins.exam;

import hudson.EnvVars;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ExamToolTest {

    ExamTool testObject;
    String name = "EXAM_4.7";
    String home = "C:\\Program Files\\EXAM";

    @Before
    public void setUp() {
        testObject = new ExamTool(name, home, new ArrayList<>());
    }

    @After
    public void tearDown() {
        testObject = null;
    }

    @Test
    public void setRelativeDataPath() {
        String testPath = "../userData";
        testObject.setRelativeDataPath(testPath);
        String setPath = Whitebox.getInternalState(testObject, "relativeDataPath");

        assertEquals(testPath, setPath);
    }

    @Test
    public void getRelativeConfigPath() {
        String testPath = "../userData";
        Whitebox.setInternalState(testObject, "relativeDataPath", testPath);
        String setPath = testObject.getRelativeConfigPath();

        assertEquals(testPath, setPath);
    }

    @Test
    public void forNode() {
    }

    @Test
    public void forEnvironment() {
        EnvVars envVars = new EnvVars();
        envVars.put("first", "first");
        envVars.put("second", "second");
        String testPath = "../userData";
        testObject.setRelativeDataPath(testPath);
        ExamTool testTool = testObject.forEnvironment(envVars);

        assertEquals(testPath, testTool.getRelativeConfigPath());
        assertEquals(name, testTool.getName());
        assertEquals(home, testTool.getHome());
    }

    @Test(expected = AssertionError.class)
    public void getDescriptorNoJenkins() throws AssertionError {
        testObject.getDescriptor();
    }

    @Test
    public void getExecutable() {
    }
}