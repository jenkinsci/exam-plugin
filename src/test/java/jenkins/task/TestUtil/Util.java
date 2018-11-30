package jenkins.task.TestUtil;

import jenkins.model.Jenkins;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.plugins.exam.config.ExamPluginConfig;
import jenkins.plugins.exam.config.ExamReportConfig;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;
import jenkins.task.TestrunFilter;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Util {

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
    private static int pluginConfigPort = 8080;
    private static int pluginConfigLicensePort = 8090;
    private static String pluginConfigLicenseHost = "";

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
        result.setDbPass(reportConfigDbPass);
        result.setDbUser(reportConfigDbUser);
        result.setServiceOrSid(reportConfigServiceOrSid);

        return result;
    }

    public static ExamPluginConfig getTestExamPluginConfig() {
        ExamPluginConfig pluginConfig = new ExamPluginConfig(
                Collections.singletonList(getTestExamModelConfig()),
                Collections.singletonList(getTestExamReportConfig()),
                pluginConfigPort,
                pluginConfigLicensePort,
                pluginConfigLicenseHost);

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

    public static PythonInstallation createAndRegisterPythonInstallation(JenkinsRule jenkinsRule, String name,
                                                                         String home) {
        PythonInstallation[] installations = jenkinsRule.getInstance()
                                                        .getDescriptorByType(PythonInstallation.DescriptorImpl.class)
                                                        .getInstallations();

        PythonInstallation[] newInstallations = new PythonInstallation[installations.length + 1];
        int index = 0;
        for (PythonInstallation installation : installations) {
            newInstallations[index] = installation;
            index++;
        }
        PythonInstallation newPythonInstallation = new PythonInstallation(name, home, Collections.emptyList());
        newInstallations[index] = newPythonInstallation;

        jenkinsRule.getInstance()
                   .getDescriptorByType(PythonInstallation.DescriptorImpl.class)
                   .setInstallations(newInstallations);

        return newPythonInstallation;
    }

    public static void cleanUpPythonInstallations(JenkinsRule jenkinsRule) {
        Jenkins jenkins = jenkinsRule.getInstance();
        if (jenkins != null) {
            PythonInstallation[] noInstallations = new PythonInstallation[0];
            jenkins.getDescriptorByType(PythonInstallation.DescriptorImpl.class)
                   .setInstallations(noInstallations);
        }
    }

    public static ExamTool createAndRegisterExamTool(JenkinsRule jenkinsRule, String examName, String examHome,
                                                     String relativeConfigPath) {
        ExamTool newExamTool;
        ExamTool[] installations = jenkinsRule.getInstance()
                                              .getDescriptorByType(ExamTool.DescriptorImpl.class)
                                              .getInstallations();
        ExamTool[] newInstallations = new ExamTool[installations.length + 1];
        int index = 0;
        for (ExamTool tool : installations) {
            newInstallations[index] = tool;
            index++;
        }
        newExamTool = new ExamTool(examName, examHome, relativeConfigPath, Collections.emptyList());
        newInstallations[index] = newExamTool;

        jenkinsRule.getInstance()
                   .getDescriptorByType(ExamTool.DescriptorImpl.class)
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
}
