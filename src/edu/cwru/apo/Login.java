package edu.cwru.apo;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cwru.apo.API.Methods;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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
						Auth.setOtpAndHmac(result.getString("key"));
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
		/*else if(method == Methods.checkAES)
		{
			if(result != null)
			{
				try {
					String encrypted = result.getString("encrypted");
					String decrypted = Auth.AesDecryptPub(encrypted.getBytes());
					Toast msg = Toast.makeText(getApplicationContext(), decrypted, Toast.LENGTH_LONG);
					msg.show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}*/
		else
		{
			Toast msg = Toast.makeText(getApplicationContext(), "Invalid method callback", Toast.LENGTH_LONG);
			msg.show();
		}
	}
}	
