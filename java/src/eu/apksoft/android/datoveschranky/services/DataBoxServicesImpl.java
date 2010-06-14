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

import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxAccessService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxDownloadService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxMessagesService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxSearchService;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxServices;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxUploadService;
import eu.apksoft.android.datoveschranky.ws.MyAndroidTransport;

public class DataBoxServicesImpl implements DataBoxServices {
	
	private DataBoxDownloadService dataBoxDownloadService;
	private DataBoxMessagesService dataBoxMessagesService;
	private DataBoxSearchService dataBoxSearchService;
	private DataBoxUploadService dataBoxUploadService;
	private DataBoxAccessService dataBoxAccessService; 

	
	public DataBoxServicesImpl(String url, String username, String password) {
		super();
		dataBoxSearchService = new DataBoxSearchServiceImpl(new MyAndroidTransport(url+"/df", username, password));
		dataBoxMessagesService = new DataBoxMessagesServiceImpl(new MyAndroidTransport(url+"/dx", username, password));
		dataBoxDownloadService = new DataBoxDownloadServiceImpl(new MyAndroidTransport(url+"/dz", username, password));
		dataBoxUploadService = new DataBoxUploadServiceImpl(new MyAndroidTransport(url+"/dz", username, password));
		dataBoxAccessService = new DataBoxAccessServiceImpl(new MyAndroidTransport(url+"/DsManage", username, password));
	}

	@Override
	public DataBoxDownloadService getDataBoxDownloadService() {
		return dataBoxDownloadService;
	}

	@Override
	public DataBoxMessagesService getDataBoxMessagesService() {
		return dataBoxMessagesService;
	}

	@Override
	public DataBoxSearchService getDataBoxSearchService() {
		return dataBoxSearchService;
	}

	@Override
	public DataBoxUploadService getDataBoxUploadService() {
		return dataBoxUploadService;
	}

	@Override
	public DataBoxAccessService getDataBoxAccessService() {
		return dataBoxAccessService;
	}
	
	

}
