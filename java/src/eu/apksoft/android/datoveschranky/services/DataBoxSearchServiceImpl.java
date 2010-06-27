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
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.Transport;
import org.xmlpull.v1.XmlPullParserException;

import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import cz.abclinuxu.datoveschranky.common.entities.DataBoxState;
import cz.abclinuxu.datoveschranky.common.entities.DataBoxType;
import cz.abclinuxu.datoveschranky.common.entities.OwnerInfo;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxSearchService;
import eu.apksoft.android.datoveschranky.ws.DSUtils;
import eu.apksoft.android.datoveschranky.ws.ServiceException;

public class DataBoxSearchServiceImpl implements DataBoxSearchService {

	private Transport transport;
	
	public DataBoxSearchServiceImpl(Transport transport) {
		super();
		this.transport = transport;
	}

	@Override
	public DataBoxState checkDataBox(DataBox db) {
		throw new UnsupportedOperationException("Unimplemented.");
	}

	@Override
	public DataBox findDataBoxByID(String id) {
		throw new UnsupportedOperationException("Unimplemented.");
	}

	@Override
	public List<DataBox> findOVMsByName(String prefix) {
	    String METHOD_NAME="FindDataBox";
	    String SOAP_ACTION=DSUtils.NAMESPACE+"/"+METHOD_NAME;

	    SoapObject request = new SoapObject(DSUtils.NAMESPACE,METHOD_NAME);
	    
	    SoapObject so = new SoapObject(DSUtils.NAMESPACE, "dbOwnerInfo");
	    so.addProperty("firmName", prefix);
	    so.addProperty("dbType",DataBoxType.OVM.toString()); //Only OVM for now
	    request.addProperty("dbOwnerInfo", so);
	    
	    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    envelope.setOutputSoapObject(request);
	    
	    try {
			transport.call(SOAP_ACTION, envelope);
			Object res = envelope.getResponse();
			List<DataBox> result = new ArrayList<DataBox>();
			
			if(res instanceof SoapObject){
				SoapObject response = ((SoapObject)res);
				int count = response.getPropertyCount();
				
				if (count == 0) {
					return result;
				}else{
					for (int i = 0; i < count; i++) {
						Object r = response.getProperty(i);
						
						if (r instanceof SoapObject) { //must be our owner object
							SoapObject property = (SoapObject)r;
							OwnerInfo ownerInfo = DSUtils.parseOwnerInfo(property);
							result.add(new DataBox(ownerInfo.getDataBoxID(),ownerInfo.getFirmName(), null));
						}else{
							result.clear();
							return result;
						}
					}
					return result;
				}
	    	}else{
	    		return result; 
	    	}
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
