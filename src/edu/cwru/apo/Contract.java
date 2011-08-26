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

// allows a user to view the status of their contract
public class Contract extends Activity implements AsyncRestRequestListener<Methods, JSONObject>{
	TextView text;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contract);
		text = (TextView)findViewById(R.id.contractText);
		
		API api = new API(this);
		if(!api.callMethod(Methods.getContract, this, (String[])null))
		{
			Toast msg = Toast.makeText(getApplicationContext(), "Error: Calling getContract", Toast.LENGTH_LONG);
			msg.show();
		}
	}
	
	public void onRestRequestComplete(Methods method, JSONObject result) {
		if(method == Methods.getContract)
		{
			if(result != null)
			{
				try {
					String requestStatus = result.getString("requestStatus");
					if(requestStatus.compareTo("success") == 0)
					{
						String contract = "";
						contract += "Status: " + result.getString("status") + "\n";
						contract += "Dues: " + result.getString("dues") + "\n\n";
						contract += "Chapter Attendance: \n";
						contract += "Attended: " + result.getDouble("chaptersAttended") + "\n";
						contract += "Required: " + result.getDouble("chaptersRequired") + "\n\n";
						contract += "Pledge Attendance: \n";
						contract += "Attended: " + result.getDouble("pledgeAttended") + "\n";
						contract += "Required: " + result.getDouble("pledgeRequired") + "\n\n";
						contract += "Hours:\n";
						contract += "Hours towards contract: " + result.getDouble("contractHours") + "/" + result.getDouble("contractHoursRequired") + "\n";
						contract += "Inside Hours: " + result.getDouble("in") + "/" + result.getDouble("inRequired") + "\n";
						contract += "Outside Hours: " + result.getDouble("out") + "\n";
						contract += "Total semester Hours: " + result.getDouble("totalHours");
						text.setText(contract);
					}
					else if(requestStatus.compareTo("timestamp invalid") == 0)
					{
						Toast msg = Toast.makeText(getApplicationContext(), "Invalid timestamp.  Please try again.", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(requestStatus.compareTo("HMAC invalid") == 0)
					{
						Toast msg = Toast.makeText(getApplicationContext(), "You have been logged out by the server.  Please log in again.", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(requestStatus.compareTo("no contract") == 0)
					{
						Toast msg = Toast.makeText(getApplicationContext(), "You have not signed a contract.", Toast.LENGTH_LONG);
						msg.show();
					}
					else
					{
						Toast msg = Toast.makeText(getApplicationContext(), "Invalid requestStatus", Toast.LENGTH_LONG);
						msg.show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}

