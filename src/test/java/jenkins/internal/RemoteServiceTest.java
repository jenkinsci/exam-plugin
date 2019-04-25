package jenkins.internal;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
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
    private static Client client;
    
    @Mock
    private PrintStream printMock;
    
    @BeforeClass
    public static void oneTimeSetup() {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        client = Client.create(clientConfig);
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
        ClientResponse response = RemoteService.get(launcher, client, apiPort, "");
        
        assertEquals(null, response);
    }
    
    @Test
    public void getJSON() throws IOException, InterruptedException {
        
        launcher = new Launcher.LocalLauncher(StreamTaskListener.fromStdout(), null);
        ClientResponse response = RemoteService.getJSON(launcher, client, apiPort, "");
        
        assertEquals(null, response);
    }
    
    @Test
    public void post() throws IOException, InterruptedException {
        
        launcher = new Launcher.LocalLauncher(StreamTaskListener.fromStdout(), null);
        ClientResponse response = RemoteService.post(launcher, client, apiPort, "", null);
        
        assertEquals(null, response);
    }
}
