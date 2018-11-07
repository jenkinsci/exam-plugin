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
package jenkins.internal.enumeration;

/**
 * EXAM Loglevel
 */
public enum RestAPILogLevelEnum {

    /** No logging */
    OFF(0),

    /** Level used for errors and exceptions */
    ERROR(10),

    /** Level used for warnings */
    WARNING(15),

    /** Default level used for user messages */
    INFO(20),

    /** Debug level */
    DEBUG(25),

    /** Finest level for debugging method calls */
    INTERNAL(30);

    private int value;

    /**
     * Constructor for LogLevel
     *
     * @param value
     *            Possible Values: 0 - OFF 5 - EXECUTION 10 - ERROR 15 - WARNING 20 - INFO 25 - DEBUG 30 - INTERNAL
     * @return LogLevelEnum
     */
    private RestAPILogLevelEnum(int value) {
        this.value = value;
    }

    /**
     * Get the Int-Value of the given LogLevel
     *
     * @return int of the LogLevel
     */
    public Integer toInt() {
        return Integer.valueOf(this.value);
    }

    /**
     * Cast the Integer to a LogLevel Possible Values: 0 - OFF 5 - EXECUTION 10 - ERROR 15 - WARNING 20 - INFO 25 -
     * DEBUG 30 - INTERNAL
     *
     * @param i
     * @return LogLevelEnum
     */
    public static RestAPILogLevelEnum fromInt(Integer i) {
        switch (i.intValue()) {
        case 0:
            return OFF;
        case 10:
            return ERROR;
        case 15:
            return WARNING;
        case 20:
            return INFO;
        case 25:
            return DEBUG;
        case 30:
            return INTERNAL;
        default:
            return null;
        }
    }

    /**
     * Checks if the given loglevel is included in the other
     *
     * @param l
     *            loglevel that should be included
     * @return true if the loglevel is included or equal
     */
    public boolean includesLogLevel(RestAPILogLevelEnum l) {
        return l.toInt().intValue() <= this.value;
    }

    /**
     * Get the possible names of the enum.
     *
     * @return the names of the enum as String array
     */
    public static String[] getValues() {
        return new String[] { OFF.name(), ERROR.name(), WARNING.name(), INFO.name(), DEBUG.name(), INTERNAL.name() };
    }
}
