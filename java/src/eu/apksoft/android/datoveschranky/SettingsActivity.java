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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import cz.abclinuxu.datoveschranky.common.entities.OwnerInfo;
import cz.abclinuxu.datoveschranky.common.interfaces.DataBoxAccessService;
import eu.apksoft.android.datoveschranky.adapters.DataBoxAccessAdapter;
import eu.apksoft.android.datoveschranky.dto.DataBoxAccess;
import eu.apksoft.android.datoveschranky.helpers.PreferencesHelper;
import eu.apksoft.android.datoveschranky.services.DataBoxServicesImpl;
import eu.apksoft.android.datoveschranky.ws.DSUtils;

public class SettingsActivity extends Activity implements OnClickListener, OnItemClickListener, OnItemLongClickListener {
	final Handler asyncHandler = new Handler();
	private DataBoxAccessAdapter databoxesAdapter;
	private Dialog passwordDialog;
	private PasswordDialogHandler passwordDialogHandler;
	private Dialog accessDialog;
	private AccessDialogHandler accessDialogHandler;
	private DataBoxAccess currentDataBoxAccess;
	private List<DataBoxAccess> dataBoxAccesses;
	
	private static final int REMOVE_DB_ID = 0;
	private static final int EDIT_DB_ID = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        findViewById(R.id.btnSetPassword).setOnClickListener(this);
        
        ListView lv = (ListView)findViewById(R.id.lstBoxes);
        dataBoxAccesses = PreferencesHelper.getDataBoxAccesses(this);
       	databoxesAdapter = new DataBoxAccessAdapter(this, dataBoxAccesses, false, true);
		
		lv.setAdapter(databoxesAdapter);
		lv.setOnItemClickListener(this);
		lv.setOnItemLongClickListener(this);
		registerForContextMenu(lv);
		
