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

import Utils.Whitebox;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ModelConfigurationTest {

    private final static String TESTSTRING = "myTestString";
    private ModelConfiguration testObject;

    @Before
    public void setUp() {
        testObject = new ModelConfiguration();
    }

    @Test
    public void getProjectName() {
        Whitebox.setInternalState(testObject, "projectName", TESTSTRING);
        String testIt = testObject.getProjectName();
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void setProjectName() {
        testObject.setProjectName(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject, "projectName");
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void getModelName() {
        Whitebox.setInternalState(testObject, "modelName", TESTSTRING);
        String testIt = testObject.getModelName();
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void setModelName() {
        testObject.setModelName(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject, "modelName");
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void getTargetEndpoint() {
        Whitebox.setInternalState(testObject, "targetEndpoint", TESTSTRING);
        String testIt = testObject.getTargetEndpoint();
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void setTargetEndpoint() {
        testObject.setTargetEndpoint(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject, "targetEndpoint");
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void getModelConfigUUID() {
        Whitebox.setInternalState(testObject, "modelConfigUUID", TESTSTRING);
        String testIt = testObject.getModelConfigUUID();
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void setModelConfigUUID() {
        testObject.setModelConfigUUID(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject, "modelConfigUUID");
        assertEquals(TESTSTRING, testIt);
    }
}
