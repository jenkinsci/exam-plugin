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

public class UtilTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void workspaceToNode() throws Exception {
        DumbSlave slave = jenkinsRule.createOnlineSlave();
        slave.setLabelString("exam");
        slave.save();
        FilePath rootPath = slave.getWorkspaceRoot();

        Node node = Util.workspaceToNode(rootPath);
        assertEquals(slave, node);

        node = Util.workspaceToNode(null);
        assertEquals(jenkinsRule.getInstance(), node);
    }

    @Test
    @WithoutJenkins
    public void isUuidValid() {
        String uuid = TUtil.generateValidUuid(false);
        assertTrue("TestUuid: " + uuid, Util.isUuidValid(uuid));
    }

    @Test
    @WithoutJenkins
    public void isUuidValidMinus() {
        String uuid = TUtil.generateValidUuid(true);
        assertTrue("TestUuid: " + uuid, Util.isUuidValid(uuid));
    }

    @Test
    @WithoutJenkins
    public void isUuidValidFalse() {
        String uuid = TUtil.generateValidUuid(false) + "a";
        assertFalse("TestUuid: " + uuid, Util.isUuidValid(uuid));
        uuid = TUtil.generateValidUuid(false).substring(1);
        assertFalse("TestUuid: " + uuid, Util.isUuidValid(uuid));
        uuid = TUtil.generateValidUuid(false).substring(1) + "g";
        assertFalse("TestUuid: " + uuid, Util.isUuidValid(uuid));
    }

    @Test
    @WithoutJenkins
    public void validateUuid() {
        FormValidation ret = Util.validateUuid(TUtil.generateValidUuid(false));
        assertEquals(FormValidation.Kind.OK, ret.kind);
    }

    @Test
    @WithoutJenkins
    public void validateUuidFalse() {
        FormValidation ret = Util.validateUuid(TUtil.generateValidUuid(false) + "g");
        assertEquals(FormValidation.Kind.ERROR, ret.kind);
    }

    @Test
    @WithoutJenkins
    public void isIdValid() throws Exception {
        String id1 = TUtil.generateValidId();
        String id2 = TUtil.generateValidId();
        String id3 = "blablablallslsjkdf";
        String id4 = "3" + TUtil.generateValidId();

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
    @WithoutJenkins
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
                Boolean result = Whitebox.invokeMethod(Util.class, "isPythonConformFSN", String.class, name);
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
    @WithoutJenkins
    public void validateElementForSearch() throws Exception {
        String newLine = "\r\n";
        String expectedErrorMsg =
                Messages.EXAM_RegExUuid() + newLine + Messages.EXAM_RegExId() + newLine + Messages.EXAM_RegExFsn()
                        + newLine;

        String invalidString = "#IAmAlsoNoPythonConformName";
        String validString = TUtil.generateValidId();

        FormValidation fv_invalidResult = Whitebox
                .invokeMethod(Util.class, "validateElementForSearch", invalidString);
        FormValidation fv_validResult = Whitebox.invokeMethod(Util.class, "validateElementForSearch", validString);

        assertEquals(FormValidation.error(expectedErrorMsg).getMessage(), fv_invalidResult.getMessage());
        assertEquals(FormValidation.ok(), fv_validResult);
    }

    @Test
    @WithoutJenkins
    public void validateSystemConfig() throws Exception {
        String newLine = "\r\n";
        String expectedErrorMsg_1 = Messages.EXAM_RegExSysConf() + newLine;
        String expectedErrorMsg_2 = Messages.EXAM_RegExSysConf() + newLine + Messages.EXAM_RegExUuid() + newLine;
        String expectedErrorMsg_3 = Messages.EXAM_RegExSysConf() + newLine + Messages.EXAM_RegExFsn() + newLine;
        String expectedErrorMsg_4 =
                Messages.EXAM_RegExSysConf() + newLine + Messages.EXAM_RegExUuid() + newLine + Messages
                        .EXAM_RegExFsn() + newLine;

        String invalidString_1 = "IAmNotValid";
        String invalidString_2 = TUtil.generateValidUuid(false) + "3 This_is_my_Sysconfig";
        String invalidString_3 = TUtil.generateValidUuid(false) + " 1This_is_my_Sysconfig";
        String invalidString_4 = TUtil.generateValidUuid(false) + "3 1This_is_my_Sysconfig";
        String validString = TUtil.generateValidUuid(false) + " This_is_my_Sysconfig";

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

    @Test
    @WithoutJenkins
    public void replaceEnvVars() {
        String workspace = "C:\\my\\work\\dir";
        String actual = "";
        String expected = "";

        EnvVars env = new EnvVars();
        actual = workspace;
        expected = workspace;
        actual = Util.replaceEnvVars(actual, env);
        assertEquals(expected, actual);

        actual = Util.replaceEnvVars(actual, null);
        assertEquals(expected, actual);

        env.put("path", "c:\\this\\is\\my\\path;C:\\and\\another\\one");
        env.put("WORKSPACE", workspace);
        env.put("something", "unknown");

        expected = "";
        actual = Util.replaceEnvVars("", env);
        assertEquals(expected, actual);

        actual = "%WORKSPACE%\\to\\my\\file.xml";
        expected = workspace + "\\to\\my\\file.xml";
        actual = Util.replaceEnvVars(actual, env);
        assertEquals(expected, actual);

        actual = "%WORKSPACE%";
        expected = workspace;
        actual = Util.replaceEnvVars(actual, env);
        assertEquals(expected, actual);

        actual = "${WORKSPACE}\\to\\my\\file.xml";
        expected = workspace + "\\to\\my\\file.xml";
        actual = Util.replaceEnvVars(actual, env);
        assertEquals(expected, actual);

        actual = "this is something ${something}";
        expected = "this is something unknown";
        actual = Util.replaceEnvVars(actual, env);
        assertEquals(expected, actual);

        actual = "%nothing% to ${replace}";
        expected = actual;
        actual = Util.replaceEnvVars(actual, env);
        assertEquals(expected, actual);
    }

    private void checkMinRestApiVersionException(ApiVersion version) throws IOException, InterruptedException {
        FakeTaskListener taskListenerMock = mock(FakeTaskListener.class);
        when(taskListenerMock.getLogger()).thenReturn(System.out);
        ClientRequest clientRequestMock = mock(ClientRequest.class, "ClientRequestMock");
        when(clientRequestMock.getApiVersion()).thenReturn(new ApiVersion(2, 2, 2));
        exception.expect(AbortException.class);
        exception.expectMessage(version.toString());
        Compatibility.checkMinRestApiVersion(taskListenerMock, version, clientRequestMock);
    }

    @Test
    @WithoutJenkins
    public void checkMinRestApiVersion() throws IOException, InterruptedException {
        FakeTaskListener taskListenerMock = mock(FakeTaskListener.class);
        when(taskListenerMock.getLogger()).thenReturn(System.out);
        ClientRequest clientRequestMock = mock(ClientRequest.class, "ClientRequestMock");
        when(clientRequestMock.getApiVersion()).thenReturn(new ApiVersion(2, 2, 2));
        Compatibility.checkMinRestApiVersion(taskListenerMock, new ApiVersion(1, 2, 2), clientRequestMock);
        Compatibility.checkMinRestApiVersion(taskListenerMock, new ApiVersion(2, 1, 2), clientRequestMock);
        Compatibility.checkMinRestApiVersion(taskListenerMock, new ApiVersion(2, 2, 1), clientRequestMock);
        Compatibility.checkMinRestApiVersion(taskListenerMock, new ApiVersion(2, 2, 2), clientRequestMock);
    }

    @Test
    @WithoutJenkins
    public void checkMinRestApiVersionExceptionMajor() throws IOException, InterruptedException {
        checkMinRestApiVersionException(new ApiVersion(3, 2, 2));
    }

    @Test
    @WithoutJenkins
    public void checkMinRestApiVersionExceptionMinor() throws IOException, InterruptedException {
        checkMinRestApiVersionException(new ApiVersion(2, 3, 2));
    }

    @Test
    @WithoutJenkins
    public void checkMinRestApiVersionExceptionFix() throws IOException, InterruptedException {
        checkMinRestApiVersionException(new ApiVersion(2, 2, 3));
    }
}
