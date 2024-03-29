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
package jenkins.internal.enumeration;

import java.util.EnumSet;

/**
 * Aufzählung der möglichen Alternativen bei der Fehlerbehandlung.
 *
 * @author warode
 */
public enum ErrorHandling {

    /** Es wird ein unsinniger Step eingefügt, der dazu führt, dass ein Problem erzeugt wird */
    GENERATE_ERROR_STEP("Generate Error Step"),

    /** TestCase wird ausgelassen und der Status auf Invalid gesetzt */
    SKIP_TESTCASE("Skip TestCase"),

    /** Der komplette Generierungsvorgang wird abgebrochen. */
    ABORT("Abort Generation");

    /** Dieses Set enthält alle ErrorHandling-Alternativen. */
    public static final EnumSet<ErrorHandling> ALL_CONSTANTS = EnumSet.allOf(ErrorHandling.class);

    private String displayString;

    private ErrorHandling(String value) {
        this.displayString = value;
    }

    /**
     * Gibt den Text des Enums für die Verwendung in der GUI zurück.
     * @return Anzeige-Text
     */
    public final String displayString() {
        return this.displayString;
    }
}
