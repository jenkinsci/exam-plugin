package jenkins.task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestrunFilterTest {

    private String name;
    private String value;
    private boolean adminCases;
    private boolean activeTestcases;
    private TestrunFilter trf;

    // setup
    @Before
    public void setUp() {
        name = "testName";
        value = "testValue";
        adminCases = true;
        activeTestcases = true;

        this.trf = new TestrunFilter(name, value, adminCases, activeTestcases);
    }

    // cleanup
    @After
    public void tearDown() {
        this.trf = null;
    }

    @Test
    public void getName() {
        assertEquals(name, trf.getName());
    }

    @Test
    public void getValue() {
        assertEquals(value, trf.getValue());
    }

    @Test
    public void getAdminCases() {
        assertEquals(adminCases, trf.getAdminCases());
    }

    @Test
    public void getActivateTestcases() {
        assertEquals(activeTestcases, trf.getActivateTestcases());
    }
}