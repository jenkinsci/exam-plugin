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

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class ExamStatusTest {

    private final static String TESTSTRING = "myTestString";
    private ExamStatus testObject;
    private Integer testint;

    @Before
    public void setUp() {
        testObject = new ExamStatus();
        Random rand = new Random();
        int tempInt = rand.nextInt();
        if (tempInt < 0) {
            tempInt *= -1;
        }
        testint = Integer.valueOf(tempInt);
    }

    @Test
    public void getJobRunning() {
        Whitebox.setInternalState(testObject, "jobRunning", Boolean.TRUE);
        Boolean testIt = testObject.getJobRunning();
        assertEquals(Boolean.TRUE, testIt);
    }

    @Test
    public void setJobRunning() {
        testObject.setJobRunning(Boolean.TRUE);
        Boolean testIt = Whitebox.getInternalState(testObject, "jobRunning");
        assertEquals(Boolean.TRUE, testIt);
    }

    @Test
    public void getJobName() {
        Whitebox.setInternalState(testObject, "jobName", TESTSTRING);
        String testIt = testObject.getJobName();
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void setJobName() {
        testObject.setJobName(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject, "jobName");
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void getTestRunState() {
        Whitebox.setInternalState(testObject, "testRunState", testint);
        Integer testIt = testObject.getTestRunState();
        assertEquals(testint, testIt);
    }

    @Test
    public void setTestRunState() {
        testObject.setTestRunState(testint);
        Integer testIt = Whitebox.getInternalState(testObject, "testRunState");
        assertEquals(testint, testIt);
    }
}
