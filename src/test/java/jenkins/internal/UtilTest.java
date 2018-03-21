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
package jenkins.internal;

import hudson.util.FormValidation;
import jenkins.task._exam.Messages;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class UtilTest {

    private char[] chars = "1234567890abcdef".toCharArray();

    private String generateValidUuid(boolean withMinus){
        String uuid = "";
        Random rand = new Random();
        for(int i=0; i<32; i++){
            int num = rand.nextInt() % chars.length;
            if(num < 0){
                num = num * -1;
            }
            uuid += chars[num];
            if(withMinus){
                if(i == 4 || i == 12 || i == 25){
                    uuid += '-';
                }
            }
        }
        return uuid;
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
        String uuid = generateValidUuid(false)+"a";
        assertFalse("TestUuid: " + uuid, Util.isUuidValid(uuid));
        uuid = generateValidUuid(false).substring(1);
        assertFalse("TestUuid: " + uuid, Util.isUuidValid(uuid));
        uuid = generateValidUuid(false).substring(1)+"g";
        assertFalse("TestUuid: " + uuid, Util.isUuidValid(uuid));
    }

    @Test
    public void validateUuid() throws Exception {
        FormValidation ret = Util.validateUuid(generateValidUuid(false));
        assertEquals(FormValidation.Kind.OK, ret.kind);
    }

    @Test
    public void validateUuidFalse() throws Exception {
        FormValidation ret = Util.validateUuid(generateValidUuid(false)+"g");
        assertEquals(FormValidation.Kind.ERROR, ret.kind);
    }
}
