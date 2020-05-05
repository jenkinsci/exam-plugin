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
import hudson.Extension;
import hudson.Util;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.internal.data.ModelConfiguration;
import jenkins.internal.data.TestConfiguration;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.task._exam.Messages;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Exam Model launcher.
 *
 * @author Thomas Reinicke
 */
public class ExamTaskModel extends ExamTask {
    
    private static final long serialVersionUID = 7181777487768961676L;
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
    
    /**
     * Constructor of ExamTaskModel
     *
     * @param examName            examName
     * @param pythonName          pythonName
     * @param examReport          examReport
     * @param executionFile       executionFile
     * @param systemConfiguration systemConfiguration
     */
    @DataBoundConstructor
    public ExamTaskModel(String examName, String pythonName, String examReport, String executionFile,
            String systemConfiguration) {
        super(examName, pythonName, examReport, systemConfiguration);
        this.executionFile = Util.fixEmptyAndTrim(executionFile);
        setUseExecutionFile(false);
    }
    
    public String getExamModel() {
        return examModel;
    }
    
    @DataBoundSetter
    public void setExamModel(String examModel) {
        this.examModel = examModel;
    }
    
    public String getExecutionFile() {
        return executionFile;
    }
    
    @DataBoundSetter
    public void setExecutionFile(String executionFile) {
        this.executionFile = executionFile;
    }
    
    public String getModelConfiguration() {
        return modelConfiguration;
    }
    
    @DataBoundSetter
    public void setModelConfiguration(String modelConfiguration) {
        this.modelConfiguration = modelConfiguration;
    }
    
    protected TestConfiguration addDataToTestConfiguration(TestConfiguration tc, EnvVars env) throws AbortException {
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
        tc.setTestObject(executionFile);
        
        return tc;
    }
    
    @Override
    public ExamTaskModel.DescriptorExamTaskModel getDescriptor() {
        return (ExamTaskModel.DescriptorExamTaskModel) super.getDescriptor();
    }
    
    /**
     * The Descriptor of DescriptorExamTaskModel
     */
    @Extension
    @Symbol("examTest_Model")
    public static class DescriptorExamTaskModel extends DescriptorExamTask {
        
        private static final long serialVersionUID = -4147581220669578429L;
        
        /**
         * @return the EXAM display name
         */
        @Nonnull
        public String getDisplayName() {
            return Messages.EXAM_DisplayNameModel();
        }
        
        /**
         * @return the default log level
         */
        public String getDefaultLogLevel() {
            return super.getDefaultLogLevel();
        }
        
        /**
         * Validates the parameter SystemConfig. Checks if it is an id, uuid or
         * exam fullscopename
         *
         * @param value String
         *
         * @return FormValidation
         */
        public FormValidation doCheckSystemConfiguration(@QueryParameter String value) {
            return jenkins.internal.Util.validateElementForSearch(value);
        }
        
        /**
         * Validates the parameter ExecutionFile. Checks if it is an id, uuid or
         * exam fullscopename
         *
         * @param value String
         *
         * @return FormValidation
         */
        public FormValidation doCheckExecutionFile(@QueryParameter String value) {
            return jenkins.internal.Util.validateElementForSearch(value);
        }
        
        /**
         * fills the ListBoxModel with all ExamModelConfigs
         *
         * @return ListBoxModel
         */
        public ListBoxModel doFillExamModelItems() {
            ListBoxModel items = new ListBoxModel();
            List<ExamModelConfig> models = getModelConfigs();
            models.sort((ExamModelConfig o1, ExamModelConfig o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
            
            for (ExamModelConfig model : models) {
                items.add(model.getDisplayName(), model.getName());
            }
            return items;
        }
    }
}
