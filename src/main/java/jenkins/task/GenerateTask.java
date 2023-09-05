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
import hudson.util.ListBoxModel;
import jenkins.internal.ClientRequest;
import jenkins.internal.Compatibility;
import jenkins.internal.Util;
import jenkins.internal.data.ApiVersion;
import jenkins.internal.data.GenerateConfiguration;
import jenkins.internal.data.LegacyGenerateConfiguration;
import jenkins.internal.data.ModelConfiguration;
import jenkins.internal.descriptor.ExamModelDescriptorTask;
import jenkins.internal.enumeration.DescriptionSource;
import jenkins.internal.enumeration.ErrorHandling;
import jenkins.internal.enumeration.StepType;
import jenkins.internal.enumeration.TestCaseState;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.task._exam.Messages;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Exam Generate Testcase Task
 */
public class GenerateTask extends Task implements SimpleBuildStep {

    private static final long serialVersionUID = 2641743348736414442L;

    /**
     * {@link jenkins.internal.data.GenerateConfiguration} properties.
     */
    private String element;
    private boolean overwriteDescriptionSource;
    private String descriptionSource;
    private boolean documentInReport;
    private String errorHandling;
    private boolean overwriteFrameSteps;
    private List<String> frameSteps;
    private boolean overwriteMappingList;
    private String mappingList;
    private List<String> testCaseStates;
    private String variant;
    private boolean setStates;
    private String stateForSuccess;
    private String stateForFail;

    public boolean isSetStates() {
        return setStates;
    }

    @DataBoundSetter
    public void setSetStates(boolean setStates) {
        this.setStates = setStates;
    }

    public String getStateForFail() {
        return stateForFail;
    }

    @DataBoundSetter
    public void setStateForFail(String stateForFail) {
        this.stateForFail = stateForFail;
    }

    public String getStateForSuccess() {
        return stateForSuccess;
    }

    @DataBoundSetter
    public void setStateForSuccess(String stateForSuccess) {
        this.stateForSuccess = stateForSuccess;
    }

    public String getElement() {
        return element;
    }

    @DataBoundSetter
    public void setElement(String element) {
        this.element = element;
    }

    public boolean getOverwriteDescriptionSource() {
        return overwriteDescriptionSource;
    }

    @DataBoundSetter
    public void setOverwriteDescriptionSource(boolean overwriteDescriptionSource) {
        this.overwriteDescriptionSource = overwriteDescriptionSource;
    }

    public boolean getDocumentInReport() {
        return documentInReport;
    }

    public boolean getOverwriteFrameSteps() {
        return overwriteFrameSteps;
    }

    public boolean getOverwriteMappingList() {
        return overwriteMappingList;
    }

    @DataBoundSetter
    public void setOverwriteMappingList(boolean overwriteMappingList) {
        this.overwriteMappingList = overwriteMappingList;
    }

    @DataBoundSetter
    public void setOverwriteFrameSteps(boolean overwriteFrameSteps) {
        this.overwriteFrameSteps = overwriteFrameSteps;
    }


    public String getDescriptionSource() {
        return descriptionSource;
    }

    @DataBoundSetter
    public void setDescriptionSource(String descriptionSource) {
        this.descriptionSource = descriptionSource;
    }

    @DataBoundSetter
    public void setDocumentInReport(boolean documentInReport) {
        this.documentInReport = documentInReport;
    }

    public String getErrorHandling() {
        return errorHandling;
    }

    @DataBoundSetter
    public void setErrorHandling(String errorHandling) {
        this.errorHandling = errorHandling;
    }

    public List<String> getFrameSteps() {
        return frameSteps;
    }

    @DataBoundSetter
    public void setFrameSteps(List<String> frameSteps) {
        this.frameSteps = frameSteps;
    }

    public String getMappingList() {
        return mappingList;
    }

    @DataBoundSetter
    public void setMappingList(String mappingList) {
        this.mappingList = mappingList;
    }

    public List<String> getTestCaseStates() {
        return testCaseStates;
    }

    @DataBoundSetter
    public void setTestCaseStates(List<String> testCaseStates) {
        List<String> tcValues = new ArrayList<>();
        if (testCaseStates.isEmpty()) {
            DescriptorGenerateTask descriptor = (DescriptorGenerateTask) getDescriptor();
            this.testCaseStates = descriptor.getDefaultTestCaseStates();
        } else {
            for (String state : testCaseStates) {
                TestCaseState convert = TestCaseState.get(state);
                if (convert != null) {
                    tcValues.add(convert.getName());
                } else {
                    tcValues.add(state);
                }
            }
            this.testCaseStates = tcValues;
        }
    }

    public String getVariant() {
        return variant;
    }

    @DataBoundSetter
    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getExamModel() {
        return examModel;
    }

    @DataBoundSetter
    public void setExamModel(String examModel) {
        this.examModel = examModel;
    }

    public String getModelConfiguration() {
        return modelConfiguration;
    }

