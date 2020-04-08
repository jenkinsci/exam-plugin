package jenkins.task;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertEquals;

public class TestrunFilterDescriptorTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private String name;
    private String value;
    private boolean adminCases;
    private boolean activeTestcases;
    private TestrunFilter testObject;
    TestrunFilter.DescriptorImpl testObjectDescriptor;

    // setup
    @Before
    public void setUp() {
        name = "testName";
        value = "testValue";
        adminCases = true;
        activeTestcases = true;

        this.testObject = new TestrunFilter(name, value, adminCases, activeTestcases);
        testObjectDescriptor = testObject.getDescriptor();
    }

    // cleanup
    @After
    public void tearDown() {
        this.testObject = null;
    }

    @Test
    public void getDisplayName() {
        assertEquals("Testrun Filter", testObjectDescriptor.getDisplayName());
    }
}