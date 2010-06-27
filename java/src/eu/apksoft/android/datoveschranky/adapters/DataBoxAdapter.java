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

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import eu.apksoft.android.datoveschranky.R;

public class DataBoxAdapter extends BaseAdapter {
	private Activity activity;
	private List<DataBox> dataBoxes;

	public DataBoxAdapter(Activity activity, List<DataBox> dataBoxes) {
		super();
		this.activity = activity;
		this.dataBoxes = dataBoxes;
	}

	public List<DataBox> getDataBoxes() {
		return dataBoxes;
	}
	
	public void setDataBoxes(List<DataBox> dataBoxes) {
		this.dataBoxes = dataBoxes;
	}

	@Override
	public int getCount() {
		return dataBoxes.size();
	}

	@Override
	public Object getItem(int position) {
		return dataBoxes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inf = activity.getLayoutInflater();
		View result = null;
		
		if (convertView == null) {
			result =  inf.inflate(R.layout.row_recipient, parent, false);
		}else{
			if (convertView.getId() == R.id.row_recipient) {
				result = convertView;
			}else{
				result =  inf.inflate(R.layout.row_recipient, parent, false);
			}
		}
			
		((TextView)result.findViewById(R.id.txtBoxID)).setText(dataBoxes.get(position).getdataBoxID());
		((TextView)result.findViewById(R.id.txtBoxName)).setText(dataBoxes.get(position).getIdentity());
		return result;
	}
}
