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
package jenkins.internal.enumeration;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class RestAPILogLevelEnumTest {
    private static List<Integer> testValues = Arrays.asList(0, 10, 15, 20, 25, 30);

    @Test
    public void fromToInt() {
        for(Integer i: testValues){
            RestAPILogLevelEnum enumObject = RestAPILogLevelEnum.fromInt(i);
            assertEquals(i, enumObject.toInt());
        }
    }


    @Test
    public void fromInt() {
            RestAPILogLevelEnum enumObject = RestAPILogLevelEnum.fromInt(Integer.valueOf(11));
            assertNull(enumObject);
    }

    @Test
    public void includesLogLevel() {
        for(Integer i: testValues){
            for(Integer k: testValues) {
                RestAPILogLevelEnum enumObject_i = RestAPILogLevelEnum.fromInt(i);
                RestAPILogLevelEnum enumObject_k = RestAPILogLevelEnum.fromInt(k);
                boolean isIncluding = enumObject_i.includesLogLevel(enumObject_k);
                assertEquals(k <= i, isIncluding);
            }
        }
    }

    @Test
    public void getValues() {
        String[] names = RestAPILogLevelEnum.getValues();
        assertEquals("size does not match", 6, names.length);
    }
}
