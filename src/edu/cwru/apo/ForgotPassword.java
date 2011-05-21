package edu.cwru.apo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPassword extends Activity implements OnClickListener{
	EditText username;
	Button reset;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_password);
		username = (EditText)findViewById(R.id.username_reset);
		reset = (Button)findViewById(R.id.reset_btn);
		reset.setOnClickListener(this);
	}

	public void onClick(View v) {
		// put the code to reset the password here
		Boolean success = true; // this will change so that to accurately reflect state.  For now it is simple defaulted to success
		
		// this code lets the user know the password has been successfully reset
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		if(success) {
			alertDialog.setTitle("Success!");
			alertDialog.setMessage("Your password has been reset! Please check your email and remember to change it once you log in");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			      finish();
			   }
			});
			alertDialog.setIcon(R.drawable.icon); // set to an icon that means success
		} else {
			alertDialog.setTitle("Something Went Wrong");
			alertDialog.setMessage("Make sure you typed your username correctly");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {			
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			alertDialog.setIcon(R.drawable.icon); // set to an icon that means failure
		}
		alertDialog.show();
	}

}
