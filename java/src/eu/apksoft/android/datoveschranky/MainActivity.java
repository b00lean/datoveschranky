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
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import eu.apksoft.android.datoveschranky.adapters.DataBoxAccessAdapter;
import eu.apksoft.android.datoveschranky.dto.DataBoxAccess;
import eu.apksoft.android.datoveschranky.helpers.PreferencesHelper;

public class MainActivity extends Activity implements OnClickListener, OnItemClickListener {
	final Handler asyncHandler = new Handler();
	private Dialog helpDialog;
	private DataBoxAccessAdapter databoxesAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.btnSettings).setOnClickListener(this);
        findViewById(R.id.btnHelp).setOnClickListener(this);
		
        
        ListView lv = (ListView)findViewById(R.id.lstBoxes);
        
        List<DataBoxAccess> dataBoxAccesses = PreferencesHelper.getDataBoxAccesses(this);
        if (dataBoxAccesses.size() == 0) {
        	databoxesAdapter = new DataBoxAccessAdapter(this, dataBoxAccesses, true, false);
        }else{
        	databoxesAdapter = new DataBoxAccessAdapter(this, dataBoxAccesses, false, false);
        }
		
		lv.setAdapter(databoxesAdapter);
		lv.setOnItemClickListener(this);

    }
    

	@Override
	protected void onResume() {
		super.onResume();
        List<DataBoxAccess> dataBoxAccesses = PreferencesHelper.getDataBoxAccesses(this);
        if (dataBoxAccesses.size() == 0) {
        	databoxesAdapter.setShowIsEmpty(true);
        }else{
        	databoxesAdapter.setShowIsEmpty(false);
        }
		databoxesAdapter.setDataBoxes(dataBoxAccesses);
		databoxesAdapter.notifyDataSetChanged();
	}


	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.btnSettings:
	        startActivity(new Intent(this, SettingsActivity.class));
			break;
		case R.id.btnHelp:
	          helpDialog = new Dialog(this);
	          helpDialog.setTitle(R.string.help);
	          
	          helpDialog.setContentView(R.layout.dlg_help);
	          Button buttonOK = (Button) helpDialog.findViewById(R.id.btnOK);
	          buttonOK.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					helpDialog.dismiss();
				}
	        	  
	          });
	          helpDialog.show(); 
			break;
		}
	}


	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		
		if (view.getId() == R.id.row_no_db_defined) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		}else{
			Intent intent = new Intent(this, DataBoxActivity.class);
			intent.putExtra(DataBoxActivity.EXTRA_KEY_DATACCESS_ID, position );
			startActivity(intent);
		}
	}
}