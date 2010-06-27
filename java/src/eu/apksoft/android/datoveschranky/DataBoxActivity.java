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

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import eu.apksoft.android.datoveschranky.dto.DataBoxAccess;
import eu.apksoft.android.datoveschranky.helpers.AndroidUtils;
import eu.apksoft.android.datoveschranky.helpers.PreferencesHelper;

public class DataBoxActivity extends Activity implements OnClickListener {
	public static final String EXTRA_KEY_DATACCESS_ID = "EXTRA_KEY_DATACCESS_ID";
	
//	private final Handler asyncHandler = new Handler();
	private int position = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidUtils.registerForExceptions(this);
        setContentView(R.layout.databox);
        
        findViewById(R.id.btnDeliveredMessages).setOnClickListener(this);
        findViewById(R.id.btnSentMessages).setOnClickListener(this);
        findViewById(R.id.btnSendMessage).setOnClickListener(this);
        
        List<DataBoxAccess> dataBoxAccesses = PreferencesHelper.getDataBoxAccesses(this);
        Bundle extras = getIntent().getExtras();
		position = extras.getInt(EXTRA_KEY_DATACCESS_ID,0);
        DataBoxAccess dataBoxAccess = dataBoxAccesses.get(position);
        ((TextView)findViewById(R.id.txtBoxName)).setText(dataBoxAccess.getBoxName() + " (" + dataBoxAccess.getBoxId() + ")");

    }

	@Override
	public void onClick(View view) {
		Intent intent;
		switch(view.getId()) {
			case R.id.btnDeliveredMessages:
				intent = new Intent(this, MessagesActivity.class);
				intent.putExtra(MessagesActivity.EXTRA_KEY_DATACCESS_ID, position );
				intent.putExtra(MessagesActivity.EXTRA_KEY_MESSAGE_TYPE, MessagesActivity.MESSAGE_TYPE_DELIVERED );
				startActivity(intent);
				break;
			case R.id.btnSentMessages:
				intent = new Intent(this, MessagesActivity.class);
				intent.putExtra(MessagesActivity.EXTRA_KEY_DATACCESS_ID, position );
				intent.putExtra(MessagesActivity.EXTRA_KEY_MESSAGE_TYPE, MessagesActivity.MESSAGE_TYPE_SENT);
				startActivity(intent);
				break;
			case R.id.btnSendMessage:
				intent = new Intent(this, SearchActivity.class);
				intent.putExtra(SearchActivity.EXTRA_KEY_DATACCESS_ID, position);
				startActivity(intent);
				break;
		}
	}
}