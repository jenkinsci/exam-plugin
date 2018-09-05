package jenkins.internal.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

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
        assertEquals(1, this.testObject.getMajor());
    }

    @Test
    public void setMajor() {
        int newMajor = this.rndTestInt;
        this.testObject.setMajor(newMajor);

        assertEquals(this.testObject.getMajor(), newMajor);
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

        assertEquals(this.testObject.getMinor(), newMinor);
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

        assertEquals(this.testObject.getFix(), newFix);
    }
}
