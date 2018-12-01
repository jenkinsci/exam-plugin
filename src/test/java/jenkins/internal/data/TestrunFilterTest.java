package jenkins.internal.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

        this.trf = new TestrunFilter(name, value, adminCases, activeTestcases);
    }

    // cleanup
    @After
    public void tearDown() {
        this.trf = null;
    }

    @Test
    public void emptyConstructorTest() {
        this.trf = new TestrunFilter();
        assertNull(this.trf.name);
        assertNull(this.trf.value);
        assertFalse(this.trf.adminCases);
        assertFalse(this.trf.activateTestcases);
    }

    @Test
    public void constructorTest() {
        assertEquals(this.name, this.trf.name);
        assertEquals(this.value, this.trf.value);
        assertEquals(this.activeTestcases, this.trf.activateTestcases);
        assertEquals(this.adminCases, this.trf.adminCases);
    }

    @Test
    public void getName() {
        assertEquals(name, trf.getName());
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
