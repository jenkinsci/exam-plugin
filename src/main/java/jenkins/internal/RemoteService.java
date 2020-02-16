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

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import hudson.Launcher;
import hudson.remoting.VirtualChannel;

import javax.annotation.Nonnull;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.Serializable;

/**
 * Execute code on remote slave
 */
public class RemoteService implements Serializable {
    
    private final static String BASEURL = "http://localhost:%s/examRest";
    private static final long serialVersionUID = 1984941733988614781L;
    
    /**
     * @param launcher Launcher
     * @param apiPort  port of REST-API
     * @param postUrl  postfix for api url
     *
     * @return ClientResponse
     *
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static RemoteServiceResponse get(@Nonnull Launcher launcher, @Nonnull int apiPort, @Nonnull String postUrl,
            Class clazz) throws IOException, InterruptedException {
        VirtualChannel channel = launcher.getChannel();
        if (channel == null) {
            return null;
        }
        
        RemoteServiceResponse answer = channel
                .call(new ExamMasterToSlaveCallable<RemoteServiceResponse, IOException>() {
                    private static final long serialVersionUID = -7246380216097840885L;
                    
                    /**
                     * @return ip address of slave
                     *
                     * @throws IOException IOException
                     */
                    public RemoteServiceResponse call() throws IOException {
                        String url = String.format(BASEURL, apiPort) + postUrl;
                        WebResource service = createClient(url);
                        ClientResponse clientResponse = service.get(ClientResponse.class);
                        RemoteServiceResponse response = getRemoteServiceResponse(clientResponse, clazz);
                        destroyClient();
                        return response;
                    }
                });
        
        return answer;
    }
    
    /**
     * @param launcher Launcher
     * @param apiPort  port of REST-API
     * @param postUrl  postfix for api url
     *
     * @return ClientResponse
     *
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static RemoteServiceResponse getJSON(@Nonnull Launcher launcher, @Nonnull int apiPort,
            @Nonnull String postUrl, Class clazz) throws IOException, InterruptedException {
        VirtualChannel channel = launcher.getChannel();
        if (channel == null) {
            return null;
        }
        RemoteServiceResponse answer = channel
                .call(new ExamMasterToSlaveCallable<RemoteServiceResponse, IOException>() {
                    private static final long serialVersionUID = -7246380216097840887L;
                    
                    /**
                     * @return ip address of slave
                     *
                     * @throws IOException IOException
                     */
                    public RemoteServiceResponse call() throws IOException {
                        String url = String.format(BASEURL, apiPort) + postUrl;
                        WebResource service = createClient(url);
                        ClientResponse clientResponse = service.accept(MediaType.APPLICATION_JSON)
                                .type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
                        RemoteServiceResponse response = getRemoteServiceResponse(clientResponse, clazz);
                        destroyClient();
                        return response;
                        
                    }
                });
        
        return answer;
    }
    
    /**
     * @param launcher Launcher
     * @param apiPort  port of REST-API
     * @param postUrl  postfix for api url
     *
     * @return ClientResponse
     *
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static RemoteServiceResponse post(@Nonnull Launcher launcher, @Nonnull int apiPort,
            @Nonnull String postUrl, Object postObject, Class clazz) throws IOException, InterruptedException {
        VirtualChannel channel = launcher.getChannel();
        if (channel == null) {
            return null;
        }
        RemoteServiceResponse answer = channel
                .call(new ExamMasterToSlaveCallable<RemoteServiceResponse, IOException>() {
                    private static final long serialVersionUID = 1083377786092322973L;
                    
                    /**
                     * @return ip address of slave
                     *
                     * @throws IOException IOException
                     */
                    public RemoteServiceResponse call() throws IOException {
                        String url = String.format(BASEURL, apiPort) + postUrl;
                        WebResource service = createClient(url);
                        ClientResponse clientResponse = null;
                        
                        if (postObject == null) {
                            clientResponse = service.accept(MediaType.APPLICATION_JSON)
                                    .type(MediaType.APPLICATION_JSON).post(ClientResponse.class);
                        } else {
                            clientResponse = service.accept(MediaType.APPLICATION_JSON)
                                    .type(MediaType.APPLICATION_JSON).post(ClientResponse.class, postObject);
                        }
                        RemoteServiceResponse response = getRemoteServiceResponse(clientResponse, clazz);
                        destroyClient();
                        return response;
                    }
                });
        
        return answer;
    }
}
