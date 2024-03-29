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

public class GroovyConfigurationTest {

    private final static String TEST_STRING = "myTestString";
    private GroovyConfiguration testObject;

    @Before
    public void setUp() {
        this.testObject = new GroovyConfiguration();
    }

    @Test
    public void testGetStartElement() {
        Whitebox.setInternalState(testObject, "startElement", TEST_STRING);
        String testIt = testObject.getStartElement();
        assertEquals(TEST_STRING, testIt);
    }

    @Test
    public void testSetStartElement() {
        testObject.setStartElement(TEST_STRING);
        String testIt = testObject.getStartElement();
        assertEquals(TEST_STRING, testIt);
    }

    @Test
    public void testGetScript() {
        Whitebox.setInternalState(testObject, "script", TEST_STRING);
        String testIt = testObject.getScript();
        assertEquals(TEST_STRING, testIt);
    }

    @Test
    public void testSetScript() {
        testObject.setScript(TEST_STRING);
        String testIt = testObject.getScript();
        assertEquals(TEST_STRING, testIt);
    }
}
