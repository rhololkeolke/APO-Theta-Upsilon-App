/*
 * Copyright 2011 Devin Schwab, Umang Banugaria
 *
 * This file is part of the APO Theta Upsilon App for Case Western Reserve University's Alpha Phi Omega Theta Upsilon Chapter.
 *
 * The APO Theta Upsilon program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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


