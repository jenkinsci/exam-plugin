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
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tools.ToolInstallation;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.internal.Util;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ant launcher.
 *
 * @author Kohsuke Kawaguchi
 */
public class ExamTaskExecutionFile extends ExamTask {

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

    @DataBoundConstructor
    public ExamTaskExecutionFile(String examName, String pythonName, String examReport, String systemConfiguration) {
        super(examName, pythonName, examReport, systemConfiguration);
        setUseExecutionFile(true);
    }

    TestConfiguration addDataToTestConfiguration(TestConfiguration tc) throws AbortException {

        tc.setPathPCode(pathPCode);
        tc.setTestObject(pathExecutionFile);

        return tc;
    }


    @Override
    public ExamTaskExecutionFile.DescriptorExamTaskExecutionFile getDescriptor() {
        return (ExamTaskExecutionFile.DescriptorExamTaskExecutionFile) super.getDescriptor();
    }

    @Extension
    @Symbol("examTest_ExecutionFile")
    public static class DescriptorExamTaskExecutionFile extends DescriptorExamTask {

        public String getDisplayName() {
            return Messages.EXAM_DisplayNameExecutionFile();
        }

        public FormValidation doCheckSystemConfiguration(@QueryParameter String value) {
            String[] splitted = value.trim().split(" ");
            if(splitted.length != 2){
                return FormValidation.error(Messages.EXAM_RegExSysConf());
            }
            if(!Util.isUuidValid(splitted[0])){
                return FormValidation.error(Messages.EXAM_RegExSysConf());
            }
            return FormValidation.ok();
        }

        public String getDefaultLogLevel() {
            return super.getDefaultLogLevel();
        }
    }
}
