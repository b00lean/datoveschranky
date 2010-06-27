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

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import eu.apksoft.android.datoveschranky.helpers.AndroidUtils;
import eu.apksoft.android.datoveschranky.helpers.PreferencesHelper;

public class LoginActivity extends Activity implements OnClickListener {
	final Handler asyncHandler = new Handler();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidUtils.registerForExceptions(this);
        setContentView(R.layout.login);
        findViewById(R.id.btnLogin).setOnClickListener(this);
		AndroidUtils.initSSLIfNeeded(this);
    }

	@Override
	protected void onResume() {
		super.onResume();
        if (isPasswordSet()) {
        	showLoginDialog();
        }
	}

	private boolean isPasswordSet() {
		return !(PreferencesHelper.getPassword(this) == null || PreferencesHelper.getPassword(this).length() == 0);
	}
	
	private void showLoginDialog() {
    	//show dialog for entering password
    	final Dialog passwordDialog = new Dialog(this);
        passwordDialog.setTitle(R.string.app_name);
          
        passwordDialog.setContentView(R.layout.dlg_password);
        Button buttonOK = (Button) passwordDialog.findViewById(R.id.btnOK);
      
        buttonOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText edtPassword = (EditText)passwordDialog.findViewById(R.id.edtPassword);
				String password = edtPassword.getText().toString();
				if (tryToLogin(password)) {
					passwordDialog.dismiss(); //user logged in, we can hide this dialog
				}
			}
        });
        passwordDialog.show(); 
	}

	private boolean tryToLogin(String password) {
		if (password.equalsIgnoreCase(PreferencesHelper.getPassword(this))) {
	        startActivity(new Intent(this, MainActivity.class));
	        return true;
		}else{
		    Toast.makeText(this, this.getResources().getText(R.string.wrong_password), Toast.LENGTH_SHORT).show();
		    return false;
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.btnLogin:
			if (isPasswordSet()) {
				showLoginDialog();
			}else{
				startActivity(new Intent(this, MainActivity.class));
			}
			break;
		}
	}
	
}