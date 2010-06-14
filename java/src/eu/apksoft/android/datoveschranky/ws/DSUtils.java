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
package eu.apksoft.android.datoveschranky.ws;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.kobjects.base64.Base64;
import org.kobjects.isodate.IsoDate;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import cz.abclinuxu.datoveschranky.common.entities.DataBoxState;
import cz.abclinuxu.datoveschranky.common.entities.DataBoxType;
import cz.abclinuxu.datoveschranky.common.entities.DocumentIdent;
import cz.abclinuxu.datoveschranky.common.entities.LegalTitle;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.entities.MessageState;
import cz.abclinuxu.datoveschranky.common.entities.MessageType;
import cz.abclinuxu.datoveschranky.common.entities.OwnerInfo;
import cz.abclinuxu.datoveschranky.common.entities.UserInfo;
import cz.abclinuxu.datoveschranky.common.entities.content.ByteContent;

public class DSUtils {
	private static final String PRODUCTION_URL = "https://ws1.mojedatovaschranka.cz/DS";
	//private static final String TEST_URL = "https://ws1.czebox.cz/DS"; //used just for testing

	public static final String SERVICE_URL = PRODUCTION_URL;
    public static final String NAMESPACE="http://isds.czechpoint.cz/v20";
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public static String toXmlDate(GregorianCalendar date) {
    	if (date == null) {
    		return null;
    	}
    	return IsoDate.dateToString(date.getTime(), IsoDate.DATE_TIME);
    }
    
    public static String toStringDate(GregorianCalendar date) {
    	if (date == null) {
    		return "";
    	}
    	return sdf.format(date.getTime());
    } 

    public static GregorianCalendar toGregorianCalendar(String xmlDate) {
    	if (xmlDate == null) {
    		return null;
    	}
    	GregorianCalendar gregorianCalendar = new GregorianCalendar();
    	gregorianCalendar.setTime(IsoDate.stringToDate(xmlDate, IsoDate.DATE_TIME));
		return gregorianCalendar;
    }
    
    public static void main(String[] args) {
		System.out.println(toGregorianCalendar(toXmlDate(new GregorianCalendar())));
	}

	public static MessageEnvelope parseMessageEnvelope(SoapObject so, MessageType messageType) {
        // odesílatel
		String senderID = getPropertyString(so,"dbIDSender");
        String senderIdentity = getPropertyString(so,"dmSender");
        String senderAddress = getPropertyString(so,"dmSenderAddress"); 
        String senderType = getPropertyString(so,"dmSenderType");
        DataBox sender = new DataBox(senderID, DataBoxType.valueOf(Integer.parseInt(senderType)), senderIdentity, senderAddress);

        // příjemce
        String recipientID = getPropertyString(so,"dbIDRecipient");
        String recipientIdentity = getPropertyString(so,"dmRecipient");
        String recipientAddress = getPropertyString(so,"dmRecipientAddress");
        DataBox recipient = new DataBox(recipientID, recipientIdentity, recipientAddress);

        // anotace
        String annotation = getPropertyString(so,"dmAnnotation");
        if (annotation == null) { // může se stát, že anotace je null...
            annotation = "";
        }

        String messageID = getPropertyString(so,"dmID");
        
    	String law = getPropertyString(so,"dmLegalTitleLaw");
    	String year = getPropertyString(so,"dmLegalTitleYear");
    	String sect = getPropertyString(so,"dmLegalTitleSect");
    	String par = getPropertyString(so,"dmLegalTitlePar");
    	String point = getPropertyString(so,"dmLegalTitlePoint");

    	String toHands = getPropertyString(so,"dmToHands");
    	boolean personalDelivery = getPropertyBoolean(so, "dmPersonalDelivery");
    	boolean allowSubstDelivery = getPropertyBoolean(so, "dmAllowSubstDelivery");
    	
        LegalTitle lt = new LegalTitle(law, year, sect, par, point);
        
        
        
        MessageEnvelope env = new MessageEnvelope(messageType, sender, recipient, messageID, annotation,lt, toHands,personalDelivery, allowSubstDelivery);
		
		String acceptanceTime = getPropertyString(so,"dmAcceptanceTime");
        
        if (acceptanceTime != null) {
            env.setAcceptanceTime(toGregorianCalendar(acceptanceTime));
        }
        String deliveryTime = getPropertyString(so,"dmDeliveryTime");
        
        if (deliveryTime != null) {
            env.setDeliveryTime(toGregorianCalendar(deliveryTime));
        }
        
        env.setState(getPropertyInt(so,"dmMessageStatus") == 0 ? null : MessageState.valueOf(getPropertyInt(so,"dmMessageStatus")));

        // identifikace zprávy odesílatelem
        String senderIdent = getPropertyString(so,"dmSenderIdent");
        String senderRefNumber = getPropertyString(so,"dmSenderRefNumber");
        env.setSenderIdent(new DocumentIdent(senderIdent, senderRefNumber));

        // identifikace zprávy příjemcem
        String recipientIdent = getPropertyString(so,"dmRecipientIdent");
        String recipientRefNumber = getPropertyString(so,"dmRecipientRefNumber");
        env.setRecipientIdent(new DocumentIdent(recipientIdent, recipientRefNumber));

        // a máme hotovo :-)
		return env;
	}

