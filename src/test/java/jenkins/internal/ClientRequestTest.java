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

import com.sun.jersey.api.client.Client;
import hudson.AbortException;
import hudson.Launcher;
import hudson.model.Api;
import hudson.model.Executor;
import jenkins.internal.data.ApiVersion;
import jenkins.internal.data.ExamStatus;
import jenkins.internal.data.FilterConfiguration;
import jenkins.internal.data.TestrunFilter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockingDetails;
import org.mockito.internal.configuration.injection.MockInjection;
import org.powermock.reflect.Whitebox;
import testData.ServerDispatcher;

import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ClientRequestTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private static String baseUrl = "http://localhost:8085";
    @Mock
    private ClientRequest testObject;
    @Mock
    private PrintStream printMock;
    @Mock
    private static MockWebServer server;
    @Mock
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
        printMock = mock(PrintStream.class, "PrintMock");
        testObject = new ClientRequest(null, printMock, baseUrl);
        Whitebox.invokeMethod(testObject, "createClient");
    }

    @After
    public void tearDown() throws Exception {
        Whitebox.invokeMethod(testObject, "destroyClient");
        server.shutdown();
    }

    @Test
    public void getBaseUrl() {
        String teststring = "myTestString";
        Whitebox.setInternalState(testObject, "baseUrl", teststring);
        String testIt = testObject.getBaseUrl();
        assertEquals(teststring, testIt);
    }

    @Test
    public void setBaseUrl() {
        String teststring = "myTestString";
        testObject.setBaseUrl(teststring);
        String testIt = Whitebox.getInternalState(testObject, "baseUrl");
        assertEquals(teststring, testIt);
    }

    @Test
    public void getStatus() throws AbortException {
        ExamStatus examStatus = testObject.getStatus();
        assertEquals("myTestJob", examStatus.getJobName());
    }

    @Test
    public void isApiAvailable() throws Exception {
        assertTrue(testObject.isApiAvailable());

        dispatcher.clearAllResponse();
        assertFalse(testObject.isApiAvailable());
        dispatcher.setDefaults();

        Whitebox.invokeMethod(testObject, "destroyClient");
        assertTrue(testObject.isApiAvailable());
    }

    @Test
    public void startTestrun() throws AbortException {
        testObject.startTestrun(null);
        verify(printMock).println("starting testrun");

        clearInvocations(printMock);
        dispatcher.removeResponse("/testrun/start");
        exception.expect(RuntimeException.class);
        testObject.startTestrun(null);
        verify(printMock).println("starting testrun");
    }

    @Test
    public void stopTestrun() throws AbortException {
        testObject.stopTestrun();
        verify(printMock).println("stopping testrun");

        clearInvocations(printMock);
        dispatcher.removeResponse("/testrun/stop");
        exception.expect(RuntimeException.class);
        testObject.stopTestrun();
        verify(printMock).println("stopping testrun");
    }

    @Test
    public void clearWorkspace() throws AbortException {
        String strAll = "deleting all projects and pcode from EXAM workspace";
        testObject.clearWorkspace(null);
        verify(printMock).println(strAll);
        clearInvocations(printMock);
        testObject.clearWorkspace("");
        verify(printMock).println(strAll);
        testObject.clearWorkspace("myProject");
        verify(printMock).println("deleting project and pcode for project \"myProject\" from EXAM workspace");

        dispatcher.clearAllResponse();
        exception.expect(RuntimeException.class);
        testObject.clearWorkspace("");
    }

    @Test
    public void shutdown() {
        testObject.shutdown();
        verify(printMock).println("closing EXAM");
    }

    @Test
    public void connectClient() throws IOException {
        assertTrue(testObject.connectClient(1000));
        verify(printMock, never()).println("ERROR: EXAM does not answer in 1s");

        server.shutdown();
        clearInvocations(printMock);
        assertFalse(testObject.connectClient(1000));
        verify(printMock).println("ERROR: EXAM does not answer in 1s");
    }

    @Test
    public void disconnectClient() throws Exception {
        testObject.disconnectClient(1000);
        verify(printMock).println("disconnect from EXAM");
        verify(printMock).println("ERROR: EXAM does not shutdown in 1000ms");

        Whitebox.invokeMethod(testObject, "createClient");
        clearInvocations(printMock);
        dispatcher.removeResponse("/testrun/status");
        testObject.disconnectClient(1000);
        verify(printMock).println("disconnect from EXAM");
        verify(printMock, never()).println("ERROR: EXAM does not shutdown in 1000ms");

        testObject.disconnectClient(1000);
        verify(printMock).println("Client is not connected");

        Whitebox.invokeMethod(testObject, "createClient");
        clearInvocations(printMock);
        server.shutdown();
        testObject.disconnectClient(1000);

        verify(printMock).println("disconnect from EXAM");
        inOrder(printMock).verify(printMock, calls(2)).println(anyString());
    }

    @Test
    public void waitForTestrunEnds() throws AbortException {
        Executor executor = mock(Executor.class);
        when(executor.isInterrupted()).thenReturn(false);
        dispatcher.setResponse("/testrun/status", new MockResponse().setResponseCode(200)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Cache-Control", "no-cache")
                .setBody("{\"jobName\":\"TestRun\",\"jobRunning\":\"false\",\"testRunState\":1}"));
        testObject.waitForTestrunEnds(executor);

        inOrder(executor).verify(executor, calls(2)).isInterrupted();
        int reqCount = server.getRequestCount();
        assertEquals("unexpected count of server calls", 2, reqCount);

        clearInvocations(executor);
        when(executor.isInterrupted()).thenReturn(true);
        testObject.waitForTestrunEnds(executor);
        inOrder(executor).verify(executor, calls(1)).isInterrupted();
    }

    @Test
    public void getLogger() {
        PrintStream printMock = mock(PrintStream.class, "PrintMock for Test");
        Whitebox.setInternalState(testObject, "logger", printMock);
        PrintStream testResult = testObject.getLogger();
        MockingDetails mockDetails = mockingDetails(testResult);
        if (!mockDetails.isMock()) {
            fail("no Mock returned");
        }
        String mockName = mockDetails.getMockCreationSettings().getMockName().toString();
        assertEquals("PrintMock for Test", mockName);
    }

    @Test
    public void setLogger() {
        PrintStream printMock = mock(PrintStream.class, "PrintMock for Test");
        testObject.setLogger(printMock);
        PrintStream testResult = Whitebox.getInternalState(testObject, "logger");
        MockingDetails mockDetails = mockingDetails(testResult);
        if (!mockDetails.isMock()) {
            fail("no Mock returned");
        }
        String mockName = mockDetails.getMockCreationSettings().getMockName().toString();
        assertEquals("PrintMock for Test", mockName);
    }

    @Test
    public void createClient() throws Exception {
        Whitebox.invokeMethod(testObject, "createClient");
        verify(printMock).println("Client already connected");
    }

    @Test
    public void noClient() throws Exception {
        Whitebox.invokeMethod(testObject, "destroyClient");

        clearInvocations(printMock);
        testObject.shutdown();
        verify(printMock).println("WARNING: no EXAM connected");

        clearInvocations(printMock);
        testObject.clearWorkspace("");
        verify(printMock).println("WARNING: no EXAM connected");

        clearInvocations(printMock);
        testObject.stopTestrun();
        verify(printMock).println("WARNING: no EXAM connected");

        clearInvocations(printMock);
        testObject.getStatus();
        verify(printMock).println("WARNING: no EXAM connected");

        clearInvocations(printMock);
        testObject.startTestrun(null);
        verify(printMock).println("WARNING: no EXAM connected");
    }

    @Test
    public void setLauncher() {
        Launcher launcherMock = mock(Launcher.class, "Launcher for Test");
        testObject.setLauncher(launcherMock);
        Launcher toTest = Whitebox.getInternalState(testObject, "launcher");
        MockingDetails mockDetails = mockingDetails(toTest);
        if (!mockDetails.isMock()) {
            fail("no Mock returned");
        }
        String mockName = mockDetails.getMockCreationSettings().getMockName().toString();
        assertEquals("Launcher for Test", mockName);
    }

    @Test
    public void getApiVersion() {
        ApiVersion toTest = new ApiVersion();
        toTest.setMajor(2);
        toTest.setMinor(5);
        toTest.setFix(7);

        ApiVersion apiVersion = testObject.getApiVersion();
        assertEquals(toTest.getFix(), apiVersion.getFix());
        assertEquals(toTest.getMinor(), apiVersion.getMinor());
        assertEquals(toTest.getMajor(), apiVersion.getMajor());
    }

    @Test
    public void setTestrunFilter() throws Exception {
        TestrunFilter testrunFilter1 = new TestrunFilter("trf1", "val1", true, false);
        TestrunFilter testrunFilter2 = new TestrunFilter("trf2", "val2", false, true);

        FilterConfiguration filterConfig = new FilterConfiguration();
        filterConfig.addTestrunFilter(testrunFilter1);
        filterConfig.addTestrunFilter(testrunFilter2);

        Whitebox.invokeMethod(testObject, "setTestrunFilter", filterConfig);

        RecordedRequest request = server.takeRequest();
        String requestRoute = request.getPath();
        String requestBody = request.getBody().readUtf8();

        ObjectMapper om = new ObjectMapper();
        String filterConfigToTest = om.writeValueAsString(filterConfig);

        assertEquals(requestBody, filterConfigToTest);
        assertEquals(requestRoute, "/testrun/setFilter");
    }

    @Test
    public void destroyClient() throws Exception {
        Whitebox.invokeMethod(testObject, "destroyClient");

        Client client = Whitebox.getInternalState(testObject, "client");

        assertNull(client);
    }

    @Test
    public void convert() throws Exception {
        String path = "/testrun/convertToJunit/";
        String testReportProject = "testProject";
        Whitebox.invokeMethod(testObject, "convert", testReportProject);

        RecordedRequest request = server.takeRequest();
        String requestBody = request.getBody().readUtf8();
        String requestRoute = request.getPath();

        assertEquals(path + testReportProject, requestRoute);
        assertEquals( "",requestBody);
    }
}
