package jenkins.internal.provider;

import com.sun.xml.internal.messaging.saaj.soap.ver1_2.Message1_2Impl;
import org.junit.Test;

import javax.xml.soap.SOAPException;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SoapXProviderTest {
    
    @Test
    public void writeTo() throws SOAPException, IOException {
        Message1_2Impl soapMessageMock = mock(Message1_2Impl.class);
        SOAPException exception = mock(SOAPException.class);
        doThrow(exception).when(soapMessageMock).writeTo(any());
        
        Soap12Provider testObject = new Soap12Provider();
        testObject.writeTo(soapMessageMock, null, null, null, null, null, null);
        verify(exception).printStackTrace();
    }
}
