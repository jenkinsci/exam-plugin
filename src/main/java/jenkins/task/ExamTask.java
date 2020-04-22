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
package jenkins.task;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.Executor;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tools.ToolInstallation;
import hudson.util.ListBoxModel;
import jenkins.internal.ClientRequest;
import jenkins.internal.data.ApiVersion;
import jenkins.internal.data.FilterConfiguration;
import jenkins.internal.data.ReportConfiguration;
import jenkins.internal.data.TestConfiguration;
import jenkins.internal.enumeration.RestAPILogLevelEnum;
import jenkins.model.Jenkins;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamPluginConfig;
import jenkins.plugins.exam.config.ExamReportConfig;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ExamTask extends Task implements SimpleBuildStep {
    
    private static final long serialVersionUID = 845638286844546158L;
    /**
     * Identifies {@link PythonInstallation} to be used.
     */
    protected String pythonName;
    protected boolean clearWorkspace;
    /**
     * Definiert die default SystemConfiguration
     */
    protected String systemConfiguration;
    protected List<TestrunFilter> testrunFilter = new ArrayList<>();
    protected boolean logging;
    protected String loglevelTestCtrl = RestAPILogLevelEnum.INFO.name();
    protected String loglevelTestLogic = RestAPILogLevelEnum.INFO.name();
    protected String loglevelLibCtrl = RestAPILogLevelEnum.INFO.name();
    /**
     * Identifies {@link jenkins.plugins.exam.config.ExamReportConfig} to be used.
     */
    protected String examReport;
    protected boolean pdfReport;
    protected String pdfReportTemplate;
    protected String pdfSelectFilter;
    protected boolean pdfMeasureImages;
    /**
     * Definiert den Report Prefix
     */
    protected String reportPrefix;
    private boolean useExecutionFile;
    
    private String pythonExe;
    
    /**
     * Constructor of ExamTask
     */
    public ExamTask(String examName, String pythonName, String examReport, String systemConfiguration) {
        this.examName = examName;
        this.pythonName = pythonName;
        this.examReport = examReport;
        this.systemConfiguration = Util.fixEmptyAndTrim(systemConfiguration);
    }
    
    public boolean getUseExecutionFile() {
        return useExecutionFile;
    }
    
    public void setUseExecutionFile(boolean useExecutionFile) {
        this.useExecutionFile = useExecutionFile;
    }
    
    public String getReportPrefix() {
        return reportPrefix;
    }
    
    @DataBoundSetter
    public void setReportPrefix(String reportPrefix) {
        this.reportPrefix = reportPrefix;
    }
    
    public boolean getPdfReport() {
        return pdfReport;
    }
    
    @DataBoundSetter
    public void setPdfReport(boolean pdfReport) {
        this.pdfReport = pdfReport;
    }
    
    public String getPdfReportTemplate() {
        return pdfReportTemplate;
    }
    
    @DataBoundSetter
    public void setPdfReportTemplate(String pdfReportTemplate) {
        this.pdfReportTemplate = pdfReportTemplate;
    }
    
    public String getPdfSelectFilter() {
        return pdfSelectFilter;
    }
    
    @DataBoundSetter
    public void setPdfSelectFilter(String pdfSelectFilter) {
        this.pdfSelectFilter = pdfSelectFilter;
    }
    
    public boolean getPdfMeasureImages() {
        return pdfMeasureImages;
    }
    
    @DataBoundSetter
    public void setPdfMeasureImages(boolean pdfMeasureImages) {
        this.pdfMeasureImages = pdfMeasureImages;
    }
    
    public boolean getLogging() {
        return logging;
    }
    
    @DataBoundSetter
    public void setLogging(boolean logging) {
        this.logging = logging;
    }
    
    public List<TestrunFilter> getTestrunFilter() {
        return testrunFilter;
    }
    
    @DataBoundSetter
    public void setTestrunFilter(List<TestrunFilter> testrunFilter) {
        this.testrunFilter = testrunFilter;
    }
    
    public String getLoglevelTestCtrl() {
        return loglevelTestCtrl;
    }
    
    @DataBoundSetter
    public void setLoglevelTestCtrl(String loglevelTestCtrl) {
        this.loglevelTestCtrl = loglevelTestCtrl;
    }
    
    public String getLoglevelTestLogic() {
        return loglevelTestLogic;
    }
    
    @DataBoundSetter
    public void setLoglevelTestLogic(String loglevelTestLogic) {
        this.loglevelTestLogic = loglevelTestLogic;
    }
    
    public String getLoglevelLibCtrl() {
        return loglevelLibCtrl;
    }
    
    @DataBoundSetter
    public void setLoglevelLibCtrl(String loglevelLibCtrl) {
        this.loglevelLibCtrl = loglevelLibCtrl;
    }
    
    public String getPythonName() {
        return pythonName;
    }
    
    public String getExamReport() {
        return examReport;
    }
    
    public String getSystemConfiguration() {
        return systemConfiguration;
    }
    
    @DataBoundSetter
    public void setSystemConfiguration(String systemConfiguration) {
        this.systemConfiguration = systemConfiguration;
    }
    
    public boolean isClearWorkspace() {
        return clearWorkspace;
    }
    
    @DataBoundSetter
    public void setClearWorkspace(boolean clearWorkspace) {
        this.clearWorkspace = clearWorkspace;
    }
    
    /**
     * Gets the EXAM to invoke, or null to invoke the default one.
     *
     * @return PythonInstallation
     */
    @Nullable
    public PythonInstallation getPython() {
        Task.DescriptorTask descriptorTask = getDescriptor();
        if (descriptorTask instanceof ExamTask.DescriptorExamTask) {
            ExamTask.DescriptorExamTask descriptorExamTask = (ExamTask.DescriptorExamTask) descriptorTask;
            for (PythonInstallation i : descriptorExamTask.getPythonInstallations()) {
                if (pythonName != null && pythonName.equals(i.getName())) {
                    return i;
                }
            }
        }
        return null;
    }
    
    public ExamTool.DescriptorImpl getToolDescriptor() {
        return ToolInstallation.all().get(ExamTool.DescriptorImpl.class);
    }
    
    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher,
            @Nonnull TaskListener taskListener) throws IOException, InterruptedException {
        
        Executor runExecutor = run.getExecutor();
        assert runExecutor != null;
        getTaskHelper().setRun(run);
        getTaskHelper().setWorkspace(workspace);
        getTaskHelper().setLauncher(launcher);
        getTaskHelper().setTaskListener(taskListener);
        
        //run.addAction(new ExamReportAction(this));
        
        PythonInstallation python = getPython();
        if (python == null) {
            run.setResult(Result.FAILURE);
            throw new AbortException("python is null");
        }
        pythonExe = getTaskHelper().getPythonExePath(python);
        
        getTaskHelper().perform(this, launcher, new ApiVersion(1, 0, 0));
    }
    
    protected void doExecuteTask(ClientRequest clientRequest) throws IOException, InterruptedException {
        Executor runExecutor = getTaskHelper().getRun().getExecutor();
        TaskListener listener = getTaskHelper().getTaskListener();
        if (clientRequest.isClientConnected()) {
            TestConfiguration tc = createTestConfiguration(getTaskHelper().getEnv());
            tc.setPythonPath(pythonExe);
            FilterConfiguration fc = new FilterConfiguration();
            
            for (TestrunFilter filter : testrunFilter) {
                fc.addTestrunFilter(
                        new jenkins.internal.data.TestrunFilter(filter.name, filter.value, filter.adminCases,
                                filter.activateTestcases));
            }
            
            if (isClearWorkspace() && tc.getModelProject() != null) {
                clientRequest.clearWorkspace(tc.getModelProject().getModelName());
            }
            clientRequest.clearWorkspace(tc.getReportProject().getProjectName());
            if (!testrunFilter.isEmpty()) {
                clientRequest.setTestrunFilter(fc);
            }
            clientRequest.startTestrun(tc);
            
            clientRequest.waitForTestrunEnds(runExecutor, getTimeout());
            listener.getLogger().println("waiting until EXAM is idle");
            clientRequest.waitForExamIdle(runExecutor, getTimeout());
            if (pdfReport) {
                listener.getLogger().println("waiting for PDF Report");
                clientRequest.waitForExportPDFReportJob(runExecutor, getTimeout() * 2);
            }
            clientRequest.convert(tc.getReportProject().getProjectName());
            getTaskHelper().copyArtifactsToTarget(tc);
        }
    }
    
    @Nullable
    private ExamReportConfig getReport(String name) {
        Task.DescriptorTask descriptorTask = getDescriptor();
        if (descriptorTask instanceof ExamTask.DescriptorExamTask) {
            ExamTask.DescriptorExamTask descriptorExamTask = (ExamTask.DescriptorExamTask) descriptorTask;
            for (ExamReportConfig rConfig : descriptorExamTask.getReportConfigs()) {
                if (rConfig.getName().equalsIgnoreCase(name)) {
                    return rConfig;
                }
            }
        }
        return null;
    }
    
    abstract protected TestConfiguration addDataToTestConfiguration(TestConfiguration testConfiguration, EnvVars env)
            throws AbortException;
    
    private TestConfiguration createTestConfiguration(EnvVars env) throws AbortException {
        TestConfiguration tc = new TestConfiguration();
        
        tc.setUseExecutionFile(useExecutionFile);
        tc.setSystemConfig(systemConfiguration);
        tc.setTestObject("");
        tc.setReportPrefix(reportPrefix);
        
        addReportToTestConfiguration(tc);
        addPdfReportToTestConfiguration(tc);
        addLogLevelToTestConfiguration(tc);
        
        tc = addDataToTestConfiguration(tc, env);
        return tc;
    }
    
    private void addPdfReportToTestConfiguration(TestConfiguration tc) {
        if (pdfReport && !pdfReportTemplate.isEmpty()) {
            tc.setPdfReportTemplate(pdfReportTemplate);
            tc.setPdfSelectFilter(pdfSelectFilter);
            tc.setPdfMeasureImages(pdfMeasureImages);
        }
    }
    
    private void addLogLevelToTestConfiguration(TestConfiguration tc) {
        tc.setLogLevelTC(RestAPILogLevelEnum.valueOf(loglevelTestCtrl));
        tc.setLogLevelTL(RestAPILogLevelEnum.valueOf(loglevelTestLogic));
        tc.setLogLevelLC(RestAPILogLevelEnum.valueOf(loglevelLibCtrl));
    }
    
    private void addReportToTestConfiguration(TestConfiguration tc) {
        ReportConfiguration rep = new ReportConfiguration();
        ExamReportConfig r = getReport(examReport);
        assert r != null;
        rep.setProjectName(r.getName());
        rep.setDbHost(r.getHost());
        rep.setDbPassword(r.getDbPass().getPlainText());
        rep.setDbPort(Integer.valueOf(r.getPort()));
        rep.setDbSchema(r.getSchema());
        rep.setDbService(r.getServiceOrSid());
        rep.setDbType(r.getDbType());
        rep.setDbUser(r.getDbUser());
        tc.setReportProject(rep);
    }
    
    /**
     * The Descriptor of DescriptorExamTask
     */
    protected static class DescriptorExamTask extends Task.DescriptorTask {
        
        private static final long serialVersionUID = 7068994149846799797L;
        
        /**
         * @return the default log level
         */
        public String getDefaultLogLevel() {
            return RestAPILogLevelEnum.INFO.name();
        }
        
        /**
         * @return all log level
         */
        public RestAPILogLevelEnum[] getLogLevels() {
            return RestAPILogLevelEnum.values();
        }
        
        /**
         * @return all Python installations
         */
        public PythonInstallation[] getPythonInstallations() {
            Jenkins instanceOrNull = Jenkins.getInstanceOrNull();
            return (instanceOrNull == null) ?
                    new PythonInstallation[0] :
                    instanceOrNull.getDescriptorByType(PythonInstallation.DescriptorImpl.class).getInstallations();
        }
        
        /**
         * adds a NoReport entry to the ReportConfigs
         *
         * @return all ExamReportConfig
         */
        protected List<ExamReportConfig> addNoReport(List<ExamReportConfig> reports) {
            boolean found = false;
            for (ExamReportConfig config : reports) {
                if (config.getName().compareTo(ReportConfiguration.NO_REPORT) == 0) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                ExamReportConfig noReport = new ExamReportConfig();
                noReport.setName(ReportConfiguration.NO_REPORT);
                noReport.setSchema("");
                noReport.setHost("");
                noReport.setPort("0");
                reports.add(0, noReport);
            }
            return reports;
        }
        
        /**
         * @return all ExamReportConfig
         */
        public List<ExamReportConfig> getReportConfigs() {
            Jenkins instanceOrNull = Jenkins.getInstanceOrNull();
            if (instanceOrNull == null) {
                return new ArrayList<>();
            }
            List<ExamReportConfig> lReportConfigs = instanceOrNull.getDescriptorByType(ExamPluginConfig.class)
                    .getReportConfigs();
            lReportConfigs = addNoReport(lReportConfigs);
            return lReportConfigs;
        }
        
        /**
         * fills the ListBoxModel with all ExamInstallations
         *
         * @return ListBoxModel
         */
        public ListBoxModel doFillExamNameItems() {
            ListBoxModel items = new ListBoxModel();
            ExamTool[] examTools = getInstallations();
            
            Arrays.sort(examTools, (ExamTool o1, ExamTool o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
            for (ExamTool tool : examTools) {
                items.add(tool.getName(), tool.getName());
            }
            return items;
        }
        
        /**
         * fills the ListBoxModel with all PythonInstallations
         *
         * @return ListBoxModel
         */
        public ListBoxModel doFillPythonNameItems() {
            ListBoxModel items = new ListBoxModel();
            PythonInstallation[] pythonTools = getPythonInstallations();
            
            Arrays.sort(pythonTools,
                    (PythonInstallation o1, PythonInstallation o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
            for (PythonInstallation tool : pythonTools) {
                items.add(tool.getName(), tool.getName());
            }
            return items;
        }
        
        /**
         * fills the ListBoxModel with all ExamReportConfig
         *
         * @return ListBoxModel
         */
        public ListBoxModel doFillExamReportItems() {
            ListBoxModel items = new ListBoxModel();
            List<ExamReportConfig> reports = getReportConfigs();
            reports.sort(
                    (ExamReportConfig o1, ExamReportConfig o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
            
            for (ExamReportConfig report : reports) {
                items.add(report.getDisplayName(), report.getName());
            }
            return items;
        }
        
        /**
         * fills the ListBoxModel with all LogLevels
         *
         * @return ListBoxModel
         */
        private ListBoxModel getLoglevelItems() {
            ListBoxModel items = new ListBoxModel();
            for (RestAPILogLevelEnum loglevel : getLogLevels()) {
                items.add(loglevel.name(), loglevel.name());
            }
            return items;
        }
        
        /**
         * fills the ListBoxModel TestCtrl with all LogLevels
         *
         * @return ListBoxModel
         */
        public ListBoxModel doFillLoglevelTestCtrlItems() {
            return getLoglevelItems();
        }
        
        /**
         * fills the ListBoxModel TestLogic with all LogLevels
         *
         * @return ListBoxModel
         */
        public ListBoxModel doFillLoglevelTestLogicItems() {
            return getLoglevelItems();
        }
        
        /**
         * fills the ListBoxModel LibCtrl with all LogLevels
         *
         * @return ListBoxModel
         */
        public ListBoxModel doFillLoglevelLibCtrlItems() {
            return getLoglevelItems();
        }
    }
}