    @DataBoundSetter
    public void setModelConfiguration(String modelConfiguration) {
        this.modelConfiguration = modelConfiguration;
    }

    /**
     * Constructor
     *
     * @param examModel          examModel
     * @param modelConfiguration modelConfiguration
     * @param element            element
     * @param descriptionSource  descriptionSource
     * @param documentInReport   documentInReport
     * @param errorHandling      errorHandling
     * @param frameSteps         frameFunctions
     * @param mappingList        mappingList
     * @param testCaseStates     testCaseStates
     * @param variant            variant
     */
    @DataBoundConstructor
    public GenerateTask(String examModel, String examName, String modelConfiguration, String element, String descriptionSource, boolean documentInReport, String errorHandling, List<String> frameSteps, String mappingList, List<String> testCaseStates, String variant, boolean setStates, String stateForFail, String stateForSuccess) {
        this.examModel = examModel;
        this.examName = examName;
        this.modelConfiguration = modelConfiguration;

        this.element = element;
        this.descriptionSource = descriptionSource;
        this.documentInReport = documentInReport;
        this.errorHandling = errorHandling;
        this.variant = variant;

        this.frameSteps = frameSteps;
        this.mappingList = mappingList;
        this.setStates = setStates;
        this.stateForFail = stateForFail;
        this.stateForSuccess = stateForSuccess;
        setTestCaseStates(testCaseStates);
    }

    @Override
    protected void doExecuteTask(ClientRequest clientRequest) throws IOException, InterruptedException {
        if (clientRequest.isClientConnected()) {
            ModelConfiguration modelConfig = createModelConfig();
            ApiVersion tcgVersion = clientRequest.getTCGVersion();
            clientRequest.createExamProject(modelConfig);

            // check tcg api version
            TaskListener listener = getTaskHelper().getTaskListener();
            boolean isApiCompatible = Compatibility.checkMinTCGVersion(listener, new ApiVersion(2, 0, 3), tcgVersion);
            if (isApiCompatible) {
                GenerateConfiguration config = generateNewConfig();
                clientRequest.generateTestcasesPost203(config);
            } else {
                LegacyGenerateConfiguration generateConfiguration = createGenerateConfig();
                clientRequest.generateTestcases(generateConfiguration);
            }
        }
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        Executor runExecutor = run.getExecutor();
        assert runExecutor != null;

        // prepare environment
        getTaskHelper().setRun(run);
        getTaskHelper().setWorkspace(workspace);
        getTaskHelper().setLauncher(launcher);
        getTaskHelper().setTaskListener(listener);

        getTaskHelper().perform(this, launcher, new ApiVersion(1, 0, 3));
    }

    private LegacyGenerateConfiguration createGenerateConfig() {
        LegacyGenerateConfiguration configuration = new LegacyGenerateConfiguration();
        configuration.setElement(getElement());
        DescriptionSource dc = DescriptionSource.valueOf(getDescriptionSource());
        configuration.setDescriptionSource(dc.getDisplayString());
        configuration.setDocumentInReport(getDocumentInReport());
        ErrorHandling eh = ErrorHandling.valueOf(getErrorHandling());
        configuration.setErrorHandling(eh.displayString());
        configuration.setFrameFunctions(getFrameSteps());
        configuration.setMappingList(convertToList(getMappingList()));

        configuration.setTestCaseStates(getTestCaseStates());
        configuration.setVariant(getVariant());

        return configuration;
    }

    private GenerateConfiguration generateNewConfig() {
        GenerateConfiguration configuration = new GenerateConfiguration();
        configuration.setElement(getElement());
        configuration.setOverwriteDescriptionSource(getOverwriteDescriptionSource());
        if (!getOverwriteDescriptionSource()) {
            this.descriptionSource = "";
        }
        configuration.setDescriptionSource(getDescriptionSource());
        configuration.setDocumentInReport(getDocumentInReport());
        configuration.setErrorHandling(getErrorHandling());
        configuration.setOverwriteFrameSteps(getOverwriteFrameSteps());
        if (!getOverwriteFrameSteps()) {
            this.frameSteps = new ArrayList<>();
        }
        configuration.setFrameFunctions(getFrameSteps());
        configuration.setOverwriteMappingList(getOverwriteMappingList());
        if (!getOverwriteMappingList()) {
            this.mappingList = "";
        }
        configuration.setMappingList(convertToList(getMappingList()));
        configuration.setTestCaseStates(getTestCaseStates());
        configuration.setVariant(getVariant());
        configuration.setSetStates(isSetStates());
        TestCaseState fail = TestCaseState.get(getStateForFail());
        configuration.setStateForFail(fail.getName());
        TestCaseState success = TestCaseState.get(getStateForSuccess());
        configuration.setStateForSuccess(success.getName());

        return configuration;
    }

