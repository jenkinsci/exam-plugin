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
import jenkins.security.MasterToSlaveCallable;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;

/**
 * Execute code on remote slave
 */
public class Remote implements Serializable {
    
    /**
     * @param launcher Launcher
     *
     * @return ip address of slave
     *
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static String getIP(@Nonnull Launcher launcher) throws IOException, InterruptedException {
        VirtualChannel channel = launcher.getChannel();
        if (channel == null) {
            return "";
        }
        String answer = channel.call(new MasterToSlaveCallable<String, IOException>() {
            private static final long serialVersionUID = -4742095943185092470L;
            
            /**
             * @return ip address of slave
             *
             * @throws IOException IOException
             */
            public String call() throws IOException {
                return InetAddress.getLocalHost().getHostAddress();
            }
        });
        
        return answer == null ? "" : answer;
    }
    
    /**
     * check if an remote path exists.
     *
     * @param launcher Launcher
     * @param file     File
     *
     * @return boolean
     *
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static boolean fileExists(@Nonnull Launcher launcher, File file) throws IOException, InterruptedException {
        VirtualChannel channel = launcher.getChannel();
        if (channel == null) {
            return false;
        }
        Boolean answer = channel.call(new MasterToSlaveCallable<Boolean, IOException>() {
            private static final long serialVersionUID = -4742095943185092470L;
            
            /**
             * check if an remote path exists.
             *
             * @return Boolean
             *
             */
            public Boolean call() {
                return file != null && file.exists() && !file.isDirectory();
            }
        });
        
        return answer == null ? false : answer;
    }
}
