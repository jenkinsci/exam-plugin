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
package jenkins.internal.data;

import java.io.Serializable;

/**
 * Information of the EXAM report configuration
 *
 * @author liu
 */
public class ReportConfiguration implements Serializable {
    
    public static final String NO_REPORT = "NO_REPORT";
    private static final long serialVersionUID = 4112737477385174340L;
    private String projectName = "";
    private String dbType = "";
    private String dbHost = "";
    private String dbService = "";
    private Integer dbPort = 0;
    private String dbUser = "";
    private String dbSchema = "";
    private String dbPassword = "";
    
    /**
     * Konstruktor.
     */
    public ReportConfiguration() {
    
    }
    
    /**
     * @return Das projectName.
     */
    public String getProjectName() {
        return this.projectName;
    }
    
    /**
     * Setzt das projectName.
     *
     * @param projectName Das zu setzende projectName.
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    /**
     * @return Das dbType.
     */
    public String getDbType() {
        return this.dbType;
    }
    
    /**
     * Setzt das dbType.
     *
     * @param dbType Das zu setzende dbType.
     */
    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
    
    /**
     * @return Das dbHost.
     */
    public String getDbHost() {
        return this.dbHost;
    }
    
    /**
     * Setzt das dbHost.
     *
     * @param dbHost Das zu setzende dbHost.
     */
    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }
    
    /**
     * @return Das dbService.
     */
    public String getDbService() {
        return this.dbService;
    }
    
    /**
     * Setzt das dbService.
     *
     * @param dbService Das zu setzende dbService.
     */
    public void setDbService(String dbService) {
        this.dbService = dbService;
    }
    
    /**
     * @return Das dbPort.
     */
    public Integer getDbPort() {
        return this.dbPort;
    }
    
    /**
     * Setzt das dbPort.
     *
     * @param dbPort Das zu setzende dbPort.
     */
    public void setDbPort(Integer dbPort) {
        this.dbPort = dbPort;
    }
    
    /**
     * @return Das dbUser.
     */
    public String getDbUser() {
        return this.dbUser;
    }
    
    /**
     * Setzt das dbUser.
     *
     * @param dbUser Das zu setzende dbUser.
     */
    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }
    
    /**
     * @return Das dbSchema.
     */
    public String getDbSchema() {
        return this.dbSchema;
    }
    
    /**
     * Setzt das dbSchema.
     *
     * @param dbSchema Das zu setzende dbSchema.
     */
    public void setDbSchema(String dbSchema) {
        this.dbSchema = dbSchema;
    }
    
    /**
     * @return Das dbPassword.
     */
    public String getDbPassword() {
        return this.dbPassword;
    }
    
    /**
     * Setzt das dbPassword.
     *
     * @param dbPassword Das zu setzende dbPassword.
     */
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
}
