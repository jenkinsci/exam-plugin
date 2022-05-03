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

import hudson.Util;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.internal.ClientRequest;
import jenkins.internal.descriptor.TaskDescriptor;
import jenkins.model.Jenkins;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.plugins.exam.config.ExamPluginConfig;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Task extends Builder implements Serializable {
    
    private static final long serialVersionUID = 8046565551678421640L;
    /**
     * JAVA_OPTS if not null.
     */
    private String javaOpts;
    /**
     * Identifies {@link ExamTool} to be used.
     */
    protected String examName;
    /**
     * timeout if not null.
     */
    private int timeout;
    
    protected ExamTaskHelper taskHelper;
    
    /**
     * Gets the ExamTaskHelper parameter.
     *
     * @return ExamTaskHelper
     */
    public ExamTaskHelper getTaskHelper() {
        if (taskHelper == null) {
            taskHelper = new ExamTaskHelper();
        }
        return taskHelper;
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
        if (timeout <= 0) {
            Jenkins instanceOrNull = Jenkins.getInstanceOrNull();
            assert instanceOrNull != null;
            ExamPluginConfig examPluginConfig = instanceOrNull.getDescriptorByType(ExamPluginConfig.class);
            return examPluginConfig.getTimeout();
        }
        
        return timeout;
    }
    
    @DataBoundSetter
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public String getExamName() {
        return examName;
    }
    
    abstract protected void doExecuteTask(ClientRequest clientRequest) throws IOException, InterruptedException;
    
    @Override
    public Task.DescriptorTask getDescriptor() {
        return (Task.DescriptorTask) super.getDescriptor();
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
    
    @Nullable
    protected ExamModelConfig getModel(String name) {
        for (ExamModelConfig mConfig : getDescriptor().getModelConfigs()) {
            if (mConfig.getName().equalsIgnoreCase(name)) {
                return mConfig;
            }
        }
        return null;
    }
    
    /**
     * The Descriptor of DescriptorExamTask
     */
    public static class DescriptorTask extends BuildStepDescriptor<Builder>
            implements TaskDescriptor, Serializable {
        
        private static final long serialVersionUID = 3498164788527439572L;
        
        /**
         * Constructor of this Descriptor
         */
        public DescriptorTask() {
            load();
        }
        
        /**
         * Constructor of this Descriptor
         *
         * @param clazz Class
         */
        protected DescriptorTask(Class<? extends Task> clazz) {
            super(clazz);
            load();
        }
        
        /**
         * is applicable for all job types
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
         * @return all ExamModelConfigs
         */
        public List<ExamModelConfig> getModelConfigs() {
            Jenkins instanceOrNull = Jenkins.getInstanceOrNull();
            if (instanceOrNull == null) {
                return new ArrayList<>();
            }
            return instanceOrNull.getDescriptorByType(ExamPluginConfig.class).getModelConfigs();
        }
    }
}
