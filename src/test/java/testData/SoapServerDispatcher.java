package testData;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class SoapServerDispatcher extends Dispatcher {

    //region Member / Constructor

    private String responseText;
    private int responseStatus;
    private boolean sendBody;
    private int examVersion = -1;

    public SoapServerDispatcher() {
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

    public void setExamVersion(int examVersion) {
        this.examVersion = examVersion;
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

        if (examVersion != -1 && examVersion >= 48) {
            return new MockResponse().addHeader("Content-Type", "application/soap+xml").setResponseCode(responseStatus)
                    .setBody(message);
        }
        return new MockResponse().addHeader("Content-Type", "text/xml").setResponseCode(responseStatus)
                .setBody(message);
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

        Name responseName = SOAPFactory.newInstance().createName("SOAPMessage", null, null);
        body.addFault(responseName, faultString);

        if (!sendBody) {
            body.detachNode();
        }

        response.saveChanges();
        TransformerFactory.newInstance().newTransformer()
                .transform(new DOMSource(response.getSOAPPart()), new StreamResult(sw));

        return sw.toString() + "--MIMEBoundary";
    }
}
