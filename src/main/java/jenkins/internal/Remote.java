package jenkins.internal;

import hudson.Launcher;
import jenkins.security.MasterToSlaveCallable;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;

/**
 * Execute code on remote slave
 */
public class Remote implements Serializable {

    /**
     * @param launcher
     *
     * @return ip address of slave
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public static String getIP(Launcher launcher) throws IOException, InterruptedException {

        return launcher.getChannel().call(new MasterToSlaveCallable<String,IOException>() {
            private static final long serialVersionUID = -4742095943185092470L;

            /**
             * @return ip address of slave
             *
             * @throws IOException
             */
            public String call() throws IOException {
                return InetAddress.getLocalHost().getHostAddress();
            }
        });
    }

    /**
     * check if an remote path exists.
     */
    public static boolean fileExists(Launcher launcher, File file) throws IOException, InterruptedException {

        return launcher.getChannel().call(new MasterToSlaveCallable<Boolean,IOException>() {
            private static final long serialVersionUID = -4742095943185092470L;
            public Boolean call() throws IOException {
                if (!file.exists() || file.isDirectory()) {
                    return false;
                }
                return true;
            }
        }).booleanValue();
    }
}
