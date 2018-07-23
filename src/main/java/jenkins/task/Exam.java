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
package jenkins.task;

import hudson.*;
import hudson.Launcher.ProcStarter;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tools.ToolInstallation;
import hudson.util.ArgumentListBuilder;
import hudson.util.FormValidation;
import jenkins.internal.ClientRequest;
import jenkins.internal.data.FilterConfiguration;
import jenkins.internal.data.ModelConfiguration;
import jenkins.internal.data.ReportConfiguration;
import jenkins.internal.data.TestConfiguration;
import jenkins.internal.descriptor.ExamDescriptor;
import jenkins.internal.enumeration.RestAPILogLevelEnum;
import jenkins.model.Jenkins;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.plugins.exam.config.ExamPluginConfig;
import jenkins.plugins.exam.config.ExamReportConfig;
import jenkins.task._exam.ExamConsoleAnnotator;
import jenkins.task._exam.ExamConsoleErrorOut;
import jenkins.task._exam.Messages;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ant launcher.
 *
 * @author Kohsuke Kawaguchi
 */
public class Exam extends Builder implements SimpleBuildStep{

    /**
     * Identifies {@link ExamTool} to be used.
     */
    private final String examName;

    /**
     * Identifies {@link jenkins.plugins.exam.config.ExamModelConfig} to be used.
     */
    private final String examModel;

    /**
     * Identifies {@link jenkins.plugins.exam.config.ExamReportConfig} to be used.
     */
    private final String examReport;

    /**
     * Definiert den Report Prefix
     */
    private String reportPrefix;

    /**
     * JAVA_OPTS if not null.
     */
    private final String javaOpts;

    /**
     * Definiert den Pfad zum ExecutionFile
     */
    private final String executionFile;

    /**
     * Definiert die default SystemConfiguration
     */
    private final String systemConfiguration;

    private List<TestrunFilter> testrunFilter = new ArrayList<TestrunFilter>();

    /**
     * Definiert die default pythonPath
     */
    private final String pythonPath;

    private boolean logging;
    private String loglevel_test_ctrl = getDescriptor().getDefaultLogLevel();
    private String loglevel_test_logic = getDescriptor().getDefaultLogLevel();
    private String loglevel_lib_ctrl = getDescriptor().getDefaultLogLevel();

    private boolean clearWorkspace;

