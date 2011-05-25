package edu.cwru.apo;

import android.app.Activity;
import android.content.Intent;
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
		
	}

	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.login:
			// check login credentials
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
