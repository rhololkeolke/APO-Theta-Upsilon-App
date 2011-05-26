package edu.cwru.apo;

import android.app.Activity;
import android.os.Bundle;

// allows a user to view and edit their profile information
public class Profile extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
	}
}
