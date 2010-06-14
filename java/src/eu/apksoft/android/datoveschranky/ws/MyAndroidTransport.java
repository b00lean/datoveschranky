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
import java.io.*;
	
import org.kobjects.base64.Base64;
import org.ksoap2.transport.*;
import org.ksoap2.*;
import org.xmlpull.v1.*;

public class MyAndroidTransport extends Transport {
	
		private String username;
		private String password;
	    /**
	     * Creates instance of HttpTransport with set url
	     *
	     * @param url
	     *            the destination to POST SOAP data
	     */
	    public MyAndroidTransport(String url) {
	        super(url);
	    	//debug = true;
	    }
	    public MyAndroidTransport(String url, String username, String password) {
	        super(url);
	    	//debug = true;
	    	this.username = username;
	    	this.password = password;
	    }
	
	    /**
	     * set the desired soapAction header field
	     *
	     * @param soapAction
	     *            the desired soapAction
	     * @param envelope
	     *            the envelope containing the information for the soap call.
	     */
	    public void call(String soapAction, SoapEnvelope envelope) throws IOException, XmlPullParserException {
	        if (soapAction == null)
	            soapAction = "\"\"";
	        byte[] requestData = createRequestData(envelope);
	        requestDump = debug ? new String(requestData) : null;
	        responseDump = null;
	        ServiceConnection connection = getServiceConnection();
	        //connection.connect();
	        try {
	                connection.setRequestProperty("User-Agent", "aDatoveSchranky/1.0");
	                if (username != null && password != null) {
	            	    String login = Base64.encode((username + ":" + password).getBytes());
	            	    connection.setRequestProperty("Authorization", "Basic " + login);
	                }
	                
	                connection.setRequestProperty("SOAPAction", soapAction);
	                connection.setRequestProperty("Content-Type", "text/xml");
	                connection.setRequestProperty("Connection", "close");
	                connection.setRequestProperty("Content-Length", "" + requestData.length);
	                connection.setRequestMethod("POST");           
	                OutputStream os = connection.openOutputStream();
	                os.write(requestData, 0, requestData.length);
	                os.flush();
	                os.close();
	                    requestData = null;
	               
	                InputStream is;
	                try {
	                    is = connection.openInputStream();
	                } catch (IOException e) {
	                    is = connection.getErrorStream();
	                    if (is == null) {
	                        connection.disconnect();
	                        throw (e);
	                    }
	                }
	                if (debug) {
	                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	                    byte[] buf = new byte[256];
	                    while (true) {
	                        int rd = is.read(buf, 0, 256);
	                        if (rd == -1)
	                            break;
	                        bos.write(buf, 0, rd);
	                    }
	                    bos.flush();
	                    buf = bos.toByteArray();
	                    responseDump = new String(buf);
	                    is.close();
	                    is = new ByteArrayInputStream(buf);
	                    if (debug) {
	                            System.out.println("DBG:request:" + requestDump);
	                            System.out.println("==============================");
	                            System.out.println("DBG:response:" + responseDump);
	                    }
	                }               
	                parseResponse(envelope, is);
	        } finally {
	                connection.disconnect();
	        }
	    }
	
	    protected ServiceConnection getServiceConnection() throws IOException {
	        return new MyAndroidServiceConnection(url);
	    }
	}