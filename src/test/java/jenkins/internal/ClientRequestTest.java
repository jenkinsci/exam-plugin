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

import hudson.AbortException;
import hudson.model.Executor;
import jenkins.internal.data.ExamStatus;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.MockingDetails;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import testData.ServerDispatcher;

import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class ClientRequestTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private static String baseUrl = "http://localhost:8085";
    private ClientRequest testObject;
    private PrintStream printMock;
    private static MockWebServer server;
    private static ServerDispatcher dispatcher;

    @BeforeClass
    public static void oneTimeSetup() throws IOException {
        dispatcher = new ServerDispatcher();
    }

    @AfterClass
    public static void oneTimeTearDown() throws IOException {
    }

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();
        dispatcher.setDefaults();
        server.setDispatcher(dispatcher);
        server.start(8085);
        printMock = Mockito.mock(PrintStream.class, "PrintMock");
        testObject = new ClientRequest(null, printMock, baseUrl);
        Whitebox.invokeMethod(ClientRequest.class,"createClient");
    }

    @After
    public void tearDown() throws Exception {
        Whitebox.invokeMethod(ClientRequest.class,"destroyClient");
        server.shutdown();
    }

    @Test
    public void getBaseUrl() {
        String teststring = "myTestString";
        Whitebox.setInternalState(ClientRequest.class, "baseUrl", teststring);
        String testIt = testObject.getBaseUrl();
        assertEquals(teststring, testIt);
    }

    @Test
    public void setBaseUrl() {
        String teststring = "myTestString";
        testObject.setBaseUrl(teststring);
        String testIt = Whitebox.getInternalState(ClientRequest.class,"baseUrl");
        assertEquals(teststring, testIt);
    }

    @Test
    public void getListener() {
    }

    @Test
    public void setListener() {
    }

    @Test
    public void getStatus() throws AbortException {
        ExamStatus examStatus = testObject.getStatus();
        assertEquals("myTestJob",examStatus.getJobName());
    }

    @Test
    public void isApiAvailable() throws Exception {
        assertTrue(testObject.isApiAvailable());

        dispatcher.clearAllResponse();
        assertFalse(testObject.isApiAvailable());
        dispatcher.setDefaults();

        Whitebox.invokeMethod(ClientRequest.class,"destroyClient");
        assertTrue(testObject.isApiAvailable());
    }

    @Test
    public void startTestrun() throws AbortException {
        testObject.startTestrun(null);
        Mockito.verify(printMock).println("starting testrun");

        Mockito.clearInvocations(printMock);
        dispatcher.removeResponse("/testrun/start");
        exception.expect(RuntimeException.class);
        testObject.startTestrun(null);
        Mockito.verify(printMock).println("starting testrun");
    }

    @Test
    public void stopTestrun() throws AbortException {
        testObject.stopTestrun();
        Mockito.verify(printMock).println("stopping testrun");

        Mockito.clearInvocations(printMock);
        dispatcher.removeResponse("/testrun/stop");
        exception.expect(RuntimeException.class);
        testObject.stopTestrun();
        Mockito.verify(printMock).println("stopping testrun");
    }

    @Test
    public void clearWorkspace() throws AbortException {
        String strAll = "deleting all projects and pcode from EXAM workspace";
        testObject.clearWorkspace(null);
        Mockito.verify(printMock).println(strAll);
        Mockito.clearInvocations(printMock);
        testObject.clearWorkspace("");
        Mockito.verify(printMock).println(strAll);
        testObject.clearWorkspace("myProject");
        Mockito.verify(printMock).println("deleting project and pcode for project \"myProject\" from EXAM workspace");

        dispatcher.clearAllResponse();
        exception.expect(RuntimeException.class);
        testObject.clearWorkspace("");
    }

    @Test
    public void shutdown() {
        testObject.shutdown();
        Mockito.verify(printMock).println("closing EXAM");
    }

    @Test
    public void connectClient() throws IOException {
        assertTrue(testObject.connectClient(1000));
        Mockito.verify(printMock, Mockito.never()).println("ERROR: EXAM does not answer in 1000ms");

            server.shutdown();
            Mockito.clearInvocations(printMock);
            assertFalse(testObject.connectClient(1000));
            Mockito.verify(printMock).println("ERROR: EXAM does not answer in 1000ms");

    }

    @Test
    public void disconnectClient() throws Exception {
        testObject.disconnectClient(1000);
        Mockito.verify(printMock).println("disconnect from EXAM");
        Mockito.verify(printMock).println("ERROR: EXAM does not shutdown in 1000ms");

        Whitebox.invokeMethod(ClientRequest.class,"createClient");
        Mockito.clearInvocations(printMock);
        dispatcher.removeResponse("/testrun/status");
        testObject.disconnectClient(1000);
        Mockito.verify(printMock).println("disconnect from EXAM");
        Mockito.verify(printMock, Mockito.never()).println("ERROR: EXAM does not shutdown in 1000ms");

        testObject.disconnectClient(1000);
        Mockito.verify(printMock).println("Client is not connected");

        Whitebox.invokeMethod(ClientRequest.class,"createClient");
        Mockito.clearInvocations(printMock);
        server.shutdown();
        testObject.disconnectClient(1000);

        Mockito.verify(printMock).println("disconnect from EXAM");
        Mockito.inOrder(printMock).verify(printMock, Mockito.calls(2)).println(Mockito.anyString());
    }

    @Test
    public void waitForTestrunEnds()  throws AbortException {
        Executor executor = Mockito.mock(Executor.class);
        Mockito.when(executor.isInterrupted()).thenReturn(false);
        dispatcher.setResponse("/testrun/status", new MockResponse().setResponseCode(200)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Cache-Control", "no-cache")
                .setBody("{\"jobName\":\"TestRun\",\"jobRunning\":\"false\",\"testRunState\":1}"));
        testObject.waitForTestrunEnds(executor);

        Mockito.inOrder(executor).verify(executor, Mockito.calls(2)).isInterrupted();
        int reqCount = server.getRequestCount();
        assertEquals("unexpected count of server calls", 2, reqCount);

        Mockito.clearInvocations(executor);
        Mockito.when(executor.isInterrupted()).thenReturn(true);
        testObject.waitForTestrunEnds(executor);
        Mockito.inOrder(executor).verify(executor, Mockito.calls(1)).isInterrupted();
    }

    @Test
    public void getLogger() {
        PrintStream printMock = Mockito.mock(PrintStream.class, "PrintMock for Test");
        Whitebox.setInternalState(ClientRequest.class, "logger", printMock);
        PrintStream testResult = testObject.getLogger();
        MockingDetails mockDetails = Mockito.mockingDetails(testResult);
        if(!mockDetails.isMock()){
            fail("no Mock returned");
        }
        String mockName = mockDetails.getMockCreationSettings().getMockName().toString();
        assertEquals("PrintMock for Test", mockName);
    }

    @Test
    public void setLogger() {
        PrintStream printMock = Mockito.mock(PrintStream.class, "PrintMock for Test");
        testObject.setLogger(printMock);
        PrintStream testResult = Whitebox.getInternalState(ClientRequest.class, "logger");
        MockingDetails mockDetails = Mockito.mockingDetails(testResult);
        if(!mockDetails.isMock()){
            fail("no Mock returned");
        }
        String mockName = mockDetails.getMockCreationSettings().getMockName().toString();
        assertEquals("PrintMock for Test", mockName);
    }

    @Test
    public void createClient() throws Exception {
        Whitebox.invokeMethod(ClientRequest.class,"createClient");
        Mockito.verify(printMock).println("Client already connected");
    }

    @Test
    public void noClient() throws Exception {
        Whitebox.invokeMethod(ClientRequest.class,"destroyClient");

        Mockito.clearInvocations(printMock);
        testObject.shutdown();
        Mockito.verify(printMock).println("WARNING: no EXAM connected");

        Mockito.clearInvocations(printMock);
        testObject.clearWorkspace("");
        Mockito.verify(printMock).println("WARNING: no EXAM connected");

        Mockito.clearInvocations(printMock);
        testObject.stopTestrun();
        Mockito.verify(printMock).println("WARNING: no EXAM connected");

        Mockito.clearInvocations(printMock);
        testObject.getStatus();
        Mockito.verify(printMock).println("WARNING: no EXAM connected");

        Mockito.clearInvocations(printMock);
        testObject.startTestrun(null);
        Mockito.verify(printMock).println("WARNING: no EXAM connected");
    }
}
