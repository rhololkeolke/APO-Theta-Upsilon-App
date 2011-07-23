package edu.cwru.apo;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cwru.apo.API.Methods;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

// displays most recent news from website
// will have a news ticker with latest twitter updates
public class News extends Activity implements AsyncRestRequestListener<Methods, JSONObject>{
	
	TextView text;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news);
		text = (TextView)findViewById(R.id.newsText);
		
		API api = new API(this);
		if(!api.callMethod(Methods.checkCredentials, this, (String[])null))
		{
			Toast msg = Toast.makeText(getApplicationContext(), "Error: Calling checkCredentials", Toast.LENGTH_LONG);
			msg.show();
		}
	}
	
	public void onRestRequestComplete(Methods method, JSONObject result)
	{
		if(method == Methods.checkCredentials)
		{
			if(result != null)
			{
				String requestStatus;
				try {
					requestStatus = result.getString("requestStatus");
					if(requestStatus.compareTo("valid") == 0)
					{
						// put message here
						Toast msg = Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG);
						msg.show();
					}
					else
					{
						Toast msg = Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG);
						msg.show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					// put invalid JSON message here
					Toast msg = Toast.makeText(getApplicationContext(), "JSON Error: Invalid element", Toast.LENGTH_LONG);
					msg.show();
					e.printStackTrace();
				}

			}
			else
			{
				Toast msg = Toast.makeText(getApplicationContext(), "Error: result is null", Toast.LENGTH_LONG);
				msg.show();
			}
		}
		else
		{
			Toast msg = Toast.makeText(getApplicationContext(), "Invalid method called", Toast.LENGTH_LONG);
			msg.show();
		}
	}
	
	/*@Override
	protected void onPause()
	{
		Auth.saveKeys(getSharedPreferences(APO.PREF_FILE_NAME, MODE_PRIVATE));
	}
	
	@Override
	protected void onResume()
	{
		Auth.loadKeys(getSharedPreferences(APO.PREF_FILE_NAME, MODE_PRIVATE));
	}*/

}


