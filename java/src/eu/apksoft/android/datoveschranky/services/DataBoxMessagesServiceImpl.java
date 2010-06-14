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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.Transport;
import org.xmlpull.v1.XmlPullParserException;

import cz.abclinuxu.datoveschranky.common.entities.DeliveryInfo;
import cz.abclinuxu.datoveschranky.common.entities.Hash;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.entities.MessageState;
import cz.abclinuxu.datoveschranky.common.entities.MessageType;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxMessagesService;
import eu.apksoft.android.datoveschranky.ws.DSUtils;

public class DataBoxMessagesServiceImpl implements DataBoxMessagesService{

	private Transport transport;
	
	public DataBoxMessagesServiceImpl(Transport transport) {
		super();
		this.transport = transport;
	}

	@Override
	public DeliveryInfo getDeliveryInfo(MessageEnvelope env) {
		throw new UnsupportedOperationException("Unimplemented.");
	}

	@Override
	public List<MessageEnvelope> getListOfReceivedMessages(GregorianCalendar from, GregorianCalendar to, EnumSet<MessageState> filter, int offset, int limit) {
		
	    String METHOD_NAME="GetListOfReceivedMessages";
	    String SOAP_ACTION=DSUtils.NAMESPACE+"/"+METHOD_NAME;

	    SoapObject request = new SoapObject(DSUtils.NAMESPACE,METHOD_NAME);
	    
	    request.addProperty("dmFromTime", DSUtils.toXmlDate(from));
	    request.addProperty("dmToTime", DSUtils.toXmlDate(to));
	    request.addProperty("dmRecipientOrgUnitNum", null);
	    request.addProperty("dmStatusFilter", MessageState.toInt(filter));
	    request.addProperty("dmOffset", offset);
	    request.addProperty("dmLimit", limit);
	    
	    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    envelope.setOutputSoapObject(request);
	    

	    try {
			transport.call(SOAP_ACTION, envelope);
			SoapObject response = ((SoapObject)envelope.getResponse());
			int numberOfEnvelopes = response.getPropertyCount();
			
			List<MessageEnvelope> result = new ArrayList<MessageEnvelope>();
			for (int i = 0; i < numberOfEnvelopes; i++) {
				result.add(DSUtils.parseMessageEnvelope((SoapObject)response.getProperty(i), MessageType.RECEIVED));
			}
			return result;
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
	public List<MessageEnvelope> getListOfSentMessages(GregorianCalendar from, GregorianCalendar to, EnumSet<MessageState> filter, int offset, int limit) {
	    String METHOD_NAME="GetListOfSentMessages";
	    String SOAP_ACTION=DSUtils.NAMESPACE+"/"+METHOD_NAME;

	    SoapObject request = new SoapObject(DSUtils.NAMESPACE,METHOD_NAME);
	    
	    request.addProperty("dmFromTime", DSUtils.toXmlDate(from));
	    request.addProperty("dmToTime", DSUtils.toXmlDate(to));
	    request.addProperty("dmSenderOrgUnitNum", null);
	    request.addProperty("dmStatusFilter", MessageState.toInt(filter));
	    request.addProperty("dmOffset", offset);
	    request.addProperty("dmLimit", limit);
	    
	    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    envelope.setOutputSoapObject(request);
	    

	    try {
			transport.call(SOAP_ACTION, envelope);
			SoapObject response = ((SoapObject)envelope.getResponse());
			int numberOfEnvelopes = response.getPropertyCount();
			
			List<MessageEnvelope> result = new ArrayList<MessageEnvelope>();
			for (int i = 0; i < numberOfEnvelopes; i++) {
				result.add(DSUtils.parseMessageEnvelope((SoapObject)response.getProperty(i), MessageType.SENT));
			}
			
			return result;
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
	public void markMessageAsDownloaded(MessageEnvelope env) {
		throw new UnsupportedOperationException("Unimplemented.");
		
	}

	@Override
	public Hash verifyMessage(MessageEnvelope envelope) {
		throw new UnsupportedOperationException("Unimplemented.");
	}

}
