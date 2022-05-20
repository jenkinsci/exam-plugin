/*
 * StepType.java
 *
 * Created on 23.01.2018
 *
 * Copyright (C) 2018 MicroNova AG, All rights reserved.
 */
package jenkins.internal.enumeration;

/**
 * Auflistung der bekannten Step-Typen
 *
 * @author warode
 */
public enum StepType {

    /** Precondition Before */
    PRECONDITION_BEFORE,

    /** Precondition After */
    PRECONDITION_AFTER,

    /** Action Before */
    ACTION_BEFORE,

    /** Action After */
    ACTION_AFTER,

    /** PostCondition Before */
    POSTCONDITION_BEFORE,

    /** PostCondition After */
    POSTCONDITION_AFTER,

    /** Expected Result Before */
    EXPECTED_RESULT_BEFORE,

    /** Expected Result After */
    EXPECTED_RESULT_AFTER,

    /** TCG_Step */
    NUMBERED_FRAME_STEP;
}
