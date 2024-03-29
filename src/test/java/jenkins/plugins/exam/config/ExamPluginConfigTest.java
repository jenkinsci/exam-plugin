package jenkins.plugins.exam.config;

import Utils.Mocks;
import Utils.Whitebox;
import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;
import jenkins.model.GlobalConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ExamPluginConfigTest {

    private final static String TESTSTRING = "myTestString";
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    ExamPluginConfig testObject;

    @Before
    public void setUp() {
        testObject = new ExamPluginConfig(Collections.emptyList(), Collections.emptyList(), 1, 1, 1, "");
    }

    @After
    public void tearDown() {
        Mocks.resetMocks();
        testObject = null;
    }

    @Test
    @WithoutJenkins
    public void getPort() {
        int testPort = 1234;
        Whitebox.setInternalState(testObject, "port", testPort);
        int setPort = testObject.getPort();

        assertEquals(testPort, setPort);
    }

    @Test
    @WithoutJenkins
    public void setPort() {
        int testPort = 9876;
        testObject.setPort(testPort);
        int setPort = Whitebox.getInternalState(testObject, "port");

        assertEquals(testPort, setPort);
    }

    @Test
    @WithoutJenkins
    public void getTimeout() {
        int testTimeout = 1234;
        Whitebox.setInternalState(testObject, "timeout", testTimeout);
        int setTimeout = testObject.getTimeout();

        assertEquals(testTimeout, setTimeout);
    }

    @Test
    @WithoutJenkins
    public void setTimeout() {
        int testTimeout = 9876;
        testObject.setTimeout(testTimeout);
        int setTimeout = Whitebox.getInternalState(testObject, "timeout");

        assertEquals(testTimeout, setTimeout);
    }

    @Test
    @WithoutJenkins
    public void getLicensePort() {
        int testPort = 1234;
        Whitebox.setInternalState(testObject, "licensePort", testPort);
        int setPort = testObject.getLicensePort();

        assertEquals(testPort, setPort);
    }

    @Test
    @WithoutJenkins
    public void setLicensePort() {
        int testPort = 9876;
        testObject.setLicensePort(testPort);
        int setPort = Whitebox.getInternalState(testObject, "licensePort");

        assertEquals(testPort, setPort);
    }

    @Test
    @WithoutJenkins
    public void getLicenseHost() {
        Whitebox.setInternalState(testObject, "licenseHost", TESTSTRING);
        String testIt = testObject.getLicenseHost();
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    @WithoutJenkins
    public void setLicenseHost() {
        testObject.setLicenseHost(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject, "licenseHost");
        assertEquals(TESTSTRING, testIt);
    }

    @Test
    @WithoutJenkins
    public void getModelConfigs() {
        ExamModelConfig testConfig1 = new ExamModelConfig("exam");
        ExamModelConfig testConfig2 = new ExamModelConfig("testConfig");
        List<ExamModelConfig> testModelConfigs = new ArrayList<>();

        testModelConfigs.add(testConfig1);
        testModelConfigs.add(testConfig2);

        Whitebox.setInternalState(testObject, "modelConfigs", testModelConfigs);
        List<ExamModelConfig> setModelConfigs = testObject.getModelConfigs();

        assertEquals(testModelConfigs, setModelConfigs);
    }

    @Test
    @WithoutJenkins
    public void setModelConfigs() {
        ExamModelConfig testConfig1 = new ExamModelConfig("testExamConfig");
        ExamModelConfig testConfig2 = new ExamModelConfig("anotherTestExamConfig");
        List<ExamModelConfig> testModelConfigs = new ArrayList<>();

        testModelConfigs.add(testConfig1);
        testModelConfigs.add(testConfig2);

        testObject.setModelConfigs(testModelConfigs);
        List<ExamModelConfig> setModelConfigs = Whitebox.getInternalState(testObject, "modelConfigs");

        assertEquals(testModelConfigs, setModelConfigs);
    }

    @Test
    @WithoutJenkins
    public void getReportConfigs() {
        ExamReportConfig testReportConfig1 = new ExamReportConfig();
        testReportConfig1.setPort("1234");
        testReportConfig1.setHost("testHost");
        ExamReportConfig testReportConfig2 = new ExamReportConfig();
        testReportConfig2.setSchema("testSchema");
        testReportConfig2.setName("testName");
        List<ExamReportConfig> testReportConfigs = new ArrayList<>();
        testReportConfigs.add(testReportConfig1);
        testReportConfigs.add(testReportConfig2);

        Whitebox.setInternalState(testObject, "reportConfigs", testReportConfigs);
        List<ExamReportConfig> setReportConfigs = testObject.getReportConfigs();

        assertEquals(testReportConfigs, setReportConfigs);
    }

    @Test
    @WithoutJenkins
    public void setReportConfigs() {
        ExamReportConfig testReportConfig1 = new ExamReportConfig();
        testReportConfig1.setPort("1234");
        testReportConfig1.setHost("testHost");
        ExamReportConfig testReportConfig2 = new ExamReportConfig();
        testReportConfig2.setSchema("testSchema");
        testReportConfig2.setName("testName");
        List<ExamReportConfig> testReportConfigs = new ArrayList<>();
        testReportConfigs.add(testReportConfig1);
        testReportConfigs.add(testReportConfig2);

        testObject.setReportConfigs(testReportConfigs);
        List<ExamReportConfig> setReportConfigs = Whitebox.getInternalState(testObject, "reportConfigs");

        assertEquals(testReportConfigs, setReportConfigs);
    }

    @Test
    public void configuration() {
        ExamPluginConfig expected = jenkinsRule.getInstance().getDescriptorByType(ExamPluginConfig.class);
        ExamPluginConfig actual = testObject.configuration();
        assertEquals(expected, actual);

        DescriptorExtensionList<GlobalConfiguration, Descriptor<GlobalConfiguration>> descriptorList = jenkinsRule
                .getInstance().getDescriptorList(GlobalConfiguration.class);
        Descriptor<GlobalConfiguration> descriptor = descriptorList.find(ExamPluginConfig.class);
        descriptorList.remove(descriptor);
        expected = ExamPluginConfig.EMPTY_CONFIG;
        actual = testObject.configuration();
        assertEquals(expected, actual);
    }
}
