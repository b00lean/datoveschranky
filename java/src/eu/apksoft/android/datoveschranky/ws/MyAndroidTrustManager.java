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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import android.os.Environment;

public class MyAndroidTrustManager implements X509TrustManager {
	private KeyStore ks;

	public MyAndroidTrustManager(KeyStore ks) {
		super();
		this.ks = ks;
	}

	public void checkClientTrusted(X509Certificate[] cert, String authType) throws CertificateException {
		//System.out.println("=============== checkClientTrusted " + cert + "  " + authType); //used just for debuging

	}

	public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {

		//  Debug Code for saving certificates, used when www.mojedatovaschranka.cz changes certificates
		/*

		try{
		  KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(null, null);
			for (int i = 0; i < certs.length; i++) {
				X509Certificate certificate = certs[i];
				ks.setCertificateEntry(i + "", certificate);
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ks.store(bos, "kiasdhkjsdh@$@R%.S1257".toCharArray());

			File root = Environment.getExternalStorageDirectory();
			if (root.canWrite()) {
				File file = new File(root, "key_store.ks");
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(bos.toByteArray());
				fos.close();
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		*/
		
		//show me better solution how to trust someone on non-rooted android phone and i buy you a beer. 
		int numberOfHits = 0;
		for (int i = 0; i < certs.length; i++) {
			X509Certificate cert = certs[i];
			try {
				String alias = ks.getCertificateAlias(cert);
				if (alias != null) {
					numberOfHits++;
				}
			} catch (KeyStoreException e) {
				e.printStackTrace();
			}
		}
		if (numberOfHits != 3) {
			throw new CertificateException("Not trusting this server. Number of hits: " + numberOfHits);
		}
		
		//System.out.println("=============== checkServerTrusted " + certs + "  " + authType);

	}

	public X509Certificate[] getAcceptedIssuers() {
		//System.out.println("=============== getAcceptedIssuers");
		return null;
	}
}