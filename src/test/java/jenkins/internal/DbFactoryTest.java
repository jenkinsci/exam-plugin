package jenkins.internal;

import com.sun.jersey.api.client.WebResource;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;
import testData.ServerDispatcher;
import testData.SoapServerDispatcher;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import javax.xml.soap.*;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

public class DbFactoryTest {

    @InjectMocks
    private DbFactory testObject = new DbFactory();

    private final String baseUrl = "http://localhost:8085";
    @Mock
    private static MockWebServer server;
    @Mock
    private static SoapServerDispatcher dispatcher;

    @BeforeClass
    public static void oneTimeSetup() throws Exception {
        server = new MockWebServer();
        dispatcher = new SoapServerDispatcher();
        server.setDispatcher(dispatcher);
        server.start(8085);
    }

    @AfterClass
    public static void oneTimeTeardown() throws Exception {
        server.shutdown();

        server = null;
        dispatcher = null;
    }

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
        // Ok response
        String expectedResponseText = "OK";
        dispatcher.setExpectedResponseValues(expectedResponseText, 200, true);
        String result = DbFactory.testModelConnection("test", "http://localhost:8085", 44);
        assertEquals(expectedResponseText, result);

        // all 4 error states
        expectedResponseText = "Wrong WebService!";
        dispatcher.setExpectedResponseValues(expectedResponseText, 200, true);
        result = DbFactory.testModelConnection("test", "http://localhost:8085", 44);
        assertEquals(expectedResponseText, result);

        expectedResponseText = "Model 'test' does not exist on this server.";
        dispatcher.setExpectedResponseValues(expectedResponseText, 200, true);
        result = DbFactory.testModelConnection("test", "http://localhost:8085", 44);
        assertEquals("Model does not exists", result);

        expectedResponseText = "WstxParsingException";
        dispatcher.setExpectedResponseValues(expectedResponseText, 200, true);
        result = DbFactory.testModelConnection("test", "http://localhost:8085", 44);
        assertEquals(expectedResponseText, result);

        expectedResponseText = "Operation not found";
        dispatcher.setExpectedResponseValues(expectedResponseText, 200, true);
        result = DbFactory.testModelConnection("test", "http://localhost:8085", 44);
        assertEquals(expectedResponseText, result);
    }

    @Test(expected = RuntimeException.class)
    public void testModelConnectionEmptyBody() throws Exception {
        String expectedResponseText = "";
        dispatcher.setExpectedResponseValues(expectedResponseText, 200, false);
        DbFactory.testModelConnection("test", "http://localhost:8085", 44);
    }

    @Test(expected = RuntimeException.class)
    public void testModelConnectionStatusNotOk() throws Exception {
        String expectedResponseText = ".";
        dispatcher.setExpectedResponseValues(expectedResponseText, 404, true);
        DbFactory.testModelConnection("test", "http://localhost:8085", 44);
    }
}