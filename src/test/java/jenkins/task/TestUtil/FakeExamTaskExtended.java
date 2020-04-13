package jenkins.task.TestUtil;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import jenkins.internal.data.ModelConfiguration;
import jenkins.internal.data.TestConfiguration;

public class FakeExamTaskExtended extends FakeExamTask {
    
    public FakeExamTaskExtended(String examName, String pythonName, String examReport, String systemConfiguration) {
        super(examName, pythonName, examReport, systemConfiguration);
    }
    
    protected TestConfiguration addDataToTestConfiguration(TestConfiguration tc, EnvVars env) throws AbortException {
        ModelConfiguration mod = new ModelConfiguration();
        mod.setModelName("modelName");
        tc.setModelProject(mod);
        return tc;
    }
    
    @Extension
    public static class DescriptorFakeTaskExtended extends DescriptorFakeTask {
        
        private static final long serialVersionUID = 2276768263667197734L;
    }
}
