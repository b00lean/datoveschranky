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

import java.util.List;

import org.ksoap2.transport.Transport;

import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import cz.abclinuxu.datoveschranky.common.entities.DataBoxState;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxSearchService;

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
		throw new UnsupportedOperationException("Unimplemented.");
	}

}
