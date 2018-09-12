package jenkins.plugins.exam.config;

import jenkins.task.ExamReport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ExamPluginConfigTest {

    ExamPluginConfig testObject;

    @Before
    public void setUp() {
        testObject = new ExamPluginConfig(Collections.emptyList(), Collections.emptyList(), 1, 1, "");
    }

    @After
    public void tearDown() {
        testObject = null;
    }

    @Test
    public void getPort() {
        int testPort = 1234;
        Whitebox.setInternalState(testObject, "port", testPort);
        int setPort = testObject.getPort();

        assertEquals(testPort, setPort);
    }

    @Test
    public void setPort() {
        int testPort = 9876;
        testObject.setPort(testPort);
        int setPort = Whitebox.getInternalState(testObject, "port");

        assertEquals(testPort, setPort);
    }

    @Test
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
}


































