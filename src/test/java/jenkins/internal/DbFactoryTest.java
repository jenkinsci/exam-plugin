package jenkins.internal;

import Utils.Whitebox;
import jakarta.xml.soap.*;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import testData.SoapServerDispatcher;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;

public class DbFactoryTest {

    @Mock
    private static MockWebServer server;
    @Mock
    private static SoapServerDispatcher dispatcher;
    @InjectMocks
    private DbFactory testObject = new DbFactory();

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
    public void getSoap11Message() throws Exception {
        String modelName = "testModell";
        int examVersion = 44;
        MessageFactory msgFactory = MessageFactory.newInstance();

        Class<?>[] types = new Class<?>[]{String.class, Integer.class, MessageFactory.class};
        SOAPMessage msg = Whitebox.invokeMethod(testObject, "getSoapMessage", types, modelName, examVersion, msgFactory);

        // create parts to compare with
        SOAPMessage messageToCompare = msgFactory.createMessage();
        SOAPPart soapPart = messageToCompare.getSOAPPart();
        SOAPEnvelope envelopeToCompare = soapPart.getEnvelope();

        // envelopeToCompare create
        envelopeToCompare
                .addNamespaceDeclaration("call", "http://call.exam" + examVersion + ".rpc.exam.volkswagenag.com");

        // body created
        SOAPBody bodyToCompare = envelopeToCompare.getBody();
        SOAPElement bodyElement = bodyToCompare.addChildElement(envelopeToCompare.createName("call:SessionLogin"));
        bodyElement.addChildElement("modelName").addTextNode(modelName);
        bodyElement.addChildElement("userName").addTextNode("system");
        bodyElement.addChildElement("userDomain").addTextNode("NT Authority");
        bodyElement.addChildElement("locale").addTextNode("de");
        bodyElement.addChildElement("clientUuid").addTextNode("jenkins");
        messageToCompare.saveChanges();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ByteArrayOutputStream os2 = new ByteArrayOutputStream();
        msg.writeTo(os);
        messageToCompare.writeTo(os2);

        String message1 = os.toString("utf-8");
        String message2 = os2.toString("utf-8");

        assertEquals(message1, message2);
    }

    @Test
    public void getSoap12Message() throws Exception {
        String modelName = "testModell";
        int examVersion = 48;
        MessageFactory msgFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        Class<?>[] types = new Class<?>[]{String.class, Integer.class, MessageFactory.class};
        SOAPMessage msg = Whitebox.invokeMethod(testObject, "getSoapMessage", types, modelName, examVersion, msgFactory);

        // create parts to compare with
        SOAPMessage messageToCompare = msgFactory.createMessage();
        SOAPPart soapPart = messageToCompare.getSOAPPart();
        SOAPEnvelope envelopeToCompare = soapPart.getEnvelope();

        // envelopeToCompare create
        envelopeToCompare
                .addNamespaceDeclaration("call", "http://call.exam" + examVersion + ".rpc.exam.volkswagenag.com");

        // body created
        SOAPBody bodyToCompare = envelopeToCompare.getBody();
        SOAPElement bodyElement = bodyToCompare.addChildElement(envelopeToCompare.createName("call:SessionLogin"));
        bodyElement.addChildElement("modelName").addTextNode(modelName);
        bodyElement.addChildElement("userName").addTextNode("system");
        bodyElement.addChildElement("userDomain").addTextNode("NT Authority");
        bodyElement.addChildElement("locale").addTextNode("de");
        bodyElement.addChildElement("clientUuid").addTextNode("jenkins");
        messageToCompare.saveChanges();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ByteArrayOutputStream os2 = new ByteArrayOutputStream();
        msg.writeTo(os);
        messageToCompare.writeTo(os2);

        String message1 = os.toString("utf-8");
        String message2 = os2.toString("utf-8");

        assertEquals(message1, message2);
    }

    @Test
    public void testModelConnection44() throws Exception {
        // Ok response Exam 4.4 (for coverage of SoapProviders)
        try {
            String expectedResponseText = "OK";
            dispatcher.setExpectedResponseValues(expectedResponseText, 200, true);
            dispatcher.setExamVersion(44);
            String result = DbFactory.testModelConnection("test", "http://localhost:8085", 44);
            assertEquals(expectedResponseText, result);
        } finally {
            // set dispatcher version back to initial to not influence other Tests.
            dispatcher.setExamVersion(-1);
        }
    }

    @Test
    public void testModelConnection() throws Exception {
        // Ok response
        String expectedResponseText = "OK";
        dispatcher.setExpectedResponseValues(expectedResponseText, 200, true);
        String result = DbFactory.testModelConnection("test", "http://localhost:8085", 48);
        assertEquals(expectedResponseText, result);

        // all 4 error states
        expectedResponseText = "Wrong WebService!";
        dispatcher.setExpectedResponseValues(expectedResponseText, 200, true);
        result = DbFactory.testModelConnection("test", "http://localhost:8085", 48);
        assertEquals(expectedResponseText, result);

        expectedResponseText = "Model 'test' does not exist on this server.";
        dispatcher.setExpectedResponseValues(expectedResponseText, 200, true);
        result = DbFactory.testModelConnection("test", "http://localhost:8085", 48);
        assertEquals("Model does not exists", result);

        expectedResponseText = "WstxParsingException";
        dispatcher.setExpectedResponseValues(expectedResponseText, 200, true);
        result = DbFactory.testModelConnection("test", "http://localhost:8085", 48);
        assertEquals(expectedResponseText, result);

        expectedResponseText = "Operation not found";
        dispatcher.setExpectedResponseValues(expectedResponseText, 200, true);
        result = DbFactory.testModelConnection("test", "http://localhost:8085", 48);
        assertEquals(expectedResponseText, result);
    }

    @Test(expected = RuntimeException.class)
    public void testModelConnectionEmptyBody() throws Exception {
        String expectedResponseText = "";
        dispatcher.setExpectedResponseValues(expectedResponseText, 200, false);
        DbFactory.testModelConnection("test", "http://localhost:8085", 48);
    }

    @Test(expected = RuntimeException.class)
    public void testModelConnectionStatusNotOk() throws Exception {
        String expectedResponseText = ".";
        dispatcher.setExpectedResponseValues(expectedResponseText, 404, true);
        DbFactory.testModelConnection("test", "http://localhost:8085", 48);
    }
}
