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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import eu.apksoft.android.datoveschranky.helpers.AndroidUtils;
import eu.apksoft.android.datoveschranky.helpers.PreferencesHelper;

public class LoginActivity extends Activity implements OnClickListener {
	final Handler asyncHandler = new Handler();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        findViewById(R.id.btnLogin).setOnClickListener(this);
		AndroidUtils.initSSLIfNeeded(this);
    }

	@Override
	protected void onResume() {
		super.onResume();
		showOrHideLogin();
	}

	private void showOrHideLogin() {
        if (PreferencesHelper.getPassword(this) == null || PreferencesHelper.getPassword(this).length() == 0) {
        	((LinearLayout)findViewById(R.id.layoutLogin)).setVisibility(View.INVISIBLE);
        }else{
        	((LinearLayout)findViewById(R.id.layoutLogin)).setVisibility(View.VISIBLE);
        }
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.btnLogin:
			EditText edtPassword = (EditText)findViewById(R.id.edtPassword);
			if (edtPassword.isShown()) {
				String password = edtPassword.getText().toString();
				
				if (password.equalsIgnoreCase(PreferencesHelper.getPassword(this))) {
			        startActivity(new Intent(this, MainActivity.class));
				}else{
				    int duration = Toast.LENGTH_SHORT;
				    Toast.makeText(this, this.getResources().getText(R.string.wrong_password), duration).show();
				}
			}else{
				startActivity(new Intent(this, MainActivity.class));
			}
			break;
		}
	}
	
}