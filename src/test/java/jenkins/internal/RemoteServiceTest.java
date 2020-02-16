package jenkins.internal;

import hudson.Launcher;
import hudson.util.StreamTaskListener;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.Mock;

import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class RemoteServiceTest {
    
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    
    private int apiPort = 8085;
    
    private Launcher.LocalLauncher launcher;
    
    @Mock
    private PrintStream printMock;
    
    @BeforeClass
    public static void oneTimeSetup() {
    }
    
    @Before
    public void mySetup() {
        launcher = jenkinsRule.createLocalLauncher();
    }
    
    @After
    public void myTearDown() {
        launcher = null;
    }
    
    @Test
    public void get() throws IOException, InterruptedException {
        
        launcher = new Launcher.LocalLauncher(StreamTaskListener.fromStdout(), null);
        RemoteServiceResponse response = RemoteService.get(launcher, apiPort, "", null);
        
        assertEquals(null, response);
    }
    
    @Test
    public void getJSON() throws IOException, InterruptedException {
        
        launcher = new Launcher.LocalLauncher(StreamTaskListener.fromStdout(), null);
        RemoteServiceResponse response = RemoteService.getJSON(launcher, apiPort, "", null);
        
        assertEquals(null, response);
    }
    
    @Test
    public void post() throws IOException, InterruptedException {
        
        launcher = new Launcher.LocalLauncher(StreamTaskListener.fromStdout(), null);
        RemoteServiceResponse response = RemoteService.post(launcher, apiPort, "", null, null);
        
        assertEquals(null, response);
    }
}
