package edu.cwru.apo;

import org.json.JSONObject;

import edu.cwru.apo.API.Methods;
import android.app.Activity;
import android.os.Bundle;

public class Report extends Activity implements AsyncRestRequestListener<Methods, JSONObject>{
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report);
	}

	public void onRestRequestComplete(Methods method, JSONObject result) {
		// TODO Auto-generated method stub
		
	}
	
	

}
