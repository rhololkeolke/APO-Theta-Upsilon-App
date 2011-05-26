package edu.cwru.apo;

import android.app.Activity;
import android.os.Bundle;

// allows a user to view the status of their contract
public class Contract extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contract);
	}

}
