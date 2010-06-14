/*
 *  Datove schranky (http://github.com/b00lean/datoveschranky)
 *  Copyright (C) 2010  Karel Kyovsky <karel.kyovsky at apksoft.eu>
 *
 *  This file is part of Datove schranky (http://github.com/b00lean/datoveschranky).
 *
 *  Datove schranky is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License
 *  
 *  Datove schranky is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Datove schranky.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package eu.apksoft.android.datoveschranky.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.Transport;
import org.xmlpull.v1.XmlPullParserException;

import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import cz.abclinuxu.datoveschranky.common.entities.Message;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.entities.MessageType;
import cz.abclinuxu.datoveschranky.common.impl.DataBoxException;
import cz.abclinuxu.datoveschranky.common.interfaces.AttachmentStorer;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxDownloadService;
import eu.apksoft.android.datoveschranky.ws.DSUtils;

public class DataBoxDownloadServiceImpl implements DataBoxDownloadService{

	private Transport transport;
	
	public DataBoxDownloadServiceImpl(Transport transport) {
		super();
		this.transport = transport;
	}

	
	public Message signedSentMessageDownload(String messageId, MessageType msgType) {
		throw new UnsupportedOperationException("Not implemented");
		/*//TODO: When I know how to parse binary signed messages, i might finish this method
	    String METHOD_NAME="SignedSentMessageDownload";
	    String SOAP_ACTION=DSUtils.NAMESPACE+"/"+METHOD_NAME;

	    SoapObject request = new SoapObject(DSUtils.NAMESPACE,METHOD_NAME);
	    
	    request.addProperty("dmID", messageId);
	    
	    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    envelope.setOutputSoapObject(request);
	    

	    try {
			transport.call(SOAP_ACTION, envelope);
			SoapPrimitive response = ((SoapPrimitive)envelope.getResponse());
			byte[] signedBinaryMessage = Base64.decode(response.toString());
			return null;
		} catch (SoapFault e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return null;
		*/
	}
	
	public MessageEnvelope messageEnvelopeDownload(String messageId, MessageType msgType) {
	    String METHOD_NAME="MessageEnvelopeDownload";
	    String SOAP_ACTION=DSUtils.NAMESPACE+"/"+METHOD_NAME;

	    SoapObject request = new SoapObject(DSUtils.NAMESPACE,METHOD_NAME);
	    
	    request.addProperty("dmID", messageId);
	    
	    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    envelope.setOutputSoapObject(request);
	    

	    try {
			transport.call(SOAP_ACTION, envelope);
			SoapObject response = ((SoapObject)envelope.getResponse());
			
			
			SoapObject dmDm = (SoapObject)response.getProperty("dmDm");
			MessageEnvelope newEnvelope = DSUtils.parseMessageEnvelope(dmDm, msgType);
			
			return newEnvelope;
		} catch (SoapFault e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Message downloadMessage(String messageId, AttachmentStorer storer) {
	    String METHOD_NAME="MessageDownload";
	    String SOAP_ACTION=DSUtils.NAMESPACE+"/"+METHOD_NAME;

	    SoapObject request = new SoapObject(DSUtils.NAMESPACE,METHOD_NAME);
	    
	    request.addProperty("dmID", messageId);
	    
	    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    envelope.setOutputSoapObject(request);
	    

	    try {
			transport.call(SOAP_ACTION, envelope);
			SoapObject response = ((SoapObject)envelope.getResponse());
			
			
			SoapObject dmDm = (SoapObject)response.getProperty("dmDm");
			MessageEnvelope newEnvelope = DSUtils.parseMessageEnvelope(dmDm, MessageType.RECEIVED);
			List<Attachment> attachements = DSUtils.parseAttachements((SoapObject)dmDm.getProperty("dmFiles"));
			return new Message(newEnvelope,attachements);
		} catch (SoapFault e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Message downloadMessage(MessageEnvelope env, AttachmentStorer storer) {
        if (env.getType() != MessageType.RECEIVED) {
            throw new DataBoxException("Sorry, can dowload only received message.");
        }
		return downloadMessage(env.getMessageID(), storer);
	}

	@Override
	public void downloadSignedMessage(MessageEnvelope envelope, OutputStream os) {
		throw new UnsupportedOperationException("Not implemented");
	}

}
