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
import hudson.Launcher;
import hudson.remoting.VirtualChannel;
import jenkins.security.MasterToSlaveCallable;

import javax.annotation.Nonnull;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.Serializable;

/**
 * Execute code on remote slave
 */
public class RemoteService implements Serializable {
    
    private final static String BASEURL = "http://localhost:%s/examRest";
    
    /**
     * @param launcher Launcher
     * @param client   Jersey client
     * @param apiPort  port of REST-API
     * @param postUrl  postfix for api url
     *
     * @return ClientResponse
     *
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static ClientResponse get(@Nonnull Launcher launcher, @Nonnull Client client, @Nonnull int apiPort,
            @Nonnull String postUrl) throws IOException, InterruptedException {
        VirtualChannel channel = launcher.getChannel();
        if (channel == null) {
            return null;
        }
        ClientResponse answer = channel.call(new MasterToSlaveCallable<ClientResponse, IOException>() {
            private static final long serialVersionUID = -7246380216097840886L;
            
            /**
             * @return ip address of slave
             *
             * @throws IOException IOException
             */
            public ClientResponse call() throws IOException {
                String url = String.format(BASEURL, apiPort) + postUrl;
                WebResource service = client.resource(url);
                
                return service.get(ClientResponse.class);
            }
        });
        
        return answer;
    }
    
    /**
     * @param launcher Launcher
     * @param client   Jersey client
     * @param apiPort  port of REST-API
     * @param postUrl  postfix for api url
     *
     * @return ClientResponse
     *
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static ClientResponse getJSON(@Nonnull Launcher launcher, @Nonnull Client client, @Nonnull int apiPort,
            @Nonnull String postUrl) throws IOException, InterruptedException {
        VirtualChannel channel = launcher.getChannel();
        if (channel == null) {
            return null;
        }
        ClientResponse answer = channel.call(new MasterToSlaveCallable<ClientResponse, IOException>() {
            private static final long serialVersionUID = -7246380216097840886L;
            
            /**
             * @return ip address of slave
             *
             * @throws IOException IOException
             */
            public ClientResponse call() throws IOException {
                String url = String.format(BASEURL, apiPort) + postUrl;
                WebResource service = client.resource(url);
                
                return service.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                        .get(ClientResponse.class);
            }
        });
        
        return answer;
    }
    
    /**
     * @param launcher Launcher
     * @param client   Jersey client
     * @param apiPort  port of REST-API
     * @param postUrl  postfix for api url
     *
     * @return ClientResponse
     *
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static ClientResponse post(@Nonnull Launcher launcher, @Nonnull Client client, @Nonnull int apiPort,
            @Nonnull String postUrl, Object postObject) throws IOException, InterruptedException {
        VirtualChannel channel = launcher.getChannel();
        if (channel == null) {
            return null;
        }
        ClientResponse answer = channel.call(new MasterToSlaveCallable<ClientResponse, IOException>() {
            private static final long serialVersionUID = 1083377786092322972L;
            
            /**
             * @return ip address of slave
             *
             * @throws IOException IOException
             */
            public ClientResponse call() throws IOException {
                String url = String.format(BASEURL, apiPort) + postUrl;
                WebResource service = client.resource(url);
                
                if (postObject == null) {
                    return service.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                            .post(ClientResponse.class);
                }
                return service.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                        .post(ClientResponse.class, postObject);
            }
        });
        
        return answer;
    }
}
