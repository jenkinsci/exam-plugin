/*
 * LogLevelEnum.java
 *
 * Created on 16.01.2018
 *
 * Copyright (C) 2018 Volkswagen AG, All rights reserved.
 */
package jenkins.internal.enumeration;

/**
 * 
 * @author liu
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
