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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import hudson.AbortException;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Executor;
import jenkins.internal.data.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.PrintStream;
import java.util.List;

public class ClientRequest {

    private String baseUrl = "";
    private PrintStream logger;
    private Client client = null;
    private Launcher launcher = null;

    private final static int OK = Response.ok().build().getStatus();

    public ClientRequest(Launcher launcher, PrintStream logger, String baseUrl) {
        this.launcher = launcher;
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

    public void setLauncher(Launcher launcher) {
        this.launcher = launcher;
    }

    public ExamStatus getStatus() {
        if(client == null){
            logger.println("WARNING: no EXAM connected");
            return null;
        }

        WebResource service = client.resource(baseUrl + "/testrun/status");
        ClientResponse response = service.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        handleResponseError(response);

        return response.getEntity(ExamStatus.class);
    }

    public ApiVersion getApiVersion() {
        if(client == null){
            logger.println("WARNING: no EXAM connected");
            return null;
        }
        WebResource service = client.resource(baseUrl + "/workspace/apiVersion");
        ClientResponse response = service.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        handleResponseError(response);

        return response.getEntity(ApiVersion.class);
    }

    public boolean isApiAvailable(){
        boolean clientCreated = false;
        boolean isAvailable = true;
        if(client == null){
            clientCreated = true;
            createClient();
        }
        try {
            getStatus();
        }catch (Exception e){
            isAvailable = false;
        }

        if(clientCreated){
            destroyClient();
        }
        return isAvailable;
    }

    public void setTestrunFilter(FilterConfiguration filterConfig) {
        if(client == null){
            logger.println("WARNING: no EXAM connected");
            return;
        }
        logger.println("setting testrun filter");
        int i = 0;
        for(TestrunFilter filter : filterConfig.getTestrunFilter()){
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

    public void convert(String reportProject)  {
        if(client == null){
            logger.println("WARNING: no EXAM connected");
            return;
        }
        logger.println("convert to junit");
        WebResource service = client.resource(baseUrl + "/testrun/convertToJunit/"+reportProject);

        ClientResponse response = service.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        handleResponseError(response);
    }

    public void startTestrun(TestConfiguration testConfig) {
        if(client == null){
            logger.println("WARNING: no EXAM connected");
            return;
        }
        logger.println("starting testrun");
        WebResource service = client.resource(baseUrl + "/testrun/start");

        ClientResponse response = service.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, testConfig);

        handleResponseError(response);
    }

    private void handleResponseError(ClientResponse response) {
        if (response.getStatus() != OK) {
            String errorMessage = "Failed : HTTP error code : " + response.getStatus();
            try{
                String entity = response.getEntity(String.class);
                if(entity instanceof String){
                    errorMessage += "\n" + entity;
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            throw new RuntimeException(errorMessage);

        }
    }

    public void stopTestrun(){
        if(client == null){
            logger.println("WARNING: no EXAM connected");
            return;
        }
        logger.println("stopping testrun");
        WebResource service = client.resource(baseUrl + "/testrun/stop?timeout=300");

        ClientResponse response = service.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                                         .post(ClientResponse.class);

        handleResponseError(response);
    }

    public void clearWorkspace(String projectName) {
        if(client == null){
            logger.println("WARNING: no EXAM connected");
            return;
        }
        WebResource service = null;
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

    public void shutdown() {
        if(client == null){
            logger.println("WARNING: no EXAM connected");
            return;
        }
        logger.println("closing EXAM");
        client.resource(baseUrl + "/workspace/shutdown");

    }

    public boolean connectClient(int timeout) {
        logger.println("connecting to EXAM");
        createClient();

        long timeoutTime = System.currentTimeMillis() + timeout;
        while (timeoutTime > System.currentTimeMillis()){
            if(isApiAvailable()){
                return true;
            }
        }
        logger.println("ERROR: EXAM does not answer in " + timeout / 1000 + "s");
        return false;
    }

    private void createClient(){
        if (client == null) {
            ClientConfig clientConfig = new DefaultClientConfig();
            clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
            client = Client.create(clientConfig);
        } else {
            logger.println("Client already connected");
        }
    }

    private void destroyClient(){
        if(client != null) {
            client.destroy();
        }
        client = null;
    }

    public void disconnectClient(int timeout) {
        if (client == null) {
            logger.println("Client is not connected");
            return;
        } else {
            logger.println("disconnect from EXAM");

            WebResource service = client.resource(baseUrl + "/workspace/shutdown");
            try {
                ClientResponse responseShutdown = service.get(ClientResponse.class);
            }catch (Exception e){
                logger.println(e.getMessage());
            }

            long timeoutTime = System.currentTimeMillis() + timeout;
            boolean shutdownOK = false;
            while (timeoutTime > System.currentTimeMillis()){
                if(!isApiAvailable()){
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

    public void waitForTestrunEnds(Executor executor){
        boolean testDetected = false;
        int breakAfter = 10;
        while(true){
            if(executor.isInterrupted()){
                this.stopTestrun();
                return;
            }
            ExamStatus status = this.getStatus();
            if(!testDetected) {
                breakAfter--;
                testDetected = "TestRun".equalsIgnoreCase(status.getJobName());
                if(!testDetected && breakAfter <= 0){
                    logger.println("No Testrun detected");
                    break;
                }
            }else{
                if(!status.getJobRunning()){
                    break;
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // nothing to do
            }
        }
    }
}
