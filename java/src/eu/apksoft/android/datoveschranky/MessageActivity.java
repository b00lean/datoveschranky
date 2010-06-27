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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import cz.abclinuxu.datoveschranky.common.entities.Message;
import cz.abclinuxu.datoveschranky.common.entities.MessageEnvelope;
import eu.apksoft.android.datoveschranky.adapters.AttachmentAdapter;
import eu.apksoft.android.datoveschranky.dto.DataBoxAccess;
import eu.apksoft.android.datoveschranky.helpers.AndroidUtils;
import eu.apksoft.android.datoveschranky.helpers.PreferencesHelper;
import eu.apksoft.android.datoveschranky.services.DataBoxDownloadServiceImpl;
import eu.apksoft.android.datoveschranky.services.DataBoxServicesImpl;
import eu.apksoft.android.datoveschranky.ws.DSUtils;

public class MessageActivity extends Activity implements OnClickListener, OnItemClickListener {
	public static final String EXTRA_KEY_DATACCESS_ID = "EXTRA_KEY_DATACCESS_ID";
	public static final String EXTRA_KEY_MESSAGE_ID = "EXTRA_KEY_MESSAGE_ID";
	final Handler asyncHandler = new Handler();
	
	private int dataBoxAccessId = 0;
	private String messageId;
	private AttachmentAdapter attachmentAdapter;
	private List<Attachment> attachments = new ArrayList<Attachment>();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidUtils.registerForExceptions(this);
        setContentView(R.layout.message);

        
        ListView lv = (ListView)findViewById(R.id.lstAttachements);
        lv.addHeaderView(getLayoutInflater().inflate(R.layout.message_detail, null));
        lv.setVisibility(View.INVISIBLE);
        
        
        Bundle extras = getIntent().getExtras();
        dataBoxAccessId = extras.getInt(EXTRA_KEY_DATACCESS_ID,0);
		messageId = extras.getString(EXTRA_KEY_MESSAGE_ID);
		

        attachmentAdapter = new AttachmentAdapter(this, attachments);
		lv.setAdapter(attachmentAdapter);
		lv.setOnItemClickListener(this);

