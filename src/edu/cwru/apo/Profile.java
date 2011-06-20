package edu.cwru.apo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

// allows a user to view and edit their profile information
public class Profile extends Activity{
	TextView text;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		text = (TextView)this.findViewById(R.id.profileText);
		//Auth.generateAESKey(512);
		try {
			String encrypted = Auth.AESencrypt("seed", "test");
			String decrypted = Auth.AESdecrypt("seed", encrypted);
			text.setText(encrypted + "\n" + decrypted);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
