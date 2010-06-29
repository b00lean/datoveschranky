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
package eu.apksoft.android.datoveschranky.adapters;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import eu.apksoft.android.datoveschranky.R;
import eu.apksoft.android.datoveschranky.dto.DataBoxAccess;
import eu.apksoft.android.datoveschranky.ws.DSUtils;

public class DataBoxAccessAdapter extends BaseAdapter {
	private Activity activity;
	private List<DataBoxAccess> databoxes;
	private boolean showIsEmpty;
	private boolean showAddNew;
	

	public DataBoxAccessAdapter(Activity activity, List<DataBoxAccess> databoxes, boolean showIsEmpty,	boolean showAddNew) {
		super();
		this.activity = activity;
		this.databoxes = databoxes;
		this.showIsEmpty = showIsEmpty;
		this.showAddNew = showAddNew;
	}

	public void setDataBoxes(List<DataBoxAccess> databoxes) {
		this.databoxes = databoxes;
		
	}

	public List<DataBoxAccess> getDataBoxes() {
		return databoxes;
	}

	@Override
	public int getCount() {
		int size = databoxes.size();
		if (isShowIsEmpty()) {
			size++;
		}
		if (isShowAddNew()) {
			size++;
		}
		return size;
	}

	@Override
	public Object getItem(int position) {
		int size = databoxes.size();
		int over = size -1 - position;
		if (over == -1) {
			if (isShowIsEmpty()) {
				return "empty";
			}else if (isShowAddNew()) {
				return "new";
			}
			return null;
		}else if (over == -2) {
			if (isShowAddNew()) {
				return "new";
			}
			return null;
				
		}else {
			return databoxes.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public boolean isShowIsEmpty() {
		return showIsEmpty;
	}

	public void setShowIsEmpty(boolean showIsEmpty) {
		this.showIsEmpty = showIsEmpty;
	}

	public boolean isShowAddNew() {
		return showAddNew;
	}

	public void setShowAddNew(boolean showAddNew) {
		this.showAddNew = showAddNew;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inf = activity.getLayoutInflater();
		View result = null;
		
		int size = databoxes.size();
		int over = size -1 - position;
		if (over == -1) {
			if (isShowIsEmpty()) {
				if (convertView != null &&convertView.getId() == R.id.row_no_db_defined) {
					result = convertView;
				}else{
					result =  inf.inflate(R.layout.row_no_db_defined, parent, false);
				}
			}else if (isShowAddNew()) {
				if (convertView != null && convertView.getId() == R.id.row_add_db) {
					result = convertView;
				}else{
					result =  inf.inflate(R.layout.row_add_db, parent, false);
				}
			}
		}else if (over == -2) {
			if (isShowAddNew()) {
				if (convertView != null && convertView.getId() == R.id.row_add_db) {
					result = convertView;
				}else{
					result =  inf.inflate(R.layout.row_add_db, parent, false);
				}
			}
		}else {
			if (convertView == null) {
				result =  inf.inflate(R.layout.row_db, parent, false);
			}else{
				if (convertView.getId() == R.id.row_db) {
					result = convertView;
				}else{
					result =  inf.inflate(R.layout.row_db, parent, false);
				}
			}
				
			((TextView)result.findViewById(R.id.txtBoxName)).setText(databoxes.get(position).getBoxName());
			((TextView)result.findViewById(R.id.txtPersonID)).setText(databoxes.get(position).getPersonId());
			((TextView)result.findViewById(R.id.txtBoxID)).setText(databoxes.get(position).getBoxId());
			((TextView)result.findViewById(R.id.txtPasswordExpires)).setText(databoxes.get(position).getPasswordExpires() == 0 ? "" : DSUtils.toStringDate(new Date(databoxes.get(position).getPasswordExpires())));
		}
		return result;
	}
}
