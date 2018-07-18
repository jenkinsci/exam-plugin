/*
 * ModelConfiguration.java
 *
 * Created on 12.01.2018
 *
 * Copyright (C) 2018 Volkswagen AG, All rights reserved.
 */
package jenkins.internal.data;

/**
 *
 * @author liu
 */
public class ModelConfiguration {

    private String projectName;
    private String modelName;
    private String targetEndpoint;
    private String modelConfigUUID;

    /**
     * Konstruktor.
     *
     */
    public ModelConfiguration() {

    }

    /** @return Das projectName. */
    public String getProjectName() {
        return this.projectName;
    }

    /**
     * Setzt das projectName.
     * @param projectName
     *            Das zu setzende projectName.
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /** @return Das modelName. */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * Setzt das modelName.
     * @param modelName
     *            Das zu setzende modelName.
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /** @return Das targetEndpoint. */
    public String getTargetEndpoint() {
        return this.targetEndpoint;
    }

    /**
     * Setzt das targetEndpoint.
     * @param targetEndpoint
     *            Das zu setzende targetEndpoint.
     */
    public void setTargetEndpoint(String targetEndpoint) {
        this.targetEndpoint = targetEndpoint;
    }

    /**
     * @return modelConfigUUID
     */
    public String getModelConfigUUID() {
        return this.modelConfigUUID;
    }

    /**
     * Setzt die config UUID.
     * @param modelConfigUUID
     *            die config UUID.
     */
    public void setModelConfigUUID(String modelConfigUUID) {
        this.modelConfigUUID = modelConfigUUID;
    }
}
