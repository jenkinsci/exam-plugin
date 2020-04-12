package jenkins.task.TestUtil;

import hudson.Extension;
import jenkins.internal.ClientRequest;
import jenkins.task.Task;

import java.io.IOException;

public class FakeTask extends Task {
    
    @Override
    protected void doExecuteTask(ClientRequest clientRequest) throws IOException, InterruptedException {
    
    }
    
    @Extension
    public static class DescriptorFakeTask extends DescriptorTask {
    
    }
}
