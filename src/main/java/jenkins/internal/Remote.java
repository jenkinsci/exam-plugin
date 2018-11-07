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

            /**
             * check if an remote path exists.
             *
             * @return
             *
             * @throws IOException
             */
            public Boolean call() throws IOException {
                if (!file.exists() || file.isDirectory()) {
                    return false;
                }
                return true;
            }
        }).booleanValue();
    }
}
