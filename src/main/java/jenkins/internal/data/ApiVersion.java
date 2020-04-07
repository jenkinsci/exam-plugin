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
package jenkins.internal.data;

import java.io.Serializable;

/**
 * Version information of this API
 */
public class ApiVersion implements Serializable {
    private static final long serialVersionUID = 1285636492233484022L;
    private int major;
    private int minor;
    private int fix;

    /**
     * Constructor of version information of this API
     */
    public ApiVersion() {
        this.major = 1;
        this.minor = 0;
        this.fix = 0;
    }

    /**
     * Constructor of version information of this API
     */
    public ApiVersion(int major, int minor, int fix) {
        this.major = major;
        this.minor = minor;
        this.fix = fix;
    }

    /**
     * get major version
     *
     * @return int
     */
    public int getMajor() {
        return this.major;
    }

    /**
     * sets major version
     *
     * @param major
     */
    public void setMajor(int major) {
        this.major = major;
    }

    /**
     * get minor version
     *
     * @return int
     */
    public int getMinor() {
        return this.minor;
    }

    /**
     * set minor version
     *
     * @param minor
     */
    public void setMinor(int minor) {
        this.minor = minor;
    }

    /**
     * get fix version
     *
     * @return int
     */
    public int getFix() {
        return this.fix;
    }

    /**
     * set fix version
     *
     * @param fix
     */
    public void setFix(int fix) {
        this.fix = fix;
    }

    /**
     * returns the full version as string
     *
     * @return version as string
     */
    public String toString() {
        return String.format("%s.%s.%s", major, minor, fix);
    }

    private int compareInt(int int1, int int2) {
        if (int1 > int2) {
            return 1;
        }
        if (int1 < int2) {
            return -1;
        }
        return 0;
    }

    public int compareTo(ApiVersion version) {
        int result = compareInt(major, version.major);
        if (result == 0) {
            result = compareInt(minor, version.minor);
            if (result == 0) {
                result = compareInt(fix, version.fix);
            }
        }
        return result;
    }
}
