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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.AbortException;
import hudson.Launcher;
import hudson.model.Executor;
import jakarta.ws.rs.core.Response;
import jenkins.internal.data.*;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.PrintStream;

/**
 * REST Api calls to EXAM
 */
public class ClientRequest {

    private final static int OK = Response.ok().build().getStatus();
    long waitTime = 1000;
    private int apiPort = 8085;
    private PrintStream logger;
    private boolean clientConnected = false;
    private Launcher launcher = null;

    /**
     * Constructor for REST Api calls to EXAM
     *
     * @param logger   PrintStream
     * @param apiPort  Port
     * @param launcher Launcher
     */
    public ClientRequest(PrintStream logger, int apiPort, Launcher launcher) {
        this.apiPort = apiPort;
        this.logger = logger;
        this.launcher = launcher;
    }

    public boolean isClientConnected() {
        return clientConnected;
    }

    public int getApiPort() {
        return apiPort;
    }

    public void setApiPort(int apiPort) {
        this.apiPort = apiPort;
    }

    public PrintStream getLogger() {
        return logger;
    }

    public void setLogger(PrintStream logger) {
        this.logger = logger;
    }

    /**
     * Request the job xxecution status from EXAM Client
     *
     * @return ExamStatus
     * @throws InterruptedException InterruptedException
     * @throws IOException          IOException
     */
    @Nullable
    public ExamStatus getStatus() throws IOException, InterruptedException {
        RemoteServiceResponse response = RemoteService.getJSON(launcher, apiPort, "/testrun/status", ExamStatus.class);
        handleResponseError(response);

        return (response == null) ? null : (ExamStatus) response.getEntity();
    }

    /**
     * Request the Api Version from EXAM Client
     *
     * @return ApiVersion
     * @throws InterruptedException InterruptedException
     * @throws IOException          IOException
     */
    @Nullable
    public ApiVersion getApiVersion() throws IOException, InterruptedException {
        if (!clientConnected) {
            logger.println("WARNING: no EXAM connected");
            return null;
        }
        RemoteServiceResponse response = RemoteService.getJSON(launcher, apiPort, "/workspace/apiVersion", ApiVersion.class);
        handleResponseError(response);

        return (response == null) ? null : (ApiVersion) response.getEntity();
    }

    /**
     * Checks, if the EXAM Client ist responding
     *
     * @return true, is available
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public boolean isApiAvailable() throws IOException, InterruptedException {
        boolean isAvailable = true;
        try {
            getStatus();
            clientConnected = true;
            Compatibility.setClientApiVersion(getApiVersion());
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                throw e;
            }
            isAvailable = false;
            clientConnected = false;
        }
        return isAvailable;
    }

    /**
     * Creates a Exam Project on the Client.
     *
     * @param modelConfiguration ModelConfiguration
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public void createExamProject(ModelConfiguration modelConfiguration) throws IOException, InterruptedException {
        if (!clientConnected) {
            logger.println("WARNING: no EXAM connected");
            return;
        }
        logger.println("creating Exam Project");

        RemoteServiceResponse response = RemoteService.post(launcher, apiPort, "/workspace/createProject", modelConfiguration, null);
        handleResponseError(response);
    }

    /**
     * Setting the  EXAM Client
     *
     * @param filterConfig FilterConfiguration
     * @throws AbortException       AbortException
     * @throws InterruptedException InterruptedException
     * @throws IOException          IOException
     */
    public void setTestrunFilter(FilterConfiguration filterConfig) throws IOException, AbortException, InterruptedException {
        if (!clientConnected) {
            logger.println("WARNING: no EXAM connected");
            return;
        }
        logger.println("setting testrun filter");
        int i = 0;
        for (TestrunFilter filter : filterConfig.getTestrunFilter()) {
            i++;
            logger.println(i + ") name: " + filter.getName());
            logger.println(i + ") regEx: " + filter.getValue());
            logger.println(i + ") admin: " + filter.isAdminCases());
            logger.println(i + ") activ: " + filter.isActivateTestcases());
            logger.println();
        }
        RemoteServiceResponse response = RemoteService.post(launcher, apiPort, "/testrun/setFilter", filterConfig, null);
        handleResponseError(response);
    }

    /**
     * Request the Api Version from EXAM Client
     *
     * @param reportProject String
     * @throws InterruptedException InterruptedException
     * @throws IOException          IOException
     */
    public void convert(String reportProject) throws IOException, InterruptedException {
        if (!clientConnected) {
            logger.println("WARNING: no EXAM connected");
            return;
        }
        logger.println("convert to junit");
        RemoteServiceResponse response = RemoteService.getJSON(launcher, apiPort, "/testrun/convertToJunit/" + reportProject, null);
        handleResponseError(response);
    }

