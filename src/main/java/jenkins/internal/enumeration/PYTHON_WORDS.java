/*
 * PYTHON_WORDS.java
 * 
 * Created on 28.05.2014
 * 
 * Copyright (C) 2014 Volkswagen AG, All rights reserved.
 */
package jenkins.internal.enumeration;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/** Namen der Funktionsaufrufe der Casts. */
public enum PYTHON_WORDS {
    // Casts
    /** cast to integer */
    INT("int"), //$NON-NLS-1$

    /** cast to long */
    LONG("long"), //$NON-NLS-1$

    /** cast to float */
    FLOAT("float"), //$NON-NLS-1$

    /** cast to string */
    STR("str"), //$NON-NLS-1$

    /** cast to boolean */
    BOOL("bool"), //$NON-NLS-1$

    // Konstanten

    /** String literal for the none type */
    NONE("None"), //$NON-NLS-1$

    /** String literal for <code>true</code> */
    TRUE("True"), //$NON-NLS-1$

    /** String literal for <code>false</code> */
    FALSE("False"), //$NON-NLS-1$

    // Sonstige reservierte Wörter.
    /***/
    LIST("list"), //$NON-NLS-1$
    /***/
    DICT("dict"), //$NON-NLS-1$
    /***/
    TUPLE("tuple"), //$NON-NLS-1$
    /***/
    AND("and"), //$NON-NLS-1$
    /***/
    DEL("del"), //$NON-NLS-1$
    /***/
    FROM("from"), //$NON-NLS-1$
    /***/
    NOT("not"), //$NON-NLS-1$
    /***/
    WHILE("while"), //$NON-NLS-1$
    /***/
    AS("as"), //$NON-NLS-1$
    /***/
    ELIF("elif"), //$NON-NLS-1$
    /***/
    GLOBAL("global"), //$NON-NLS-1$
    /***/
    OR("or"), //$NON-NLS-1$
    /***/
    WITH("with"), //$NON-NLS-1$
    /***/
    ASSERT("assert"), //$NON-NLS-1$
    /***/
    ELSE("else"), //$NON-NLS-1$
    /***/
    IF("if"), //$NON-NLS-1$
    /***/
    PASS("pass"), //$NON-NLS-1$
    /***/
    YIELD("yield"), //$NON-NLS-1$
    /***/
    BREAK("break"), //$NON-NLS-1$
    /***/
    EXCEPT("except"), //$NON-NLS-1$
    /***/
    IMPORT("import"), //$NON-NLS-1$
    /***/
    PRINT("print"), //$NON-NLS-1$
    /***/
    CLASS("class"), //$NON-NLS-1$
    /***/
    EXEC("exec"), //$NON-NLS-1$
    /***/
    IN("in"), //$NON-NLS-1$
    /***/
    RAISE("raise"), //$NON-NLS-1$
    /***/
    CONTINUE("continue"), //$NON-NLS-1$
    /***/
    FINALLY("finally"), //$NON-NLS-1$
    /***/
    IS("is"), //$NON-NLS-1$
    /***/
    RETURN("return"), //$NON-NLS-1$
    /***/
    DEF("def"), //$NON-NLS-1$
    /***/
    FOR("for"), //$NON-NLS-1$
    /***/
    LAMBDA("lambda"), //$NON-NLS-1$
    /***/
    TRY("try"), //$NON-NLS-1$
    /***/
    CATCH("catch"), //$NON-NLS-1$

    ;

    /** Leere Menge. */
    public static final EnumSet<PYTHON_WORDS> SET_EMPTY = EnumSet.noneOf(PYTHON_WORDS.class);
    /** Die Casts: {@link #INT}, {@link #LONG}, {@link #FLOAT}, {@link #STR}, {@link #BOOL}. */
    public static final EnumSet<PYTHON_WORDS> SET_CASTS = EnumSet.of(INT, LONG, FLOAT, STR, BOOL);
    /** Die Namen von Konstanten: {@link #NONE}, {@link #TRUE}, {@link #FALSE}. */
    public static final EnumSet<PYTHON_WORDS> SET_CONSTS = EnumSet.of(NONE, TRUE, FALSE);
    /** The literal None. Don't modify! */
    public static final EnumSet<PYTHON_WORDS> SET_CONST_NONE = EnumSet.of(NONE);
    /** The literals True and False. Don't modify! */
    public static final EnumSet<PYTHON_WORDS> SET_CONST_BOOL = EnumSet.of(TRUE, FALSE);
    /** identifiers of calls that are accepted for numeric data types. */
    public static final EnumSet<PYTHON_WORDS> NUMERIC_CALLS = EnumSet.of(FLOAT, INT, LONG, BOOL);

    /** identifiers of calls that are accepted for boolean data types. */
    public static final EnumSet<PYTHON_WORDS> BOOLEAN_CALLS = EnumSet.of(INT, LONG, BOOL);

    /** identifiers of calls that are accepted for boolean data types. */
    public static final EnumSet<PYTHON_WORDS> STRING_CALLS = EnumSet.of(STR);

    /** identifiers of calls that are accepted for numeric data types. */
    public static final EnumSet<PYTHON_WORDS> ENUM_CALLS = EnumSet.of(INT);

    /** KeyPYTHON_WORDS reserved in Python. */
    public static final EnumSet<PYTHON_WORDS> RESERVED_WORDS = EnumSet.of(INT, LONG, FLOAT, STR, BOOL, NONE, TRUE, FALSE,
            LIST, DICT, TUPLE, AND, DEL, FROM, NOT, WHILE, AS, ELIF, GLOBAL, OR, WITH, ASSERT, ELSE, IF, PASS, YIELD,
            BREAK, EXCEPT, IMPORT, PRINT, CLASS, EXEC, IN, RAISE, CONTINUE, FINALLY, IS, RETURN, DEF, FOR, LAMBDA,
            TRY, CATCH);

    /** Set for checking whether an identifier is one of the {@link PYTHON_WORDS}. */
    private static final Map<String, PYTHON_WORDS> LITERALS = new HashMap<>(5);
    static {
        for (PYTHON_WORDS member : PYTHON_WORDS.values()) {
            PYTHON_WORDS.LITERALS.put(member.getLiteral(), member);
        }
    }

    /**
     * @param identifier
     *            Ein Bezeichner aus irgend einem Python-Quelltext.
     * @return das reservierte Wort dazu, oder null falls unbekannt.
     */
    public static PYTHON_WORDS get(String identifier) {
        return LITERALS.get(identifier);
    }

    private String literal;

    private PYTHON_WORDS(String literal) {
        this.literal = literal;
    }

    /** @return Das reservierte Wort, so wie es im Python-Code verwendet wird. */
    public String getLiteral() {
        return this.literal;
    }
}
