package jenkins.task;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import jenkins.internal.data.TestConfiguration;

public class FakeTask extends ExamTask {
    
    FakeTask(String examName, String pythonName, String examReport, String systemConfiguration) {
        super(examName, pythonName, examReport, systemConfiguration);
    }
    
    TestConfiguration addDataToTestConfiguration(TestConfiguration tc, EnvVars env) throws AbortException {
        return tc;
    }
    
    @Extension
    public static class DescriptorFakeTask extends DescriptorExamTask {
    
    }
}
