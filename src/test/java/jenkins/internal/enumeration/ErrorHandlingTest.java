package jenkins.internal.enumeration;

import junit.framework.TestCase;

public class ErrorHandlingTest extends TestCase {

    public void testDisplayString() {
        assertEquals("Generate Error Step", ErrorHandling.GENERATE_ERROR_STEP.displayString());
    }
}