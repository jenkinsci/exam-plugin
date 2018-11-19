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
import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tools.ToolInstallation;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
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
import jenkins.plugins.shiningpanda.tools.PythonInstallation;
import jenkins.task._exam.Messages;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.util.Arrays;
import java.util.List;

/**
 * Ant launcher.
 *
 * @author Kohsuke Kawaguchi
 */
public class Exam extends ExamTask {


    private String useExecutionFile;

    /**
     * Identifies {@link jenkins.plugins.exam.config.ExamModelConfig} to be used.
     */
    private String examModel;

    /**
     * Definiert den Pfad zum ExecutionFile
     */
    private String executionFile;

    /**
     * Definiert die ModelConfiguration
     */
    private String modelConfiguration;

    private String pathExecutionFile;
    private String pathPCode;


    public String getPathExecutionFile() {
        return pathExecutionFile;
    }

    @DataBoundSetter
    public void setPathExecutionFile(String pathExecutionFile) {
        this.pathExecutionFile = pathExecutionFile;
    }

    public String getPathPCode() {
        return pathPCode;
    }

    @DataBoundSetter
    public void setPathPCode(String pathPCode) {
        this.pathPCode = pathPCode;
    }

    public String getUseExecutionFile() {
        return useExecutionFile;
    }

    @DataBoundSetter
    public void setExamModel(String examModel) {
        this.examModel = examModel;
    }

    @DataBoundSetter
    public void setExecutionFile(String executionFile) {
        this.executionFile = executionFile;
    }

    @DataBoundSetter
    public void setUseExecutionFile(String useExecutionFile) {
        this.useExecutionFile = useExecutionFile;
    }

    @DataBoundSetter
    public void setModelConfiguration(String modelConfiguration) {
        this.modelConfiguration = modelConfiguration;
    }

    @DataBoundConstructor
    public Exam(String examName, String pythonName, String examReport, String executionFile,
                String systemConfiguration) {
        this.examName = examName;
        this.pythonName = pythonName;
        this.examReport = examReport;
        this.executionFile = Util.fixEmptyAndTrim(executionFile);
        this.systemConfiguration = Util.fixEmptyAndTrim(systemConfiguration);
    }

    public String getExamModel() {
        return examModel;
    }

    public String getExecutionFile() {
        return executionFile;
    }

    public String getModelConfiguration() {
        return modelConfiguration;
    }

    public ExamTool.DescriptorImpl getToolDescriptor() {
        return ToolInstallation.all().get(ExamTool.DescriptorImpl.class);
    }

    private ExamModelConfig getModel(String name) {
        for (ExamModelConfig mConfig : getDescriptor().getModelConfigs()) {
            if (mConfig.getName().equalsIgnoreCase(name)) {
                return mConfig;
            }
        }
        return null;
    }

    TestConfiguration addDataToTestConfiguration(TestConfiguration tc) throws AbortException {
        ModelConfiguration mod = new ModelConfiguration();
        ExamModelConfig m = getModel(examModel);
        if (m == null) {
            throw new AbortException("ERROR: no model configured with name: " + examModel);
        }
        mod.setProjectName(m.getName());
        mod.setModelName(m.getModelName());
        mod.setTargetEndpoint(m.getTargetEndpoint());
        mod.setModelConfigUUID(modelConfiguration);

        tc.setModelProject(mod);

        return tc;
    }

    @Extension
    @Symbol("examTest")
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> implements ExamDescriptor {

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

        public String getDefaultLogLevel() {
            return RestAPILogLevelEnum.INFO.name();
        }

        public RestAPILogLevelEnum[] getLogLevels() {
            return RestAPILogLevelEnum.values();
        }

        public FormValidation doCheckExecutionFile(@QueryParameter String value) {
            return jenkins.internal.Util.validateElementForSearch(value);
        }

        public FormValidation doCheckSystemConfiguration(@QueryParameter String value) {
            return jenkins.internal.Util.validateElementForSearch(value);
        }

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public ExamTool[] getInstallations() {
            return Jenkins.getInstance().getDescriptorByType(ExamTool.DescriptorImpl.class).getInstallations();
        }


        public PythonInstallation[] getPythonInstallations() {
            return Jenkins.getInstance().getDescriptorByType(PythonInstallation.DescriptorImpl.class).getInstallations();
        }

        public List<ExamModelConfig> getModelConfigs() {
            return Jenkins.getInstance().getDescriptorByType(ExamPluginConfig.class)
                    .getModelConfigs();
        }

        private List<ExamReportConfig> addNoReport(List<ExamReportConfig> reports){
            List<ExamReportConfig> lReportConfigs = reports;
            boolean found = false;
            for(ExamReportConfig config : reports){
                if(config.getName().compareTo(ReportConfiguration.NO_REPORT) == 0){
                    found = true;
                    break;
                }
            }
            if(!found){
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

        public ListBoxModel doFillExamNameItems(){
            ListBoxModel items = new ListBoxModel();
            ExamTool[] examTools = getInstallations();

            Arrays.sort(examTools, (ExamTool o1, ExamTool o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
            for (ExamTool tool : examTools) {
                items.add(tool.getName(), tool.getName());
            }
            return items;
        }

        public ListBoxModel doFillPythonNameItems(){
            ListBoxModel items = new ListBoxModel();
            PythonInstallation[] pythonTools = getPythonInstallations();

            Arrays.sort(pythonTools, (PythonInstallation o1, PythonInstallation o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
            for (PythonInstallation tool : pythonTools) {
                items.add(tool.getName(), tool.getName());
            }
            return items;
        }

        public ListBoxModel doFillExamModelItems(){
            ListBoxModel items = new ListBoxModel();
            List<ExamModelConfig> models = getModelConfigs();
            models.sort((ExamModelConfig o1, ExamModelConfig o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

            for (ExamModelConfig model : models) {
                items.add(model.getDisplayName(), model.getName());
            }
            return items;
        }

        public ListBoxModel doFillExamReportItems(){
            ListBoxModel items = new ListBoxModel();
            List<ExamReportConfig> reports = getReportConfigs();
            reports.sort((ExamReportConfig o1, ExamReportConfig o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

            for (ExamReportConfig report : reports) {
                items.add(report.getDisplayName(), report.getName());
            }
            return items;
        }

        private ListBoxModel getLoglevelItems(){
            ListBoxModel items = new ListBoxModel();
            for (RestAPILogLevelEnum loglevel : getLogLevels()) {
                items.add(loglevel.name(), loglevel.name());
            }
            return items;
        }

        public ListBoxModel doFillLoglevelTestCtrlItems(){
            return getLoglevelItems();
        }

        public ListBoxModel doFillLoglevelTestLogicItems(){
            return getLoglevelItems();
        }

        public ListBoxModel doFillLoglevelLibCtrlItems(){
            return getLoglevelItems();
        }
    }
}
