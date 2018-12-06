package jenkins.task;

import hudson.util.FormValidation;
import jenkins.internal.enumeration.RestAPILogLevelEnum;
import jenkins.task.TestUtil.TUtil;
import jenkins.task._exam.Messages;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertEquals;

public class ExamTaskExecutionFileDescriptorTest {
    
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    
    private ExamTaskExecutionFile testObject;
    ExamTaskExecutionFile.DescriptorExamTaskExecutionFile testObjectDescriptor;
    private String examName;
    private String pythonName;
    private String examReport;
    private String examSysConfig;
    
    @Before
    public void setUp() {
        examName = "EXAM";
        pythonName = "Python-2.7";
        examReport = "examReport";
        examSysConfig = "testExamSystemConfig";
        testObject = new ExamTaskExecutionFile(examName, pythonName, examReport, examSysConfig);
        testObjectDescriptor = testObject.getDescriptor();
    }
    
    @After
    public void tearDown() {
        testObject = null;
    }
    
    @Test
    public void getDefaultLogLevel() {
        assertEquals(RestAPILogLevelEnum.INFO.name(), testObjectDescriptor.getDefaultLogLevel());
    }
    
    @Test
    public void doCheckSystemConfiguration() {
        String newLine = "\r\n";
        String expectedErrorMsg_1 = Messages.EXAM_RegExSysConf();
        String expectedErrorMsg_2 = Messages.EXAM_RegExUuid();
        String expectedErrorMsg_3 = Messages.EXAM_RegExPython();
        String expectedErrorMsg_4 = Messages.EXAM_RegExUuid() + newLine + Messages.EXAM_RegExPython();
        
        String invalidString_1 = "IAmNotValid";
        String invalidString_2 = TUtil.generateValidUuid(false) + "3 This_is_my_Sysconfig";
        String invalidString_3 = TUtil.generateValidUuid(false) + " 1This_is_my_Sysconfig";
        String invalidString_4 = TUtil.generateValidUuid(false) + "3 1This_is_my_Sysconfig";
        String validString = TUtil.generateValidUuid(false) + " This_is_my_Sysconfig";
        
        FormValidation fv_invalidResult_1 = testObjectDescriptor.doCheckSystemConfiguration(invalidString_1);
        FormValidation fv_invalidResult_2 = testObjectDescriptor.doCheckSystemConfiguration(invalidString_2);
        FormValidation fv_invalidResult_3 = testObjectDescriptor.doCheckSystemConfiguration(invalidString_3);
        FormValidation fv_invalidResult_4 = testObjectDescriptor.doCheckSystemConfiguration(invalidString_4);
        FormValidation fv_validResult = testObjectDescriptor.doCheckSystemConfiguration(validString);
        
        assertEquals(FormValidation.error(expectedErrorMsg_1).getMessage(), fv_invalidResult_1.getMessage());
        assertEquals(FormValidation.error(expectedErrorMsg_2).getMessage(), fv_invalidResult_2.getMessage());
        assertEquals(FormValidation.error(expectedErrorMsg_3).getMessage(), fv_invalidResult_3.getMessage());
        assertEquals(FormValidation.error(expectedErrorMsg_4).getMessage(), fv_invalidResult_4.getMessage());
        assertEquals(FormValidation.ok(), fv_validResult);
    }
    
}
