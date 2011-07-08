package edu.cwru.apo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

// allows a user to view the status of their contract
public class Contract extends Activity {
	TextView text;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contract);
		text = (TextView)findViewById(R.id.contractText);
	}

}
