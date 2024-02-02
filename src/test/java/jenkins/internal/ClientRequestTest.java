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
import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.Launcher;
import hudson.model.Executor;
import jenkins.internal.data.*;
import net.sf.json.test.JSONAssert;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;
import org.mockito.Mock;
import org.mockito.MockingDetails;
import testData.ServerDispatcher;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ClientRequestTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private Launcher.LocalLauncher launcher;

    private Random random = new Random();

    private static int apiPort = 8085;
    @Mock
    private static MockWebServer server;
    @Mock
    private static ServerDispatcher dispatcher;
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    @Mock
    private ClientRequest testObject;
    @Mock
    private PrintStream printMock;

    @BeforeClass
    public static void oneTimeSetup() throws Exception {
        dispatcher = new ServerDispatcher();
    }

    @AfterClass
    public static void oneTimeTearDown() {
    }

    private static void setServer(MockWebServer myServer) {
        ClientRequestTest.server = myServer;
    }

    @Before
    public void setUp() throws Exception {
        launcher = jenkinsRule.createLocalLauncher();
        ClientRequestTest.setServer(new MockWebServer());
        dispatcher.setDefaults();
        server.setDispatcher(dispatcher);
        server.start(apiPort);
        printMock = mock(PrintStream.class, "PrintMock");
        testObject = new ClientRequest(printMock, apiPort, launcher);
        Whitebox.setInternalState(testObject, "waitTime", 100);
        Whitebox.setInternalState(testObject, "clientConnected", true);
    }

    @After
    public void tearDown() throws Exception {
        if (server != null) {
            server.shutdown();
        }
        launcher = null;
        Whitebox.setInternalState(testObject, "clientConnected", false);
    }

    @Test
    @WithoutJenkins
    public void isClientConnected() {
        Whitebox.setInternalState(testObject, "clientConnected", false);
        assertFalse(testObject.isClientConnected());
        Whitebox.setInternalState(testObject, "clientConnected", true);
        assertTrue(testObject.isClientConnected());
    }

    @Test
    @WithoutJenkins
    public void getApiPort() {
        int testInt = random.nextInt();
        Whitebox.setInternalState(testObject, "apiPort", testInt);
        int testIt = testObject.getApiPort();
        assertEquals(testInt, testIt);
    }

    @Test
    @WithoutJenkins
    public void setApiPort() {
        int testInt = random.nextInt();
        testObject.setApiPort(testInt);
        int testIt = Whitebox.getInternalState(testObject, "apiPort");
        assertEquals(testInt, testIt);
    }

    @Test
    public void getStatus() {
        try {
            ExamStatus examStatus = testObject.getStatus();
            assertEquals("myTestJob", examStatus.getJobName());
        } catch (IOException | InterruptedException e) {
            assertTrue("Exception was thrown: " + e.toString(), false);
        }
    }

    @Test
    public void getTestRunStatus() {
        try {
            dispatcher.setResponse("/examRest/testrun/status", new MockResponse().setResponseCode(200)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Cache-Control", "no-cache").setBody(
                            "{\"jobRunning\": true,\"jobName\": \"myTestJob\",\"testRunState\": -1,\"status\": {\"testCaseCount\": 4,\"currentTestCaseIdx\": "
                                    + "2,\"expectedRuntime\": \"00:00:01:00\",\"currentRuntime\": \"00:00:00:20\",\"remainingRuntime\": \"00:00:00:40\",\"name\": \"testName\",\"fullScopedName\": \"test.testName\"}}"));

            ExamStatus examStatus = testObject.getStatus();
            TestRunStatus testRunStatus = examStatus.getStatus();

            assertEquals(2, testRunStatus.getCurrentTestCaseIdx());
            assertEquals(4, testRunStatus.getTestCaseCount());
            assertEquals("00:00:01:00", testRunStatus.getExpectedRuntime());
            assertEquals("00:00:00:20", testRunStatus.getCurrentRuntime());
            assertEquals("00:00:00:40", testRunStatus.getRemainingRuntime());
            assertEquals("testName", testRunStatus.getName());
            assertEquals("test.testName", testRunStatus.getFullScopedName());

            assertEquals("myTestJob", examStatus.getJobName());
        } catch (IOException | InterruptedException e) {
            assertTrue("Exception was thrown: " + e.toString(), false);
        }
    }

    @Test
    public void getTestRunStatusNull() {
        try {
            dispatcher.setResponse("/examRest/testrun/status", new MockResponse().setResponseCode(200)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Cache-Control", "no-cache")
                    .setBody("{\"jobRunning\": true,\"jobName\": \"myTestJob\",\"testRunState\": -1}"));

            ExamStatus examStatus = testObject.getStatus();
            TestRunStatus testRunStatus = examStatus.getStatus();

            assertNull(testRunStatus);
            assertEquals("myTestJob", examStatus.getJobName());
        } catch (IOException | InterruptedException e) {
            assertTrue("Exception was thrown: " + e.toString(), false);
        }
    }

    @Test
    public void isApiAvailable() throws Exception {
        assertTrue(testObject.isApiAvailable());

        dispatcher.clearAllResponse();
        assertFalse(testObject.isApiAvailable());
        dispatcher.setDefaults();

        Whitebox.setInternalState(testObject, "clientConnected", false);
        assertTrue(testObject.isApiAvailable());
    }

    @Test
    @WithoutJenkins
    public void startTestrun() {
        try {
            testObject.startTestrun(null);
            verify(printMock).println("starting testrun");
        } catch (IOException | InterruptedException e) {
            assertTrue("Exception was thrown: " + e.toString(), false);
        }
    }

    @Test
    public void startTestrunWithException() throws IOException, InterruptedException {
        dispatcher.removeResponse("/examRest/testrun/start");
        exception.expect(IOException.class);
        testObject.startTestrun(null);
        verify(printMock).println("starting testrun");
    }

    @Test
    @WithoutJenkins
    public void createExamProject() {
        try {
            testObject.createExamProject(null);
            verify(printMock).println("creating Exam Project");
        } catch (IOException | InterruptedException e) {
            assertTrue("Exception was thronw: " + e.toString(), false);
        }
    }

    @Test
    public void createExamProjectWithException() throws IOException, InterruptedException {
        dispatcher.removeResponse("/examRest/workspace/createProject");
        exception.expect(IOException.class);
        testObject.createExamProject(null);
        verify(printMock).println("ecreating Exam Project");
    }

    @Test
    @WithoutJenkins
    public void executeGroovyScript() {
        try {
            testObject.executeGroovyScript(null);
            verify(printMock).println("executing Groovy Script");
        } catch (IOException | InterruptedException e) {
            assertTrue("Exception was thrown: " + e.toString(), false);
        }
    }

    @Test
    public void executeGroovyScriptWithException() throws IOException, InterruptedException {
        dispatcher.removeResponse("/examRest/groovy/executeGroovyScript");
        exception.expect(IOException.class);
        testObject.executeGroovyScript(null);
        verify(printMock).println("executing Groovy Script");
    }

    @Test
    @WithoutJenkins
    public void generateTestcases() {
        try {
            testObject.generateTestcases(null);
            verify(printMock).println("generating Testcases");
        } catch (Exception e) {
            fail("Exception was thrown: " + e.toString());
        }
    }

    @Test
    @WithoutJenkins
    public void generateTestcasesNewApi() {
        try {
            TCGResult res = new TCGResult();
            res.setCode(200);
            res.setMessage("fine");

            ObjectMapper mapper = new ObjectMapper();
            String response = mapper.writeValueAsString(res);

            dispatcher.setResponse("/examRest/TCG/generate",
                    new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json; charset=utf-8")
                            .addHeader("Cache-Control", "no-cache").setBody(response));
            GenerateConfiguration config = getGenerateConfiguration();

            testObject.generateTestcasesPost203(config);
            RecordedRequest latestRequest = server.takeRequest();
            String body = latestRequest.getBody().readUtf8();

            // any of those properties are not supposed to be in the JSON due to api changes with 2.0.3
            assertFalse(body.contains("overwriteDescriptionSource"));
            assertFalse(body.contains("overwriteFrameSteps"));
            assertFalse(body.contains("overwriteMappingList"));
            // following properties are supposed to be contained
            assertTrue(body.contains("descriptionSource"));
            assertTrue(body.contains("frameFunctions"));
            assertTrue(body.contains("mappingList"));

            verify(printMock).println("generating Testcases");
        } catch (Exception e) {
            fail("Exception was thrown: " + e.toString());
        }
    }

    @Test
    public void generateTestcasesWithException() throws IOException, InterruptedException {
        dispatcher.removeResponse("/examRest/TCG/generate");
        exception.expect(IOException.class);
        testObject.generateTestcases(null);
        verify(printMock).println("executing Groovy Script");
    }

    @Test
    @WithoutJenkins
    public void stopTestrun() {
        try {
            testObject.stopTestrun();
            verify(printMock).println("stopping testrun");
        } catch (IOException | InterruptedException e) {
            assertTrue("Exception was thrown: " + e.toString(), false);
        }
    }

    @Test
    public void stopTestrunWithException() throws IOException, InterruptedException {
        dispatcher.removeResponse("/examRest/testrun/stop");
        exception.expect(IOException.class);
        testObject.stopTestrun();
        verify(printMock).println("stopping testrun");
    }

    @Test
    @WithoutJenkins
    public void clearWorkspace() {
        try {
            String strAll = "deleting all projects and pcode from EXAM workspace";
            Compatibility.setClientApiVersion(new ApiVersion(2, 0, 0));
            testObject.clearWorkspace(null);
            verify(printMock).println(strAll);

            Compatibility.setClientApiVersion(new ApiVersion(1, 0, 0));
            clearInvocations(printMock);
            testObject.clearWorkspace("");
            verify(printMock).println(strAll);

            clearInvocations(printMock);
            testObject.clearWorkspace("");
            verify(printMock).println(strAll);
            testObject.clearWorkspace("myProject");
            verify(printMock).println("deleting project and pcode for project \"myProject\" from EXAM workspace");

        } catch (IOException | InterruptedException e) {
            assertTrue("Exception was thrown: " + e.toString(), false);
        }
    }

    @Test
    public void clearWorkspaceWithException() throws IOException, InterruptedException {
        Compatibility.setClientApiVersion(new ApiVersion(2, 0, 0));
        dispatcher.clearAllResponse();
        exception.expect(IOException.class);
        testObject.clearWorkspace("");
    }

    @Test
    public void connectClient() throws IOException, InterruptedException {
        Executor executor = mock(Executor.class);
        assertTrue(testObject.connectClient(executor, 1));
        verify(printMock, never()).println("ERROR: EXAM does not answer in 1s");

        server.shutdown();
        clearInvocations(printMock);
        assertFalse(testObject.connectClient(executor, 1));
        verify(printMock).println("ERROR: EXAM does not answer in 1s");

        clearInvocations(printMock);
        clearInvocations(executor);
        when(executor.isInterrupted()).thenReturn(true);
        testObject.connectClient(executor, 10);
        verify(printMock, never()).println("ERROR: EXAM does not answer in 10s");
        verify(printMock).println("Job interrupted");
    }

    @Test
    public void disconnectClient() throws Exception {
        Executor executor = mock(Executor.class);
        testObject.disconnectClient(executor, 1);
        verify(printMock).println("disconnect from EXAM");
        verify(printMock).println("ERROR: EXAM does not shutdown in 1s");

        Whitebox.setInternalState(testObject, "clientConnected", true);
        clearInvocations(printMock);
        dispatcher.removeResponse("/examRest/workspace/apiVersion");
        testObject.disconnectClient(executor, 1);
        verify(printMock).println("disconnect from EXAM");
        verify(printMock, never()).println("ERROR: EXAM does not shutdown in 1s");

        testObject.disconnectClient(executor, 1);
        verify(printMock).println("Client is not connected");

        Whitebox.setInternalState(testObject, "clientConnected", true);
        clearInvocations(printMock);
        server.shutdown();
        testObject.disconnectClient(executor, 1);

        verify(printMock).println("disconnect from EXAM");
        inOrder(printMock).verify(printMock, calls(2)).println(anyString());

        clearInvocations(printMock);
        clearInvocations(executor);
        when(executor.isInterrupted()).thenReturn(true);
        Whitebox.setInternalState(testObject, "clientConnected", true);
        testObject.disconnectClient(executor, 10);
        verify(printMock, never()).println("ERROR: EXAM does not shutdown in 10s");
        verify(printMock).println("Job interrupted");
    }

    @Test
    public void waitForTestrunEnds() {
        int numCalls = 10;
        try {
            Executor executor = mock(Executor.class);
            when(executor.isInterrupted()).thenReturn(false);
            dispatcher.setResponse("/examRest/testrun/status", new MockResponse().setResponseCode(200)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Cache-Control", "no-cache")
                    .setBody("{\"jobName\":\"TestRun\",\"jobRunning\":\"false\",\"testRunState\":1}"));
            testObject.waitForTestrunEnds(executor, numCalls);

            inOrder(executor).verify(executor, calls(2)).isInterrupted();
            int reqCount = server.getRequestCount();
            assertEquals("unexpected count of server calls", 2, reqCount);

            clearInvocations(executor);
            when(executor.isInterrupted()).thenReturn(true);
            testObject.waitForTestrunEnds(executor, numCalls);
            inOrder(executor).verify(executor, calls(1)).isInterrupted();
        } catch (IOException | InterruptedException e) {
            assertTrue("Exception was thrown: " + e.toString(), false);
        }
    }

    @Test
    public void waitForTestrunEnds_noStart() {
        int numCalls = 10;
        try {
            Executor executor = mock(Executor.class);
            when(executor.isInterrupted()).thenReturn(false);
            dispatcher.setResponse("/examRest/testrun/status", new MockResponse().setResponseCode(200)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Cache-Control", "no-cache")
                    .setBody("{\"jobName\":\"nothing\",\"jobRunning\":\"false\",\"testRunState\":1}"));
            testObject.waitForTestrunEnds(executor, numCalls);

            inOrder(executor).verify(executor, calls(numCalls)).isInterrupted();
            int reqCount = server.getRequestCount();
            assertEquals("unexpected count of server calls", numCalls, reqCount);

            clearInvocations(executor);
            when(executor.isInterrupted()).thenReturn(true);
            testObject.waitForTestrunEnds(executor, numCalls);
            inOrder(executor).verify(executor, calls(1)).isInterrupted();
        } catch (IOException | InterruptedException e) {
            assertTrue("Exception was thrown: " + e.toString(), false);
        }
    }

    @Test
    public void waitForExamIdle() {
        int numCalls = 10;
        try {
            Executor executor = mock(Executor.class);
            when(executor.isInterrupted()).thenReturn(false);
            dispatcher.setResponse("/examRest/testrun/status", new MockResponse().setResponseCode(200)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Cache-Control", "no-cache")
                    .setBody("{\"jobName\":\"TestRun\",\"jobRunning\":\"false\",\"testRunState\":0}"));
            testObject.waitForExamIdle(executor, numCalls);

            inOrder(executor).verify(executor, calls(1)).isInterrupted();
            int reqCount = server.getRequestCount();
            assertEquals("unexpected count of server calls", 1, reqCount);

            clearInvocations(executor);
            when(executor.isInterrupted()).thenReturn(true);
            testObject.waitForExamIdle(executor, numCalls);
            inOrder(executor).verify(executor, calls(1)).isInterrupted();
        } catch (IOException | InterruptedException e) {
            assertTrue("Exception was thrown: " + e.toString(), false);
        }
    }

    @Test
    public void waitForExamIdle_isBusy() {
        int numCalls = 10;
        try {
            Executor executor = mock(Executor.class);
            when(executor.isInterrupted()).thenReturn(false);
            dispatcher.setResponse("/examRest/testrun/status", new MockResponse().setResponseCode(200)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Cache-Control", "no-cache")
                    .setBody("{\"jobName\":\"nothing\",\"jobRunning\":\"true\",\"testRunState\":0}"));
            testObject.waitForExamIdle(executor, numCalls);

            inOrder(executor).verify(executor, calls(numCalls)).isInterrupted();
            int reqCount = server.getRequestCount();
            assertEquals("unexpected count of server calls", numCalls, reqCount);

            clearInvocations(executor);
            when(executor.isInterrupted()).thenReturn(true);
            testObject.waitForExamIdle(executor, numCalls);
            inOrder(executor).verify(executor, calls(1)).isInterrupted();
        } catch (IOException | InterruptedException e) {
            assertTrue("Exception was thrown: " + e.toString(), false);
        }
    }

    @Test
    public void waitForExportPDFReportJob() {
        int numCalls = 10;
        String jobName = "Export Reports to PDF.";
        try {
            Executor executor = mock(Executor.class);
            when(executor.isInterrupted()).thenReturn(false);
            dispatcher.setResponse("/examRest/testrun/status", new MockResponse().setResponseCode(200)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Cache-Control", "no-cache")
                    .setBody("{\"jobName\":\"" + jobName + "\",\"jobRunning\":\"false\",\"testRunState\":0}"));
            testObject.waitForExportPDFReportJob(executor, numCalls);

            inOrder(executor).verify(executor, calls(1)).isInterrupted();
            int reqCount = server.getRequestCount();
            assertEquals("unexpected count of server calls", 1, reqCount);

            clearInvocations(executor);

            dispatcher.setResponse("/examRest/testrun/status", new MockResponse().setResponseCode(200)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Cache-Control", "no-cache")
                    .setBody("{\"jobName\":\"nothing\",\"jobRunning\":\"true\",\"testRunState\":0}"));
            testObject.waitForExportPDFReportJob(executor, numCalls);

            inOrder(executor).verify(executor, calls(1)).isInterrupted();
            reqCount = server.getRequestCount();
            assertEquals("unexpected count of server calls", 2, reqCount);

        } catch (IOException | InterruptedException e) {
            assertTrue("Exception was thrown: " + e.toString(), false);
        }
    }

    @Test
    public void waitForExportPDFReportJob_isBusy() {
        int numCalls = 10;
        String jobName = "Export Reports to PDF.";
        try {
            Executor executor = mock(Executor.class);
            when(executor.isInterrupted()).thenReturn(false);
            dispatcher.setResponse("/examRest/testrun/status", new MockResponse().setResponseCode(200)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Cache-Control", "no-cache")
                    .setBody("{\"jobName\":\"" + jobName + "\",\"jobRunning\":\"true\",\"testRunState\":0}"));
            testObject.waitForExportPDFReportJob(executor, numCalls);

            inOrder(executor).verify(executor, calls(numCalls)).isInterrupted();
            int reqCount = server.getRequestCount();
            assertEquals("unexpected count of server calls", numCalls, reqCount);

            clearInvocations(executor);
            when(executor.isInterrupted()).thenReturn(true);
            testObject.waitForExportPDFReportJob(executor, numCalls);
            inOrder(executor).verify(executor, calls(1)).isInterrupted();
        } catch (IOException | InterruptedException e) {
            assertTrue("Exception was thrown: " + e.toString(), false);
        }
    }

    @Test
    @WithoutJenkins
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
    @WithoutJenkins
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
    @WithoutJenkins
    public void noClient() throws Exception {
        Whitebox.setInternalState(testObject, "clientConnected", false);

        clearInvocations(printMock);
        testObject.getApiVersion();
        verify(printMock).println("WARNING: no EXAM connected");

        clearInvocations(printMock);
        testObject.setTestrunFilter(null);
        verify(printMock).println("WARNING: no EXAM connected");

        clearInvocations(printMock);
        testObject.convert(null);
        verify(printMock).println("WARNING: no EXAM connected");

        clearInvocations(printMock);
        testObject.clearWorkspace("");
        verify(printMock).println("WARNING: no EXAM connected");

        clearInvocations(printMock);
        testObject.stopTestrun();
        verify(printMock).println("WARNING: no EXAM connected");

        clearInvocations(printMock);
        testObject.startTestrun(null);
        verify(printMock).println("WARNING: no EXAM connected");

        clearInvocations(printMock);
        testObject.createExamProject(null);
        verify(printMock).println("WARNING: no EXAM connected");

        clearInvocations(printMock);
        testObject.executeGroovyScript(null);
        verify(printMock).println("WARNING: no EXAM connected");

        clearInvocations(printMock);
        testObject.generateTestcases(null);
        verify(printMock).println("WARNING: no EXAM connected");
    }

    @Test
    @WithoutJenkins
    public void getApiVersion() {
        ApiVersion toTest = new ApiVersion();
        toTest.setMajor(2);
        toTest.setMinor(5);
        toTest.setFix(7);

        ApiVersion apiVersion = null;
        try {
            apiVersion = testObject.getApiVersion();
        } catch (IOException | InterruptedException e) {
            assertTrue("Exception was thrown: " + e.toString(), false);
        }
        assertEquals(toTest.getFix(), apiVersion.getFix());
        assertEquals(toTest.getMinor(), apiVersion.getMinor());
        assertEquals(toTest.getMajor(), apiVersion.getMajor());
    }

    @Test
    @WithoutJenkins
    public void getTCGVersion() {
        ApiVersion apiVersion = null;

        try {
            apiVersion = testObject.getTCGVersion();
        } catch (IOException | InterruptedException e) {
            assertTrue("Exception was thrown: " + e.toString(), false);
        }
        assertNotNull(apiVersion);
        assertEquals(2, apiVersion.getMajor());
        assertEquals(0, apiVersion.getMinor());
        assertEquals(3, apiVersion.getFix());
    }

    // Note:
    // This Test has to run after the "startTestrun" Test because it manipulates the mocked Response
    @Test(expected = Exception.class)
    @WithoutJenkins
    public void handleResponseError() throws Exception {
        // change Response that handleResponseError gets an Error
        dispatcher.removeResponse("/examRest/testrun/start");
        dispatcher.setResponse("/examRest/testrun/start",
                new MockResponse().setResponseCode(204).addHeader("Content-Type", "application/json; charset=utf-8")
                        .addHeader("Cache-Control", "no-cache").setBody("{}"));

        // start Testrun with error Response => handleResponseError gets called
        testObject.startTestrun(null);
    }

    @Test
    @WithoutJenkins
    public void handleResponseErrorException() throws Exception {
        Whitebox.invokeMethod(testObject, "handleResponseError", RemoteServiceResponse.class, null);
    }

    @Test
    @WithoutJenkins
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

        JSONAssert.assertJsonEquals(requestBody, filterConfigToTest);
        assertEquals(requestRoute, "/examRest/testrun/setFilter");
    }

    @Test
    @WithoutJenkins
    public void convert() throws Exception {
        String path = "/examRest/testrun/convertToJunit/";
        String testReportProject = "testProject";
        Whitebox.invokeMethod(testObject, "convert", testReportProject);

        RecordedRequest request = server.takeRequest();
        String requestBody = request.getBody().readUtf8();
        String requestRoute = request.getPath();

        assertEquals(path + testReportProject, requestRoute);
        assertEquals("", requestBody);
    }

    private static GenerateConfiguration getGenerateConfiguration() {
        GenerateConfiguration config = new GenerateConfiguration();
        config.setElement("testElement");
        config.setOverwriteDescriptionSource(true);
        config.setDescriptionSource("DESCRIPTION");
        config.setDocumentInReport(false);
        config.setErrorHandling("errorHandling");
        config.setOverwriteFrameSteps(true);
        config.setFrameFunctions(Collections.singletonList("ACTION_BEFORE"));
        config.setOverwriteMappingList(true);
        config.setMappingList(Collections.singletonList("test"));
        config.setVariant("variant");
        config.setSetStates(true);
        config.setStateForSuccess("test");
        config.setStateForFail("test2");
        return config;
    }

}
