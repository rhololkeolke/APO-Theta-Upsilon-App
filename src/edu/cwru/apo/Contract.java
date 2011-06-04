package edu.cwru.apo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

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
		JSONObject jObject = API.getContract(getApplicationContext());
		//List<String> results = new ArrayList<String>(); //use this to iterate automatically over returned values
		try {
			String requestStatus = jObject.getString("requestStatus");
			//String totalHours = jObject.getString("totalHours");
			
			text.setText(requestStatus );//+ "\n" + totalHours);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
