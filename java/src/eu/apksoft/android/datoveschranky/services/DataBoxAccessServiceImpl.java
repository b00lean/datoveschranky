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
import java.util.GregorianCalendar;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.Transport;
import org.xmlpull.v1.XmlPullParserException;

import cz.abclinuxu.datoveschranky.common.entities.OwnerInfo;
import cz.abclinuxu.datoveschranky.common.entities.UserInfo;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxAccessService;
import eu.apksoft.android.datoveschranky.ws.DSUtils;
import eu.apksoft.android.datoveschranky.ws.ServiceException;

public class DataBoxAccessServiceImpl implements DataBoxAccessService {

	private Transport transport;
	
	public DataBoxAccessServiceImpl(Transport transport) {
		super();
		this.transport = transport;
	}

	@Override
	public OwnerInfo GetOwnerInfoFromLogin() {
	    String METHOD_NAME="GetOwnerInfoFromLogin";
	    String SOAP_ACTION=DSUtils.NAMESPACE+"/"+METHOD_NAME;

	    SoapObject request = new SoapObject(DSUtils.NAMESPACE,METHOD_NAME);
	    
	    request.addProperty("dbDummy", "");
	    
	    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    envelope.setOutputSoapObject(request);
	    

	    try {
			transport.call(SOAP_ACTION, envelope);
			SoapObject response = ((SoapObject)envelope.getResponse());
			
			return DSUtils.parseOwnerInfo(response);
			
			
		} catch (SoapFault e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
	}
	
	@Override
	public UserInfo GetUserInfoFromLogin() {
	    String METHOD_NAME="GetUserInfoFromLogin";
	    String SOAP_ACTION=DSUtils.NAMESPACE+"/"+METHOD_NAME;

	    SoapObject request = new SoapObject(DSUtils.NAMESPACE,METHOD_NAME);
	    
	    request.addProperty("dbDummy", "");
	    
	    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    envelope.setOutputSoapObject(request);
	    

	    try {
			transport.call(SOAP_ACTION, envelope);
			SoapObject response = ((SoapObject)envelope.getResponse());
			
			return DSUtils.parseUserInfo(response);
		} catch (SoapFault e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
	}

	

	@Override
	public GregorianCalendar GetPasswordInfo() {
	    String METHOD_NAME="GetPasswordInfo";
	    String SOAP_ACTION=DSUtils.NAMESPACE+"/"+METHOD_NAME;

	    SoapObject request = new SoapObject(DSUtils.NAMESPACE,METHOD_NAME);
	    
	    request.addProperty("dbDummy", "");
	    
	    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    envelope.setOutputSoapObject(request);
	    

	    try {
			transport.call(SOAP_ACTION, envelope);
			SoapPrimitive response = ((SoapPrimitive)envelope.getResponse());
			return DSUtils.toGregorianCalendar(response.toString());
		} catch (SoapFault e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
	}


}
