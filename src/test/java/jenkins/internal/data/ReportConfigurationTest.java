/**
 * Copyright (c) 2018 MicroNova AG
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this
 *        list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this
 *        list of conditions and the following disclaimer in the documentation and/or
 *        other materials provided with the distribution.
 *
 *     3. Neither the name of MicroNova AG nor the names of its
 *        contributors may be used to endorse or promote products derived from
 *        this software without specific prior written permission.
 *
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

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class ReportConfigurationTest {

    private ReportConfiguration testObject;
    private final static String TESTSTRING = "myTestString";
    private int testint;

    @Before
    public void setUp(){
        testObject = new ReportConfiguration();
        Random rand = new Random();
        testint = rand.nextInt();
        if(testint < 0 ){
            testint *= -1;
        }
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
        String testIt = Whitebox.getInternalState(testObject,"projectName");
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void getDbType() {
        Whitebox.setInternalState(testObject, "dbType", TESTSTRING);
        String testIt = testObject.getDbType();
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void setDbType() {
        testObject.setDbType(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject,"dbType");
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void getDbHost() {
        Whitebox.setInternalState(testObject, "dbHost", TESTSTRING);
        String testIt = testObject.getDbHost();
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void setDbHost() {
        testObject.setDbHost(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject,"dbHost");
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void getDbService() {
        Whitebox.setInternalState(testObject, "dbService", TESTSTRING);
        String testIt = testObject.getDbService();
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void setDbService() {
        testObject.setDbService(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject,"dbService");
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void getDbPort() {
        Whitebox.setInternalState(testObject, "dbPort", testint);
        int testIt = testObject.getDbPort();
        assertEquals(testint, testIt);
    }

    @Test
    public void setDbPort() {
        testObject.setDbPort(testint);
        int testIt = Whitebox.getInternalState(testObject,"dbPort");
        assertEquals(testint, testIt);
    }

    @Test
    public void getDbUser() {
        Whitebox.setInternalState(testObject, "dbUser", TESTSTRING);
        String testIt = testObject.getDbUser();
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void setDbUser() {
        testObject.setDbUser(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject,"dbUser");
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void getDbSchema() {
        Whitebox.setInternalState(testObject, "dbSchema", TESTSTRING);
        String testIt = testObject.getDbSchema();
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void setDbSchema() {
        testObject.setDbSchema(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject,"dbSchema");
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void getDbPassword() {
        Whitebox.setInternalState(testObject, "dbPassword", TESTSTRING);
        String testIt = testObject.getDbPassword();
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    public void setDbPassword() {
        testObject.setDbPassword(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject,"dbPassword");
        assertEquals(TESTSTRING, testIt);
    }
}
