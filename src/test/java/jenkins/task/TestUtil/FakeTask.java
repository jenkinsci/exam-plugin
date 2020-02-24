package jenkins.task.TestUtil;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import jenkins.internal.data.TestConfiguration;
import jenkins.task.ExamTask;

public class FakeTask extends ExamTask {

    public FakeTask(String examName, String pythonName, String examReport, String systemConfiguration) {
        super(examName, pythonName, examReport, systemConfiguration);
    }

    protected TestConfiguration addDataToTestConfiguration(TestConfiguration tc, EnvVars env) throws AbortException {
        return tc;
    }

    @Extension
    public static class DescriptorFakeTask extends DescriptorExamTask {

    }
}
