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
package jenkins.task._exam;

import Utils.Whitebox;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ExamConsoleAnnotatorTest {

    private ExamConsoleAnnotator testObject;
    private String pauseString = "-- begin listing genericProperties --";
    private String resumeString = "-- end listing genericProperties --";

    @Before
    public void setUp() {
        testObject = new ExamConsoleAnnotator(null, StandardCharsets.UTF_8);
    }

    @Test
    public void eol() throws IOException {
        OutputStream writeMock = Mockito.mock(OutputStream.class);
        ExamConsoleAnnotator obj = new ExamConsoleAnnotator(writeMock, StandardCharsets.UTF_8);
        String testString = "dfhkjd f akdf la k dhf sd";
        String expected = "EXAM: ";
        obj.eol(testString.getBytes(StandardCharsets.UTF_8), testString.length());
        Mockito.verify(writeMock).write(expected.getBytes(StandardCharsets.UTF_8));
        Mockito.verify(writeMock).write(testString.getBytes(StandardCharsets.UTF_8), 0, testString.length());
    }

    @Test
    public void eol_pause() throws IOException {
        OutputStream writeMock = Mockito.mock(OutputStream.class);
        ExamConsoleAnnotator obj = new ExamConsoleAnnotator(writeMock, StandardCharsets.UTF_8);
        String testString = "dfhkjd f akdf la k dhf sd";
        String expected = "EXAM: ";
        obj.eol(pauseString.getBytes(StandardCharsets.UTF_8), pauseString.length());
        obj.eol(testString.getBytes(StandardCharsets.UTF_8), testString.length());
        obj.eol(resumeString.getBytes(StandardCharsets.UTF_8), resumeString.length());
        obj.eol(testString.getBytes(StandardCharsets.UTF_8), testString.length());
        Mockito.verify(writeMock).write(expected.getBytes(StandardCharsets.UTF_8));
        Mockito.verify(writeMock).write(testString.getBytes(StandardCharsets.UTF_8), 0, testString.length());
        Mockito.verifyNoMoreInteractions(writeMock);
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

        Whitebox.setInternalState(testObject, "out", stream);
        testObject.close();
    }
}
