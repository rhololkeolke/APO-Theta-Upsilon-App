package edu.cwru.apo;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cwru.apo.API.Methods;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View.OnClickListener;
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
			Toast msg = Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG);
			msg.show();
		}
	}

	public void onRestRequestComplete(Methods method, JSONObject result) {
		if(method == Methods.checkCredentials)
		{
			if(result != null)
			{
				try {
					if(result.getString("requestStatus").compareTo("valid") == 0)
					{
						Toast msg = Toast.makeText(getApplicationContext(), "Valid Credentials", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(result.getString("requestStatus").compareTo("No response") == 0)
					{
						Auth.rollbackOtp(); // rollback the OTP so the user won't have to login again when the internet is restored
						Toast msg = Toast.makeText(getApplicationContext(), "Could not contact web server. Please check your connection", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(result.getString("requestStatus").compareTo("timestamp invalid") == 0)
					{
						Auth.clearKeys(getSharedPreferences(APO.PREF_FILE_NAME, MODE_PRIVATE)); // one or all are invalid so delete them all
						Toast msg = Toast.makeText(getApplicationContext(), "Timestamp Invalid", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(result.getString("requestStatus").compareTo("HMAC invalid") == 0)
					{
						Auth.clearKeys(getSharedPreferences(APO.PREF_FILE_NAME, MODE_PRIVATE));
						Toast msg = Toast.makeText(getApplicationContext(), "HMAC Invalid", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(result.getString("requestStatus").compareTo("OTP invalid") == 0)
					{
						Auth.clearKeys(getSharedPreferences(APO.PREF_FILE_NAME, MODE_PRIVATE));
						Toast msg = Toast.makeText(getApplicationContext(), "OTP Invalid", Toast.LENGTH_LONG);
						msg.show();
					}
					else
					{
						Auth.clearKeys(getSharedPreferences(APO.PREF_FILE_NAME, MODE_PRIVATE));
						Toast msg = Toast.makeText(getApplicationContext(), "unknown requestStatus", Toast.LENGTH_LONG);
						msg.show();
					}
					
				} catch (JSONException e) {
					Toast msg = Toast.makeText(getApplicationContext(), "JSON error: Invalid JSON response", Toast.LENGTH_LONG);
					msg.show();
					e.printStackTrace();
				}
			}
			else
			{
				Toast msg = Toast.makeText(getApplicationContext(), "JSON error: No JSON Object to read", Toast.LENGTH_LONG);
				msg.show();
			}
		}
		else
		{
			Toast msg = Toast.makeText(getApplicationContext(), "Invalid method", Toast.LENGTH_LONG);
			msg.show();
		}
		
	}

}
