package jenkins.internal.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ApiVersionTest {

    private ApiVersion testObject;
    private Integer rndTestInt;

    // Setup
    @Before
    public void setUp() {
        this.testObject = new ApiVersion();
        Random rnd = new Random();
        int testInt = rnd.nextInt();
        if (testInt < 0) {
            testInt = testInt * -1;
        }
        this.rndTestInt = testInt;
    }

    // Teardown
    @After
    public void tearDown() {
        this.testObject = null;
        this.rndTestInt = null;
    }

    // member test: major
    @Test
    public void getMajor() {
        assertEquals(this.testObject.getMajor(), 1);
    }

    @Test
    public void setMajor() {
        int newMajor = this.rndTestInt;
        this.testObject.setMajor(newMajor);

        assertEquals(newMajor, this.testObject.getMajor());
    }

    // member test: minor
    @Test
    public void getMinor() {
        assertEquals(0, this.testObject.getMinor());
    }

    @Test
    public void setMinor() {
        int newMinor = this.rndTestInt;
        this.testObject.setMinor(newMinor);

        assertEquals(newMinor, this.testObject.getMinor());
    }

    // member test: fix
    @Test
    public void getFix() {
        assertEquals(0, this.testObject.getFix());
    }

    @Test
    public void setFix() {
        int newFix = this.rndTestInt;
        this.testObject.setFix(newFix);

        assertEquals(newFix, this.testObject.getFix());
    }

    @Test
    public void toStringTest() {
        String sNumber = rndTestInt.toString();
        testObject.setMajor(this.rndTestInt);
        testObject.setMinor(this.rndTestInt);
        testObject.setFix(this.rndTestInt);

        assertEquals(sNumber + "." + sNumber + "." + sNumber, this.testObject.toString());
    }

    @Test
    public void testCompareTo() {
        testObject = new ApiVersion(2, 2, 2);
        int result = testObject.compareTo(new ApiVersion(1, 2, 2));
        assertTrue(result > 0);
        result = testObject.compareTo(new ApiVersion(2, 1, 2));
        assertTrue(result > 0);
        result = testObject.compareTo(new ApiVersion(2, 2, 1));
        assertTrue(result > 0);
        result = testObject.compareTo(new ApiVersion(2, 2, 2));
        assertTrue(result == 0);
        result = testObject.compareTo(new ApiVersion(3, 2, 2));
        assertTrue(result < 0);
        result = testObject.compareTo(new ApiVersion(2, 3, 2));
        assertTrue(result < 0);
        result = testObject.compareTo(new ApiVersion(2, 2, 3));
        assertTrue(result < 0);
    }

    @Test
    public void testEquals() {
        assertTrue(testObject.equals(testObject));
        assertFalse(testObject.equals(new ApiVersion(0, 0, 0)));
    }

    @Test
    public void testHashCode() {
        int hash = testObject.hashCode();
        assertTrue(hash != 0);
    }
}
