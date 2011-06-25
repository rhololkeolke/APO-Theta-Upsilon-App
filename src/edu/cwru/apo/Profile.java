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
	}
}
