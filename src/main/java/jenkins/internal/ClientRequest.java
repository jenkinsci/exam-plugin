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
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import hudson.AbortException;
import hudson.model.Executor;
import jenkins.internal.data.ApiVersion;
import jenkins.internal.data.ExamStatus;
import jenkins.internal.data.FilterConfiguration;
import jenkins.internal.data.TestConfiguration;
import jenkins.internal.data.TestrunFilter;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.PrintStream;

/**
 * REST Api calls to EXAM
 */
public class ClientRequest {
    
    private final static int OK = Response.ok().build().getStatus();
    long waitTime = 1000;
    private String baseUrl = "";
    private PrintStream logger;
    private Client client = null;
    
    /**
     * Constructor for REST Api calls to EXAM
     *
     * @param logger  PrintStream
     * @param baseUrl Url
     */
    public ClientRequest(PrintStream logger, String baseUrl) {
        this.baseUrl = baseUrl;
        this.logger = logger;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
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
     *
     * @throws AbortException AbortException
     */
    public ExamStatus getStatus() throws AbortException {
        if (client == null) {
            logger.println("WARNING: no EXAM connected");
            return null;
        }
        
        WebResource service = client.resource(baseUrl + "/testrun/status");
        ClientResponse response = service.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
        
        handleResponseError(response);
        
        return response.getEntity(ExamStatus.class);
    }
    
    /**
     * Request the Api Version from EXAM Client
     *
     * @return ApiVersion
     *
     * @throws AbortException AbortException
     */
    public ApiVersion getApiVersion() throws AbortException {
        if (client == null) {
            logger.println("WARNING: no EXAM connected");
            return null;
        }
        WebResource service = client.resource(baseUrl + "/workspace/apiVersion");
        ClientResponse response = service.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
        
        handleResponseError(response);
        
        return response.getEntity(ApiVersion.class);
    }
    
    /**
     * Checks, if the EXAM Client ist responding
     *
     * @return true, is available
     */
    public boolean isApiAvailable() {
        boolean clientCreated = false;
        boolean isAvailable = true;
        if (client == null) {
            clientCreated = true;
            createClient();
        }
        try {
            getStatus();
        } catch (Exception e) {
            isAvailable = false;
        }
        
        if (clientCreated) {
            destroyClient();
        }
        return isAvailable;
    }
    
    /**
     * Setting the  EXAM Client
     *
     * @param filterConfig FilterConfiguration
     *
     * @throws AbortException AbortException
     */
    public void setTestrunFilter(FilterConfiguration filterConfig) throws AbortException {
        if (client == null) {
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
        WebResource service = client.resource(baseUrl + "/testrun/setFilter");
        
        ClientResponse response = service.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, filterConfig);
        
        handleResponseError(response);
    }
    
    /**
     * Request the Api Version from EXAM Client
     *
     * @param reportProject String
     *
     * @throws AbortException AbortException
     */
    public void convert(String reportProject) throws AbortException {
        if (client == null) {
            logger.println("WARNING: no EXAM connected");
            return;
        }
        logger.println("convert to junit");
        WebResource service = client.resource(baseUrl + "/testrun/convertToJunit/" + reportProject);
        
        ClientResponse response = service.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
        
        handleResponseError(response);
    }
    
    /**
     * Configure and start testrun at EXAM Client
     *
     * @param testConfig TestConfiguration
     *
     * @throws AbortException AbortException
     */
    public void startTestrun(TestConfiguration testConfig) throws AbortException {
        if (client == null) {
            logger.println("WARNING: no EXAM connected");
            return;
        }
        logger.println("starting testrun");
        WebResource service = client.resource(baseUrl + "/testrun/start");
        
        ClientResponse response = service.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, testConfig);
        
        handleResponseError(response);
    }
    
