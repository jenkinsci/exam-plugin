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

import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
import jakarta.xml.soap.*;
import jenkins.internal.provider.Soap11Provider;
import jenkins.internal.provider.Soap12Provider;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyInvocation;
import org.glassfish.jersey.client.JerseyWebTarget;

import javax.annotation.Nullable;

public class DbFactory {

    private final static int OK = Response.ok().build().getStatus();

    private static SOAPMessage getSoapMessage(String modelName, Integer examVersion, MessageFactory messageFactory)
            throws SOAPException {
        SOAPMessage message = messageFactory.createMessage();
        SOAPPart soapPart = message.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.addNamespaceDeclaration("call", "http://call.exam" + examVersion.intValue() + ".rpc.exam.volkswagenag.com");

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
    public static String testModelConnection(String modelName, String targetEndpoint, Integer examVersion)
            throws SOAPException {
        Response response = callExamModeler(modelName, targetEndpoint, examVersion);

        SOAPMessage retMessage = (SOAPMessage) response.getEntity();
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
    private static Response callExamModeler(String modelName, String targetEndpoint, Integer examVersion)
            throws SOAPException {
        MessageFactory messageFactory;
        String type;
        JerseyClient client = JerseyClientBuilder.createClient();
        JerseyClient register = client;
        if (examVersion >= 48) {
            type = SOAPConstants.SOAP_1_2_CONTENT_TYPE;
            register = client.register(Soap12Provider.class);
            messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        } else {
            type = SOAPConstants.SOAP_1_1_CONTENT_TYPE;
            register = client.register(Soap11Provider.class);
            messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        }
        JerseyWebTarget service = register.target(targetEndpoint);
        JerseyInvocation.Builder builder = service.request().header("SOAPAction", "sessionLogin");
        SOAPMessage message = getSoapMessage(modelName, examVersion, messageFactory);
        return builder.accept(type).post(Entity.entity(message, type));
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
