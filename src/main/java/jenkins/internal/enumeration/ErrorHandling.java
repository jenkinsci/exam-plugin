/*
 * ErrorHandling.java
 *
 * Created on 04.04.2018
 *
 * Copyright (C) 2018 MicroNova AG, All rights reserved.
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
