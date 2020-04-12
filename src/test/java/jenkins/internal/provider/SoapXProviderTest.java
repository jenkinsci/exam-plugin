package jenkins.internal.provider;

import org.junit.Test;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SoapXProviderTest {
    
    @Test
    public void writeTo() throws SOAPException, IOException {
        SOAPMessage soapMessageMock = mock(SOAPMessage.class);
        SOAPException exception = mock(SOAPException.class);
        doThrow(exception).when(soapMessageMock).writeTo(any());
        
        Soap12Provider testObject = new Soap12Provider();
        testObject.writeTo(soapMessageMock, null, null, null, null, null, null);
        verify(exception).printStackTrace();
    }
}
