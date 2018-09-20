package testData;

import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import jenkins.internal.SoapProvider;
import okhttp3.Response;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class SoapServerDispatcher extends Dispatcher {

    //region Member / Constructor

    private Map<String, MockResponse> responseMap;

    private String responseText;
    private int responseStatus;
    private boolean sendBody;

    public SoapServerDispatcher() {
        responseMap = new HashMap<>();
        responseText = "";
        responseStatus = 0;
        sendBody = true;
    }

    public String getResponseText() {
        return this.responseText;
    }

    public void setResponseText(String expectedResponseText) {
        this.responseText = expectedResponseText;
    }

    public boolean isSendBody() {
        return this.sendBody;
    }

    public void setSendBody(boolean sendBody) {
        this.sendBody = sendBody;
    }

    public void setResponseStatus(int status) {
        this.responseStatus = status;
    }

    public int getResponseStatus() {
        return this.responseStatus;
    }

    //endregion

    //region Dispatcher Overrides

    @Override
    public MockResponse dispatch(RecordedRequest request) {
        String message = "";

        try {
            message = this.getSoapMessageWithFaultResponse(responseText);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        MockResponse res = new MockResponse().addHeader("Content-Type", "text/xml")
                .setResponseCode(responseStatus).setBody(message);

        return res;
    }

    //endregion

    public void setExpectedResponseValues(String expectedText, int expectedStatus, boolean sendBody) {
        setResponseText(expectedText);
        setResponseStatus(expectedStatus);
        setSendBody(sendBody);
    }

    private String getSoapMessageWithFaultResponse(String faultString) throws Exception {
        StringWriter sw = new StringWriter();
        SOAPMessage response = MessageFactory.newInstance().createMessage();
        SOAPPart part = response.getSOAPPart();
        SOAPEnvelope envelope = part.getEnvelope();
        SOAPBody body = envelope.getBody();

        Name responseName = NameImpl.create("SOAPMessage", null, null);
        body.addFault(responseName, faultString);

        if (!sendBody) {
            body.detachNode();
        }

        response.saveChanges();
        TransformerFactory.newInstance().newTransformer().transform(
                new DOMSource(response.getSOAPPart()),
                new StreamResult(sw)
        );

        return sw.toString() + "--MIMEBoundary";
    }
}