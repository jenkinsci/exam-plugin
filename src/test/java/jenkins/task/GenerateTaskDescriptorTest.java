package jenkins.task;

import Utils.Whitebox;
import hudson.model.FreeStyleProject;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.internal.enumeration.*;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.plugins.exam.config.ExamPluginConfig;
import jenkins.task.TestUtil.TUtil;
import jenkins.task._exam.Messages;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class GenerateTaskDescriptorTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    GenerateTask.DescriptorGenerateTask testObjectDescriptor;
    FreeStyleProject freeStyleProject;
    private GenerateTask testObject;
    private String examName;
    private String examModel;
    private String modelConfiguration;

    private String element;
    private String descriptionSource;
    private boolean documentInReport;
    private String errorHandling;
    private List<String> frameFunctions;
    private String mappingList;
    private List<String> testCaseStates;
    private String variant;

    private boolean setStates;

    private String stateForSuccess;
    private String stateForFail;

    @Before
    public void setUp() throws IOException {
        examName = "examName";
        examModel = "examModel";
        modelConfiguration = "config";

        element = "testElement";
        descriptionSource = "descSource";
        documentInReport = true;
        errorHandling = "error";
        frameFunctions = new ArrayList<>();
        frameFunctions.add("frame");
        mappingList = "mList";
        testCaseStates = new ArrayList<>();
        testCaseStates.add("tCStates");
        variant = "var";
        setStates = true;
        stateForFail = "NotYetSpecified";
        stateForSuccess = "Reviewed";
        freeStyleProject = jenkinsRule.createFreeStyleProject();
        testObject = new GenerateTask(examModel, examName, modelConfiguration, element, descriptionSource, documentInReport,
                errorHandling, frameFunctions, mappingList, testCaseStates, variant, setStates, stateForFail, stateForSuccess);
        testObjectDescriptor = (GenerateTask.DescriptorGenerateTask) testObject.getDescriptor();
    }

    @After
    public void tearDown() {
        testObject = null;
    }

    @Test
    public void testGetInstallations() {
        String examHome = "examHome";
        String examRelativePath = "examRelativePath";
        int num = 5;
        ExamTool[] expected = new ExamTool[num];
        for (int i = 0; i < num; i++) {
            expected[i] = TUtil
                    .createAndRegisterExamTool(jenkinsRule, examName + "_" + i, examHome, examRelativePath);
        }
        ExamTool[] actual = testObjectDescriptor.getInstallations();
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetModelConfigs() {
        ExamPluginConfig descriptor = (ExamPluginConfig) jenkinsRule.getInstance()
                .getDescriptor(ExamPluginConfig.class);
        List<ExamModelConfig> modelConfigs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            modelConfigs.add(new ExamModelConfig("EMC_" + i));
        }
        descriptor.setModelConfigs(modelConfigs);
        List<ExamModelConfig> actual = testObjectDescriptor.getModelConfigs();
        assertEquals(modelConfigs, actual);
    }

    @Test
    public void testDoFillExamNameItems() {
        String examHome = "examHome";
        String examRelativePath = "examRelativePath";
        int num = 5;
        String[] expected = new String[num];
        for (int i = 0; i < num; i++) {
            expected[i] = examName + "_" + i;
            TUtil.createAndRegisterExamTool(jenkinsRule, examName + "_" + i, examHome, examRelativePath);
        }

        ListBoxModel items = testObjectDescriptor.doFillExamNameItems();

        String[] actual = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            actual[i] = items.get(i).name;
        }
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testDoFillExamModelItems() {
        ExamPluginConfig descriptor = (ExamPluginConfig) jenkinsRule.getInstance()
                .getDescriptor(ExamPluginConfig.class);
        List<ExamModelConfig> modelConfigs = new ArrayList<>();
        int num = 5;
        String[] expected = new String[num];
        for (int i = 0; i < num; i++) {
            String name = "EMC_" + i;
            ExamModelConfig modelConfig = new ExamModelConfig(name);
            modelConfig.setName(name);
            modelConfigs.add(modelConfig);
            expected[i] = name;
        }

        descriptor.setModelConfigs(modelConfigs);
        ListBoxModel items = testObjectDescriptor.doFillExamModelItems();

        String[] actual = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            actual[i] = items.get(i).value;
        }

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testDoCheckModelConfiguration() throws Exception {
        TUtil.doCheckValidSearchElement(testObjectDescriptor, "doCheckModelConfiguration");
    }

    @Test
    public void testDoCheckElement() throws Exception {
        TUtil.doCheckValidSearchElement(testObjectDescriptor, "doCheckElement");
    }

    @Test
    public void testDoCheckMappingList() throws Exception {
        doCheckValidEmpty("doCheckMappingList");
        doCheckValidList("doCheckMappingList");
        doCheckInvalidList("doCheckMappingList");
    }

    @Test
    public void testDoCheckVariant() throws Exception {
        TUtil.doCheckValidSearchElement(testObjectDescriptor, "doCheckVariant");
        doCheckValidEmpty("doCheckVariant");
    }

    @Test
    public void testGetDefaultErrorHandling() {
        assertEquals(ErrorHandling.GENERATE_ERROR_STEP.displayString(), testObjectDescriptor.getDefaultErrorHandling());
    }

    @Test
    public void testGetDefaultDescriptionSource() {
        assertEquals(DescriptionSource.DESCRIPTION.getDisplayString(), testObjectDescriptor.getDefaultDescriptionSource());
    }

    @Test
    public void testGetDefaultTestCaseStates() {
        assertArrayEquals(new String[]{TestCaseState.NOT_YET_IMPLEMENTED.toString()}, testObjectDescriptor.getDefaultTestCaseStates().toArray());
    }

    @Test
    public void testDoFillDescriptionSourceItems() {
        ListBoxModel items = testObjectDescriptor.doFillDescriptionSourceItems();
        checkEnumValues(items, DescriptionSource.values());
    }

    @Test
    public void testDoFillErrorHandlingItems() {
        ListBoxModel items = testObjectDescriptor.doFillErrorHandlingItems();
        checkEnumValues(items, ErrorHandling.values());
    }

    @Test
    public void testDoFillTestCaseStatesItems() {
        TestCaseState[] items = testObjectDescriptor.doFillTestCaseStatesItems();
        assertArrayEquals(items, TestCaseState.values());
    }

    @Test
    public void testDoFillFrameStepsItems() {
        StepType[] items = testObjectDescriptor.doFillFrameStepsItems();
        assertArrayEquals(items, StepType.values());
    }

    @Test
    public void isApplicable() {
        assertTrue(testObjectDescriptor.isApplicable(freeStyleProject.getClass()));
    }


    @Test
    public void testDoCheckExamModel() throws Exception {
        fillExamModel("testModel", 50);
        FormValidation validResult = Whitebox.invokeMethod(testObjectDescriptor, "doCheckExamModel", "testModel");
        assertEquals(FormValidation.ok(), validResult);

        fillExamModel("testModel", 49);
        String expectedErrorMsg = Messages.TCG_EXAM_MIN_VERSION();
        validResult = Whitebox.invokeMethod(testObjectDescriptor, "doCheckExamModel", "testModel");
        assertEquals(FormValidation.error(expectedErrorMsg).getMessage(), validResult.getMessage());

        validResult = Whitebox.invokeMethod(testObjectDescriptor, "doCheckExamModel", "ModelNotIncluded");
        assertEquals(FormValidation.error(expectedErrorMsg).getMessage(), validResult.getMessage());
    }

    // HELP METHOD
    private void doCheckValidList(String method) throws Exception {
        String validFSN = "name.of.my.package";
        String validID = TUtil.generateValidId();
        String validUUID = TUtil.generateValidUuid(false);

        TUtil.doCheckSearchElement(testObjectDescriptor, method, validFSN, false);
        TUtil.doCheckSearchElement(testObjectDescriptor, method, validID, false);
        TUtil.doCheckSearchElement(testObjectDescriptor, method, validUUID, false);
        TUtil.doCheckSearchElement(testObjectDescriptor, method, validFSN + "," + validUUID, false);
        TUtil.doCheckSearchElement(testObjectDescriptor, method, validID + "," + validFSN + "," + validUUID, false);
    }

    // HELP METHOD

    private void fillExamModel(String name, int version) {
        ExamPluginConfig descriptor = (ExamPluginConfig) jenkinsRule.getInstance()
                .getDescriptor(ExamPluginConfig.class);
        List<ExamModelConfig> modelConfigs = new ArrayList<>();
        ExamModelConfig modelConfig = new ExamModelConfig(name);
        modelConfig.setName(name);
        modelConfig.setExamVersion(version);
        modelConfigs.add(modelConfig);

        descriptor.setModelConfigs(modelConfigs);
    }

    private void doCheckInvalidList(String method) throws Exception {
        String invalidFSN = "#IAmAlsoNoPythonConformName";
        String invalidID = "123456";
        String invalidUUID = "61b56acdbe4247a9e04400144f6890f"; // to short

        String validFSN = "this.is.my.path";
        String validID = TUtil.generateValidId();
        String validUUID = TUtil.generateValidUuid(false);

        TUtil.doCheckSearchElement(testObjectDescriptor, method, validID + "," + invalidFSN + "," + validUUID, true);
        TUtil.doCheckSearchElement(testObjectDescriptor, method, invalidID + "," + validFSN + "," + validUUID, true);
        TUtil.doCheckSearchElement(testObjectDescriptor, method, validUUID + "," + validFSN + "," + invalidUUID, true);
        TUtil.doCheckSearchElement(testObjectDescriptor, method, validID + "," + invalidID + "," + validUUID, true);
        TUtil.doCheckSearchElement(testObjectDescriptor, method, invalidFSN + "," + validFSN + "," + invalidUUID, true);
    }

    // HELP METHOD
    private void doCheckValidEmpty(String method) throws Exception {
        FormValidation fv_result = Whitebox.invokeMethod(testObjectDescriptor, method, "");
        assertEquals(FormValidation.ok(), fv_result);
    }


    private void checkEnumValues(ListBoxModel items, Object[] expectedArray) {
        String[] expected = new String[expectedArray.length];
        String[] actual = new String[items.size()];

        for (int i = 0; i < expectedArray.length; i++) {
            Object elmt = expectedArray[i];
            if (elmt instanceof Enum) {
                expected[i] = ((Enum<?>) elmt).name();
            }
        }

        for (int i = 0; i < items.size(); i++) {
            actual[i] = items.get(i).value;
        }
        assertArrayEquals(expected, actual);
    }
}
