package jenkins.internal.data;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GenerateConfigurationTest {

    private GenerateConfiguration testObject;
    private final static String TEST_STRING = "TEST_string";

    @Before
    public void setUp() {
        this.testObject = new GenerateConfiguration();
    }

    @Test
    public void testGetElement() {
        Whitebox.setInternalState(testObject, "element", TEST_STRING);
        assertEquals(TEST_STRING, testObject.getElement());
    }

    @Test
    public void testSetElement() {
        testObject.setElement(TEST_STRING);
        assertEquals(TEST_STRING, testObject.getElement());
    }

    @Test
    public void testGetDescriptionSource() {
        Whitebox.setInternalState(testObject, "descriptionSource", TEST_STRING);
        assertEquals(TEST_STRING, testObject.getDescriptionSource());
    }

    @Test
    public void testSetDescriptionSource() {
        testObject.setDescriptionSource(TEST_STRING);
        assertEquals(TEST_STRING, testObject.getDescriptionSource());
    }

    @Test
    public void testGetDocumentInReport() {
        Whitebox.setInternalState(testObject, "documentInReport", true);
        assertEquals(true, testObject.isDocumentInReport());
    }

    @Test
    public void testSetDocumentInReport() {
        testObject.setDocumentInReport(true);
        assertEquals(true, testObject.isDocumentInReport());
    }

    @Test
    public void testGetErrorHandling() {
        Whitebox.setInternalState(testObject, "errorHandling", TEST_STRING);
        assertEquals(TEST_STRING, testObject.getErrorHandling());
    }

    @Test
    public void testSetErrorHandling() {
        testObject.setErrorHandling(TEST_STRING);
        assertEquals(TEST_STRING, testObject.getErrorHandling());
    }

    @Test
    public void testGetVariant() {
        Whitebox.setInternalState(testObject, "variant", TEST_STRING);
        assertEquals(TEST_STRING, testObject.getVariant());
    }

    @Test
    public void testSetVariant() {
        testObject.setVariant(TEST_STRING);
        assertEquals(TEST_STRING, testObject.getVariant());
    }

    @Test
    public void testGetFrameFunctions() {
        Whitebox.setInternalState(testObject, "frameFunctions", Collections.singletonList(TEST_STRING));
        assertTrue(Collections.singletonList(TEST_STRING).equals(testObject.getFrameFunctions()));
    }

    @Test
    public void testSetFrameFunctions() {
        testObject.setFrameFunctions(Collections.singletonList(TEST_STRING));
        assertTrue(Collections.singletonList(TEST_STRING).equals(testObject.getFrameFunctions()));
    }

    @Test
    public void testGetMappingList() {
        Whitebox.setInternalState(testObject, "mappingList", Collections.singletonList(TEST_STRING));
        assertTrue(Collections.singletonList(TEST_STRING).equals(testObject.getMappingList()));
    }

    @Test
    public void testSetMappingList() {
        testObject.setMappingList(Collections.singletonList(TEST_STRING));
        assertTrue(Collections.singletonList(TEST_STRING).equals(testObject.getMappingList()));
    }

    @Test
    public void testGetTestCaseStates() {
        Whitebox.setInternalState(testObject, "testCaseStates", Collections.singletonList(TEST_STRING));
        assertTrue(Collections.singletonList(TEST_STRING).equals(testObject.getTestCaseStates()));
    }

    @Test
    public void testSetTestCaseStates() {
        testObject.setTestCaseStates(Collections.singletonList(TEST_STRING));
        assertTrue(Collections.singletonList(TEST_STRING).equals(testObject.getTestCaseStates()));
    }
}
