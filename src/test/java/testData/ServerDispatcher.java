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
package testData;

import jenkins.internal.data.ApiVersion;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import java.util.HashMap;
import java.util.Map;

public class ServerDispatcher extends Dispatcher {

    private Map<String, MockResponse> mResponse;

    public ServerDispatcher() {
        mResponse = new HashMap<>();
        setDefaults();
    }

    public void setDefaults() {
        clearAllResponse();

        setResponse("/examRest/testrun/status",
                new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json; charset=utf-8")
                        .addHeader("Cache-Control", "no-cache")
                        .setBody("{\"jobName\":\"myTestJob\",\"jobRunning\":\"true\",\"testRunState\":-1}"));

        setResponse("/examRest/testrun/start",
                new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json; charset=utf-8")
                        .addHeader("Cache-Control", "no-cache").setBody("{}"));

        setResponse("/examRest/testrun/stop",
                new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json; charset=utf-8")
                        .addHeader("Cache-Control", "no-cache").setBody("{}"));

        setResponse("/examRest/workspace/apiVersion",
                new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json; charset=utf-8")
                        .addHeader("Cache-Control", "no-cache").setBody(this.getApiVersionResponseString()));

        setResponse("/examRest/testrun/setFilter",
                new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json; charset=utf-8")
                        .addHeader("Cache-Control", "no-cache").setBody("{}"));

        setResponse("/examRest/testrun/convertToJunit/testProject",
                new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json; charset=utf-8")
                        .addHeader("Cache-Control", "no-cache"));

        setResponse("/examRest/workspace/createProject",
                new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json; charset=utf-8")
                        .addHeader("Cache-Control", "no-cache"));

        setResponse("/examRest/groovy/executeGroovyScript",
                new MockResponse().setResponseCode(200).addHeader("Content-Type", "application/json; charset=utf-8")
                        .addHeader("Cache-Control", "no-cache"));

        setResponse("/examRest/workspace/delete", new MockResponse().setResponseCode(200));
        setResponse("/examRest/workspace/shutdown", new MockResponse().setResponseCode(200));
    }

    public void setResponse(String path, MockResponse response) {
        mResponse.put(path, response);
    }

    public void removeResponse(String path) {
        if (mResponse.containsKey(path)) {
            mResponse.remove(path);
        }
    }

    public void clearAllResponse() {
        mResponse.clear();
    }

    // Helper Method for ApiVersion Response
    private String getApiVersionResponseString() {
        ApiVersion apiVersion = new ApiVersion();
        apiVersion.setMajor(2);
        apiVersion.setMinor(5);
        apiVersion.setFix(7);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            String json = ow.writeValueAsString(apiVersion);
            return json;
        } catch (Exception e) {
            return "{ major: \"2\", minor: \"5\", fix: \"7\" }";
        }
    }

    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        String path = request.getPath();
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }
        if (mResponse.containsKey(path)) {
            return mResponse.get(path);
        }
        return new MockResponse().setResponseCode(404);
    }
}
