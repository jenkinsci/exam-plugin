package jenkins.internal;

import Utils.Mocks;
import hudson.model.Executor;
import jakarta.ws.rs.core.Response;
import jenkins.internal.data.ExamStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientRequestStaticTest {

    private static MockedStatic<RemoteService> remoteServiceMockedStatic;
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    private ClientRequest testObject;

    @Before
    public void setUp() {
        testObject = new ClientRequest(System.out, 0, null);
    }

    @Test
    public void isApiAvailable() throws Exception {
        remoteServiceMockedStatic = Mocks.mockStatic(RemoteService.class);
        remoteServiceMockedStatic.when(() -> RemoteService.getJSON(any(), anyInt(), anyString(), any()))
                .thenThrow(new InterruptedException());
        exception.expect(InterruptedException.class);
        testObject.isApiAvailable();
    }

    private Executor prepareMocks(String statusJobName) throws InterruptedException, IOException {
        ExamStatus status = new ExamStatus();
        status.setJobRunning(Boolean.TRUE);
        status.setJobName(statusJobName);

        remoteServiceMockedStatic = Mocks.mockStatic(RemoteService.class);
        remoteServiceMockedStatic.when(() -> RemoteService.getJSON(any(), anyInt(), any(), any()))
                .thenReturn(new RemoteServiceResponse(Response.ok().build().getStatus(), status, ""));

        Executor executorMock = mock(Executor.class);
        when(executorMock.isInterrupted()).thenReturn(Boolean.FALSE);

        return executorMock;
    }

    private void verifyMocks(Executor executorMock) throws InterruptedException {
        verify(executorMock, times(2)).isInterrupted();
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
