package edu.cwru.apo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity implements OnClickListener{
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
			/* need to implement checking of login credentials
			 currently saves whatever values are input into username and password
			 If there are input values saved when the app starts then it will bypass the login screen
			 */
			
			SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
			SharedPreferences.Editor prefEditor = preferences.edit();
			prefEditor.putString("username", username.getText().toString());
			prefEditor.putString("passHash", password.getText().toString());
			prefEditor.commit();
			
			Intent homeIntent = new Intent(Login.this, Home.class);
			Login.this.startActivity(homeIntent);
			finish();
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
}
