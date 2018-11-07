/*
 * ReportConfiguration.java
 *
 * Created on 12.01.2018
 *
 * Copyright (C) 2018 Volkswagen AG, All rights reserved.
 */
package jenkins.internal.data;

/**
 * Information of the EXAM report configuration
 *
 * @author liu
 */
public class ReportConfiguration {

    private String projectName = "";
    private String dbType = "";
    private String dbHost = "";
    private String dbService = "";
    private Integer dbPort = 0;
    private String dbUser = "";
    private String dbSchema = "";
    private String dbPassword = "";

    public static final String NO_REPORT = "NO_REPORT";

    /**
     * Konstruktor.
     * 
     */
    public ReportConfiguration() {

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

    /** @return Das dbType. */
    public String getDbType() {
        return this.dbType;
    }

    /**
     * Setzt das dbType.
     * @param dbType
     *            Das zu setzende dbType.
     */
    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    /** @return Das dbHost. */
    public String getDbHost() {
        return this.dbHost;
    }

    /**
     * Setzt das dbHost.
     * @param dbHost
     *            Das zu setzende dbHost.
     */
    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    /** @return Das dbService. */
    public String getDbService() {
        return this.dbService;
    }

    /**
     * Setzt das dbService.
     * @param dbService
     *            Das zu setzende dbService.
     */
    public void setDbService(String dbService) {
        this.dbService = dbService;
    }

    /** @return Das dbPort. */
    public Integer getDbPort() {
        return this.dbPort;
    }

    /**
     * Setzt das dbPort.
     * @param dbPort
     *            Das zu setzende dbPort.
     */
    public void setDbPort(Integer dbPort) {
        this.dbPort = dbPort;
    }

    /** @return Das dbUser. */
    public String getDbUser() {
        return this.dbUser;
    }

    /**
     * Setzt das dbUser.
     * @param dbUser
     *            Das zu setzende dbUser.
     */
    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    /** @return Das dbSchema. */
    public String getDbSchema() {
        return this.dbSchema;
    }

    /**
     * Setzt das dbSchema.
     * @param dbSchema
     *            Das zu setzende dbSchema.
     */
    public void setDbSchema(String dbSchema) {
        this.dbSchema = dbSchema;
    }

    /** @return Das dbPassword. */
    public String getDbPassword() {
        return this.dbPassword;
    }

    /**
     * Setzt das dbPassword.
     * @param dbPassword
     *            Das zu setzende dbPassword.
     */
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
}
