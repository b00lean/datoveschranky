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
import java.util.List;

import cz.abclinuxu.datoveschranky.common.entities.Attachment;
import eu.apksoft.android.datoveschranky.adapters.AttachmentAdapter;
import eu.apksoft.android.datoveschranky.dto.DataBoxAccess;
import eu.apksoft.android.datoveschranky.helpers.AndroidUtils;
import eu.apksoft.android.datoveschranky.helpers.PreferencesHelper;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MessageComposeActivity extends Activity implements OnClickListener, OnItemClickListener {
	private int dataBoxAccessId = 0;
	private String recipientDataBoxName;
	private String recipientDataBoxId;
	
	public static final String EXTRA_KEY_DATACCESS_ID = "EXTRA_KEY_DATACCESS_ID";
	public static final String EXTRA_KEY_RECIPIENT_DATABOX_ID = "EXTRA_KEY_RECIPIENT_DATABOX_ID";
	public static final String EXTRA_KEY_RECIPIENT_DATABOX_NAME = "EXTRA_KEY_RECIPIENT_DATABOX_NAME";
	final Handler asyncHandler = new Handler();
	private AttachmentAdapter attachmentAdapter;
	private List<Attachment> attachments = new ArrayList<Attachment>();


	private static final int SELECT_PICTURE = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidUtils.registerForExceptions(this);
        setContentView(R.layout.message);
        
        
        ListView lv = (ListView)findViewById(R.id.lstAttachements);
        lv.addHeaderView(getLayoutInflater().inflate(R.layout.message_compose_detail, null));
        lv.addFooterView(getLayoutInflater().inflate(R.layout.message_compose_footer, null));
        lv.setVisibility(View.VISIBLE);
        
        Bundle extras = getIntent().getExtras();
        dataBoxAccessId = extras.getInt(EXTRA_KEY_DATACCESS_ID,0);
		recipientDataBoxId = extras.getString(EXTRA_KEY_RECIPIENT_DATABOX_ID);
		recipientDataBoxName = extras.getString(EXTRA_KEY_RECIPIENT_DATABOX_NAME);
		
		List<DataBoxAccess> dataBoxAccesses = PreferencesHelper.getDataBoxAccesses(this);
		DataBoxAccess dataBoxAccess = dataBoxAccesses.get(dataBoxAccessId);

		((TextView)findViewById(R.id.txtSenderBoxId)).setText(dataBoxAccess.getBoxId());
		((TextView)findViewById(R.id.txtSenderName)).setText(dataBoxAccess.getBoxName());
		
		((TextView)findViewById(R.id.txtRecipientBoxId)).setText(recipientDataBoxId);
		((TextView)findViewById(R.id.txtRecipientName)).setText(recipientDataBoxName);
		
		findViewById(R.id.btnSend).setOnClickListener(this);
		findViewById(R.id.btnAddAttachment).setOnClickListener(this);
		
        attachmentAdapter = new AttachmentAdapter(this, attachments);
		lv.setAdapter(attachmentAdapter);
		lv.setOnItemClickListener(this);
		
		Toast.makeText(this, "Není funkční, bude ve verzi 2.0", Toast.LENGTH_SHORT).show();
		
    }

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnSend:
				Toast.makeText(this, "Není funkční, bude ve verzi 2.0", Toast.LENGTH_SHORT).show();
				break;
			case R.id.btnAddAttachment:
				Toast.makeText(this, "Není funkční, bude ve verzi 2.0", Toast.LENGTH_SHORT).show();
			    Intent intent = new Intent();
		        intent.setType("image/*");
		        intent.setAction(Intent.ACTION_GET_CONTENT);
		        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.choose_picture)), SELECT_PICTURE);
				break;
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK) {
	        if (requestCode == SELECT_PICTURE) {
	            Uri selectedImageUri = data.getData();
	            String selectedImagePath = getPath(selectedImageUri);
	            Toast.makeText(this, selectedImagePath, Toast.LENGTH_SHORT);
	        }
	    }
	}
	
	public String getPath(Uri uri) {
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
	}
}