    public String getReportPrefix() {
        return reportPrefix;
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

    public String getLoglevel_test_ctrl() {
        return loglevel_test_ctrl;
    }

    @DataBoundSetter
    public void setLoglevel_test_ctrl(String loglevel_test_ctrl) {
        this.loglevel_test_ctrl = loglevel_test_ctrl;
    }

    public String getLoglevel_test_logic() {
        return loglevel_test_logic;
    }

    @DataBoundSetter
    public void setLoglevel_test_logic(String loglevel_test_logic) {
        this.loglevel_test_logic = loglevel_test_logic;
    }

    public String getLoglevel_lib_ctrl() {
        return loglevel_lib_ctrl;
    }

    @DataBoundSetter
    public void setLoglevel_lib_ctrl(String loglevel_lib_ctrl) {
        this.loglevel_lib_ctrl = loglevel_lib_ctrl;
    }

    @DataBoundSetter
    public void setClearWorkspace(boolean clearWorkspace) {
        this.clearWorkspace = clearWorkspace;
    }


    @DataBoundConstructor public Exam(String examName, String examModel, String examReport, String javaOpts,
            String executionFile, String systemConfiguration, String pythonPath) {
        this.examName = examName;
        this.examModel = examModel;
        this.examReport = examReport;
        this.javaOpts = Util.fixEmptyAndTrim(javaOpts);
        this.executionFile = Util.fixEmptyAndTrim(executionFile);
        this.systemConfiguration = Util.fixEmptyAndTrim(systemConfiguration);
        this.pythonPath = pythonPath;
    }

    public String getExamName() {
        return examName;
    }

    public String getExamModel() {
        return examModel;
    }

    public String getExamReport() {
        return examReport;
    }

    public String getExecutionFile() {
        return executionFile;
    }

    public String getSystemConfiguration() {
        return systemConfiguration;
    }

    public String getPythonPath() {
        return pythonPath;
    }

    public boolean isClearWorkspace() {
        return clearWorkspace;
    }

    /**
     * Gets the EXAM to invoke, or null to invoke the default one.
     */
    public ExamTool getExam() {
        for (ExamTool i : getDescriptor().getInstallations()) {
            if (examName != null && examName.equals(i.getName()))
                return i;
        }
        return null;
    }

    public ExamTool.DescriptorImpl getToolDescriptor() {
        return ToolInstallation.all().get(ExamTool.DescriptorImpl.class);
    }

    /**
     * Gets the JAVA_OPTS parameter, or null.
     */
    public String getJavaOpts() {
        return javaOpts;
    }


    @Override public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher,
            @Nonnull TaskListener listener) throws InterruptedException, IOException {

        ArgumentListBuilder args = new ArgumentListBuilder();

        EnvVars env = run.getEnvironment(listener);

        ExamTool examTool = getExam();
        String exe = "EXAM.exe";
        if (examTool == null) {
            args.add("EXAM.exe");
        } else {
            Node node = Computer.currentComputer().getNode();
            if (node == null) {
                throw new AbortException(Messages.EXAM_NodeOffline());
            }
            examTool = examTool.forNode(node, listener);
            //examTool = examTool.forEnvironment(env);
            exe = examTool.getExecutable(launcher);
            if (exe == null) {
                throw new AbortException(Messages.EXAM_ExecutableNotFound(examTool.getName()));
            }
            args.add(exe);
        }

        File buildFile = new File(exe);
        FilePath buildFilePath = new FilePath(buildFile);

        String configuration = null;
        String dataPath = examTool.getHome();
        String configurationPath = null;
        String examWorkspace = null;
        String relativeDataPath = examTool.getRelativeConfigPath();
        if (relativeDataPath != null && !relativeDataPath.trim().isEmpty()) {
            dataPath = examTool.getHome() + File.separator + relativeDataPath;
        }
        configurationPath = dataPath + File.separator + "configuration";
        examWorkspace = dataPath + File.separator + "workspace_restApi";

        File configurationFile = new File(dataPath + File.separator + "configuration" + File.separator + "config.ini");
        if (!configurationFile.exists() || configurationFile.isDirectory()) {
            throw new AbortException(Messages.EXAM_NotExamConfigDirectory(configuration));
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


        int port = Jenkins.getInstance().getDescriptorByType(ExamPluginConfig.class).getPort();
        args.add("--launcher.appendVmargs","-vmargs","-DUSE_CONSOLE=true","-DRESTAPI=true","-DRESTAPI_PORT=" + port);
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
            ExamConsoleErrorOut examErr = new ExamConsoleErrorOut(listener.getLogger(), run.getCharset());
            boolean ret = true;
            try {
                ClientRequest.setBaseUrl("http://localhost:"+port+"/examRest");
                ClientRequest.setLogger(listener.getLogger());
/*
                ProcStarter process = launcher.launch().cmds(args).envs(env).pwd(buildFilePath.getParent());
                if(ClientRequest.isApiAvailable()){
                    listener.getLogger().println("ERROR: EXAM is allready running");
                    throw new AbortException("ERROR: EXAM is allready running");
                }
                process.stderr(examErr);
                process.stdout(eca);
                process.start();
*/
                ret = ClientRequest.connectClient(30 * 1000);
                if(ret){
                    TestConfiguration tc = createTestConfiguration();
                    FilterConfiguration fc = new FilterConfiguration();
                      for (TestrunFilter filter : testrunFilter) {
                        fc.addTestrunFilter(new jenkins.internal.data.TestrunFilter(filter.name, filter.value, Boolean.valueOf(filter.adminCases),
                                Boolean.valueOf(filter.activateTestcases)));
                    }

                    if(isClearWorkspace()){
                        ClientRequest.clearWorkspace(tc.getModelProject().getModelName());
                        ClientRequest.clearWorkspace(tc.getReportProject().getProjectName());
                    }
                    if(!testrunFilter.isEmpty()) {
                        ClientRequest.setTestrunFilter(fc);
                    }
                    ClientRequest.startTestrun(tc);
                }

                ClientRequest.waitForTestrunEnds(run.getExecutor());
                ClientRequest.disconnectClient(30 * 1000);
            } finally {
                eca.forceEol();
            }
            return;
        } catch (IOException e) {
            Util.displayIOException(e, listener);

            String errorMessage = Messages.EXAM_ExecFailed();
            if (examTool == null && (System.currentTimeMillis() - startTime) < 1000) {
                if (getDescriptor().getInstallations() == null)
                    // looks like the user didn't configure any EXAM
                    // installation
                    errorMessage += Messages.EXAM_GlobalConfigNeeded();
                else
                    // There are EXAM installations configured but the project
                    // didn't pick it
                    errorMessage += Messages.EXAM_ProjectConfigNeeded();
            }
            throw new AbortException(errorMessage);
        }
    }

