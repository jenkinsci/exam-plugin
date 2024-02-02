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
package jenkins.internal.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

/**
 * Configuration for running a Generate Task in EXAM.
 */
public class GenerateConfiguration implements Serializable {

    /**
     * UUID, ID or FSN of the element where the generation should be started from.
     */
    private String element;

    /**
     * if overwrite the models default
     */
    @JsonIgnore
    private boolean overwriteDescriptionSource;
    /**
     * Type of the DescriptionSource (has to be one of the following: [BESCHREIBUNG, DESCRIPTION])
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String descriptionSource;
    /**
     * boolean if the document should be in the Report.
     */
    private boolean documentInReport;
    /**
     * Type of the ErrorHandling (has to be one of the following: [GENERATE_ERROR_STEP, SKIP_TESTCASE, ABORT])
     */
    private String errorHandling;
    /**
     * if overwrite the models default
     */
    @JsonIgnore
    private boolean overwriteFrameSteps;
    /**
     * List of FrameFunctions (possible values are: ["PRECONDITION_BEFORE", "PRECONDITION_AFTER", "ACTION_BEFORE", "ACTION_AFTER", "POSTCONDITION_BEFORE", "POSTCONDITION_AFTER", "EXPECTED_RESULT_BEFORE", "EXPECTED_RESULT_AFTER", "NUMBERED_FRAME_STEP"])
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> frameFunctions;
    /**
     * if overwrite the models default
     */
    @JsonIgnore
    private boolean overwriteMappingList;
    /**
     * List of OperationMappings consisting out of UUID, ID or FSN
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> mappingList;
    /**
     * List of TestCaseStates to consider during the generation (has to be one of the following. [NOT_YET_SPECIFIED, SPECIFIED, REVIEWED, NOT_YET_IMPLEMENTED, IMPLEMENTED, PRODUCTIVE, INVALID])
     */
    private List<String> testCaseStates;
    /**
     * UUID, ID or FSN of the Variant
     */
    private String variant;
    /**
     * if states are relevant for generation.
     */
    private boolean setStates;
    /**
     * TestCaseState for successful generation.
     */
    private String stateForSuccess;
    /**
     * TestCaseState for failed generation.
     */
    private String stateForFail;

    /**
     * Constructor
     */
    public GenerateConfiguration() {
    }

    public void setSetStates(boolean setStates) {
        this.setStates = setStates;
    }

    public boolean getSetStates() {
        return setStates;
    }

    public void setStateForFail(String stateForFail) {
        this.stateForFail = stateForFail;
    }

    public String getStateForFail() {
        return stateForFail;
    }

    public void setStateForSuccess(String stateForSuccess) {
        this.stateForSuccess = stateForSuccess;
    }

    public String getStateForSuccess() {
        return stateForSuccess;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getDescriptionSource() {
        return descriptionSource;
    }

    public void setDescriptionSource(String descriptionSource) {
        this.descriptionSource = descriptionSource;
    }

    public boolean isDocumentInReport() {
        return documentInReport;
    }

    public void setDocumentInReport(boolean documentInReport) {
        this.documentInReport = documentInReport;
    }

    public String getErrorHandling() {
        return errorHandling;
    }

    public void setErrorHandling(String errorHandling) {
        this.errorHandling = errorHandling;
    }

    public List<String> getFrameFunctions() {
        return frameFunctions;
    }

    public void setFrameFunctions(List<String> frameFunctions) {
        this.frameFunctions = frameFunctions;
    }

    public List<String> getMappingList() {
        return mappingList;
    }

    public void setMappingList(List<String> mappingList) {
        this.mappingList = mappingList;
    }

    public List<String> getTestCaseStates() {
        return testCaseStates;
    }

    public void setTestCaseStates(List<String> testCaseStates) {
        this.testCaseStates = testCaseStates;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public boolean getOverwriteDescriptionSource() {
        return overwriteDescriptionSource;
    }

    public void setOverwriteDescriptionSource(boolean overwriteDescriptionSource) {
        this.overwriteDescriptionSource = overwriteDescriptionSource;
    }

    public boolean isOverwriteFrameSteps() {
        return overwriteFrameSteps;
    }

    public void setOverwriteFrameSteps(boolean overwriteFrameSteps) {
        this.overwriteFrameSteps = overwriteFrameSteps;
    }

    public boolean isOverwriteMappingList() {
        return overwriteMappingList;
    }

    public void setOverwriteMappingList(boolean overwriteMappingList) {
        this.overwriteMappingList = overwriteMappingList;
    }
}
