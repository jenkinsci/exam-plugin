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
package jenkins.task._exam;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

public class ExamConsoleErrorOutTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    private ExamConsoleErrorOut testObject;

    @Before
    public void setUp(){
        testObject = new ExamConsoleErrorOut(null, Charset.defaultCharset());

    }

    @Test
    public void eol() throws IOException {
        OutputStream writeMock = Mockito.mock(OutputStream.class);
        ExamConsoleErrorOut obj = new ExamConsoleErrorOut(writeMock,Charset.defaultCharset());
        String testString = "dfhkjd f akdf la k dhf sd";
        obj.eol(testString.getBytes(),testString.length());
        Mockito.verify(writeMock,Mockito.never());
    }

    @Test
    public void close() throws IOException {
        testObject.close();

        OutputStream stream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                return;
            }
        };

        Whitebox.setInternalState(testObject,"out",stream);
        testObject.close();
    }

    @Test
    public void endsWith() throws Exception {
        boolean returned = Whitebox.invokeMethod(testObject, "endsWith",
                new Object[]{"", 'k'});
        assertFalse(returned);

        returned = Whitebox.invokeMethod(testObject, "endsWith",
                new Object[]{"sdgsfjhgkd", 'k'});
        assertFalse(returned);

        returned = Whitebox.invokeMethod(testObject, "endsWith",
                new Object[]{"sdgsfjhgk", 'k'});
        assertTrue(returned);
    }
}
