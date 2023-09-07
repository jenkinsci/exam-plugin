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

import hudson.AbortException;
import hudson.model.TaskListener;
import jenkins.internal.data.ApiVersion;
import jenkins.internal.data.TestConfiguration;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Compatibility to EXAM REST Api versions
 */
public class Compatibility {

    private static ApiVersion clientApiVersion;


    /**
     * returns the actual client api Version
     *
     * @return ApiVersion
     */
    public static ApiVersion getClientApiVersion() {
        return clientApiVersion;
    }

    /**
     * @param clientApiVersion ApiVersion
     */
    public static void setClientApiVersion(ApiVersion clientApiVersion) {
        Compatibility.clientApiVersion = clientApiVersion;
    }

    /**
     * Checks whether the REST-API of EXAM has the minimum required version
     *
     * @param taskListener       taskListener for logging
     * @param minRequiredVersion minimum Version required
     * @throws IOException IOException
     */
    public static void checkMinRestApiVersion(@Nonnull TaskListener taskListener, ApiVersion minRequiredVersion) throws IOException {
        checkMinRestApiVersion(taskListener, minRequiredVersion, "EXAM REST-API");
    }

    /**
     * Checks whether the REST-API of EXAM has the minimum required version
     *
     * @param taskListener       taskListener for logging
     * @param minRequiredVersion minimum Version required
     * @throws IOException IOException
     */
    private static void checkMinRestApiVersion(@Nonnull TaskListener taskListener, ApiVersion minRequiredVersion, String text) throws IOException {
        ApiVersion actualRestVersion = getClientApiVersion();
        String sApiVersion = (actualRestVersion == null) ? "unknown" : actualRestVersion.toString();
        taskListener.getLogger().println("EXAM api version: " + sApiVersion);
        if (actualRestVersion == null || minRequiredVersion.compareTo(actualRestVersion) > 0) {
            StringBuilder message = new StringBuilder("ERROR: ");
            message.append(text);
            message.append(" requires minimum version ");
            message.append(minRequiredVersion.toString());
            message.append(" but actual version is ");
            message.append(sApiVersion);
            throw new AbortException(message.toString());
        }
    }

    /**
     * Checks whether the TCG-REST-API of EXAM has the minimum required version
     *
     * @param taskListener       taskListener for logging
     * @param minRequiredVersion minimum Version required
     * @param tcgVersion         the ApiVersion of the TCG
     * @throws IOException IOException
     */
    public static boolean checkMinTCGVersion(@Nonnull TaskListener taskListener, ApiVersion minRequiredVersion, ApiVersion tcgVersion) {
        taskListener.getLogger().println("TCG api version: " + tcgVersion.toString());
        if (minRequiredVersion.compareTo(tcgVersion) > 0) {
            return false;
        }
        return true;
    }

    /**
     * Checks whether TestConfig ist compatible with the REST API version
     *
     * @throws IOException IOException
     */
    public static void checkTestConfig(TaskListener listener, TestConfiguration tc) throws IOException {
        if (tc.getModelProject() == null) {
            return;
        }

        String modelConfigUUID = tc.getModelProject().getModelConfigUUID();
        if (!Util.isUuidValid(modelConfigUUID)) {
            checkMinRestApiVersion(listener, new ApiVersion(2, 0, 0), "ModelConfig with name");
        }
    }

    /**
     * Returns true is the REST-API version is equal or higher 2.0.0
     */
    public static boolean isVersionHigher200() {
        ApiVersion minApiVersion = new ApiVersion(2, 0, 0);
        return minApiVersion.compareTo(getClientApiVersion()) <= 0;
    }
}
