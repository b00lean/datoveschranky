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

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import cz.abclinuxu.datoveschranky.common.entities.DataBox;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxSearchService;
import eu.apksoft.android.datoveschranky.adapters.DataBoxAdapter;
import eu.apksoft.android.datoveschranky.dto.DataBoxAccess;
import eu.apksoft.android.datoveschranky.helpers.AndroidUtils;
import eu.apksoft.android.datoveschranky.helpers.PreferencesHelper;
import eu.apksoft.android.datoveschranky.services.DataBoxServicesImpl;
import eu.apksoft.android.datoveschranky.ws.DSUtils;

public class SearchActivity extends Activity implements OnClickListener, OnItemClickListener{
	public static final String EXTRA_KEY_DATACCESS_ID = "EXTRA_KEY_DATACCESS_ID";
	private int position = 0;
	private DataBoxAdapter adapter;
	final Handler asyncHandler = new Handler();
	
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidUtils.registerForExceptions(this);
		setContentView(R.layout.search);

		
	    Bundle extras = getIntent().getExtras();
		position = extras.getInt(EXTRA_KEY_DATACCESS_ID,0);
	    
	    findViewById(R.id.btnSearch).setOnClickListener(this);

        ListView lv = (ListView)findViewById(R.id.lstRecipients);
	    lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
       	adapter = new DataBoxAdapter(this, new ArrayList<DataBox>());
		
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		
		
		((EditText)findViewById(R.id.edtSearch)).setOnKeyListener(new View.OnKeyListener() {
			    public boolean onKey(View v, int keyCode, KeyEvent event) {
			        // If the event is a key-down event on the "enter" button
			        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
			          search(((EditText)findViewById(R.id.edtSearch)).getText().toString());
			          return true;
			        }
			        return false;
			    }
			});
	}
    
    
    private void search(final String prefix){
		showDialog(0);
		asyncHandler.postDelayed(new Runnable() {
			public void run() {
				List<DataBoxAccess> dataBoxAccesses = PreferencesHelper.getDataBoxAccesses(SearchActivity.this);
			    DataBoxAccess dataBoxAccess = dataBoxAccesses.get(position);
				DataBoxServicesImpl services = new DataBoxServicesImpl(DSUtils.SERVICE_URL, dataBoxAccess.getPersonId(), dataBoxAccess.getPassword());
				
				DataBoxSearchService dataBoxSearchService = services.getDataBoxSearchService();
				try {
					List<DataBox> dataBoxes = dataBoxSearchService.findOVMsByName(prefix);
					adapter.setDataBoxes(dataBoxes);
					adapter.notifyDataSetChanged();
				} catch (RuntimeException e) {
					AndroidUtils.showError(SearchActivity.this, e);
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
		}
	    return null;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnSearch:
				search(((EditText)findViewById(R.id.edtSearch)).getText().toString());
				break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
		DataBox item = (DataBox) adapter.getItem(position);
		Intent intent = new Intent(this, MessageComposeActivity.class);
		intent.putExtra(MessageComposeActivity.EXTRA_KEY_DATACCESS_ID, position );
		intent.putExtra(MessageComposeActivity.EXTRA_KEY_RECIPIENT_DATABOX_ID, item.getdataBoxID());
		intent.putExtra(MessageComposeActivity.EXTRA_KEY_RECIPIENT_DATABOX_NAME, item.getIdentity());
		startActivity(intent);
	}
}
