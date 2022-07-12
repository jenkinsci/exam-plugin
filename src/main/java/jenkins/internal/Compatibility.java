package jenkins.internal;

import hudson.AbortException;
import hudson.model.TaskListener;
import jenkins.internal.data.ApiVersion;
import jenkins.internal.data.TestConfiguration;

import javax.annotation.Nonnull;
import java.io.IOException;

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
     * @param clientApiVersion
     */
    public static void setClientApiVersion(ApiVersion clientApiVersion) {
        Compatibility.clientApiVersion = clientApiVersion;
    }

    /**
     * Checks whether the REST-API of EXAM has the minimum required version
     *
     * @param taskListener       taskListener for logging
     * @param minRequiredVersion minimum Version required
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static void checkMinRestApiVersion(@Nonnull TaskListener taskListener, ApiVersion minRequiredVersion)
            throws IOException {
        checkMinRestApiVersion(taskListener, minRequiredVersion, "EXAM REST-API");
    }

    /**
     * Checks whether the REST-API of EXAM has the minimum required version
     *
     * @param taskListener       taskListener for logging
     * @param minRequiredVersion minimum Version required
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    private static void checkMinRestApiVersion(@Nonnull TaskListener taskListener, ApiVersion minRequiredVersion,
                                               String text) throws IOException {
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

    public static void checkTestConfig(TaskListener listener, TestConfiguration tc) throws IOException {

        String modelConfigUUID = tc.getModelProject().getModelConfigUUID();
        if(!Util.isUuidValid(modelConfigUUID)) {
            checkMinRestApiVersion(listener, new ApiVersion(2, 0, 0), "ModelConfig with name");
        }
    }

    public static boolean isVersionHigher200(){
        ApiVersion minApiVersion = new ApiVersion(2, 0, 0);
        return minApiVersion.compareTo(getClientApiVersion()) <= 0;
    }
}
