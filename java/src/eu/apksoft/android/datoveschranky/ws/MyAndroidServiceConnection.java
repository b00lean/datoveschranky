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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.ksoap2.transport.ServiceConnection;


public class MyAndroidServiceConnection implements ServiceConnection{
	 private HttpURLConnection connection;

	    public MyAndroidServiceConnection(String url) throws IOException {
	    	System.setProperty("http.keepAlive", "false");
	        connection = (HttpURLConnection) new URL(url).openConnection();
	        connection.setUseCaches(false);
	        connection.setDoOutput(true);
	        connection.setDoInput(true);
	    }

	    public void connect() throws IOException {
	        connection.connect();
	    }

	    public void disconnect() {
	        connection.disconnect();
	    }

	    public void setRequestProperty(String string, String soapAction) {
	        connection.setRequestProperty(string, soapAction);
	    }

	    public void setRequestMethod(String requestMethod) throws IOException {
	        connection.setRequestMethod(requestMethod);
	    }

	    public OutputStream openOutputStream() throws IOException {
	        return connection.getOutputStream();
	    }

	    public InputStream openInputStream() throws IOException {
	        return connection.getInputStream();
	    }

	    public InputStream getErrorStream() {
	        return connection.getErrorStream();
	    }

}