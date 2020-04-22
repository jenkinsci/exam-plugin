package jenkins.task.TestUtil;

import hudson.Extension;
import jenkins.internal.ClientRequest;

import java.io.IOException;

public class FakeExamTaskWithoutExecute extends FakeExamTask {
    
    public FakeExamTaskWithoutExecute(String examName, String pythonName, String examReport,
            String systemConfiguration) {
        super(examName, pythonName, examReport, systemConfiguration);
    }
    
    @Override
    protected void doExecuteTask(ClientRequest clientRequest) throws IOException, InterruptedException {
    
    }
    
    @Extension
    public static class DescriptorFakeExamTaskWithoutExecute extends DescriptorFakeTask {
        
        private static final long serialVersionUID = -1351715707510827454L;
    }
}
