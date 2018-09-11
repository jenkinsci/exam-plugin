package jenkins.internal;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.xml.soap.*;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

public class DbFactoryTest {

    @InjectMocks
    private DbFactory testObject = new DbFactory();

    @Test
    public void getSoapMessage() throws Exception {
        String modelName = "testModell";
        int examVersion = 44;

        SOAPMessage msg = Whitebox.invokeMethod(testObject, "getSoapMessage", modelName, examVersion);

        // create parts to compare with
        MessageFactory msgFactory = MessageFactory.newInstance();
        SOAPMessage messageToCompare = msgFactory.createMessage();
        SOAPPart soapPart = messageToCompare.getSOAPPart();
        SOAPEnvelope envelopeToCompare = soapPart.getEnvelope();

        // envelopeToCompare create
        envelopeToCompare.addNamespaceDeclaration("call", "http://call.exam" + examVersion + ".rpc.exam.volkswagenag.com");

        // body created
        SOAPBody bodyToCompare = envelopeToCompare.getBody();
        SOAPElement bodyElement = bodyToCompare.addChildElement(envelopeToCompare.createName("call:SessionLogin"));
        bodyElement.addChildElement("modelName").addTextNode(modelName);
        bodyElement.addChildElement("userName").addTextNode("system");
        bodyElement.addChildElement("userDomain").addTextNode("NT Authority");
        bodyElement.addChildElement("locale").addTextNode("de");
        bodyElement.addChildElement("clientUuid").addTextNode("jenkins");
        messageToCompare.saveChanges();

        OutputStream os = new ByteArrayOutputStream();
        OutputStream os2 = new ByteArrayOutputStream();
        msg.writeTo(os);
        messageToCompare.writeTo(os2);

        String message1 = ((ByteArrayOutputStream) os).toString("utf-8");
        String message2 = ((ByteArrayOutputStream) os2).toString("utf-8");

        assertEquals(message1, message2);
    }

    @Test
    public void testModelConnection() throws Exception {
        // TODO: implementieren
    }
}