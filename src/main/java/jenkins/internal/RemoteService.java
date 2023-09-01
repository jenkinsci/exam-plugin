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

import hudson.Launcher;
import hudson.remoting.VirtualChannel;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.JerseyWebTarget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
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
     * @param clazz    Class
     * @return RemoteServiceResponse
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    @Nullable
    public static RemoteServiceResponse get(@Nonnull Launcher launcher, int apiPort, @Nonnull String postUrl,
                                            Class clazz) throws IOException, InterruptedException {
        VirtualChannel channel = launcher.getChannel();
        if (channel == null) {
            return null;
        }

        return channel.call(new ExamMasterToSlaveCallable<RemoteServiceResponse, IOException>() {
            private static final long serialVersionUID = -7246380216097840885L;

            /**
             * @return ip address of slave
             */
            public RemoteServiceResponse call() {
                String url = String.format(BASEURL, apiPort) + postUrl;
                JerseyWebTarget service = createClient(url);
                Response clientResponse = service.request(MediaType.APPLICATION_JSON).get();
                RemoteServiceResponse response = getRemoteServiceResponse(clientResponse, clazz);
                destroyClient();
                return response;
            }
        });
    }

    /**
     * @param launcher Launcher
     * @param apiPort  port of REST-API
     * @param postUrl  postfix for api url
     * @return RemoteServiceResponse
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    @Nullable
    public static RemoteServiceResponse delete(@Nonnull Launcher launcher, int apiPort, @Nonnull String postUrl)
            throws IOException, InterruptedException {
        VirtualChannel channel = launcher.getChannel();
        if (channel == null) {
            return null;
        }

        return channel.call(new ExamMasterToSlaveCallable<RemoteServiceResponse, IOException>() {
            private static final long serialVersionUID = -7246380216097840885L;

            /**
             * @return ip address of slave
             */
            public RemoteServiceResponse call() {
                String url = String.format(BASEURL, apiPort) + postUrl;
                JerseyWebTarget service = createClient(url);
                Response clientResponse = service.request(MediaType.APPLICATION_JSON).delete();
                RemoteServiceResponse response = getRemoteServiceResponse(clientResponse, null);
                destroyClient();
                return response;
            }
        });
    }

    /**
     * @param launcher Launcher
     * @param apiPort  port of REST-API
     * @param postUrl  postfix for api url
     * @return RemoteServiceResponse
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    @Nullable
    public static RemoteServiceResponse put(@Nonnull Launcher launcher, int apiPort, @Nonnull String postUrl)
            throws IOException, InterruptedException {
        VirtualChannel channel = launcher.getChannel();
        if (channel == null) {
            return null;
        }

        return channel.call(new ExamMasterToSlaveCallable<RemoteServiceResponse, IOException>() {
            private static final long serialVersionUID = -7246380216097840885L;

            /**
             * @return ip address of slave
             */
            public RemoteServiceResponse call() {
                String url = String.format(BASEURL, apiPort) + postUrl;
                JerseyWebTarget service = createClient(url);
                Response clientResponse = service.request(MediaType.APPLICATION_JSON).put(Entity.text(""));
                RemoteServiceResponse response = getRemoteServiceResponse(clientResponse, null);
                destroyClient();
                return response;
            }
        });
    }

    /**
     * @param launcher Launcher
     * @param apiPort  port of REST-API
     * @param postUrl  postfix for api url
     * @param clazz    Class
     * @return RemoteServiceResponse
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    @Nullable
    public static RemoteServiceResponse getJSON(@Nonnull Launcher launcher, int apiPort, @Nonnull String postUrl,
                                                Class clazz) throws IOException, InterruptedException {
        VirtualChannel channel = launcher.getChannel();
        if (channel == null) {
            return null;
        }
        return channel.call(new ExamMasterToSlaveCallable<RemoteServiceResponse, IOException>() {
            private static final long serialVersionUID = -7246380216097840887L;

            /**
             * @return ip address of slave
             */
            public RemoteServiceResponse call() {
                String url = String.format(BASEURL, apiPort) + postUrl;
                JerseyWebTarget service = createClient(url);
                Response clientResponse = service.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .get();
                RemoteServiceResponse response = getRemoteServiceResponse(clientResponse, clazz);
                destroyClient();
                return response;

            }
        });
    }

    /**
     * @param launcher   Launcher
     * @param apiPort    port of REST-API
     * @param postUrl    postfix for api url
     * @param clazz      Class
     * @param postObject object to post
     * @return RemoteServiceResponse
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    @Nullable
    public static RemoteServiceResponse post(@Nonnull Launcher launcher, int apiPort, @Nonnull String postUrl,
                                             Object postObject, Class clazz) throws IOException, InterruptedException {
        VirtualChannel channel = launcher.getChannel();
        if (channel == null) {
            return null;
        }
        return channel.call(new ExamMasterToSlaveCallable<RemoteServiceResponse, IOException>() {
            private static final long serialVersionUID = 1083377786092322973L;

            /**
             * @return ip address of slave
             */
            public RemoteServiceResponse call() {
                String url = String.format(BASEURL, apiPort) + postUrl;
                JerseyWebTarget service = createClient(url);
                Response clientResponse = null;

                if (postObject == null) {
                    clientResponse = service.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                            .post(Entity.text(""));
                } else {
                    if (postObject instanceof String) {
                        String json = (String) postObject;
                        clientResponse = service.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                                .post(Entity.entity(json, MediaType.APPLICATION_JSON));
                    } else {
                        clientResponse = service.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                                .post(Entity.json(postObject));
                    }
                }
                RemoteServiceResponse response = getRemoteServiceResponse(clientResponse, clazz);
                destroyClient();
                return response;
            }
        });
    }
}
