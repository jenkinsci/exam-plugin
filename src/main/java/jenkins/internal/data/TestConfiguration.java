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

import jenkins.internal.enumeration.RestAPILogLevelEnum;

/**
 * TestConfiguration
 *
 * @author liu
 */
public class TestConfiguration {
    
    private ModelConfiguration modelProject;
    private ReportConfiguration reportProject;
    
    private String systemConfig;
    private String modelConfig;
    private String testObject;
    
    private RestAPILogLevelEnum logLevelTC;
    private RestAPILogLevelEnum logLevelTL;
    private RestAPILogLevelEnum logLevelLC;
    
    private String reportPrefix = "";
    private String pythonPath;
    private String pdfReportTemplate = "";
    private String pdfSelectFilter;
    private Boolean pdfMeasureImages;
    
    private Boolean useExecutionFile;
    private String pathPCode;
    
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
     * @return Das logLevelTC.
     */
    public RestAPILogLevelEnum getLogLevelTC() {
        return this.logLevelTC;
    }
    
    /**
     * Setzt das logLevelTC.
     *
     * @param logLevelTC Das zu setzende logLevelTC.
     */
    public void setLogLevelTC(RestAPILogLevelEnum logLevelTC) {
        this.logLevelTC = logLevelTC;
    }
    
    /**
     * @return Das logLevelTL.
     */
    public RestAPILogLevelEnum getLogLevelTL() {
        return this.logLevelTL;
    }
    
    /**
     * Setzt das logLevelTL.
     *
     * @param logLevelTL Das zu setzende logLevelTL.
     */
    public void setLogLevelTL(RestAPILogLevelEnum logLevelTL) {
        this.logLevelTL = logLevelTL;
    }
    
    /**
     * @return Das logLevelLC.
     */
    public RestAPILogLevelEnum getLogLevelLC() {
        return this.logLevelLC;
    }
    
    /**
     * Setzt das logLevelLC.
     *
     * @param logLevelLC Das zu setzende logLevelLC.
     */
    public void setLogLevelLC(RestAPILogLevelEnum logLevelLC) {
        this.logLevelLC = logLevelLC;
    }
    
    /**
     * @return Das pythonPath.
     */
    public String getPythonPath() {
        return this.pythonPath;
    }
    
    /**
     * Setzt das pythonPath.
     *
     * @param pythonPath Das zu setzende pythonPath.
     */
    public void setPythonPath(String pythonPath) {
        this.pythonPath = pythonPath;
    }
    
    /**
     * @return Name des pdfReportTemplate.
     */
    public String getPdfReportTemplate() {
        return this.pdfReportTemplate;
    }
    
    /**
     * Setzt das pdfReportTemplate.
     *
     * @param pdfReportTemplate Das zu setzende pdfReportTemplate.
     */
    public void setPdfReportTemplate(String pdfReportTemplate) {
        this.pdfReportTemplate = pdfReportTemplate;
    }
    
    /**
     * @return Name des pdfSelectFilter.
     */
    public String getPdfSelectFilter() {
        return this.pdfSelectFilter;
    }
    
    /**
     * Setzt den pdfSelectFilter.
     *
     * @param pdfSelectFilter Der zu setzende pdfSelectFilter.
     */
    public void setPdfSelectFilter(String pdfSelectFilter) {
        this.pdfSelectFilter = pdfSelectFilter;
    }
    
    /**
     * @return include pdfMeasureImages
     */
    public Boolean getPdfMeasureImages() {
        return this.pdfMeasureImages;
    }
    
    /**
     * Setzt pdfMeasureImages.
     *
     * @param pdfMeasureImages include pdfMeasureImages
     */
    public void setPdfMeasureImages(Boolean pdfMeasureImages) {
        this.pdfMeasureImages = pdfMeasureImages;
    }
    
    /**
     * @return useExecutionFile.
     */
    public Boolean getUseExecutionFile() {
        return this.useExecutionFile;
    }
    
    /**
     * Setzt das useExecutionFile.
     *
     * @param useExecutionFile Das zu setzende useExecutionFile.
     */
    public void setUseExecutionFile(Boolean useExecutionFile) {
        this.useExecutionFile = useExecutionFile;
    }
    
    /**
     * get path of the pCode folder
     *
     * @return path
     */
    public String getPathPCode() {
        return this.pathPCode;
    }
    
    /**
     * set path of the pCode folder
     *
     * @param pathPCode path as String
     */
    public void setPathPCode(String pathPCode) {
        this.pathPCode = pathPCode;
    }
}
