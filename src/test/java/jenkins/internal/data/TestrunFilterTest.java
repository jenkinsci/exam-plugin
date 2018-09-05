package jenkins.internal.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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

        TestrunFilter trf = new TestrunFilter(name, value, adminCases, activeTestcases);
    }

    // cleanup
    @After
    public void tearDown() {

    }

    @Test
    public void emptyConstructorTest() {
        this.trf = new TestrunFilter();
        assertNull(this.trf.name);
        assertNull(this.trf.value);
        assertEquals(this.trf.adminCases, false);
        assertEquals(this.trf.activateTestcases, false);
    }

    @Test
    public void constructorTest() {
        assertEquals(this.trf.name, name);
        assertEquals(this.trf.value, value);
        assertEquals(this.trf.activateTestcases, activeTestcases);
        assertEquals(this.trf.adminCases, adminCases);
    }

    @Test
    public void getName() {
        assertEquals(this.trf.name, trf.getName());
    }

    @Test
    public void setName() {
        assertEquals(this.trf.name, this.trf.getName());
        trf.setName("setNewName");
        assertEquals("setNewName", this.trf.getName());
    }

    @Test
    public void getValue() {
        assertEquals(this.trf.value, trf.getValue());
    }

    @Test
    public void setValue() {
        assertEquals(this.trf.value, this.trf.getValue());
        trf.setValue("setNewValue");
        assertEquals("setNewValue", this.trf.getValue());
    }

    @Test
    public void isAdminCases() {
        assertEquals(this.trf.adminCases, trf.isAdminCases());
    }

    @Test
    public void setAdminCases() {
        assertEquals(this.trf.adminCases, this.trf.isAdminCases());
        trf.setAdminCases(false);
        assertEquals(false, this.trf.isAdminCases());
    }

    @Test
    public void isActivateTestcases() {
        assertEquals(this.trf.activateTestcases, trf.isActivateTestcases());
    }

    @Test
    public void setActivateTestcases() {
        assertEquals(this.trf.activateTestcases, this.trf.isActivateTestcases());
        trf.setActivateTestcases(false);
        assertEquals(false, this.trf.isActivateTestcases());
    }
}
