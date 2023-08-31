package jenkins.internal.enumeration;

import junit.framework.TestCase;

public class TestCaseStateTest extends TestCase {

    public void testGet() {
        assertEquals(TestCaseState.NOT_YET_SPECIFIED, TestCaseState.getByName("Not_Yet_Specified"));
        assertEquals(null, TestCaseState.getByName("no matching value"));
    }

    public void testGetByName() {
        assertEquals(TestCaseState.IMPLEMENTED, TestCaseState.get("Implemented"));
        assertEquals(null, TestCaseState.get("no matching value"));
    }

    public void testGetValue() {
        assertEquals(7, TestCaseState.INVALID.getValue());
    }

    public void testTestGetName() {
        assertEquals("Productive", TestCaseState.PRODUCTIVE.getName());
    }

    public void testGetLiteral() {
        assertEquals("Not_Yet_Implemented", TestCaseState.NOT_YET_IMPLEMENTED.getLiteral());
    }
}