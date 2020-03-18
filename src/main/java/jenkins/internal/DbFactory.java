/**
 * Copyright (c) 2018 MicroNova AG
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * <p>
 * 3. Neither the name of MicroNova AG nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jenkins.internal;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import jenkins.internal.provider.Soap12Provider;
import jenkins.internal.provider.Soap11Provider;

import javax.annotation.Nullable;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import javax.xml.soap.*;

public class DbFactory {

    private final static int OK = Response.ok().build().getStatus();

    private static SOAPMessage getSoapMessage(String modelName, int examVersion, MessageFactory messageFactory) throws SOAPException {
        SOAPMessage message = messageFactory.createMessage();
        SOAPPart soapPart = message.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.addNamespaceDeclaration("call", "http://call.exam" + examVersion + ".rpc.exam.volkswagenag.com");

        SOAPBody body = envelope.getBody();
        SOAPElement bodyElement = body.addChildElement(envelope.createName("call:SessionLogin"));
        bodyElement.addChildElement("modelName").addTextNode(modelName);
        bodyElement.addChildElement("userName").addTextNode("system");
        bodyElement.addChildElement("userDomain").addTextNode("NT Authority");
        bodyElement.addChildElement("locale").addTextNode("de");
        bodyElement.addChildElement("clientUuid").addTextNode("jenkins");
        message.saveChanges();

        return message;
    }

    /**
     * Try to connect to the EXAM server and check the connection status.
     *
     * @param modelName      String
     * @param targetEndpoint String
     * @param examVersion    int
     * @return String
     * @throws SOAPException SOAPException
     */
    public static String testModelConnection(String modelName, String targetEndpoint, int examVersion)
            throws SOAPException {
        ClientResponse response = callExamModeler(modelName, targetEndpoint, examVersion);

        SOAPMessage retMessage = response.getEntity(SOAPMessage.class);
        SOAPEnvelope retEnvelope = retMessage.getSOAPPart().getEnvelope();
        SOAPBody retBody = retEnvelope.getBody();
        if (retBody == null) {
            throw new ServerErrorException("Failed : HTTP error code : " + response.getStatus(),
                    response.getStatus());
        }
        SOAPFault retFault = retBody.getFault();
        String examServerFault = getExamServerFault(modelName, retFault);
        if (examServerFault != null) {
            return examServerFault;
        }
        if (response.getStatus() != OK) {
            throw new ServerErrorException("Failed : HTTP error code : " + response.getStatus(),
                    response.getStatus());
        }
        return "OK";
    }

    /**
     * Calls the ExamModelerService depending on the examVersion. If 4.8+ is used SOAP 1.2 is active.
     *
     * @param modelName      String
     * @param targetEndpoint String
     * @param examVersion    int
     * @return String
     * @throws SOAPException SOAPException
     */
    private static ClientResponse callExamModeler(String modelName, String targetEndpoint, int examVersion) throws SOAPException {
        ClientConfig clientConfig = new DefaultClientConfig();
        MessageFactory messageFactory;
        if (examVersion >= 48) {
            clientConfig.getClasses().add(Soap12Provider.class);
            messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        } else {
            clientConfig.getClasses().add(Soap11Provider.class);
            messageFactory = MessageFactory.newInstance();
        }
        Client client = Client.create(clientConfig);
        SOAPMessage message = getSoapMessage(modelName, examVersion, messageFactory);

        WebResource service = client.resource(targetEndpoint);
        return service.header("SOAPAction", "sessionLogin").post(ClientResponse.class, message);
    }

    @Nullable
    private static String getExamServerFault(String modelName, SOAPFault retFault) {
        if (retFault != null) {
            String text = retFault.getFaultString();
            if (text.contains("Wrong WebService!")) {
                return "Wrong WebService!";
            }

            if (text.contains("Model '" + modelName + "' does not exist on this server.")) {
                return "Model does not exists";
            }

            if (text.contains("WstxParsingException")) {
                return "WstxParsingException";
            }

            if (text.contains("Operation not found")) {
                return "Operation not found";
            }
        }
        return null;
    }
}
