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

import Utils.Whitebox;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.Node;
import hudson.slaves.DumbSlave;
import hudson.util.FormValidation;
import jenkins.internal.data.ApiVersion;
import jenkins.internal.data.ModelConfiguration;
import jenkins.internal.data.TestConfiguration;
import jenkins.task.TestUtil.FakeTaskListener;
import jenkins.task.TestUtil.TUtil;
import jenkins.task._exam.Messages;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompatibilityTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private void checkMinRestApiVersionException(ApiVersion version) throws IOException {
        FakeTaskListener taskListenerMock = mock(FakeTaskListener.class);
        when(taskListenerMock.getLogger()).thenReturn(System.out);
        Compatibility.setClientApiVersion(new ApiVersion(2, 2, 2));
        exception.expect(AbortException.class);
        exception.expectMessage(version.toString());
        Compatibility.checkMinRestApiVersion(taskListenerMock, version);
    }

    @Test
    public void checkMinRestApiVersion() throws IOException {
        FakeTaskListener taskListenerMock = mock(FakeTaskListener.class);
        when(taskListenerMock.getLogger()).thenReturn(System.out);
        Compatibility.setClientApiVersion(new ApiVersion(2, 2, 2));
        Compatibility.checkMinRestApiVersion(taskListenerMock, new ApiVersion(1, 2, 2));
        Compatibility.checkMinRestApiVersion(taskListenerMock, new ApiVersion(2, 1, 2));
        Compatibility.checkMinRestApiVersion(taskListenerMock, new ApiVersion(2, 2, 1));
        Compatibility.checkMinRestApiVersion(taskListenerMock, new ApiVersion(2, 2, 2));
    }

    @Test
    public void checkMinTCGVersionTest() throws Exception {
        FakeTaskListener taskListenerMock = mock(FakeTaskListener.class);
        when(taskListenerMock.getLogger()).thenReturn(System.out);
        ApiVersion tcgVersion = new ApiVersion(2, 1, 3);
        ApiVersion minVersion = new ApiVersion(1, 1, 1);
        String message = "minVersion: %s, tcgVersion: %s";

        assertTrue(String.format(message, minVersion, tcgVersion), Compatibility.checkMinTCGVersion(taskListenerMock, minVersion, tcgVersion));
        assertTrue(String.format(message, minVersion, tcgVersion), Compatibility.checkMinTCGVersion(taskListenerMock, minVersion, new ApiVersion(1, 1, 1)));
        assertTrue(String.format(message, minVersion, tcgVersion), Compatibility.checkMinTCGVersion(taskListenerMock, minVersion, new ApiVersion(1, 1, 1)));
        assertTrue(String.format(message, minVersion, tcgVersion), Compatibility.checkMinTCGVersion(taskListenerMock, minVersion, new ApiVersion(1, 1, 2)));
        assertFalse(String.format(message, minVersion, tcgVersion), Compatibility.checkMinTCGVersion(taskListenerMock, minVersion, new ApiVersion(1, 1, 0)));
    }

    @Test
    public void checkMinRestApiVersionExceptionMajor() throws IOException {
        checkMinRestApiVersionException(new ApiVersion(3, 2, 2));
    }

    @Test
    public void checkMinRestApiVersionExceptionMinor() throws IOException {
        checkMinRestApiVersionException(new ApiVersion(2, 3, 2));
    }

    @Test
    public void checkMinRestApiVersionExceptionFix() throws IOException {
        checkMinRestApiVersionException(new ApiVersion(2, 2, 3));
    }

    @Test
    public void checkTestConfigName() throws IOException, InterruptedException {
        FakeTaskListener taskListenerMock = mock(FakeTaskListener.class);
        when(taskListenerMock.getLogger()).thenReturn(System.out);
        Compatibility.setClientApiVersion(new ApiVersion(2, 0, 0));

        ModelConfiguration mc = new ModelConfiguration();
        mc.setModelConfigUUID("configName");
        TestConfiguration tc = new TestConfiguration();

        Compatibility.checkTestConfig(taskListenerMock, tc);

        tc.setModelProject(mc);

        Compatibility.checkTestConfig(taskListenerMock, tc);

        Compatibility.setClientApiVersion(new ApiVersion(1, 2, 0));
        exception.expect(AbortException.class);
        Compatibility.checkTestConfig(taskListenerMock, tc);
    }

    @Test
    public void checkTestConfigUUid() throws IOException, InterruptedException {
        FakeTaskListener taskListenerMock = mock(FakeTaskListener.class);
        when(taskListenerMock.getLogger()).thenReturn(System.out);
        ClientRequest clientRequestMock = mock(ClientRequest.class, "ClientRequestMock");
        when(clientRequestMock.getApiVersion()).thenReturn(new ApiVersion(2, 0, 0));

        ModelConfiguration mc = new ModelConfiguration();
        mc.setModelConfigUUID(TUtil.generateValidUuid(false));
        TestConfiguration tc = new TestConfiguration();
        tc.setModelProject(mc);

        Compatibility.checkTestConfig(taskListenerMock, tc);
        when(clientRequestMock.getApiVersion()).thenReturn(new ApiVersion(1, 2, 0));
        Compatibility.checkTestConfig(taskListenerMock, tc);
    }

    @Test
    public void isVersionHigher200() {
        Compatibility.setClientApiVersion(new ApiVersion(1, 9, 9));
        assertFalse(Compatibility.isVersionHigher200());
        Compatibility.setClientApiVersion(new ApiVersion(2, 0, 0));
        assertTrue(Compatibility.isVersionHigher200());
        Compatibility.setClientApiVersion(new ApiVersion(9, 9, 9));
        assertTrue(Compatibility.isVersionHigher200());
    }
}
