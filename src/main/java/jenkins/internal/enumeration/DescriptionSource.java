/*
 * ErrorHandling.java
 *
 * Created on 23.09.2019
 *
 * Copyright (C) 2019 MicroNova AG, All rights reserved.
 */
package jenkins.internal.enumeration;

import java.util.EnumSet;

/**
 * Aufzählung der möglichen Description Quellen.
 * @author bechtold
 */

public enum DescriptionSource {

    /**
     * Das Feld Beschreibung wird als Quelle benutzt.
     */
    BESCHREIBUNG("Beschreibung"),

    /**
     * Das Feld Description wird als Quelle benutzt.
     */
    DESCRIPTION("Description");

    /** Dieses Set enthält alle DescriptionSource-Alternativen. */
    public static final EnumSet<DescriptionSource> ALL_CONSTANTS = EnumSet.allOf(DescriptionSource.class);

    private String displayString;

    private DescriptionSource(String value) {
        this.displayString = value;
    }

    /**
     * Gibt den Text des Enums für die Verwendung in der GUI zurück.
     * @return Anzeige-Text
     */
    public String getDisplayString() {
        return this.displayString;
    }
}
