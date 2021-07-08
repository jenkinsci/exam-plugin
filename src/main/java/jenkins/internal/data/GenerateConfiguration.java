package jenkins.internal.data;

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
     * Type of the DescriptionSource (has to be one of the following: [BESCHREIBUNG, DESCRIPTION])
     */
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
     * List of FrameFunctions (possible values are: ["PRECONDITION_BEFORE", "PRECONDITION_AFTER", "ACTION_BEFORE", "ACTION_AFTER", "POSTCONDITION_BEFORE", "POSTCONDITION_AFTER", "EXPECTED_RESULT_BEFORE", "EXPECTED_RESULT_AFTER", "NUMBERED_FRAME_STEP"])
     */
    private List<String> frameFunctions;
    /**
     * List of OperationMappings consisting out of UUID, ID or FSN
     */
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
     * Constructor
     */
    public GenerateConfiguration() {}

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
}
