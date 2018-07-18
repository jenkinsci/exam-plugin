/*
 * ExamStatus.java
 *
 * Created on 18.01.2018
 *
 * Copyright (C) 2018 Volkswagen AG, All rights reserved.
 */
package jenkins.internal.data;

/**
 * 
 * @author liu
 */
public class ExamStatus {

    private Boolean jobRunning;
    private String jobName;
    private Integer testRunState;

    /** @return Das jobRunning. */
    public Boolean getJobRunning() {
        return this.jobRunning;
    }

    /**
     * Setzt das jobRunning.
     * @param jobRunning
     *            Das zu setzende jobRunning.
     */
    public void setJobRunning(Boolean jobRunning) {
        this.jobRunning = jobRunning;
    }

    /** @return Das jobName. */
    public String getJobName() {
        return this.jobName;
    }

    /**
     * Setzt das jobName.
     * @param jobName
     *            Das zu setzende jobName.
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /** @return Das testRunState. */
    public Integer getTestRunState() {
        return this.testRunState;
    }

    /**
     * Setzt das testRunState.
     * @param testRunState
     *            Das zu setzende testRunState.
     */
    public void setTestRunState(Integer testRunState) {
        this.testRunState = testRunState;
    }
}