    /**
     * Configure and start testrun at EXAM Client
     *
     * @param testConfig TestConfiguration
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public void startTestrun(TestConfiguration testConfig) throws IOException, InterruptedException {
        if (!clientConnected) {
            logger.println("WARNING: no EXAM connected");
            return;
        }
        logger.println("starting testrun");
        RemoteServiceResponse response = RemoteService.post(launcher, apiPort, "/testrun/start", testConfig, null);
        handleResponseError(response);
    }

    /**
     * Executes a Groovy Script at the Exam Client.
     *
     * @param groovyConfiguration GroovyConfiguration
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public void executeGroovyScript(GroovyConfiguration groovyConfiguration) throws IOException, InterruptedException {
        if (!clientConnected) {
            logger.println("WARNING: no EXAM connected");
            return;
        }
        logger.println("executing Groovy Script");
        RemoteServiceResponse response = RemoteService.post(launcher, apiPort, "/groovy/executeGroovyScript", groovyConfiguration, null);
        handleResponseError(response);
    }

    /**
     * Generates Testcases with the TCG at the Exam Client.
     *
     * @param generateConfiguration LegacyGenerateConfiguration
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public void generateTestcases(LegacyGenerateConfiguration generateConfiguration) throws IOException, InterruptedException {
        if (!clientConnected) {
            logger.println("WARNING: no EXAM connected");
            return;
        }
        logger.println("generating Testcases");
        ObjectMapper mapper = new ObjectMapper();

        String config = mapper.writeValueAsString(generateConfiguration);
        RemoteServiceResponse response = RemoteService.post(launcher, apiPort, "/TCG/generate", config, null);
        handleResponseError(response);
    }

    /**
     * Generates Testcases with the TCG at the Exam Client.
     *
     * @param generateConfiguration GenerateConfiguration
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public void generateTestcasesPost203(GenerateConfiguration generateConfiguration) throws IOException, InterruptedException {
        if (!clientConnected) {
            logger.println("WARNING: no EXAM connected");
            return;
        }
        logger.println("generating Testcases");
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        String config = mapper.writeValueAsString(generateConfiguration);
        RemoteServiceResponse response = RemoteService.post(launcher, apiPort, "/TCG/generate", config, TCGResult.class);
        handleResponseError(response);

        TCGResult res = (TCGResult) response.getEntity();
        logger.println("INFO: " + res.getMessage());
    }

    /**
     * Gets the API Version of the TCG API.
     * If the request fails, which is supposed to happen pre version 2.0.3 we return version 2.0.2 so the rest uses the old api
     *
     * @return ApiVersion
     * @throws InterruptedException
     * @throws IOException
     */
    public ApiVersion getTCGVersion() throws InterruptedException, IOException {
        if (!clientConnected) {
            logger.println("WARNING: no EXAM connected");
            return null;
        }
        logger.println("getting TCG Api Version.");
        RemoteServiceResponse response = RemoteService.getJSON(launcher, apiPort, "/TCG/apiVersion", ApiVersion.class);
        if (response != null && response.getStatus() == 404) {
            return new ApiVersion(2, 0, 2);
        }
        handleResponseError(response);
        return (response == null) ? null : (ApiVersion) response.getEntity();
    }

    /**
     * Handles the response Error
     *
     * @param response response
     * @throws AbortException exception
     */
    private void handleResponseError(@Nullable RemoteServiceResponse response) throws AbortException {
        if (response == null) {
            return;
        }
        if (response.getStatus() != OK) {
            String errorMessage = "Failed : HTTP error code : " + response.getStatus();
            String entity = response.getEntityString();
            errorMessage += "\n" + entity;
            logger.println("ERROR: " + errorMessage);
            throw new AbortException(errorMessage);
        }
    }

