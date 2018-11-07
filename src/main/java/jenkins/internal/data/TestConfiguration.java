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

    public String getPdfReportTemplate() {
        return this.pdfReportTemplate;
    }

    public void setPdfReportTemplate(String pdfReportTemplate) {
        this.pdfReportTemplate = pdfReportTemplate;
    }

    public String getPdfSelectFilter() {
        return this.pdfSelectFilter;
    }

    public void setPdfSelectFilter(String pdfSelectFilter) {
        this.pdfSelectFilter = pdfSelectFilter;
    }

    public Boolean getPdfMeasureImages() {
        return this.pdfMeasureImages;
    }

    public void setPdfMeasureImages(Boolean pdfMeasureImages) {
        this.pdfMeasureImages = pdfMeasureImages;
    }
}
