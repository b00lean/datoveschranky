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
package eu.apksoft.android.datoveschranky.dto;

public class DataBoxAccess {
	private String personId;
	private String password;
	private String boxId;
	private String boxName;
	private String lastKnownMessageId;
	
	public DataBoxAccess(String personId, String password, String boxId, String boxName, String lastKnownMessageId) {
		super();
		this.personId = personId;
		this.password = password;
		this.boxId = boxId;
		this.boxName = boxName;
		this.lastKnownMessageId = lastKnownMessageId;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBoxId() {
		return boxId;
	}

	public void setBoxId(String boxId) {
		this.boxId = boxId;
	}

	public String getBoxName() {
		return boxName;
	}

	public void setBoxName(String boxName) {
		this.boxName = boxName;
	}

	public String getLastKnownMessageId() {
		return lastKnownMessageId;
	}

	public void setLastKnownMessageId(String lastKnownMessageId) {
		this.lastKnownMessageId = lastKnownMessageId;
	}

	
	
	
}
