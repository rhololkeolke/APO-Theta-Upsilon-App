package edu.cwru.apo;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

// displays most recent news from website
// will have a news ticker with latest twitter updates
public class News extends Activity{
	
	TextView text;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news);
		text = (TextView)findViewById(R.id.newsText);
		JSONObject jObject = API.HMACTest(getApplicationContext());
		if(jObject != null)
		{
			try{
				/*double timestamp = jObject.getDouble("timestamp")/1000000000000.0;
				double time = jObject.getDouble("time")/1000000000;
				boolean equal = false;
				if((time - timestamp) < 1e-8)
					equal = true;
				String msg = "timestamp: " + timestamp + " time: " + time + " equal: " + equal + " difference: " + (timestamp-time);*/
				text.setText(jObject.getString("timeStatus") + "\n" + jObject.getString("HMACStatus") + "\n" + jObject.getString("ServerHMAC") + "\n" + jObject.getString("ClientHMAC"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