    /**
     * stops a testrun
     *
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public void stopTestrun() throws IOException, InterruptedException {
        if (!clientConnected) {
            logger.println("WARNING: no EXAM connected");
            return;
        }
        logger.println("stopping testrun");
        RemoteServiceResponse response;
        if (Compatibility.isVersionHigher200()) {
            response = RemoteService.put(launcher, apiPort, "/testrun/stop?timeout=300");
        } else {
            response = RemoteService.post(launcher, apiPort, "/testrun/stop?timeout=300", null, null);
        }
        handleResponseError(response);
    }

    /**
     * Deletes the project configuration at EXAM and deletes the corresponding report and pcode folders
     *
     * @param projectName String
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public void clearWorkspace(String projectName) throws IOException, InterruptedException {
        if (!clientConnected) {
            logger.println("WARNING: no EXAM connected");
            return;
        }
        String postUrl;
        if (projectName == null || projectName.isEmpty()) {
            logger.println("deleting all projects and pcode from EXAM workspace");
            postUrl = "/workspace/delete";
        } else {
            logger.println("deleting project and pcode for project \"" + projectName + "\" from EXAM workspace");
            postUrl = "/workspace/delete?projectName=" + projectName;
        }
        RemoteServiceResponse response;
        if (Compatibility.isVersionHigher200()) {
            response = RemoteService.delete(launcher, apiPort, postUrl);
        } else {
            response = RemoteService.get(launcher, apiPort, postUrl, null);
        }
        handleResponseError(response);
    }

    /**
     * try to connect to EXAM REST Server within a timeout
     *
     * @param executor Executor
     * @param timeout  millis
     * @return true, if connected
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public boolean connectClient(Executor executor, int timeout) throws IOException, InterruptedException {
        logger.println("connecting to EXAM");

        long timeoutTime = System.currentTimeMillis() + timeout * 1000;
        while (timeoutTime > System.currentTimeMillis()) {
            if (executor.isInterrupted()) {
                logger.println("Job interrupted");
                return false;
            }
            if (isApiAvailable()) {
                clientConnected = true;
                return true;
            }
        }
        logger.println("ERROR: EXAM does not answer in " + timeout + "s");
        return false;
    }

    /**
     * Try to disconnect from EXAM Client
     *
     * @param executor Executor
     * @param timeout  millis
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public void disconnectClient(Executor executor, int timeout) throws IOException, InterruptedException {
        if (!clientConnected) {
            logger.println("Client is not connected");
        } else {
            logger.println("disconnect from EXAM");

            try {
                RemoteService.get(launcher, apiPort, "/workspace/shutdown", null);
            } catch (Exception e) {
                logger.println(e.getMessage());
            }

            long timeoutTime = System.currentTimeMillis() + timeout * 1000;
            boolean shutdownOK = false;
            while (timeoutTime > System.currentTimeMillis()) {
                if (executor.isInterrupted()) {
                    logger.println("Job interrupted");
                    return;
                }
                if (!isApiAvailable()) {
                    shutdownOK = true;
                    break;
                }
            }
            if (!shutdownOK) {
                logger.println("ERROR: EXAM does not shutdown in " + timeout + "s");
            }

            clientConnected = false;
        }
    }

    /**
     * Waits for the EXAM Testrun ends
     *
     * @param executor Executor
     * @param wait     time in s
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public void waitForTestrunEnds(Executor executor, int wait) throws IOException, InterruptedException {
        boolean testDetected = false;
        int breakAfter = wait;
        while (true) {
            if (executor.isInterrupted()) {
                this.stopTestrun();
                return;
            }
            ExamStatus status = this.getStatus();
            if (!testDetected) {
                breakAfter--;
                testDetected = "TestRun".equalsIgnoreCase(status.getJobName());
                if (!testDetected && breakAfter <= 0) {
                    logger.printf("No Testrun detected after %sms", breakAfter);
                    break;
                }
                if (testDetected) {
                    logger.println("Testrun detected");
                }
            } else {
                if (!status.getJobRunning()) {
                    break;
                }
            }
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                // nothing to do
            }
        }
    }

    /**
     * Waits until EXAM is idle
     *
     * @param executor Executor
     * @param wait     time in s
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public void waitForExamIdle(Executor executor, int wait) throws IOException, InterruptedException {
        int breakAfter = wait;
        while (true) {
            if (executor.isInterrupted()) {
                return;
            }
            breakAfter--;
            ExamStatus status = this.getStatus();
            if (!status.getJobRunning()) {
                break;
            }
            if (breakAfter <= 0) {
                logger.println("EXAM is not idle after " + wait + "s");
                break;
            }
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                // nothing to do
            }
        }
    }

    /**
     * Waits until EXAM is idle
     *
     * @param executor Executor
     * @param wait     time in s
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public void waitForExportPDFReportJob(Executor executor, int wait) throws IOException, InterruptedException {
        int breakAfter = wait;
        while (true) {
            if (executor.isInterrupted()) {
                return;
            }
            breakAfter--;
            ExamStatus status = this.getStatus();
            if (!status.getJobRunning() || !"Export Reports to PDF.".equalsIgnoreCase(status.getJobName())) {
                break;
            }
            if (breakAfter <= 0) {
                logger.println("EXAM is not idle after " + wait + "s");
                break;
            }
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                // nothing to do
            }
        }
    }
}