    private List<String> convertToList(String list) {
        if (list == null || list.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String[] split = list.split(",");
        return Arrays.asList(split);
    }

    public boolean isTestCaseStateSelected(String value) {
        return this.testCaseStates.contains(value);
    }

    public boolean isFrameStepsSelected(String value) {
        return this.frameSteps.contains(value);
    }

    /**
     * The Descriptor of the GenerateTask
     */
    @Extension
    @Symbol("examTCG")
    public static class DescriptorGenerateTask extends ExamModelDescriptorTask {


        /**
         * @return the EXAM Groovy display name
         */
        @Nonnull
        public String getDisplayName() {
            return Messages.EXAM_GenerateTask();
        }

        /**
         * Constructor of this Descriptor
         */
        public DescriptorGenerateTask() {
            load();
        }


        /**
         * Checks if the Element is a valid EXAM ID,UUID or FSN.
         *
         * @param value value
         * @return If the form is ok
         */
        public FormValidation doCheckElement(@QueryParameter String value) {
            return Util.validateElementForSearch(value);
        }


        private ExamModelConfig getModel(String name) {
            for (ExamModelConfig mConfig : getModelConfigs()) {
                if (mConfig.getName().equalsIgnoreCase(name)) {
                    return mConfig;
                }
            }
            return null;
        }

        /**
         * Checks if the ExamModels version ich at minimim 5.0.
         *
         * @param value value
         * @return If the form is ok
         */
        public FormValidation doCheckExamModel(@QueryParameter String value) {
            ExamModelConfig m = getModel(value);
            if (m == null || m.getExamVersion() < 50) {
                return FormValidation.error(Messages.TCG_EXAM_MIN_VERSION());
            }
            return FormValidation.ok();
        }

        /**
         * Checks if the mappingList is valid.
         *
         * @param value value
         * @return If the form is ok
         */
        public FormValidation doCheckMappingList(@QueryParameter String value) {
            FormValidation ok = FormValidation.ok();
            if (value.isEmpty()) {
                return ok;
            }
            for (String elmt : value.split(",")) {
                FormValidation elmtValid = Util.validateElementForSearch(elmt);
                if (!ok.equals(elmtValid)) {
                    return elmtValid;
                }
            }
            return ok;
        }

        /**
         * checks if the variant is valid
         *
         * @param value value
         * @return If the form is ok
         */
        public FormValidation doCheckVariant(@QueryParameter String value) {
            if (value.isEmpty()) {
                return FormValidation.ok();
            }
            return Util.validateElementForSearch(value);
        }

        /**
         * @return the default errorHandle
         */
        public String getDefaultErrorHandling() {
            return ErrorHandling.GENERATE_ERROR_STEP.displayString();
        }

        /**
         * fills the ListBoxModel ErrorHandle with all ErrorHandless
         *
         * @return ListBoxModel
         */
        public ListBoxModel doFillDescriptionSourceItems() {
            ListBoxModel items = new ListBoxModel();
            for (DescriptionSource descriptionSource : DescriptionSource.values()) {
                items.add(descriptionSource.getDisplayString(), descriptionSource.name());
            }
            return items;
        }

        /**
         * @return the default description source
         */
        public String getDefaultDescriptionSource() {
            return DescriptionSource.DESCRIPTION.getDisplayString();
        }

        /**
         * fills the ListBoxModel ErrorHandle with all ErrorHandless
         *
         * @return ListBoxModel
         */
        public ListBoxModel doFillErrorHandlingItems() {
            ListBoxModel items = new ListBoxModel();
            for (ErrorHandling errorHandle : ErrorHandling.values()) {
                items.add(errorHandle.displayString(), errorHandle.name());
            }
            return items;
        }

        /**
         * @return all TestCaseStates
         */
        public TestCaseState[] doFillTestCaseStatesItems() {
            return TestCaseState.values();
        }

        /**
         * fills the ListBoxModel ErrorHandle with all ErrorHandless
         *
         * @return ListBoxModel
         */
        public ListBoxModel doFillStateForSuccessItems() {
            ListBoxModel items = new ListBoxModel();
            for (TestCaseState tcs : TestCaseState.values()) {
                items.add(tcs.getName(), tcs.getLiteral());
            }
            return items;
        }


        public ListBoxModel doFillStateForFailItems() {
            ListBoxModel items = new ListBoxModel();
            for (TestCaseState tcs : TestCaseState.values()) {
                items.add(tcs.getName(), tcs.getLiteral());
            }
            return items;
        }

        public String getDefaultStateForSuccess() {
            return TestCaseState.REVIEWED.getName();
        }

        public String getDefaultStateForFail() {
            return TestCaseState.NOT_YET_SPECIFIED.getName();
        }

        /**
         * @return the default errorHandle
         */
        public List<String> getDefaultTestCaseStates() {
            List<String> list = new ArrayList<>();
            list.add(TestCaseState.NOT_YET_IMPLEMENTED.toString());
            return list;
        }


        /**
         * @return all StepType
         */
        public StepType[] doFillFrameStepsItems() {
            return StepType.values();
        }
    }
}
