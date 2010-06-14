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
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import eu.apksoft.android.datoveschranky.R;
import eu.apksoft.android.datoveschranky.ws.DSUtils;

public class MessageEnvelopeAdapter extends BaseAdapter {
	private Activity activity;
	private List<MessageEnvelope> envelopes;
	private boolean showIsEmpty;
	private boolean showConnectionError;
	private int messageType;

	public MessageEnvelopeAdapter(Activity activity, int messageType, List<MessageEnvelope> envelopes, boolean showIsEmpty,	boolean showConnectionError) {
		super();
		this.activity = activity;
		this.envelopes = envelopes;
		this.showIsEmpty = showIsEmpty;
		this.showConnectionError = showConnectionError;
		this.messageType = messageType;
	}

	public void setEnvelopes(List<MessageEnvelope> databoxes) {
		this.envelopes = databoxes;
		
	}

	public List<MessageEnvelope> getEnvelopes() {
		return envelopes;
	}

	@Override
	public int getCount() {
		int size = envelopes.size();
		if (isShowIsEmpty()) {
			size++;
		}
		if (isShowConnectionError()) {
			size++;
		}
		return size;
	}

	@Override
	public Object getItem(int position) {
		int size = envelopes.size();
		int over = size -1 - position;
		if (over == -1) {
			if (isShowIsEmpty()) {
				return "empty";
			}else if (isShowConnectionError()) {
				return "err";
			}
			return null;
		}else if (over == -2) {
			if (isShowConnectionError()) {
				return "err";
			}
			return null;
				
		}else {
			return envelopes.get(position);
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

	public boolean isShowConnectionError() {
		return showConnectionError;
	}

	public void setShowConnectionError(boolean showConnectionError) {
		this.showConnectionError = showConnectionError;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inf = activity.getLayoutInflater();
		View result = null;
		
		int size = envelopes.size();
		int over = size -1 - position;
		if (over == -1) {
			if (isShowIsEmpty()) {
				if (convertView != null &&convertView.getId() == R.id.row_no_message) {
					result = convertView;
				}else{
					result =  inf.inflate(R.layout.row_no_message, parent, false);
				}
			}else if (isShowConnectionError()) {
				if (convertView != null && convertView.getId() == R.id.row_could_not_read_messages) {
					result = convertView;
				}else{
					result =  inf.inflate(R.layout.row_could_not_read_messages, parent, false);
				}
			}
		}else if (over == -2) {
			if (isShowConnectionError()) {
				if (convertView != null && convertView.getId() == R.id.row_could_not_read_messages) {
					result = convertView;
				}else{
					result =  inf.inflate(R.layout.row_could_not_read_messages, parent, false);
				}
			}
		}else {
			if (convertView == null) {
				result =  inf.inflate(R.layout.row_message, parent, false);
			}else{
				if (convertView.getId() == R.id.row_message) {
					result = convertView;
				}else{
					result =  inf.inflate(R.layout.row_message, parent, false);
				}
			}
				
			((TextView)result.findViewById(R.id.txtDelivered)).setText(DSUtils.toStringDate(envelopes.get(position).getDeliveryTime()));
			((TextView)result.findViewById(R.id.txtHandin)).setText(DSUtils.toStringDate(envelopes.get(position).getAcceptanceTime()));
			((TextView)result.findViewById(R.id.txtConcern)).setText(envelopes.get(position).getAnnotation());
			((TextView)result.findViewById(R.id.txtAuthor)).setText(envelopes.get(position).getSender().getIdentity());
		}
		return result;
	}
}
