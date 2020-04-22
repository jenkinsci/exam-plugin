package jenkins.task.TestUtil;

import hudson.Launcher;
import hudson.Proc;
import hudson.model.TaskListener;
import org.jvnet.hudson.test.FakeLauncher;

import javax.annotation.Nonnull;
import java.io.IOException;

public class FakeExamLauncher extends Launcher.DummyLauncher implements FakeLauncher {
    
    public FakeExamLauncher(@Nonnull TaskListener listener) {
        super(listener);
    }
    
    public Proc launch(Launcher.ProcStarter starter) throws IOException {
        return new FinishedProc(0);
    }
    
    @Override
    public Proc onLaunch(Launcher.ProcStarter procStarter) throws IOException {
        return new FinishedProc(0);
    }
}
