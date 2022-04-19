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

import jakarta.ws.rs.core.Response;
import jenkins.security.MasterToSlaveCallable;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;


/**
 * Calls function on Jenkins Agent {@link MasterToSlaveCallable}
 *
 * @param <V> object to return
 * @param <T> extends Throwable
 */
public abstract class ExamMasterToSlaveCallable<V, T extends Throwable> extends MasterToSlaveCallable<V, T> {
    private static final long serialVersionUID = -6079064212915937266L;
    protected JerseyClient client;
    private final static int OK = Response.ok().build().getStatus();
    
    protected JerseyWebTarget createClient(String url) {
        client = JerseyClientBuilder.createClient();
        return client.target(url);
    }
    
    protected void destroyClient() {
        client.close();
        client = null;
    }
    
    protected RemoteServiceResponse getRemoteServiceResponse(Response clientResponse, Class clazz) {
        Object entity = null;
        String entityString = "";
        if (clientResponse.getStatus() != OK) {
            entityString = clientResponse.readEntity(String.class);
        } else {
            if (clazz != null) {
                entity = clientResponse.readEntity(clazz);
            }
        }
        return new RemoteServiceResponse(clientResponse.getStatus(), entity, entityString);
    }
}
