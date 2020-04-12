package jenkins.internal;

import hudson.model.Executor;
import jenkins.internal.data.ExamStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RemoteService.class, Thread.class, ClientRequest.class })
@PowerMockIgnore({ "javax.crypto.*" })
public class ClientRequestPowerMockTest {
    
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    private ClientRequest testObject;
    
    @Before
    public void setUp() {
        testObject = new ClientRequest(System.out, 0, null);
    }
    
    @Test
    public void isApiAvailable() throws Exception {
        PowerMockito.mockStatic(RemoteService.class);
        PowerMockito.when(RemoteService.getJSON(any(), anyInt(), anyString(), any()))
                .thenThrow(new InterruptedException());
        exception.expect(InterruptedException.class);
        testObject.isApiAvailable();
    }
    
    private Executor prepareMocks(String statusJobName) throws InterruptedException, IOException {
        ExamStatus status = new ExamStatus();
        status.setJobRunning(Boolean.TRUE);
        status.setJobName(statusJobName);
        
        PowerMockito.mockStatic(RemoteService.class);
        PowerMockito.when(RemoteService.getJSON(any(), anyInt(), any(), any()))
                .thenReturn(new RemoteServiceResponse(Response.ok().build().getStatus(), status, ""));
        
        Executor executor = mock(Executor.class);
        when(executor.isInterrupted()).thenReturn(Boolean.FALSE);
        
        PowerMockito.mockStatic(Thread.class);
        PowerMockito.doThrow(new InterruptedException()).when(Thread.class);
        Thread.sleep(Mockito.anyLong());
        
        return executor;
    }
    
    private void verifyMocks(Executor executor) throws InterruptedException {
        verify(executor, times(2)).isInterrupted();
        PowerMockito.verifyStatic(Thread.class, times(1));
        Thread.sleep(Mockito.anyLong());
    }
    
    @Test
    public void waitForTestrunEnds() throws InterruptedException, IOException {
        Executor executor = prepareMocks("");
        testObject.waitForTestrunEnds(executor, 2);
        verifyMocks(executor);
    }
    
    @Test
    public void waitForExamIdle() throws InterruptedException, IOException {
        Executor executor = prepareMocks("");
        testObject.waitForExamIdle(executor, 2);
        verifyMocks(executor);
    }
    
    @Test
    public void waitForExportPDFReportJob() throws InterruptedException, IOException {
        Executor executor = prepareMocks("Export Reports to PDF.");
        testObject.waitForExportPDFReportJob(executor, 2);
        verifyMocks(executor);
    }
}
