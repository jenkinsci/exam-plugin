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
import hudson.model.AbstractProject;
import hudson.model.Node;
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

public abstract class ExamTask extends Builder implements SimpleBuildStep {

    private boolean useExecutionFile;

    private String hash = "";

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

    @DataBoundSetter
    public void setSystemConfiguration(String systemConfiguration) {
        this.systemConfiguration = systemConfiguration;
    }

    public String getReportPrefix() {
        return reportPrefix;
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

    @DataBoundSetter
    public void setReportPrefix(String reportPrefix) {
        this.reportPrefix = reportPrefix;
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

    @DataBoundSetter
    public void setClearWorkspace(boolean clearWorkspace) {
        this.clearWorkspace = clearWorkspace;
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

    public boolean isClearWorkspace() {
        return clearWorkspace;
    }

    /**
     * Gets the EXAM to invoke, or null to invoke the default one.
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
     */
    public PythonInstallation getPython() {
        for (PythonInstallation i : getDescriptor().getPythonInstallations()) {
            if (pythonName != null && pythonName.equals(i.getName())) {
                return i;
            }
        }
        return null;
    }

    @DataBoundSetter
    public void setJavaOpts(String javaOpts) {
        this.javaOpts = Util.fixEmptyAndTrim(javaOpts);
    }

    /**
     * Gets the JAVA_OPTS parameter, or null.
     */
    public String getJavaOpts() {
        return javaOpts;
    }

    public ExamTool.DescriptorImpl getToolDescriptor() {
        return ToolInstallation.all().get(ExamTool.DescriptorImpl.class);
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher,
                        @Nonnull TaskListener listener) throws InterruptedException, IOException {

        ArgumentListBuilder args = new ArgumentListBuilder();

        EnvVars env = run.getEnvironment(listener);

        ExamTool examTool = getExam();
        PythonInstallation python = getPython();
        String exe = "EXAM.exe";
        String pythonexe = "";
        Node node = jenkins.internal.Util.workspaceToNode(workspace);
        if (examTool == null) {
            args.add("EXAM.exe");
        } else {
            if (node == null) {
                throw new AbortException(Messages.EXAM_NodeOffline());
            }
            examTool = examTool.forNode(node, listener);
            python = python.forNode(node, listener);
            exe = examTool.getExecutable(launcher);
            pythonexe = python.getHome();
            if (!pythonexe.endsWith("exe")) {
                if (!pythonexe.endsWith("\\") && !pythonexe.endsWith("/")) {
                    pythonexe += File.separator;
                }
                pythonexe += "python.exe";
            }
            if (exe == null) {
                throw new AbortException(Messages.EXAM_ExecutableNotFound(examTool.getName()));
            }
            args.add(exe);
        }

        File buildFile = new File(exe);
        FilePath buildFilePath = new FilePath(buildFile);

        String dataPath = examTool.getHome();
        String configurationPath = null;
        String examWorkspace = null;
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
            throw new AbortException(Messages.EXAM_NotExamConfigDirectory(configurationFile.getPath()));
        }

        if (workspace != null) {
            args.add("-data", examWorkspace);
        }
        if (configurationPath != null) {
            args.add("-configuration", configurationPath);
        }

        if (examTool != null) {
            examTool.buildEnvVars(env);
        }

        ExamPluginConfig examPluginConfig = Jenkins.getInstance().getDescriptorByType(ExamPluginConfig.class);
        int port = examPluginConfig.getPort();
        args.add("--launcher.appendVmargs", "-vmargs", "-DUSE_CONSOLE=true", "-DRESTAPI=true",
                 "-DRESTAPI_PORT=" + port);

        if (examPluginConfig.getLicenseHost().isEmpty() || examPluginConfig.getLicensePort() == 0) {
            throw new AbortException(Messages.EXAM_LicenseServerNotConfigured());
        }
        args.add("-DLICENSE_PORT=" + examPluginConfig.getLicensePort(),
                 "-DLICENSE_HOST=" + examPluginConfig.getLicenseHost());

        if (javaOpts != null) {
            env.put("JAVA_OPTS", env.expand(javaOpts));
            args.add(javaOpts.split(" "));
        }

        if (!launcher.isUnix()) {
            args = toWindowsCommand(args.toWindowsCommand());
        }

        long startTime = System.currentTimeMillis();
        try {
            ExamConsoleAnnotator eca = new ExamConsoleAnnotator(listener.getLogger(), run.getCharset());
            ExamConsoleErrorOut examErr = new ExamConsoleErrorOut(listener.getLogger());
            boolean ret = true;
            String slaveIp = Remote.getIP(launcher);
            ClientRequest clientRequest = new ClientRequest(listener.getLogger(),
                                                            "http://" + slaveIp + ":" + port + "/examRest");
            try {

/*                Launcher.ProcStarter process = launcher.launch().cmds(args).envs(env).pwd(buildFilePath.getParent());
                if (clientRequest.isApiAvailable()) {
                    listener.getLogger().println("ERROR: EXAM is allready running");
                    throw new AbortException("ERROR: EXAM is allready running");
                }
                process.stderr(examErr);
                process.stdout(eca);
                process.start();
*/
                ret = clientRequest.connectClient(5 * 60 * 1000);
                if (ret) {
                    ApiVersion apiVersion = clientRequest.getApiVersion();
                    listener.getLogger().println("EXAM api version: " + apiVersion.toString());
                    TestConfiguration tc = createTestConfiguration();
                    tc.setPythonPath(pythonexe);
                    FilterConfiguration fc = new FilterConfiguration();

                    for (TestrunFilter filter : testrunFilter) {
                        fc.addTestrunFilter(new jenkins.internal.data.TestrunFilter(filter.name, filter.value,
                                                                                    Boolean.valueOf(filter.adminCases),
                                                                                    Boolean.valueOf(
                                                                                            filter.activateTestcases)));
                    }

                    if (isClearWorkspace()) {
                        clientRequest.clearWorkspace(tc.getModelProject().getModelName());
                    }
                    clientRequest.clearWorkspace(tc.getReportProject().getProjectName());
                    if (!testrunFilter.isEmpty()) {
                        clientRequest.setTestrunFilter(fc);
                    }
                    clientRequest.startTestrun(tc);

                    clientRequest.waitForTestrunEnds(run.getExecutor());
                    clientRequest.convert(tc.getReportProject().getProjectName());

                    hash = "__" + RandomStringUtils.random(5, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray());
                    source = source.child("reports").child(tc.getReportProject().getProjectName()).child("junit");
                    target = target.child("test-reports").child(tc.getModelProject().getProjectName() + hash);
                    source.copyRecursiveTo(target);
                }
            } catch (Exception e) {
                throw new AbortException("ERROR: " + e.toString());
            } finally {
                eca.forceEol();
                //                clientRequest.disconnectClient(60 * 1000);
            }
            return;
        } catch (IOException e) {
            Util.displayIOException(e, listener);

            String errorMessage = Messages.EXAM_ExecFailed();
            if (examTool == null && (System.currentTimeMillis() - startTime) < 1000) {
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
            throw new AbortException(errorMessage);
        }
    }

    /**
     * Backward compatibility by checking the number of parameters
     */
    protected static ArgumentListBuilder toWindowsCommand(ArgumentListBuilder args) {
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

    private ExamReportConfig getReport(String name) {
        for (ExamReportConfig rConfig : getDescriptor().getReportConfigs()) {
            if (rConfig.getName().equalsIgnoreCase(name)) {
                return rConfig;
            }
        }
        return null;
    }

    abstract TestConfiguration addDataToTestConfiguration(TestConfiguration testConfiguration) throws AbortException;

    @Override
    public ExamTask.DescriptorExamTask getDescriptor() {
        return (ExamTask.DescriptorExamTask) super.getDescriptor();
    }

    private TestConfiguration createTestConfiguration() throws AbortException {
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

        TestConfiguration tc = new TestConfiguration();
        tc.setUseExecutionFile(Boolean.valueOf(useExecutionFile));
        tc.setReportProject(rep);
        tc.setModelConfig("");
        tc.setSystemConfig(systemConfiguration);
        tc.setTestObject("");
        tc.setReportPrefix(reportPrefix);

        if (pdfReport && !pdfReportTemplate.isEmpty()) {
            tc.setPdfReportTemplate(pdfReportTemplate);
            tc.setPdfSelectFilter(pdfSelectFilter);
            tc.setPdfMeasureImages(pdfMeasureImages);
        }

        tc.setLogLevelTC(RestAPILogLevelEnum.valueOf(loglevelTestCtrl));
        tc.setLogLevelTL(RestAPILogLevelEnum.valueOf(loglevelTestLogic));
        tc.setLogLevelLC(RestAPILogLevelEnum.valueOf(loglevelLibCtrl));

        tc = addDataToTestConfiguration(tc);
        return tc;
    }

    protected static class DescriptorExamTask extends BuildStepDescriptor<Builder> implements ExamDescriptor,
                                                                                              Serializable {

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
                        (PythonInstallation o1, PythonInstallation o2) -> o1.getName()
                                                                            .compareToIgnoreCase(o2.getName()));
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
