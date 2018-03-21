/**
 * Copyright (c) 2018 MicroNova AG
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this
 *        list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this
 *        list of conditions and the following disclaimer in the documentation and/or
 *        other materials provided with the distribution.
 *
 *     3. Neither the name of MicroNova AG nor the names of its
 *        contributors may be used to endorse or promote products derived from
 *        this software without specific prior written permission.
 *
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

import jenkins.internal.enumeration.RestAPILogLevelEnum;

/**
 * @author liu
 */
public class TestConfiguration {

    private ModelConfiguration modelProject;
    private ReportConfiguration reportProject;

    private String systemConfig;
    private String modelConfig;
    private String testObject;

    private RestAPILogLevelEnum logLevel_TC = RestAPILogLevelEnum.INFO;
    private RestAPILogLevelEnum logLevel_TL = RestAPILogLevelEnum.INFO;
    private RestAPILogLevelEnum logLevel_LC = RestAPILogLevelEnum.INFO;

    private String reportPrefix = "";
    private String pythonPath = "";

    /**
     * Konstruktor.
     */
    public TestConfiguration() {

    }

    /**
     * @return Das reportPrefix.
     */
    public String getReportPrefix() {
        return this.reportPrefix;
    }

    /**
     * Setzt das reportPrefix.
     *
     * @param reportPrefix Das zu setzende reportPrefix.
     */
    public void setReportPrefix(String reportPrefix) {
        this.reportPrefix = reportPrefix;
    }

    /**
     * @return Das modelProject.
     */
    public ModelConfiguration getModelProject() {
        return this.modelProject;
    }

    /**
     * Setzt das modelProject.
     *
     * @param modelProject Das zu setzende modelProject.
     */
    public void setModelProject(ModelConfiguration modelProject) {
        this.modelProject = modelProject;
    }

    /**
     * @return Das reportProject.
     */
    public ReportConfiguration getReportProject() {
        return this.reportProject;
    }

    /**
     * Setzt das reportProject.
     *
     * @param reportProject Das zu setzende reportProject.
     */
    public void setReportProject(ReportConfiguration reportProject) {
        this.reportProject = reportProject;
    }

    /**
     * @return Das systemConfig.
     */
    public String getSystemConfig() {
        return this.systemConfig;
    }

    /**
     * Setzt das systemConfig.
     *
     * @param systemConfig Das zu setzende systemConfig.
     */
    public void setSystemConfig(String systemConfig) {
        this.systemConfig = systemConfig;
    }

    /**
     * @return Das modelConfig.
     */
    public String getModelConfig() {
        return this.modelConfig;
    }

    /**
     * Setzt das modelConfig.
     *
     * @param modelConfig Das zu setzende modelConfig.
     */
    public void setModelConfig(String modelConfig) {
        this.modelConfig = modelConfig;
    }

    /**
     * @return Das testObject.
     */
    public String getTestObject() {
        return this.testObject;
    }

    /**
     * Setzt das testObject.
     *
     * @param testObject Das zu setzende testObject.
     */
    public void setTestObject(String testObject) {
        this.testObject = testObject;
    }

    /**
     * @return Das logLevel_TC.
     */
    public RestAPILogLevelEnum getLogLevel_TC() {
        return this.logLevel_TC;
    }

    /**
     * Setzt das logLevel_TC.
     *
     * @param logLevel_TC Das zu setzende logLevel_TC.
     */
    public void setLogLevel_TC(RestAPILogLevelEnum logLevel_TC) {
        this.logLevel_TC = logLevel_TC;
    }

    /**
     * @return Das logLevel_TL.
     */
    public RestAPILogLevelEnum getLogLevel_TL() {
        return this.logLevel_TL;
    }

    /**
     * Setzt das logLevel_TL.
     *
     * @param logLevel_TL Das zu setzende logLevel_TL.
     */
    public void setLogLevel_TL(RestAPILogLevelEnum logLevel_TL) {
        this.logLevel_TL = logLevel_TL;
    }

    /**
     * @return Das logLevel_LC.
     */
    public RestAPILogLevelEnum getLogLevel_LC() {
        return this.logLevel_LC;
    }

    /**
     * Setzt das logLevel_LC.
     *
     * @param logLevel_LC Das zu setzende logLevel_LC.
     */
    public void setLogLevel_LC(RestAPILogLevelEnum logLevel_LC) {
        this.logLevel_LC = logLevel_LC;
    }

    /** @return Das pythonPath. */
    public String getPythonPath() {
        return this.pythonPath;
    }

    /**
     * Setzt das pythonPath.
     * @param pythonPath
     *            Das zu setzende pythonPath.
     */
    public void setPythonPath(String pythonPath) {
        this.pythonPath = pythonPath;
    }
}
