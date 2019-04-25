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
import jenkins.internal.Remote;
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
import jenkins.task._exam.ExamConsoleAnnotator;
import jenkins.task._exam.ExamConsoleErrorOut;
import jenkins.task._exam.Messages;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.RandomStringUtils;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.File;
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
    protected List<TestrunFilter> testrunFilter = new ArrayList<TestrunFilter>();
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
    private String hash = "";
    
    public ExamTask(String examName, String pythonName, String examReport, String systemConfiguration) {
        this.examName = examName;
        this.pythonName = pythonName;
        this.examReport = examReport;
        this.systemConfiguration = Util.fixEmptyAndTrim(systemConfiguration);
    }
    
    /**
     * Backward compatibility by checking the number of parameters
     */
    private static ArgumentListBuilder toWindowsCommand(ArgumentListBuilder args) {
        List<String> arguments = args.toList();
        
        // branch for core equals or greater than 1.654
        boolean[] masks = args.toMaskArray();
        // don't know why are missing single quotes.
        
        args = new ArgumentListBuilder();
        args.add(arguments.get(0), arguments.get(1)); // "cmd.exe", "/C",
        // ...
        
        int size = arguments.size();
        for (int i = 2; i < size; i++) {
            String arg = arguments.get(i).replaceAll("^(-D[^\" ]+)=$", "$0\"\"");
            
            if (masks[i]) {
                args.addMasked(arg);
            } else {
                args.add(arg);
            }
        }
        
        return args;
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
    
    public ExamTool.DescriptorImpl getToolDescriptor() {
        return ToolInstallation.all().get(ExamTool.DescriptorImpl.class);
    }
    
    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher,
            @Nonnull TaskListener listener) throws IOException, InterruptedException {
        
        ArgumentListBuilder args = new ArgumentListBuilder();
        
        EnvVars env = run.getEnvironment(listener);
        
        ExamTool examTool = getExam();
        PythonInstallation python = getPython();
        String exe = "EXAM.exe";
        String pythonexe = "";
        Node node = jenkins.internal.Util.workspaceToNode(workspace);
        if (examTool == null || python == null) {
            run.setResult(Result.FAILURE);
            throw new AbortException("examTool or python is null");
        } else {
            if (node == null) {
                run.setResult(Result.FAILURE);
                throw new AbortException(Messages.EXAM_NodeOffline());
            }
            examTool = examTool.forNode(node, listener);
            python = python.forNode(node, listener);
            exe = examTool.getExecutable(launcher);
            pythonexe = python.getHome();
            if (pythonexe == null || pythonexe.trim().isEmpty()) {
                run.setResult(Result.FAILURE);
                throw new AbortException("python home not set");
            }
            if (!pythonexe.endsWith("exe")) {
                if (!pythonexe.endsWith("\\") && !pythonexe.endsWith("/")) {
                    pythonexe += File.separator;
                }
                pythonexe += "python.exe";
            }
            if (exe.trim().isEmpty()) {
                run.setResult(Result.FAILURE);
                throw new AbortException(Messages.EXAM_ExecutableNotFound(examTool.getName()));
            }
            args.add(exe);
        }
        
        File buildFile = new File(exe);
        FilePath buildFilePath = new FilePath(buildFile);
        
        String dataPath = examTool.getHome();
        String configurationPath;
        String examWorkspace;
        String relativeDataPath = examTool.getRelativeConfigPath();
        if (relativeDataPath != null && !relativeDataPath.trim().isEmpty()) {
            dataPath = examTool.getHome() + File.separator + relativeDataPath;
        }
        configurationPath = dataPath + File.separator + "configuration";
        examWorkspace = workspace + File.separator + "workspace_exam_restApi";
        FilePath source = workspace.child("workspace_exam_restApi");
        FilePath target = workspace.child("target");
        examWorkspace = examWorkspace.replaceAll("[\\/]]", File.separator);
        File configurationFile = new File(
                dataPath + File.separator + "configuration" + File.separator + "config.ini");
        if (!Remote.fileExists(launcher, configurationFile)) {
            run.setResult(Result.FAILURE);
            throw new AbortException(Messages.EXAM_NotExamConfigDirectory(configurationFile.getPath()));
        }
        
        args.add("-data", examWorkspace);
        args.add("-configuration", configurationPath);
        examTool.buildEnvVars(env);
        
        ExamPluginConfig examPluginConfig = Jenkins.getInstance().getDescriptorByType(ExamPluginConfig.class);
        int port = examPluginConfig.getPort();
        args.add("--launcher.appendVmargs", "-vmargs", "-DUSE_CONSOLE=true", "-DRESTAPI=true",
                "-DRESTAPI_PORT=" + port);
        
        if (examPluginConfig.getLicenseHost().isEmpty() || examPluginConfig.getLicensePort() == 0) {
            run.setResult(Result.FAILURE);
            throw new AbortException(Messages.EXAM_LicenseServerNotConfigured());
        }
        args.add("-DLICENSE_PORT=" + examPluginConfig.getLicensePort(),
                "-DLICENSE_HOST=" + examPluginConfig.getLicenseHost());
        
        args.add("-Dfile.encoding=UTF-8");
        args.add("-Dsun.jnu.encoding=UTF-8");
        
        if (javaOpts != null) {
            env.put("JAVA_OPTS", env.expand(javaOpts));
            args.add(javaOpts.split(" "));
        }
        
        if (!launcher.isUnix()) {
            args = toWindowsCommand(args);
        }
        
        long startTime = System.currentTimeMillis();
        ExamConsoleAnnotator eca = new ExamConsoleAnnotator(listener.getLogger(), run.getCharset());
        ExamConsoleErrorOut examErr = new ExamConsoleErrorOut(listener.getLogger());
        try {
            ClientRequest clientRequest = new ClientRequest(listener.getLogger(), port, launcher);
            Proc proc = null;
            try {
                
                Launcher.ProcStarter process = launcher.launch().cmds(args).envs(env).pwd(buildFilePath.getParent());
                if (clientRequest.isApiAvailable()) {
                    listener.getLogger().println("ERROR: EXAM is already running");
                    run.setResult(Result.FAILURE);
                    throw new AbortException("ERROR: EXAM is already running");
                }
                process.stderr(examErr);
                process.stdout(eca);
                proc = process.start();
                
                boolean ret = clientRequest.connectClient(5 * 60 * 1000);
                if (ret) {
                    ApiVersion apiVersion = clientRequest.getApiVersion();
                    listener.getLogger().println("EXAM api version: " + apiVersion.toString());
                    TestConfiguration tc = createTestConfiguration(env);
                    tc.setPythonPath(pythonexe);
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
                    
                    Executor runExecutor = run.getExecutor();
                    if (runExecutor != null) {
                        clientRequest.waitForTestrunEnds(runExecutor, 60);
                        listener.getLogger().println("waiting until EXAM is idle");
                        clientRequest.waitForExamIdle(runExecutor, 300);
                        if (pdfReport) {
                            listener.getLogger().println("waiting for PDF Report");
                            clientRequest.waitForExportPDFReportJob(runExecutor, 600);
                        }
                    }
                    clientRequest.convert(tc.getReportProject().getProjectName());
                    
                    hash = "__" + RandomStringUtils.random(5, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray());
                    source = source.child("reports").child(tc.getReportProject().getProjectName()).child("junit");
                    target = target.child("test-reports").child(tc.getReportProject().getProjectName() + hash);
                    source.copyRecursiveTo(target);
                }
            } catch (IOException e) {
                run.setResult(Result.FAILURE);
                throw new AbortException("ERROR: " + e.toString());
            } finally {
                try {
                    clientRequest.disconnectClient(10 * 1000);
                } finally {
                    if (proc != null && proc.isAlive()) {
                        proc.joinWithTimeout(10, TimeUnit.SECONDS, listener);
                    }
                }
            }
            run.setResult(Result.SUCCESS);
        } catch (IOException e) {
            Util.displayIOException(e, listener);
            
            String errorMessage = Messages.EXAM_ExecFailed();
            if ((System.currentTimeMillis() - startTime) < 1000) {
                if (getDescriptor().getInstallations() == null)
                // looks like the user didn't configure any EXAM
                // installation
                {
                    errorMessage += Messages.EXAM_GlobalConfigNeeded();
                } else
                // There are EXAM installations configured but the project
                // didn't pick it
                {
                    errorMessage += Messages.EXAM_ProjectConfigNeeded();
                }
            }
            run.setResult(Result.FAILURE);
            throw new AbortException(errorMessage);
        } finally {
            Result result = run.getResult();
            if (result != null) {
                listener.getLogger().println(result.toString());
            }
            eca.forceEol();
            examErr.forceEol();
        }
    }
    
    private ExamReportConfig getReport(String name) {
        for (ExamReportConfig rConfig : getDescriptor().getReportConfigs()) {
            if (rConfig.getName().equalsIgnoreCase(name)) {
                return rConfig;
            }
        }
        return null;
    }
    
    abstract TestConfiguration addDataToTestConfiguration(TestConfiguration testConfiguration, EnvVars env)
            throws AbortException;
    
    @Override
    public ExamTask.DescriptorExamTask getDescriptor() {
        return (ExamTask.DescriptorExamTask) super.getDescriptor();
    }
    
    private TestConfiguration createTestConfiguration(EnvVars env) throws AbortException {
        TestConfiguration tc = new TestConfiguration();
        
        tc.setUseExecutionFile(Boolean.valueOf(useExecutionFile));
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
    
    protected static class DescriptorExamTask extends BuildStepDescriptor<Builder>
            implements ExamDescriptor, Serializable {
        
        public DescriptorExamTask() {
            load();
        }
        
        protected DescriptorExamTask(Class<? extends ExamTask> clazz) {
            super(clazz);
            load();
        }
        
        public String getDefaultLogLevel() {
            return RestAPILogLevelEnum.INFO.name();
        }
        
        public RestAPILogLevelEnum[] getLogLevels() {
            return RestAPILogLevelEnum.values();
        }
        
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }
        
        public ExamTool[] getInstallations() {
            return Jenkins.getInstance().getDescriptorByType(ExamTool.DescriptorImpl.class).getInstallations();
        }
        
        public PythonInstallation[] getPythonInstallations() {
            return Jenkins.getInstance().getDescriptorByType(PythonInstallation.DescriptorImpl.class)
                    .getInstallations();
        }
        
        public List<ExamModelConfig> getModelConfigs() {
            return Jenkins.getInstance().getDescriptorByType(ExamPluginConfig.class).getModelConfigs();
        }
        
        protected List<ExamReportConfig> addNoReport(List<ExamReportConfig> reports) {
            List<ExamReportConfig> lReportConfigs = reports;
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
                lReportConfigs.add(0, noReport);
            }
            return lReportConfigs;
        }
        
        public List<ExamReportConfig> getReportConfigs() {
            List<ExamReportConfig> lReportConfigs = Jenkins.getInstance().getDescriptorByType(ExamPluginConfig.class)
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
        
        private ListBoxModel getLoglevelItems() {
            ListBoxModel items = new ListBoxModel();
            for (RestAPILogLevelEnum loglevel : getLogLevels()) {
                items.add(loglevel.name(), loglevel.name());
            }
            return items;
        }
        
        public ListBoxModel doFillLoglevelTestCtrlItems() {
            return getLoglevelItems();
        }
        
        public ListBoxModel doFillLoglevelTestLogicItems() {
            return getLoglevelItems();
        }
        
        public ListBoxModel doFillLoglevelLibCtrlItems() {
            return getLoglevelItems();
        }
    }
}
