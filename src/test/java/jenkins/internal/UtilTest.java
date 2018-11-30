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
package jenkins.internal;

import hudson.util.FormValidation;
import jenkins.task._exam.Messages;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

public class UtilTest {
    
    private char[] chars = "1234567890abcdef".toCharArray();
    
    private String generateValidUuid(boolean withMinus) {
        String uuid = "";
        Random rand = new Random();
        for (int i = 0; i < 32; i++) {
            int num = rand.nextInt() % chars.length;
            if (num < 0) {
                num = num * -1;
            }
            uuid += chars[num];
            if (withMinus) {
                if (i == 4 || i == 12 || i == 25) {
                    uuid += '-';
                }
            }
        }
        return uuid;
    }
    
    private String generateValidId() {
        Random rnd = new Random();
        
        int rndId = rnd.nextInt(999999999) + 1;
        return "I" + rndId;
    }
    
    @Test
    public void isUuidValid() throws Exception {
        String uuid = generateValidUuid(false);
        assertTrue("TestUuid: " + uuid, Util.isUuidValid(uuid));
    }
    
    @Test
    public void isUuidValidMinus() throws Exception {
        String uuid = generateValidUuid(true);
        assertTrue("TestUuid: " + uuid, Util.isUuidValid(uuid));
    }
    
    @Test
    public void isUuidValidFalse() throws Exception {
        String uuid = generateValidUuid(false) + "a";
        assertFalse("TestUuid: " + uuid, Util.isUuidValid(uuid));
        uuid = generateValidUuid(false).substring(1);
        assertFalse("TestUuid: " + uuid, Util.isUuidValid(uuid));
        uuid = generateValidUuid(false).substring(1) + "g";
        assertFalse("TestUuid: " + uuid, Util.isUuidValid(uuid));
    }
    
    @Test
    public void validateUuid() throws Exception {
        FormValidation ret = Util.validateUuid(generateValidUuid(false));
        assertEquals(FormValidation.Kind.OK, ret.kind);
    }
    
    @Test
    public void validateUuidFalse() throws Exception {
        FormValidation ret = Util.validateUuid(generateValidUuid(false) + "g");
        assertEquals(FormValidation.Kind.ERROR, ret.kind);
    }
    
    @Test
    public void isIdValid() throws Exception {
        String id1 = this.generateValidId();
        String id2 = this.generateValidId();
        String id3 = "blablablallslsjkdf";
        String id4 = "3" + this.generateValidId();
        
        Boolean result1 = Whitebox.invokeMethod(Util.class, "isIdValid", id1);
        Boolean result2 = Whitebox.invokeMethod(Util.class, "isIdValid", id2);
        Boolean result3 = Whitebox.invokeMethod(Util.class, "isIdValid", id3);
        Boolean result4 = Whitebox.invokeMethod(Util.class, "isIdValid", id4);
        
        assertTrue(result1);
        assertTrue(result2);
        assertFalse(result3);
        assertFalse(result4);
    }
    
    @Test
    public void isPythonConformFSN() {
        Map<String, Boolean> testsAndExpectedResults = new HashMap<String, Boolean>() {{
            put("_IAmAPythonConformName", true);
            put("_alskfdkjlsajf_I@##___IAmAlsoAPythonConformName@@", true);
            put("12IAmNotAPythonConformName", false);
            put("#IAmAlsoNoPythonConformName", false);
            put("IAmAlsoNoPythonConformName", false);
            put("break", false);
            put("IAmAlsoNo.P.ythonConformName", true);
            put("AmAlsoNo.break.hallo", false);
            put("AmAlsoNo.34huhu", false);
            put(null, false);
        }};
        
        testsAndExpectedResults.forEach((name, expectedValue) -> {
            try {
                Boolean result = Whitebox.invokeMethod(Util.class, "isPythonConformFSN", name);
                if (expectedValue) {
                    assertTrue(result);
                } else {
                    assertFalse(result);
                }
            } catch (Exception e) {
                fail("isPythonConformName threw an Exception");
            }
        });
    }
    
    @Test
    public void validateElementForSearch() throws Exception {
        String newLine = "\r\n";
        String expectedErrorMsg =
                Messages.EXAM_RegExUuid() + newLine + Messages.EXAM_RegExId() + newLine + Messages.EXAM_RegExFsn()
                        + newLine;
        
        String invalidString = "#IAmAlsoNoPythonConformName";
        String validString = this.generateValidId();
        
        FormValidation fv_invalidResult = Whitebox
                .invokeMethod(Util.class, "validateElementForSearch", invalidString);
        FormValidation fv_validResult = Whitebox.invokeMethod(Util.class, "validateElementForSearch", validString);
        
        assertEquals(FormValidation.error(expectedErrorMsg).getMessage(), fv_invalidResult.getMessage());
        assertEquals(FormValidation.ok(), fv_validResult);
    }
    
    @Test
    public void validateSystemConfig() throws Exception {
        String newLine = "\r\n";
        String expectedErrorMsg_1 = Messages.EXAM_RegExSysConf() + newLine;
        String expectedErrorMsg_2 = Messages.EXAM_RegExSysConf() + newLine + Messages.EXAM_RegExUuid() + newLine;
        String expectedErrorMsg_3 = Messages.EXAM_RegExSysConf() + newLine + Messages.EXAM_RegExFsn() + newLine;
        String expectedErrorMsg_4 =
                Messages.EXAM_RegExSysConf() + newLine + Messages.EXAM_RegExUuid() + newLine + Messages
                        .EXAM_RegExFsn() + newLine;
        
        String invalidString_1 = "IAmNotValid";
        String invalidString_2 = generateValidUuid(false) + "3 This_is_my_Sysconfig";
        String invalidString_3 = generateValidUuid(false) + " 1This_is_my_Sysconfig";
        String invalidString_4 = generateValidUuid(false) + "3 1This_is_my_Sysconfig";
        String validString = generateValidUuid(false) + " This_is_my_Sysconfig";
        
        FormValidation fv_invalidResult_1 = Whitebox
                .invokeMethod(Util.class, "validateSystemConfig", invalidString_1);
        FormValidation fv_invalidResult_2 = Whitebox
                .invokeMethod(Util.class, "validateSystemConfig", invalidString_2);
        FormValidation fv_invalidResult_3 = Whitebox
                .invokeMethod(Util.class, "validateSystemConfig", invalidString_3);
        FormValidation fv_invalidResult_4 = Whitebox
                .invokeMethod(Util.class, "validateSystemConfig", invalidString_4);
        FormValidation fv_validResult = Whitebox.invokeMethod(Util.class, "validateSystemConfig", validString);
        
        assertEquals(FormValidation.error(expectedErrorMsg_1).getMessage(), fv_invalidResult_1.getMessage());
        assertEquals(FormValidation.error(expectedErrorMsg_2).getMessage(), fv_invalidResult_2.getMessage());
        assertEquals(FormValidation.error(expectedErrorMsg_3).getMessage(), fv_invalidResult_3.getMessage());
        assertEquals(FormValidation.error(expectedErrorMsg_4).getMessage(), fv_invalidResult_4.getMessage());
        assertEquals(FormValidation.ok(), fv_validResult);
    }
}
