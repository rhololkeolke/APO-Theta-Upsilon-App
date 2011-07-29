/*
 * Copyright 2011 Devin Schwab, Umang Banugaria
 *
 * This file is part of the APO Theta Upsilon App for Case Western Reserve University's Alpha Phi Omega Theta Upsilon Chapter.
 *
 * The APO Theta Upsilon program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.cwru.apo;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cwru.apo.API.Methods;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener, AsyncRestRequestListener<Methods, JSONObject>{
	private Button login_btn;
	private Button forgot_btn;
	private EditText username;
	private EditText password;
	
	public static final String PREF_FILE_NAME = "PrefFile";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		Bundle extras = getIntent().getExtras();
		if(extras.get("msg") != null)
		{
			Toast message = Toast.makeText(getApplicationContext(), extras.getString("msg"), Toast.LENGTH_SHORT);
			message.show();
		}
		// map the login button to the layout
		login_btn = (Button)findViewById(R.id.login);
		login_btn.setOnClickListener(this);
		// map the forgot password button to the layout
		forgot_btn = (Button)findViewById(R.id.forgot_password);
		forgot_btn.setOnClickListener(this);
		
		username = (EditText)findViewById(R.id.username);
		password = (EditText)findViewById(R.id.password);
		
	}

	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.login:
			API api = new API(this);
			if(!api.callMethod(Methods.login, this, username.getText().toString(), password.getText().toString()))
			{
				Toast msg = Toast.makeText(this, "Error: You must enter both a username and password to login", Toast.LENGTH_SHORT);
				msg.show();
			}
			//api.callMethod(Methods.checkAES,this, (String[])null);
			break;
				
		case R.id.forgot_password:
			//start forgot password activity
			Intent forgotPasswordIntent = new Intent(Login.this, ForgotPassword.class);
			Login.this.startActivity(forgotPasswordIntent);
			break;
		default:
			//something went wrong add a throw here
		}
	}

	public void onRestRequestComplete(Methods method, JSONObject result) {
		if(method == Methods.login)
		{
			if(result != null)
			{
				try {
					String requestStatus = result.getString("requestStatus");
					if(requestStatus.compareTo("valid login") == 0)
					{
						Auth.Hmac.setCounter(result.getInt("counter"));
						Auth.Hmac.setIncrement(result.getInt("increment"));
						Intent homeIntent = new Intent(Login.this, Home.class);
						Login.this.startActivity(homeIntent);
						finish();
					}
					else if(requestStatus.compareTo("invalid username") == 0)
					{
						Toast msg = Toast.makeText(getApplicationContext(), "Invalid username", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(requestStatus.compareTo("invalid login") == 0)
					{
						Toast msg = Toast.makeText(getApplicationContext(), "Invalid username and/or password", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(requestStatus.compareTo("no user") == 0)
					{
						Toast msg = Toast.makeText(getApplicationContext(), "No username was provided", Toast.LENGTH_LONG);
						msg.show();
					}
					else
					{
						Toast msg = Toast.makeText(getApplicationContext(), "Invalid requestStatus", Toast.LENGTH_LONG);
						msg.show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				Toast msg = Toast.makeText(getApplicationContext(), "Could not contact web server.  Please check your connection", Toast.LENGTH_LONG);
				msg.show();
			}
		}
		else
		{
			Toast msg = Toast.makeText(getApplicationContext(), "Invalid method callback", Toast.LENGTH_LONG);
			msg.show();
		}
	}
	
	/*@Override
	protected void onPause()
	{
		Auth.saveKeys(getSharedPreferences(APO.PREF_FILE_NAME, MODE_PRIVATE));
	}
	
	@Override
	protected void onResume()
	{
		Auth.loadKeys(getSharedPreferences(APO.PREF_FILE_NAME, MODE_PRIVATE));
	}*/
	
}	

