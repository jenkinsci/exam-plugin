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

import hudson.EnvVars;
import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.internal.Util;
import jenkins.internal.data.TestConfiguration;
import jenkins.task._exam.Messages;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Ant launcher.
 *
 * @author Kohsuke Kawaguchi
 */
public class ExamTaskExecutionFile extends ExamTask {

    private String pathExecutionFile;
    private String pathPCode;

    @DataBoundConstructor
    public ExamTaskExecutionFile(String examName, String pythonName, String examReport, String systemConfiguration) {
        super(examName, pythonName, examReport, systemConfiguration);
        setUseExecutionFile(true);
    }

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

    TestConfiguration addDataToTestConfiguration(TestConfiguration tc, @Nullable EnvVars env) {

        tc.setPathPCode(Util.replaceEnvVars(pathPCode, env));
        tc.setTestObject(Util.replaceEnvVars(pathExecutionFile, env));

        return tc;
    }

    @Override
    public ExamTaskExecutionFile.DescriptorExamTaskExecutionFile getDescriptor() {
        return (ExamTaskExecutionFile.DescriptorExamTaskExecutionFile) super.getDescriptor();
    }

    @Extension
    @Symbol("examTest_ExecutionFile")
    public static class DescriptorExamTaskExecutionFile extends DescriptorExamTask {

        private static final long serialVersionUID = 8392999844814000476L;

        @Nonnull
        public String getDisplayName() {
            return Messages.EXAM_DisplayNameExecutionFile();
        }

        public FormValidation doCheckSystemConfiguration(@QueryParameter String value) {
            String newLine = "\r\n";
            StringBuilder errorMsg = new StringBuilder();
            String[] splitted = value.trim().split(" ");
            boolean errorFound = false;
            if (splitted.length != 2) {
                errorMsg.append(Messages.EXAM_RegExSysConf());
                errorMsg.append(newLine);
                errorFound = true;
            }
            if (splitted.length >= 2 && !Util.isUuidValid(splitted[0])) {
                errorMsg.append(Messages.EXAM_RegExUuid());
                errorMsg.append(newLine);
                errorFound = true;
            }
            if (splitted.length >= 2 && !Util.isPythonConformName(splitted[1])) {
                errorMsg.append(Messages.EXAM_RegExPython());
                errorMsg.append(newLine);
                errorFound = true;
            }
            if (errorFound) {
                errorMsg.deleteCharAt(errorMsg.length() - 1);
                errorMsg.deleteCharAt(errorMsg.length() - 1);
                return FormValidation.error(errorMsg.toString());
            }
            return FormValidation.ok();
        }

        public String getDefaultLogLevel() {
            return super.getDefaultLogLevel();
        }
    }
}