    private void handleResponseError(ClientResponse response) throws AbortException {
        if (response.getStatus() != OK) {
            String errorMessage = "Failed : HTTP error code : " + response.getStatus();
            try {
                String entity = response.getEntity(String.class);
                errorMessage += "\n" + entity;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            logger.println("ERROR: " + errorMessage);
            throw new AbortException(errorMessage);
        }
    }
    
    /**
     * stops a testrun
     *
     * @throws AbortException AbortException
     */
    public void stopTestrun() throws AbortException {
        if (client == null) {
            logger.println("WARNING: no EXAM connected");
            return;
        }
        logger.println("stopping testrun");
        WebResource service = client.resource(baseUrl + "/testrun/stop?timeout=300");
        
        ClientResponse response = service.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class);
        
        handleResponseError(response);
    }
    
    /**
     * Deletes the project configuration at EXAM and deletes the corresponding report and pcode folders
     *
     * @param projectName String
     *
     * @throws AbortException AbortException
     */
    public void clearWorkspace(String projectName) throws AbortException {
        if (client == null) {
            logger.println("WARNING: no EXAM connected");
            return;
        }
        WebResource service;
        if (projectName == null || projectName.isEmpty()) {
            logger.println("deleting all projects and pcode from EXAM workspace");
            service = client.resource(baseUrl + "/workspace/delete");
        } else {
            logger.println("deleting project and pcode for project \"" + projectName + "\" from EXAM workspace");
            service = client.resource(baseUrl + "/workspace/delete?projectName=" + projectName);
        }
        
        ClientResponse response = service.get(ClientResponse.class);
        
        handleResponseError(response);
    }
    
    /**
     * make EXAM shutting down
     */
    public void shutdown() {
        if (client == null) {
            logger.println("WARNING: no EXAM connected");
            return;
        }
        logger.println("closing EXAM");
        client.resource(baseUrl + "/workspace/shutdown");
        
    }
    
    /**
     * try to connect to EXAM REST Server within a timeout
     *
     * @param timeout millis
     *
     * @return true, if connected
     */
    public boolean connectClient(int timeout) {
        logger.println("connecting to EXAM");
        createClient();
        
        long timeoutTime = System.currentTimeMillis() + timeout;
        while (timeoutTime > System.currentTimeMillis()) {
            if (isApiAvailable()) {
                return true;
            }
        }
        logger.println("ERROR: EXAM does not answer in " + timeout / 1000 + "s");
        return false;
    }
    
    private void createClient() {
        if (client == null) {
            ClientConfig clientConfig = new DefaultClientConfig();
            clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
            client = Client.create(clientConfig);
        } else {
            logger.println("Client already connected");
        }
    }
    
    private void destroyClient() {
        if (client != null) {
            client.destroy();
        }
        client = null;
    }
    
    /**
     * Try to disconnect from EXAM Client
     *
     * @param timeout millis
     */
    public void disconnectClient(int timeout) {
        if (client == null) {
            logger.println("Client is not connected");
        } else {
            logger.println("disconnect from EXAM");
            
            WebResource service = client.resource(baseUrl + "/workspace/shutdown");
            try {
                service.get(ClientResponse.class);
            } catch (Exception e) {
                logger.println(e.getMessage());
            }
            
            long timeoutTime = System.currentTimeMillis() + timeout;
            boolean shutdownOK = false;
            while (timeoutTime > System.currentTimeMillis()) {
                if (!isApiAvailable()) {
                    shutdownOK = true;
                    break;
                }
            }
            if (!shutdownOK) {
                logger.println("ERROR: EXAM does not shutdown in " + timeout + "ms");
            }
            
            destroyClient();
        }
    }
    
    /**
     * Waits for the EXAM Testrun ends
     *
     * @param executor Executor
     * @param wait     time in s
     *
     * @throws AbortException AbortException
     */
    public void waitForTestrunEnds(Executor executor, int wait) throws AbortException {
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
                    logger.println("No Testrun detected");
                    break;
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
     *
     * @throws AbortException AbortException
     */
    public void waitForExamIdle(Executor executor, int wait) throws AbortException {
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
     *
     * @throws AbortException AbortException
     */
    public void waitForExportPDFReportJob(Executor executor, int wait) throws AbortException {
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
