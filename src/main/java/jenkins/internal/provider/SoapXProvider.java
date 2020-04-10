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
package jenkins.internal.provider;

import javax.annotation.Nullable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public abstract class SoapXProvider implements MessageBodyWriter<SOAPMessage>, MessageBodyReader<SOAPMessage> {
    /**
     * check writable
     *
     * @param aClass      Class
     * @param type        Type
     * @param annotations Annotation Array
     * @param mediaType   MediaType
     *
     * @return boolean
     */
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return SOAPMessage.class.isAssignableFrom(aClass);
    }
    
    /**
     * reading form a soap message
     *
     * @param soapEnvelopeClass          class SOAPMessage
     * @param type                       Type
     * @param annotations                Annotation Array
     * @param mediaType                  MediaType
     * @param stringStringMultivaluedMap MultivaluedMap
     * @param inputStream                InputStream
     *
     * @return SOAPMessage
     *
     * @throws IOException             IOException
     * @throws WebApplicationException WebApplicationException
     */
    @Nullable
    public SOAPMessage readFrom(Class<SOAPMessage> soapEnvelopeClass, Type type, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, String> stringStringMultivaluedMap, InputStream inputStream)
            throws IOException, WebApplicationException {
        try {
            String data = "";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                data = builder.toString();
            }
            data = data.substring(data.indexOf("<?xml "), data.lastIndexOf("--MIMEBoundary"));
            MessageFactory messageFactory = getMessageFactory();
            StreamSource messageSource = new StreamSource(new StringReader(data));
            SOAPMessage message = messageFactory.createMessage();
            SOAPPart soapPart = message.getSOAPPart();
            soapPart.setContent(messageSource);
            soapPart.getEnvelope();
            return message;
        } catch (SOAPException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * gets the MessageFactory for the SOAP Version.
     */
    public abstract MessageFactory getMessageFactory() throws SOAPException;
    
    /**
     * get size of soap message
     *
     * @param soapMessage SOAPMessage
     * @param aClass      Class
     * @param type        Type
     * @param annotations Annotation Array
     * @param mediaType   MediaType
     *
     * @return long
     */
    public long getSize(SOAPMessage soapMessage, Class<?> aClass, Type type, Annotation[] annotations,
            MediaType mediaType) {
        return -1;
    }
    
    /**
     * sending a soap message
     *
     * @param soapMessage                SOAPMessage
     * @param aClass                     Class
     * @param type                       Type
     * @param annotations                Annotation Array
     * @param mediaType                  MediaType
     * @param stringObjectMultivaluedMap MultivaluedMap
     * @param outputStream               OutputStream
     *
     * @throws IOException             IOException
     * @throws WebApplicationException WebApplicationException
     */
    public void writeTo(SOAPMessage soapMessage, Class<?> aClass, Type type, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> stringObjectMultivaluedMap, OutputStream outputStream)
            throws IOException, WebApplicationException {
        try {
            soapMessage.writeTo(outputStream);
        } catch (SOAPException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return aClass.isAssignableFrom(SOAPMessage.class);
    }
}