    private ExamModelConfig getModel(String name){
        for(ExamModelConfig mConfig : getDescriptor().getModelConfigs()){
            if(mConfig.getName().equalsIgnoreCase(name)){
                return mConfig;
            }
        }
        return null;
    }

    private ExamReportConfig getReport(String name){
        for(ExamReportConfig rConfig : getDescriptor().getReportConfigs()){
            if(rConfig.getName().equalsIgnoreCase(name)){
                return rConfig;
            }
        }
        return null;
    }

    private TestConfiguration createTestConfiguration(){
        ModelConfiguration mod = new ModelConfiguration();
        ExamModelConfig m = getModel(examModel);
        mod.setProjectName(m.getName());
        mod.setModelName(m.getModelName());
        mod.setTargetEndpoint(m.getTargetEndpoint());
        mod.setModelConfigUUID("");

        ReportConfiguration rep = new ReportConfiguration();
        ExamReportConfig r = getReport(examReport);
        rep.setDbHost(r.getHost());
        rep.setDbPassword(r.getDbPass());
        rep.setDbPort(Integer.valueOf(r.getPort()));
        rep.setDbSchema(r.getSchema());
        rep.setDbService(r.getServiceOrSid());
        rep.setDbType(r.getDbType());
        rep.setDbUser(r.getDbUser());
        rep.setProjectName(r.getName());

        TestConfiguration tc = new TestConfiguration();
        tc.setModelProject(mod);
        tc.setReportProject(rep);
        tc.setModelConfig("");
        tc.setSystemConfig(systemConfiguration);
        tc.setTestObject(executionFile);
        tc.setReportPrefix(reportPrefix);
        tc.setPythonPath(pythonPath);

        tc.setLogLevel_TC(RestAPILogLevelEnum.valueOf(loglevel_test_ctrl));
        tc.setLogLevel_TL(RestAPILogLevelEnum.valueOf(loglevel_test_logic));
        tc.setLogLevel_LC(RestAPILogLevelEnum.valueOf(loglevel_lib_ctrl));

        return tc;
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

    @Override public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension @Symbol("examTest") public static class DescriptorImpl extends BuildStepDescriptor<Builder>
            implements ExamDescriptor {

        public DescriptorImpl() {
            load();
        }

        protected DescriptorImpl(Class<? extends Exam> clazz) {
            super(clazz);
            load();
        }
        public String getDisplayName() {
            return Messages.EXAM_DisplayName();
        }

        public String getDefaultLogLevel(){
            return RestAPILogLevelEnum.INFO.name();
        }

        public RestAPILogLevelEnum[] getLogLevels(){
            return RestAPILogLevelEnum.values();
        }

        public FormValidation doCheckExecutionFile(@QueryParameter String value) {
            return jenkins.internal.Util.validateUuid(value);
        }

        public FormValidation doCheckSystemConfiguration(@QueryParameter String value) {
            return jenkins.internal.Util.validateUuid(value);
        }

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }


        public ExamTool[] getInstallations() {
            return Jenkins.getInstance().getDescriptorByType(ExamTool.DescriptorImpl.class).getInstallations();
        }

        public ExamModelConfig[] getModelConfigs() {
            ExamModelConfig[] ret = Jenkins.getInstance().getDescriptorByType(ExamPluginConfig.class)
                    .getModelConfigs().toArray(new ExamModelConfig[0]);
            return ret;
        }

        public ExamReportConfig[] getReportConfigs() {
            ExamReportConfig[] ret = Jenkins.getInstance().getDescriptorByType(ExamPluginConfig.class)
                    .getReportConfigs().toArray(new ExamReportConfig[0]);
            return ret;
        }
    }
}
