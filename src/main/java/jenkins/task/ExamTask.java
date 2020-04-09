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
import hudson.Proc;
import hudson.Util;
import hudson.model.AbstractProject;
import hudson.model.Executor;
import hudson.model.Node;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tools.ToolInstallation;
import hudson.util.ArgumentListBuilder;
import hudson.util.ListBoxModel;
import jenkins.internal.ClientRequest;
import jenkins.internal.data.ApiVersion;
import jenkins.internal.data.FilterConfiguration;
import jenkins.internal.data.ReportConfiguration;
import jenkins.internal.data.TestConfiguration;
import jenkins.internal.descriptor.ExamDescriptor;
import jenkins.internal.enumeration.RestAPILogLevelEnum;
import jenkins.model.Jenkins;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.plugins.exam.config.ExamPluginConfig;
import jenkins.plugins.exam.config.ExamReportConfig;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;
import jenkins.report.ExamReportAction;
import jenkins.task._exam.ExamConsoleAnnotator;
import jenkins.task._exam.ExamConsoleErrorOut;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class ExamTask extends Builder implements SimpleBuildStep {
    
    /**
     * JAVA_OPTS if not null.
     */
    protected String javaOpts;
    
    /**
     * timeout if not null.
     */
    protected int timeout;
    /**
     * Identifies {@link ExamTool} to be used.
     */
    protected String examName;
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
    
    public String getExamName() {
        return examName;
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
     * @return ExamTool
     */
    @Nullable
    public ExamTool getExam() {
        for (ExamTool i : getDescriptor().getInstallations()) {
            if (examName != null && examName.equals(i.getName())) {
                return i;
            }
        }
        return null;
    }
    
    /**
     * Gets the EXAM to invoke, or null to invoke the default one.
     *
     * @return PythonInstallation
     */
    @Nullable
    public PythonInstallation getPython() {
        for (PythonInstallation i : getDescriptor().getPythonInstallations()) {
            if (pythonName != null && pythonName.equals(i.getName())) {
                return i;
            }
        }
        return null;
    }
    
    /**
     * Gets the JAVA_OPTS parameter, or null.
     *
     * @return javaOpts
     */
    public String getJavaOpts() {
        return javaOpts;
    }
    
    @DataBoundSetter
    public void setJavaOpts(String javaOpts) {
        this.javaOpts = Util.fixEmptyAndTrim(javaOpts);
    }
    
    /**
     * Gets the timeout parameter, or null.
     *
     * @return timeout
     */
    public int getTimeout() {
        return timeout;
    }
    
    @DataBoundSetter
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public ExamTool.DescriptorImpl getToolDescriptor() {
        return ToolInstallation.all().get(ExamTool.DescriptorImpl.class);
    }
    
    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher,
            @Nonnull TaskListener listener) throws IOException, InterruptedException {
        
        ExamTaskHelper etHelper = new ExamTaskHelper(run, workspace, launcher, listener);
        
        run.addAction(new ExamReportAction(this));
        ExamTool examTool = etHelper.getTool(getExam());
        PythonInstallation python = getPython();
        if (python == null) {
            run.setResult(Result.FAILURE);
            throw new AbortException("python is null");
        }
        Node node = etHelper.getNode();
        examTool = examTool.forNode(node, listener);
        
        String pythonExe = etHelper.getPythonExePath(python);
        ArgumentListBuilder args = new ArgumentListBuilder();
        FilePath buildFilePath = etHelper.prepareWorkspace(examTool, args);
        
        Jenkins instanceOrNull = Jenkins.getInstanceOrNull();
        assert instanceOrNull != null;
        ExamPluginConfig examPluginConfig = instanceOrNull.getDescriptorByType(ExamPluginConfig.class);
        args = etHelper.handleAdditionalArgs(javaOpts, args, examPluginConfig);
        
        if (timeout <= 0) {
            timeout = examPluginConfig.getTimeout();
        }
        
        long startTime = System.currentTimeMillis();
        try {
            ClientRequest clientRequest = new ClientRequest(listener.getLogger(), examPluginConfig.getPort(),
                    launcher);
            Proc proc = null;
            Executor runExecutor = run.getExecutor();
            if (runExecutor != null) {
                if (clientRequest.isApiAvailable()) {
                    listener.getLogger().println("ERROR: EXAM is already running");
                    run.setResult(Result.FAILURE);
                    throw new AbortException("ERROR: EXAM is already running");
                }
                try (ExamConsoleAnnotator eca = new ExamConsoleAnnotator(listener.getLogger(), run.getCharset());
                        ExamConsoleErrorOut examErr = new ExamConsoleErrorOut(listener.getLogger())) {
                    jenkins.internal.Util.checkMinRestApiVersion(listener, new ApiVersion(1, 0, 0), clientRequest);
                    Launcher.ProcStarter process = launcher.launch().cmds(args).envs(etHelper.getEnv())
                            .pwd(buildFilePath.getParent());
                    process.stderr(examErr).stdout(eca);
                    proc = process.start();
                    
                    doExecuteExamTestrun(etHelper, pythonExe, clientRequest, runExecutor);
                } catch (IOException e) {
                    run.setResult(Result.FAILURE);
                    throw new AbortException("ERROR: " + e.toString());
                } finally {
                    try {
                        clientRequest.disconnectClient(runExecutor, timeout);
                    } finally {
                        if (proc != null && proc.isAlive()) {
                            proc.joinWithTimeout(10, TimeUnit.SECONDS, listener);
                        }
                    }
                }
            }
            run.setResult(Result.SUCCESS);
        } catch (IOException e) {
            etHelper.handleIOException(startTime, e, getDescriptor().getInstallations());
        } finally {
            Result result = run.getResult();
            if (result != null) {
                listener.getLogger().println(result.toString());
            }
        }
    }
    
    public void doExecuteExamTestrun(ExamTaskHelper etHelper, String pythonExe, ClientRequest clientRequest,
            Executor runExecutor) throws IOException, InterruptedException {
        boolean ret = clientRequest.connectClient(runExecutor, timeout);
        TaskListener listener = etHelper.getListener();
        if (ret) {
            TestConfiguration tc = createTestConfiguration(etHelper.getEnv());
            tc.setPythonPath(pythonExe);
            FilterConfiguration fc = new FilterConfiguration();
            
            for (TestrunFilter filter : testrunFilter) {
                fc.addTestrunFilter(
                        new jenkins.internal.data.TestrunFilter(filter.name, filter.value, filter.adminCases,
                                filter.activateTestcases));
            }
            
            if (isClearWorkspace()) {
                clientRequest.clearWorkspace(tc.getModelProject().getModelName());
            }
            clientRequest.clearWorkspace(tc.getReportProject().getProjectName());
            if (!testrunFilter.isEmpty()) {
                clientRequest.setTestrunFilter(fc);
            }
            clientRequest.startTestrun(tc);
            
            clientRequest.waitForTestrunEnds(runExecutor, 60);
            listener.getLogger().println("waiting until EXAM is idle");
            clientRequest.waitForExamIdle(runExecutor, timeout);
            if (pdfReport) {
                listener.getLogger().println("waiting for PDF Report");
                clientRequest.waitForExportPDFReportJob(runExecutor, timeout * 2);
            }
            clientRequest.convert(tc.getReportProject().getProjectName());
            etHelper.copyArtifactsToTarget(tc);
        }
    }
    
    @Nullable
    private ExamReportConfig getReport(String name) {
        for (ExamReportConfig rConfig : getDescriptor().getReportConfigs()) {
            if (rConfig.getName().equalsIgnoreCase(name)) {
                return rConfig;
            }
        }
        return null;
    }
    
    abstract protected TestConfiguration addDataToTestConfiguration(TestConfiguration testConfiguration, EnvVars env)
            throws AbortException;
    
    @Override
    public ExamTask.DescriptorExamTask getDescriptor() {
        return (ExamTask.DescriptorExamTask) super.getDescriptor();
    }
    
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
        rep.setDbPassword(r.getDbPass());
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
    protected static class DescriptorExamTask extends BuildStepDescriptor<Builder>
            implements ExamDescriptor, Serializable {
        
        private static final long serialVersionUID = 7068994149846799797L;
        
        /**
         * Constructor of this Descriptor
         */
        public DescriptorExamTask() {
            load();
        }
        
        /**
         * Constructor of this Descriptor
         */
        protected DescriptorExamTask(Class<? extends ExamTask> clazz) {
            super(clazz);
            load();
        }
        
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
         * is applicaple for all job types
         *
         * @return true
         */
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }
        
        /**
         * @return all EXAM tools
         */
        public ExamTool[] getInstallations() {
            Jenkins instanceOrNull = Jenkins.getInstanceOrNull();
            return (instanceOrNull == null) ?
                    new ExamTool[0] :
                    instanceOrNull.getDescriptorByType(ExamTool.DescriptorImpl.class).getInstallations();
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
         * @return all ExamModelConfigs
         */
        public List<ExamModelConfig> getModelConfigs() {
            Jenkins instanceOrNull = Jenkins.getInstanceOrNull();
            if (instanceOrNull == null) {
                return new ArrayList<>();
            }
            return instanceOrNull.getDescriptorByType(ExamPluginConfig.class).getModelConfigs();
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
