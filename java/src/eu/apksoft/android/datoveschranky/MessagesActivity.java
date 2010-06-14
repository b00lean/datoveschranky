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
package eu.apksoft.android.datoveschranky;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import cz.abclinuxu.datoveschranky.common.entities.MessageState;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxMessagesService;
import eu.apksoft.android.datoveschranky.adapters.MessageEnvelopeAdapter;
import eu.apksoft.android.datoveschranky.dto.DataBoxAccess;
import eu.apksoft.android.datoveschranky.helpers.AndroidUtils;
import eu.apksoft.android.datoveschranky.helpers.PreferencesHelper;
import eu.apksoft.android.datoveschranky.services.DataBoxServicesImpl;
import eu.apksoft.android.datoveschranky.ws.DSUtils;

public class MessagesActivity extends Activity implements OnClickListener, OnItemClickListener {
	
	public static final int MESSAGE_TYPE_DELIVERED = 0; 
	public static final int MESSAGE_TYPE_SENT = 1;
	
	public static final String EXTRA_KEY_DATACCESS_ID = "EXTRA_KEY_DATACCESS_ID";
	public static final String EXTRA_KEY_MESSAGE_TYPE = "EXTRA_KEY_MESSAGE_TYPE";
	
	private final Handler asyncHandler = new Handler();
	private int dataBoxAccessId = 0;
	private int messageType = MESSAGE_TYPE_DELIVERED;
	
	private MessageEnvelopeAdapter messagesAdapter;
	private List<MessageEnvelope> messageEnvelopes = new ArrayList<MessageEnvelope>();
	
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);
        
        
        Bundle extras = getIntent().getExtras();
        dataBoxAccessId = extras.getInt(EXTRA_KEY_DATACCESS_ID,0);
		messageType = extras.getInt(EXTRA_KEY_MESSAGE_TYPE,MESSAGE_TYPE_DELIVERED);
		
		switch(messageType) {
		case MESSAGE_TYPE_DELIVERED:
			((TextView)findViewById(R.id.lblMessagesType)).setText(getResources().getString(R.string.delivered_messages));
			break;
		case MESSAGE_TYPE_SENT:
			((TextView)findViewById(R.id.lblMessagesType)).setText(getResources().getString(R.string.sent_messages));
			break;
		}
		
		
		
		messagesAdapter = new MessageEnvelopeAdapter(this, messageType, messageEnvelopes, false, false);
		ListView lv = (ListView)findViewById(R.id.lstMessages);
		lv.setAdapter(messagesAdapter);
		lv.setOnItemClickListener(this);

		
		readMessages();
    }

	private void readMessages() {
		showDialog(0);
		asyncHandler.postDelayed(new Runnable() {
			public void run() {
				List<DataBoxAccess> dataBoxAccesses = PreferencesHelper.getDataBoxAccesses(MessagesActivity.this);
				DataBoxAccess dataBoxAccess = dataBoxAccesses.get(dataBoxAccessId);
				DataBoxServicesImpl services = new DataBoxServicesImpl(DSUtils.SERVICE_URL, dataBoxAccess.getPersonId(), dataBoxAccess.getPassword());
				
				DataBoxMessagesService dataBoxMessagesService = services.getDataBoxMessagesService();
				
			    try {
				    GregorianCalendar from = new GregorianCalendar();
				    GregorianCalendar to = new GregorianCalendar();
				    from.roll(Calendar.DAY_OF_YEAR, -28);
				    to.roll(Calendar.DAY_OF_YEAR, 1);
				    EnumSet<MessageState> state = null;
				    
				    List<MessageEnvelope> envelopes = null;
				    switch (messageType){
					case MESSAGE_TYPE_DELIVERED:
				    	envelopes = dataBoxMessagesService.getListOfReceivedMessages(from, to, state, 0, 100);
						break;
					case MESSAGE_TYPE_SENT:
						envelopes = dataBoxMessagesService.getListOfSentMessages(from, to, state, 0, 100);
						break;
				    }
				    
				    messageEnvelopes.clear();
				    if (envelopes.isEmpty()) {
				    	messagesAdapter.setShowIsEmpty(true);
				    	messagesAdapter.setShowConnectionError(false);
				    }else{
				    	messageEnvelopes.addAll(envelopes);
				    	if (messageType == MESSAGE_TYPE_DELIVERED) { //update last known messageId for widget (applies only for delivered messages)
					    	MessageEnvelope envelope = envelopes.get(0);
					    	dataBoxAccess.setLastKnownMessageId(envelope.getMessageID());
					    	PreferencesHelper.setDataBoxAccesses(dataBoxAccesses, MessagesActivity.this);
				    	}
				    	
				    	messagesAdapter.setShowIsEmpty(false);
				    	messagesAdapter.setShowConnectionError(false);
				    }
				    
			    } catch (RuntimeException e) {
					e.printStackTrace();
	                new AlertDialog.Builder(MessagesActivity.this)
	                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
	                })
	                .setMessage(R.string.comm_error)
	                .show();
	                
	                if (messageEnvelopes.size() == 0) {
	                	messagesAdapter.setShowIsEmpty(true);
	                }else{
	                	messagesAdapter.setShowIsEmpty(false);
	                }
			    	messagesAdapter.setShowConnectionError(true);
	                
				}finally{
					messagesAdapter.notifyDataSetChanged();
					dismissDialog(0);
				}
			}
		}, 200);
	}
    
	protected Dialog onCreateDialog(int id) {
		if (id == 0) {
		    ProgressDialog dialog = new ProgressDialog(this);
		    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    dialog.setTitle(getResources().getText(R.string.please_wait));
		    dialog.setMessage(getResources().getText(R.string.accessing_db));
		    dialog.setIndeterminate(true);
		    dialog.setCancelable(false);
		    return dialog;
		}
	    return null;
	}


	@Override
	public void onClick(View view) {
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		if (view.getId() == R.id.row_could_not_read_messages) {
			readMessages();
		}else if (view.getId() == R.id.row_no_message) {
				//NOP
		}else{
			Intent intent = new Intent(this, MessageActivity.class);
			MessageEnvelope envelope = (MessageEnvelope) messagesAdapter.getItem(position);
			intent.putExtra(MessageActivity.EXTRA_KEY_DATACCESS_ID, dataBoxAccessId);
			AndroidUtils.setSelectedEnvelope(envelope); //THIS IS UGLY HACK, because there is no other later way to get Sent message unsigned envelope by message id from webservice.
			if (messageType == MESSAGE_TYPE_DELIVERED) {
				intent.putExtra(MessageActivity.EXTRA_KEY_MESSAGE_ID, envelope.getMessageID());
			}
			startActivity(intent);
		}
		
	}
}