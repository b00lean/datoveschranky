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
package eu.apksoft.android.datoveschranky.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;

import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources.NotFoundException;

import com.nullwire.trace.ExceptionHandler;

import cz.abclinuxu.datoveschranky.common.entities.LegalTitle;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import eu.apksoft.android.datoveschranky.R;
import eu.apksoft.android.datoveschranky.ws.MyAndroidTrustManager;
import eu.apksoft.android.datoveschranky.ws.ServiceException;

public class AndroidUtils {
	private static MessageEnvelope selectedEnvelope;
	private static boolean sslInited = false; 
	
	public static void registerForExceptions(Context context) {
		ExceptionHandler.register(context, "http://www.apksoft.eu/android/exception.jsp");
	}
	
	public static void initSSLIfNeeded(Context context) {
		if (!sslInited) {
			try {
		        SSLContext sslcontext = SSLContext.getInstance("TLS");
		        KeyStore ks ;
				try {
					ks = KeyStore.getInstance("BKS");//KeyStore.getDefaultType()); //force Bouncy castle since that is the format of our keystore file
					
					InputStream keyStoreStream = context.getResources().openRawResource(R.raw.key_store);
					ks.load(keyStoreStream,"kiasdhkjsdh@$@R%.S1257".toCharArray());
	
					sslcontext.init(null, new TrustManager[] { new MyAndroidTrustManager(ks) }, null);
			        HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
	
				} catch (KeyStoreException e) {
					showError(context, R.string.error_keystore_exception);
				} catch (NotFoundException e) {
					showError(context, R.string.error_keystore_exception);
				} catch (CertificateException e) {
					showError(context, R.string.error_keystore_exception);
				} catch (IOException e) {
					showError(context, R.string.error_keystore_exception);
				}
		        
			} catch (KeyManagementException e1) {
				showError(context, R.string.error_keystore_exception);
			} catch (NoSuchAlgorithmException e1) {
				showError(context, R.string.error_keystore_exception);
			}
			sslInited =true;
		}
	}

	public static String formatLegalTitle(Context context, LegalTitle legalTitle) {
		if (legalTitle == null) {
			return "";
		}
		String result = "";
		
		if (legalTitle.getLaw() != null && legalTitle.getLaw().trim().length() != 0 && Integer.parseInt(legalTitle.getLaw()) > 0) {
			result +=legalTitle.getLaw();
		}
		if (legalTitle.getYear() != null && legalTitle.getYear().trim().length() != 0 && Integer.parseInt(legalTitle.getYear()) > 0) {
			result +="/" + legalTitle.getYear();
		}

		if (legalTitle.getSect() != null && legalTitle.getSect().trim().length() != 0) {
			result +=" §" + legalTitle.getSect();
		}

		if (legalTitle.getPar() != null && legalTitle.getPar().trim().length() != 0) {
			result +=" " + context.getResources().getString(R.string.paragraph) + " " + legalTitle.getPar();
		}

		if (legalTitle.getPoint() != null && legalTitle.getPoint().trim().length() != 0) {
			result +=" " + context.getResources().getString(R.string.character) + legalTitle.getPoint();
		}
		return result;
	}
	
	public static String formatBoolean(Context context, boolean value) {
		if (value) {
			return context.getString(R.string.yes);
		}else{
			return context.getString(R.string.no);
		}
	}

	public static MessageEnvelope getSelectedEnvelope() {
		return selectedEnvelope;
	}

	public static void setSelectedEnvelope(MessageEnvelope selectedEnvelope) {
		AndroidUtils.selectedEnvelope = selectedEnvelope;
	}

	public static void showError(Context context,String errorMessage) {
		new AlertDialog.Builder(context)
        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
        })
        .setMessage(errorMessage)
        .show();
	}
	
	public static void showError(Context context,int errorMessage) {
		showError(context, context.getResources().getString(errorMessage));
	}

	public static void showError(Context context, Throwable e) {
		if (e instanceof ServiceException) {
			e = ((ServiceException)e).getCause();
		}
		e.printStackTrace();
		
		int errorMessage = R.string.unknown_error;
		
		if (e instanceof UnknownHostException) {
			errorMessage = R.string.error_unknown_host;
		}else if (e instanceof SSLException) {
			errorMessage = R.string.error_ssl_exception;
		}else if (e instanceof XmlPullParserException) {
			errorMessage = R.string.error_access_denied;
		}else if (e instanceof IOException) {
			errorMessage = R.string.comm_error;
		}
		if (errorMessage == R.string.unknown_error) {
			showError(context, context.getResources().getString(errorMessage, e.getMessage()));
		}else{
			showError(context, errorMessage);
		}
	}

}
