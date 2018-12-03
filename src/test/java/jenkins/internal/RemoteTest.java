package jenkins.internal;

import hudson.Launcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import static org.junit.Assert.*;

public class RemoteTest {
    
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    
    private Launcher.LocalLauncher launcher;
    
    @Before
    public void mySetup() {
        launcher = jenkinsRule.createLocalLauncher();
    }
    
    @After
    public void myTearDown() {
        launcher = null;
    }
    
    @Test
    public void getIP() throws IOException, InterruptedException {
        String myIp = InetAddress.getLocalHost().getHostAddress();
        String ip = Remote.getIP(launcher);
        
        assertEquals(myIp, ip);
    }
    
    @Test
    public void fileExists() throws IOException, InterruptedException {
        String testPath = "C:\\this\\does\\not\\exists";
        boolean actual = Remote.fileExists(launcher, new File(testPath));
        assertFalse(actual);
        
        String remote = jenkinsRule.getInstance().getRootPath().getRemote();
        actual = Remote.fileExists(launcher, new File(remote));
        assertFalse(actual);
        
        actual = Remote.fileExists(launcher, new File(remote + File.separator + "config.xml"));
        assertTrue(actual);
    }
}
