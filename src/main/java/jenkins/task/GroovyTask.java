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

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Executor;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import jenkins.internal.ClientRequest;
import jenkins.internal.Util;
import jenkins.internal.data.ApiVersion;
import jenkins.internal.data.GroovyConfiguration;
import jenkins.internal.data.ModelConfiguration;
import jenkins.internal.descriptor.ExamModelDescriptorTask;
import jenkins.task._exam.Messages;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Exam Groovy launcher.
 *
 * @author koblofsky
 */
public class GroovyTask extends Task implements SimpleBuildStep {

    private static final long serialVersionUID = 2641943348736414442L;
    /**
     * the script, and if specified the startElement, as ID, UUID, or FullScopedName
     */
    private String script;
    private String startElement;
    private boolean useStartElement;

    /**
     * Constructor of GroovyTask
     *
     * @param examName           examName
     * @param examModel          examModel
     * @param modelConfiguration modelConfiguration
     * @param script             script
     * @param startElement       startElement
     */
    @DataBoundConstructor
    public GroovyTask(String script, String startElement, String examName, String examModel,
                      String modelConfiguration) {
        this.script = script;
        this.startElement = startElement;
        this.examName = examName;
        this.examModel = examModel;
        this.modelConfiguration = modelConfiguration;
    }

    public String getStartElement() {
        return startElement;
    }

    @DataBoundSetter
    public void setUseStartElement(boolean useStartElement) {
        this.useStartElement = useStartElement;
    }

    public boolean isUseStartElement() {
        return useStartElement;
    }

    @DataBoundSetter
    public void setStartElement(String startElement) {
        this.startElement = startElement;
    }

    public String getScript() {
        return script;
    }

    @DataBoundSetter
    public void setScript(String script) {
        this.script = script;
    }

    public String getModelConfiguration() {
        return modelConfiguration;
    }

    @DataBoundSetter
    public void setModelConfiguration(String modelConfiguration) {
        this.modelConfiguration = modelConfiguration;
    }

    public String getExamModel() {
        return examModel;
    }

    @DataBoundSetter
    public void setExamModel(String examModel) {
        this.examModel = examModel;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher,
                        @Nonnull TaskListener taskListener) throws IOException, InterruptedException {
        Executor runExecutor = run.getExecutor();
        assert runExecutor != null;

        // prepare environment
        getTaskHelper().setRun(run);
        getTaskHelper().setWorkspace(workspace);
        getTaskHelper().setLauncher(launcher);
        getTaskHelper().setTaskListener(taskListener);

        getTaskHelper().perform(this, launcher, new ApiVersion(1, 0, 2));
    }

    protected void doExecuteTask(ClientRequest clientRequest) throws IOException, InterruptedException {
        if (clientRequest.isClientConnected()) {
            ModelConfiguration modelConfig = createModelConfig();
            GroovyConfiguration config = createGroovyConfig();

            clientRequest.clearWorkspace(null);
            clientRequest.createExamProject(modelConfig);
            clientRequest.executeGroovyScript(config);
        }
    }

    private GroovyConfiguration createGroovyConfig() {
        GroovyConfiguration config = new GroovyConfiguration();
        config.setScript(getScript());
        if (isUseStartElement()) {
            config.setStartElement(getStartElement());
        } else {
            config.setStartElement("");
        }

        return config;
    }

    /**
     * The Descriptor of DescriptorGroovyTask
     */
    @Extension
    @Symbol("examRun_Groovy")
    public static class DescriptorGroovyTask extends ExamModelDescriptorTask {

        private static final long serialVersionUID = 4277406576918447167L;

        /**
         * @return the EXAM Groovy display name
         */
        @Nonnull
        public String getDisplayName() {
            return Messages.EXAM_RunGroovyTask();
        }

        /**
         * Constructor of this Descriptor
         */
        public DescriptorGroovyTask() {
            load();
        }

        /**
         * Validates the parameter Script. Checks if it is an id, uuid or
         * exam fullscopename
         *
         * @param value String
         * @return FormValidation
         */
        public FormValidation doCheckScript(@QueryParameter String value) {
            return Util.validateElementForSearch(value);
        }

        /**
         * Validates the parameter StartElement. Checks if it is an id, uuid or
         * exam fullscopename
         *
         * @param value String
         * @return FormValidation
         */
        public FormValidation doCheckStartElement(@QueryParameter String value) {
            return Util.validateElementForSearch(value);
        }
    }
}
