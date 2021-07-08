/**
 * Copyright (c) 2018 MicroNova AG
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * <p>
 * 3. Neither the name of MicroNova AG nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
        assertTrue(testObject.isDocumentInReport());
    }

    @Test
    public void testSetDocumentInReport() {
        testObject.setDocumentInReport(true);
        assertTrue(testObject.isDocumentInReport());
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