	private static String getPropertyString(SoapObject so, String name) {
		try {
			Object property = so.getProperty(name);
			if (property==null) {
				return null;
			}else if(property instanceof SoapPrimitive){
				SoapPrimitive primitive = (SoapPrimitive)property;
				return primitive.toString();
			}
		} catch (RuntimeException e) {
			return null;
		}
		return null;
	}
	
	private static String getAttributeString(SoapObject so, String name) {
		try {
			return (String)so.getAttribute(name);
		} catch (RuntimeException e) {
			return null;
		}
	}


	private static int getPropertyInt(SoapObject so, String name) {
		String result = getPropertyString(so, name);
		if (result == null) {
			return 0;
		}else{
			return Integer.parseInt(result);
		}
	}

	private static boolean getPropertyBoolean(SoapObject so, String name) {
		String result = getPropertyString(so, name);
		if (result == null) {
			return false;
		}else{
			return "true".equalsIgnoreCase(result);
		}
	}

	public static List<Attachment> parseAttachements(SoapObject dmFiles) {
		List<Attachment> result = new ArrayList<Attachment>();
		int numberOfAttachements = dmFiles.getPropertyCount();
		for (int i=0;i<numberOfAttachements;i++) {
			SoapObject dmFile = (SoapObject)dmFiles.getProperty(i);
			String dmFileDescr = getAttributeString(dmFile,"dmFileDescr");
			String dmFileMetaType = getAttributeString(dmFile,"dmFileMetaType");
			String dmMimeType = getAttributeString(dmFile,"dmMimeType");
			String dmEncodedContent = getPropertyString(dmFile, "dmEncodedContent"); //TODO:add reencoding
			
			Attachment attachment = new Attachment(dmFileDescr,new ByteContent(Base64.decode(dmEncodedContent)));
			attachment.setMetaType(dmFileMetaType);
			attachment.setMimeType(dmMimeType);
			result.add(attachment);
		}
		return result;
	}

	public static OwnerInfo parseOwnerInfo(SoapObject so) {
		OwnerInfo result = new OwnerInfo(getPropertyString(so, "dbID"), DataBoxType.valueOfByName(getPropertyString(so, "dbType")),DataBoxState.create(getPropertyInt(so, "dbState")),getPropertyBoolean(so, "dbEffectiveOVM"), getPropertyBoolean(so, "dbOpenAddressing"));
		result.setIC(getPropertyString(so, "ic"));
		result.setFirmName(getPropertyString(so, "firmName"));

		result.setPersonNameFirstName(getPropertyString(so, "pnFirstName"));
		result.setPersonNameLastName(getPropertyString(so, "pnLastName"));
		result.setPersonNameLastNameAtBirth(getPropertyString(so, "pnLastNameAtBirth"));
		
		result.setBirthDate(getPropertyString(so, "biDate"));
		result.setBirthCity(getPropertyString(so, "biCity"));
		result.setBirthCounty(getPropertyString(so, "biCounty"));
		result.setBirthState(getPropertyString(so, "biState"));

		result.setAddressCity(getPropertyString(so, "adCity"));
		result.setAddressStreet(getPropertyString(so, "adStreet"));
		result.setAddressNumberInStreet(getPropertyString(so, "adNumberInStreet"));
		result.setAddressNumberInMunicipality(getPropertyString(so, "adNumberInMunicipality"));
		result.setAddressZipCode(getPropertyString(so, "adZipCode"));
		result.setAddressState(getPropertyString(so, "adState"));
		
		result.setNationality(getPropertyString(so, "nationality"));
		result.setIdentifier(getPropertyString(so, "identifier"));
		result.setRegistryCode(getPropertyString(so, "registryCode"));
		
		
		return result;
	}

	public static UserInfo parseUserInfo(SoapObject so) {
		UserInfo result = new UserInfo(
				getPropertyString(so, "userID"), 
				getPropertyString(so, "userType"),
				getPropertyString(so, "userPrivils"));
		
		result.setIC(getPropertyString(so, "ic"));
		result.setFirmName(getPropertyString(so, "firmName"));

		result.setPersonNameFirstName(getPropertyString(so, "pnFirstName"));
		result.setPersonNameLastName(getPropertyString(so, "pnLastName"));
		result.setPersonNameLastNameAtBirth(getPropertyString(so, "pnLastNameAtBirth"));
		
		result.setBirthDate(getPropertyString(so, "biDate"));

		result.setAddressCity(getPropertyString(so, "adCity"));
		result.setAddressStreet(getPropertyString(so, "adStreet"));
		result.setAddressNumberInStreet(getPropertyString(so, "adNumberInStreet"));
		result.setAddressNumberInMunicipality(getPropertyString(so, "adNumberInMunicipality"));
		result.setAddressZipCode(getPropertyString(so, "adZipCode"));
		result.setAddressState(getPropertyString(so, "adState"));

		result.setContactAdressCity(getPropertyString(so, "caCity"));
		result.setContactAdressStreet(getPropertyString(so, "caStreet"));
		result.setContactAdressZipCode(getPropertyString(so, "caZipCode"));
		return result;
	}



}
