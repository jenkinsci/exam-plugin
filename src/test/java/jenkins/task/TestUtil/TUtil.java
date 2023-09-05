package jenkins.task.TestUtil;

import Utils.Whitebox;
import hudson.util.FormValidation;
import hudson.util.Secret;
import jenkins.internal.data.GenerateConfiguration;
import jenkins.internal.data.LegacyGenerateConfiguration;
import jenkins.internal.data.GroovyConfiguration;
import jenkins.internal.data.ModelConfiguration;
import jenkins.model.Jenkins;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.plugins.exam.config.ExamPluginConfig;
import jenkins.plugins.exam.config.ExamReportConfig;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;
import jenkins.task.TestrunFilter;
import jenkins.task._exam.Messages;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class TUtil {

    // exam model config
    private static final String modelConfigName = "test";
    private static final String modelConfigModelName = "test";
    private static final int modelConfigExamVersion = 44;
    private static final String modelConfigTargetEndpoint = "testtargetendpoint";

    // exam report config
    private static final String reportConfigName = "test";
    private static final String reportConfigSchema = "tetSchema";
    private static final String reportConfigPort = "8080";
    private static final String reportConfigHost = "localhost";
    private static final String reportConfigDbType = "PostgreSQL";
    private static final String reportConfigDbPass = "dbPass";
    private static final String reportConfigDbUser = "dbPass";
    private static final String reportConfigServiceOrSid = "service";

    // exam plugin config
    public static int pluginConfigPort = 8080;
    private static int pluginConfigTimeout = 300;
    private static int pluginConfigLicensePort = 8090;
    private static String pluginConfigLicenseHost = "localhost";

    private static char[] chars = "1234567890abcdef".toCharArray();

    public static String generateValidUuid(boolean withMinus) {
        StringBuilder uuidBuilder = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 32; i++) {
            int num = rand.nextInt() % chars.length;
            if (num < 0) {
                num = num * -1;
            }
            uuidBuilder.append(chars[num]);
            if (withMinus) {
                if (i == 4 || i == 12 || i == 25) {
                    uuidBuilder.append('-');
                }
            }
        }
        return uuidBuilder.toString();
    }

    public static String generateValidId() {
        Random rnd = new Random();

        int rndId = rnd.nextInt(999999999) + 1;
        return "I" + rndId;
    }

    public static ExamModelConfig getTestExamModelConfig() {
        ExamModelConfig result = new ExamModelConfig(modelConfigName);
        result.setModelName(modelConfigModelName);
        result.setExamVersion(modelConfigExamVersion);
        result.setTargetEndpoint(modelConfigTargetEndpoint);

        return result;
    }

    public static ExamReportConfig getTestExamReportConfig() {
        ExamReportConfig result = new ExamReportConfig();
        result.setName(reportConfigName);
        result.setSchema(reportConfigSchema);
        result.setPort(reportConfigPort);
        result.setHost(reportConfigHost);
        result.setDbType(reportConfigDbType);
        result.setDbPass(Secret.fromString(reportConfigDbPass));
        result.setDbUser(reportConfigDbUser);
        result.setServiceOrSid(reportConfigServiceOrSid);

        return result;
    }

    public static ExamPluginConfig getTestExamPluginConfig() {
        List<ExamModelConfig> examModelConfigs = new ArrayList<ExamModelConfig>() {{
            add(getTestExamModelConfig());
        }};
        List<ExamReportConfig> examReportConfigs = new ArrayList<ExamReportConfig>() {{
            add(getTestExamReportConfig());
        }};
        ExamPluginConfig pluginConfig = new ExamPluginConfig(examModelConfigs, examReportConfigs, pluginConfigPort,
                pluginConfigLicensePort, pluginConfigTimeout, pluginConfigLicenseHost);

        return pluginConfig;
    }

    public static List<TestrunFilter> createTestrunFilter() {
        List<TestrunFilter> testrunFilters = new ArrayList<>();
        testrunFilters.add(new TestrunFilter("testrunFilter1", "testrunValue1", true, true));
        testrunFilters.add(new TestrunFilter("testrunFilter2", "testrunValue2", false, false));
        testrunFilters.add(new TestrunFilter("testrunFilter3", "testrunValue3", true, false));
        testrunFilters.add(new TestrunFilter("testrunFilter4", "testrunValue4", false, true));

        return testrunFilters;
    }

    public static void createAndRegisterExamPluginConfig(JenkinsRule jenkinsRule) {
        ExamPluginConfig testExamPluginConfig = getTestExamPluginConfig();
        ExamPluginConfig examPluginConfig = jenkinsRule.getInstance().getDescriptorByType(ExamPluginConfig.class);
        examPluginConfig.setModelConfigs(testExamPluginConfig.getModelConfigs());
        examPluginConfig.setReportConfigs(testExamPluginConfig.getReportConfigs());
        examPluginConfig.setLicenseHost(pluginConfigLicenseHost);
        examPluginConfig.setLicensePort(pluginConfigLicensePort);
        examPluginConfig.setPort(pluginConfigPort);
    }

    public static PythonInstallation createAndRegisterPythonInstallation(JenkinsRule jenkinsRule, String name,
                                                                         String home) {
        PythonInstallation[] installations = jenkinsRule.getInstance()
                .getDescriptorByType(PythonInstallation.DescriptorImpl.class).getInstallations();

        PythonInstallation[] newInstallations = new PythonInstallation[installations.length + 1];
        int index = 0;
        for (PythonInstallation installation : installations) {
            newInstallations[index] = installation;
            index++;
        }
        PythonInstallation newPythonInstallation = new PythonInstallation(name, home, Collections.emptyList());
        newInstallations[index] = newPythonInstallation;

        jenkinsRule.getInstance().getDescriptorByType(PythonInstallation.DescriptorImpl.class)
                .setInstallations(newInstallations);

        return newPythonInstallation;
    }

    public static void cleanUpPythonInstallations(JenkinsRule jenkinsRule) {
        Jenkins jenkins = jenkinsRule.getInstance();
        if (jenkins != null) {
            PythonInstallation[] noInstallations = new PythonInstallation[0];
            jenkins.getDescriptorByType(PythonInstallation.DescriptorImpl.class).setInstallations(noInstallations);
        }
    }

    public static ExamTool createAndRegisterExamTool(JenkinsRule jenkinsRule, String examName, String examHome,
                                                     String relativeConfigPath) {
        ExamTool newExamTool;
        ExamTool[] installations = jenkinsRule.getInstance().getDescriptorByType(ExamTool.DescriptorImpl.class)
                .getInstallations();
        ExamTool[] newInstallations = new ExamTool[installations.length + 1];
        int index = 0;
        for (ExamTool tool : installations) {
            newInstallations[index] = tool;
            index++;
        }
        newExamTool = new ExamTool(examName, examHome, Collections.emptyList(), relativeConfigPath);
        newInstallations[index] = newExamTool;

        jenkinsRule.getInstance().getDescriptorByType(ExamTool.DescriptorImpl.class)
                .setInstallations(newInstallations);

        return newExamTool;
    }

    public static void cleanUpExamTools(JenkinsRule jenkinsRule) {
        Jenkins jenkins = jenkinsRule.getInstance();
        if (jenkins != null) {
            ExamTool[] noInstallations = new ExamTool[0];
            jenkins.getDescriptorByType(ExamTool.DescriptorImpl.class).setInstallations(noInstallations);
        }
    }

    public static void assertModelConfig(ModelConfiguration mc1, ModelConfiguration mc2) {
        if (mc1 != null & mc2 != null) {
            assertEquals(mc1.getModelName(), mc2.getModelName());
            assertEquals(mc1.getProjectName(), mc2.getProjectName());
            assertEquals(mc1.getTargetEndpoint(), mc2.getTargetEndpoint());
            assertEquals(mc1.getModelConfigUUID(), mc2.getModelConfigUUID());
            return;
        }
        fail();
    }

    public static void assertGroovyConfig(GroovyConfiguration c1, GroovyConfiguration c2) {
        if (c1 != null && c2 != null) {
            assertEquals(c1.getScript(), c2.getScript());
            assertEquals(c1.getStartElement(), c2.getStartElement());
            return;
        }
        fail();
    }

    public static void assertGenerateConfig(LegacyGenerateConfiguration c1, LegacyGenerateConfiguration c2) {
        if (c1 != null && c2 != null) {
            assertEquals(c1.getElement(), c2.getElement());
            assertEquals(c1.getDescriptionSource(), c2.getDescriptionSource());
            assertEquals(c1.isDocumentInReport(), c2.isDocumentInReport());
            assertEquals(c1.getErrorHandling(), c2.getErrorHandling());
            assertEquals(c1.getVariant(), c2.getVariant());
            assertTrue(c1.getFrameFunctions().equals(c2.getFrameFunctions()));
            assertTrue(c1.getMappingList().equals(c2.getMappingList()));
            assertTrue(c1.getTestCaseStates().equals(c2.getTestCaseStates()));
            return;
        }
        fail();
    }

    public static void assertGenerateConfig(GenerateConfiguration c1, GenerateConfiguration c2) {
        if (c1 != null && c2 != null) {
            assertEquals(c1.getElement(), c2.getElement());
            assertEquals(c1.getDescriptionSource(), c2.getDescriptionSource());
            assertEquals(c1.getOverwriteDescriptionSource(), c2.getOverwriteDescriptionSource());
            assertEquals(c1.isDocumentInReport(), c2.isDocumentInReport());
            assertEquals(c1.getErrorHandling(), c2.getErrorHandling());
            assertEquals(c1.getVariant(), c2.getVariant());
            assertEquals(c1.isOverwriteFrameSteps(), c2.isOverwriteFrameSteps());
            assertTrue(c1.getFrameFunctions().equals(c2.getFrameFunctions()));
            assertEquals(c1.isOverwriteMappingList(), c2.isOverwriteMappingList());
            assertTrue(c1.getMappingList().equals(c2.getMappingList()));
            assertTrue(c1.getTestCaseStates().equals(c2.getTestCaseStates()));
            assertEquals(c1.getSetStates(), c2.getSetStates());
            assertEquals(c1.getStateForSuccess(), c2.getStateForSuccess());
            assertEquals(c1.getStateForFail(), c2.getStateForFail());
            return;
        }
        fail();
    }


    // HELP METHOD
    public static void doCheckSearchElement(Object targetClass, String method, String input, boolean errorExpected) throws Exception {
        String newLine = "\r\n";
        String expectedErrorMsg =
                Messages.EXAM_RegExUuid() + newLine + Messages.EXAM_RegExId() + newLine + Messages.EXAM_RegExFsn()
                        + newLine;

        FormValidation validResult = Whitebox.invokeMethod(targetClass, method, input);

        if (errorExpected) {
            assertEquals(FormValidation.error(expectedErrorMsg).getMessage(), validResult.getMessage());
        } else {
            assertEquals(FormValidation.ok(), validResult);
        }
    }

    public static void doCheckValidSearchElement(Object targetClass, String method) throws Exception {
        String validFSN = "name.of.my.package";
        String validID = generateValidId();
        String validUUID = generateValidUuid(false);

        String invalidFSN = "#IAmAlsoNoPythonConformName";
        String invalidID = "123456";
        String invalidUUID = "61b56acdbe4247a9e04400144f6890f"; // to short

        doCheckSearchElement(targetClass, method, validFSN, false);
        doCheckSearchElement(targetClass, method, validID, false);
        doCheckSearchElement(targetClass, method, validUUID, false);

        doCheckSearchElement(targetClass, method, invalidFSN, true);
        doCheckSearchElement(targetClass, method, invalidID, true);
        doCheckSearchElement(targetClass, method, invalidUUID, true);
    }
}