		passwordDialogHandler= new PasswordDialogHandler();
		accessDialogHandler= new AccessDialogHandler();

        
    }

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnSetPassword:
		          passwordDialog = new Dialog(this);
		          passwordDialog.setTitle(R.string.set_password);
		          
		          passwordDialog.setContentView(R.layout.dlg_password);
		          Button buttonOK = (Button) passwordDialog.findViewById(R.id.btnSet);
		          Button buttonCancel = (Button) passwordDialog.findViewById(R.id.btnCancel);
		          
		          buttonOK.setOnClickListener(passwordDialogHandler);
		          buttonCancel.setOnClickListener(passwordDialogHandler);
		          passwordDialog.show(); 

				break;
		}
	}
	
	class PasswordDialogHandler implements OnClickListener{
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.btnCancel:
					passwordDialog.dismiss();
					break;
				case R.id.btnSet:
					String password = ((TextView) passwordDialog.findViewById(R.id.edtPassword)).getText().toString().trim();
					passwordDialog.dismiss();
					PreferencesHelper.setPassword(SettingsActivity.this, password);
					int duration = Toast.LENGTH_SHORT;
					if (password.length() == 0) {
						Toast.makeText(SettingsActivity.this, SettingsActivity.this.getResources().getText(R.string.password_not_required), duration).show();
					}else{
						Toast.makeText(SettingsActivity.this, SettingsActivity.this.getResources().getText(R.string.new_password_set), duration).show();
					}
					break;
			}
		}
		
	}
	class AccessDialogHandler implements OnClickListener{

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btnCancel:
				accessDialog.dismiss();
				break;
			case R.id.btnSave:
				final String personId = ((TextView) accessDialog.findViewById(R.id.edtPersonID)).getText().toString().trim();
				final String password = ((TextView) accessDialog.findViewById(R.id.edtPassword)).getText().toString().trim();
				
				showDialog(0);
				asyncHandler.postDelayed(new Runnable() {
					public void run() {
						DataBoxServicesImpl services = new DataBoxServicesImpl(DSUtils.SERVICE_URL, personId, password);
						
						DataBoxAccessService dataBoxAccessService = services.getDataBoxAccessService();
						
					    try {
							OwnerInfo ownerInfoFromLogin = dataBoxAccessService.GetOwnerInfoFromLogin();
							String boxId = ownerInfoFromLogin.getDataBoxID();
							String boxName = ownerInfoFromLogin.getDataBoxType() + "  - " + ownerInfoFromLogin.getFirmName();
							
							
						
							int duration = Toast.LENGTH_SHORT;
							if (currentDataBoxAccess == null) { 
								//save new box
								dataBoxAccesses.add(new DataBoxAccess(personId, password, boxId, boxName,null));
								PreferencesHelper.setDataBoxAccesses(dataBoxAccesses, SettingsActivity.this);
								Toast.makeText(SettingsActivity.this, SettingsActivity.this.getResources().getText(R.string.new_databox_added), duration).show();
							}else{
								//save change
								currentDataBoxAccess.setPersonId(personId);
								currentDataBoxAccess.setPassword(password);
								currentDataBoxAccess.setBoxName(boxName);
								currentDataBoxAccess.setBoxId(boxId);
								PreferencesHelper.setDataBoxAccesses(dataBoxAccesses, SettingsActivity.this);
								Toast.makeText(SettingsActivity.this, SettingsActivity.this.getResources().getText(R.string.databox_saved), duration).show();
								
							}
							//2. refresh list
							databoxesAdapter.notifyDataSetChanged();
							
							accessDialog.dismiss();
						} catch (RuntimeException e) {
							e.printStackTrace();
			                new AlertDialog.Builder(SettingsActivity.this)
			                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
			                })
			                .setMessage(R.string.comm_error)
			                .show();
						}
						dismissDialog(0);
					}
				}, 200);
				break;
			}
		}
		
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
	public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
		if (view.getId() == R.id.row_add_db) {
		}else{
			currentDataBoxAccess = (DataBoxAccess) databoxesAdapter.getItem(position);
		}
		return false;
	}
	
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo menuInfo2 = (AdapterContextMenuInfo)menuInfo;
		if (menuInfo2.targetView.getId() != R.id.row_add_db) {
			menu.add(0, EDIT_DB_ID, 0, R.string.edit_databox);
	        menu.add(0, REMOVE_DB_ID, 1, R.string.remove_databox);
		}
        
	}
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
    	case REMOVE_DB_ID:
    		if (currentDataBoxAccess != null) {
    			dataBoxAccesses.remove(currentDataBoxAccess);
    			PreferencesHelper.setDataBoxAccesses(dataBoxAccesses, SettingsActivity.this);
    			databoxesAdapter.notifyDataSetChanged();
	    		Toast.makeText(this, getResources().getText(R.string.databox_removed), Toast.LENGTH_SHORT).show();
	    		return true;
    		}
    	case EDIT_DB_ID:
    		if (currentDataBoxAccess != null) {
  	          accessDialog = new Dialog(this);
	          accessDialog.setTitle(R.string.new_box_access);
	          
	          accessDialog.setContentView(R.layout.dlg_boxaccess);
	          
	          Button buttonOK = (Button) accessDialog.findViewById(R.id.btnSave);
	          Button buttonCancel = (Button) accessDialog.findViewById(R.id.btnCancel);
	          
	          ((EditText) accessDialog.findViewById(R.id.edtPersonID)).setText(currentDataBoxAccess.getPersonId());
	          ((EditText) accessDialog.findViewById(R.id.edtPassword)).setText(currentDataBoxAccess.getPassword());
	          
	          buttonOK.setOnClickListener(accessDialogHandler);
	          buttonCancel.setOnClickListener(accessDialogHandler);
	          accessDialog.show(); 
	          return true;
    		}
		}
		return super.onContextItemSelected(item);
	}


	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		if (view.getId() == R.id.row_add_db) {
			  currentDataBoxAccess = null;
	          accessDialog = new Dialog(this);
	          accessDialog.setTitle(R.string.new_box_access);
	          
	          accessDialog.setContentView(R.layout.dlg_boxaccess);
	          
	          Button buttonOK = (Button) accessDialog.findViewById(R.id.btnSave);
	          Button buttonCancel = (Button) accessDialog.findViewById(R.id.btnCancel);

	          buttonOK.setOnClickListener(accessDialogHandler);
	          buttonCancel.setOnClickListener(accessDialogHandler);
	          accessDialog.show(); 
		}else{
			currentDataBoxAccess = (DataBoxAccess) databoxesAdapter.getItem(position);
			
	          accessDialog = new Dialog(this);
	          accessDialog.setTitle(R.string.new_box_access);
	          
	          accessDialog.setContentView(R.layout.dlg_boxaccess);
	          
	          Button buttonOK = (Button) accessDialog.findViewById(R.id.btnSave);
	          Button buttonCancel = (Button) accessDialog.findViewById(R.id.btnCancel);
	          
	          ((EditText) accessDialog.findViewById(R.id.edtPersonID)).setText(currentDataBoxAccess.getPersonId());
	          ((EditText) accessDialog.findViewById(R.id.edtPassword)).setText(currentDataBoxAccess.getPassword());
	          
	          buttonOK.setOnClickListener(accessDialogHandler);
	          buttonCancel.setOnClickListener(accessDialogHandler);
	          accessDialog.show(); 
			
			
		}
	}

}