		if (messageId == null) {
			((TextView)findViewById(R.id.lblTopic)).setText(getResources().getString(R.string.sent_message));
			MessageEnvelope envelope = AndroidUtils.getSelectedEnvelope();
			if (envelope != null) {
				setEnvelopeTexts(envelope);
		    	attachments.clear();
		    	attachmentAdapter.notifyDataSetChanged();
		    	findViewById(R.id.lstAttachements).setVisibility(View.VISIBLE);
			}else{
				finish();
			}
		}else{
			((TextView)findViewById(R.id.lblTopic)).setText(getResources().getString(R.string.delivered_message));
			downloadMessage();
		}
    }
    


    /* TODO: Not working yet
	private void sentMessageDownload() {
		showDialog(0);
		asyncHandler.postDelayed(new Runnable() {
			public void run() {
				List<DataBoxAccess> dataBoxAccesses = PreferencesHelper.getDataBoxAccesses(MessageActivity.this);
				DataBoxAccess dataBoxAccess = dataBoxAccesses.get(dataBoxAccessId);
				DataBoxServicesImpl services = new DataBoxServicesImpl(DSUtils.PRODUCTION_URL, dataBoxAccess.getPersonId(), dataBoxAccess.getPassword());
				
				DataBoxDownloadServiceImpl dataBoxDownloadService = (DataBoxDownloadServiceImpl) services.getDataBoxDownloadService();
				
			    try {
			    	Message message = dataBoxDownloadService.signedSentMessageDownload(messageId, null);
			    	
			    	//((TextView)findViewById(R.id.txtState)).setText(message.getEnvelope().getState().toString()); //TODO: show also state
			    	setEnvelopeTexts(message.getEnvelope());
			    	
			    	attachments.clear();
			    	attachments.addAll(message.getAttachments());
			    	attachmentAdapter.notifyDataSetChanged();
			    	findViewById(R.id.lstAttachements).setVisibility(View.VISIBLE);
			    } catch (RuntimeException e) {
					e.printStackTrace();
	                new AlertDialog.Builder(MessageActivity.this)
	                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
	                })
	                .setMessage(R.string.comm_error)
	                .show();
	                
	                finish(); //go back
	                
				}
			    dismissDialog(0);
				}

		}, 200);
	}
	*/

	private void setEnvelopeTexts(MessageEnvelope envelope) {
		((TextView)findViewById(R.id.txtMessageId)).setText(envelope.getMessageID());
    	((TextView)findViewById(R.id.txtSenderName)).setText(envelope.getSender().getIdentity());
    	((TextView)findViewById(R.id.txtSenderBoxId)).setText(envelope.getSender().getdataBoxID());
    	((TextView)findViewById(R.id.txtSenderBoxType)).setText(envelope.getSender().getDataBoxType().toString());
    	((TextView)findViewById(R.id.txtRecipientName)).setText(envelope.getRecipient().getIdentity());
    	
    	GregorianCalendar deliveryTime = envelope.getDeliveryTime();
    	if (deliveryTime == null) {
       	 	//ANOTHER UGLY WORKAROUND Due to the bug in ksoap delivery time is not read from xml when message is fully downloaded (containing attachements) downloaded
        	deliveryTime = AndroidUtils.getSelectedEnvelope().getDeliveryTime();
    		
    	}
    	
		((TextView)findViewById(R.id.txtDelivered)).setText(DSUtils.toStringDate(deliveryTime));
    	((TextView)findViewById(R.id.txtConcern)).setText(envelope.getAnnotation());
    	((TextView)findViewById(R.id.txtWarrant)).setText(AndroidUtils.formatLegalTitle(this, envelope.getLegalTitle()));
    	((TextView)findViewById(R.id.txtOurCJ)).setText(envelope.getSenderIdent().getRefNumber());
    	((TextView)findViewById(R.id.txtOurSPZN)).setText(envelope.getSenderIdent().getIdent());
    	((TextView)findViewById(R.id.txtYourCJ)).setText(envelope.getRecipientIdent().getRefNumber());
    	((TextView)findViewById(R.id.txtYourSPZN)).setText(envelope.getRecipientIdent().getIdent());
    	((TextView)findViewById(R.id.txtToHandsName)).setText(envelope.getToHands());
    	((TextView)findViewById(R.id.txtToHandsEnabled)).setText(AndroidUtils.formatBoolean(this, envelope.getPersonalDelivery()));
    	((TextView)findViewById(R.id.txtFictionAllowed)).setText(AndroidUtils.formatBoolean(this, envelope.getAllowSubstDelivery()));
	}


	private void downloadMessage() {
		showDialog(0);
		asyncHandler.postDelayed(new Runnable() {
			public void run() {
				List<DataBoxAccess> dataBoxAccesses = PreferencesHelper.getDataBoxAccesses(MessageActivity.this);
				DataBoxAccess dataBoxAccess = dataBoxAccesses.get(dataBoxAccessId);
				DataBoxServicesImpl services = new DataBoxServicesImpl(DSUtils.SERVICE_URL, dataBoxAccess.getPersonId(), dataBoxAccess.getPassword());
				
				DataBoxDownloadServiceImpl dataBoxDownloadService = (DataBoxDownloadServiceImpl) services.getDataBoxDownloadService();
				
			    try {
			    	Message message = dataBoxDownloadService.downloadMessage(messageId, null);
			    	MessageEnvelope envelope = message.getEnvelope();
			    	setEnvelopeTexts(envelope);
			    	//((TextView)findViewById(R.id.txtState)).setText(message.getEnvelope().getState().toString()); //TODO: show also state
			    	
			    	attachments.clear();
			    	attachments.addAll(message.getAttachments());
			    	attachmentAdapter.notifyDataSetChanged();
			    	findViewById(R.id.lstAttachements).setVisibility(View.VISIBLE);
			    } catch (RuntimeException e) {
					AndroidUtils.showError(MessageActivity.this, e);
	                finish(); //go back
				}finally {
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
		}else if (id == 1) {
		    ProgressDialog dialog = new ProgressDialog(this);
		    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    dialog.setTitle(getResources().getText(R.string.please_wait));
		    dialog.setMessage(getResources().getText(R.string.save_attachment_on_sdcard));
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
	public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
    	if (position > 0 ) { //0 is header
			ListView lv = (ListView)findViewById(R.id.lstAttachements);
			
			final Attachment attachment = (Attachment) lv.getAdapter().getItem(position);

			final CharSequence[] items = {
					getResources().getString(R.string.save_attachment_on_sdcard),
					getResources().getString(R.string.save_attachment_on_sdcard_and_open)
					};
	
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.choose_action));
			builder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			    	if (item == 0) {
			    	   saveAttachement(attachment, false);
				   } else if (item == 1) {
					   saveAttachement(attachment, true);
				    }
			    }

				private void saveAttachement(final Attachment attachment, final boolean openAfterSave) {
					showDialog(1);
					asyncHandler.postDelayed(new Runnable() {
						public void run() {
							try {
							    File root = Environment.getExternalStorageDirectory();
							    if (root.canWrite()){
							        File file = new File(root, attachment.getDescription());
							        FileOutputStream fos = new FileOutputStream(file);
							        InputStream is = attachment.getContent().getInputStream();
							        byte[] buffer = new byte[64000];
							        int len = -1;
							        do {
							        	len = is.read(buffer);
							        	if (len != -1) {
							        		fos.write(buffer,0,len);
							        	}
							        }while (len != -1);
							        fos.close();
							        is.close();
							        dismissDialog(1);
								    Toast.makeText(getApplicationContext(), String.format(getResources().getString(R.string.attachment_has_been_saved_to), attachment.getDescription(), root), Toast.LENGTH_SHORT).show();
								    if (openAfterSave) {
								    	Intent intent = new Intent();  
								    	intent.setAction(android.content.Intent.ACTION_VIEW);
								    	intent.setDataAndType(Uri.fromFile(file), attachment.getMimeType());  
								    	try {
											startActivity(intent);
										} catch (ActivityNotFoundException e) {
											e.printStackTrace();
							                new AlertDialog.Builder(MessageActivity.this)
							                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener(){
												@Override
												public void onClick(DialogInterface dialog, int which) {
												}
							                })
							                .setMessage(String.format(getResources().getString(R.string.could_not_openattachment), file, attachment.getMimeType()))
							                .show();
										}   
								    }
							    }else{
							    	Toast.makeText(getApplicationContext(), getResources().getString(R.string.could_not_save_attachement), Toast.LENGTH_SHORT).show();
							    	dismissDialog(1);
							    }
							    
							} catch (IOException e) {
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.could_not_save_attachement), Toast.LENGTH_SHORT).show();
								dismissDialog(1);
							}
						}
					}, 200);
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
    	}
		
	}
}