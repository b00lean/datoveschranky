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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.content.Context;
import eu.apksoft.android.datoveschranky.dto.DataBoxAccess;

public class PreferencesHelper {
	private static boolean inited = false;
	private static Properties settings = new Properties();
	private static final String FILE = "datoveschranky.properties";
	
	private static void load(Context context){
		settings = new Properties();
		try {
			InputStream is = context.openFileInput(FILE);
			settings.load(is);
			is.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		inited = true;
	}
	
	public static String getProperty(Context context, String key, String defaultValue) {
		synchronized (settings) {
			if (!inited) {
				load(context);
			}
			return settings.getProperty(key, defaultValue);
		}
	}
		
	public static void setProperty(Context context, String key, String value) {
		synchronized (settings) {
			if (!inited) {
				load(context);
			}
			settings.put(key, value);			
			try {
				OutputStream os = context.openFileOutput(FILE, Context.MODE_PRIVATE);
				settings.save(os, FILE);
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void setDataBoxAccesses(List<DataBoxAccess> accesses,Context context) {
		synchronized (FILE) {
			setProperty(context, "access.count", accesses.size() + "");
			for (int i=0;i<accesses.size();i++){
				setProperty(context, "access." + i +".personId", accesses.get(i).getPersonId());
				setProperty(context, "access." + i +".password", accesses.get(i).getPassword());
				setProperty(context, "access." + i +".boxId", accesses.get(i).getBoxId());
				setProperty(context, "access." + i +".boxName", accesses.get(i).getBoxName());
				if (accesses.get(i).getLastKnownMessageId() != null) {
					setProperty(context, "access." + i +".lastMessageId", accesses.get(i).getLastKnownMessageId());
				}else{
					setProperty(context, "access." + i +".lastMessageId", "");
				}
				setProperty(context, "access." + i + ".passwordExpires", accesses.get(i).getPasswordExpires() + "");
				setProperty(context, "access." + i + ".passwordExpirationLastChecked", accesses.get(i).getPasswordExpirationLastChecked() +"");
			}
		}
		
	}

	public static List<DataBoxAccess> getDataBoxAccesses(Context context) {
		synchronized (FILE) {
			List<DataBoxAccess> result = new ArrayList<DataBoxAccess>(); 
			int count = Integer.parseInt(getProperty(context, "access.count", "0"));
			for (int i=0;i<count;i++){
				result.add(new DataBoxAccess(
						getProperty(context, "access." + i + ".personId", ""), //too short keys, i'm lazy to create constants for them
						getProperty(context, "access." + i + ".password", ""),
						getProperty(context, "access." + i + ".boxId", ""),
						getProperty(context, "access." + i + ".boxName", ""),
						getProperty(context, "access." + i + ".lastMessageId", ""),
						Long.parseLong(getProperty(context, "access." + i + ".passwordExpires", "0")),
						Long.parseLong(getProperty(context, "access." + i + ".passwordExpirationLastChecked", "0"))
				));
			}
			return result;
		}
	}
	
	public static String getPassword(Context context) {
		return getProperty(context, "password", null);
	}
	
	public static void setPassword(Context context, String password) {
		setProperty(context, "password", password);
	}

}
