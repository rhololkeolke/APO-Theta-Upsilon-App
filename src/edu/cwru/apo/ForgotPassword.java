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
