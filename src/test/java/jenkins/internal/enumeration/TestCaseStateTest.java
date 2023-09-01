package jenkins.internal.enumeration;

import junit.framework.TestCase;

public class TestCaseStateTest extends TestCase {

    public void testGet() {
        assertEquals(TestCaseState.NOT_YET_SPECIFIED, TestCaseState.getByName("NotYetSpecified"));
        assertEquals(null, TestCaseState.getByName("no matching value"));
    }

    public void testGetByName() {
        assertEquals(TestCaseState.IMPLEMENTED, TestCaseState.get("impl"));
        assertEquals(null, TestCaseState.get("no matching value"));
    }

    public void testGetValue() {
        assertEquals(7, TestCaseState.INVALID.getValue());
    }

    public void testTestGetName() {
        assertEquals("Productive", TestCaseState.PRODUCTIVE.getName());
    }

    public void testGetLiteral() {
        assertEquals("notImpl", TestCaseState.NOT_YET_IMPLEMENTED.getLiteral());
    }
}