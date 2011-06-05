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
				text.setText(jObject.getString("timeStatus") + "\n" + jObject.getString("HMACStatus") + "\n" + jObject.getString("ServerHMAC") + "\n" + jObject.getString("ClientHMAC") + "\n" + jObject.getString("Session ID"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
