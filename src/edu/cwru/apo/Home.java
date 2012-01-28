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

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cwru.apo.API.Methods;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;


//This is the main window of the application.  Every sub activity gets launched from here
public class Home extends Activity implements OnItemClickListener, AsyncRestRequestListener<Methods, JSONObject>{
	
	private static PhoneOpenHelper phoneDB;
	private static SQLiteDatabase database = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);
		
		GridView gridview = (GridView) findViewById(R.id.gridView);
		gridview.setAdapter(new ImageAdapter(this));
		
		gridview.setOnItemClickListener(this);
		
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		if (!Auth.loggedIn)
		{
			Intent nextActivity = new Intent(Home.this, Login.class);
			Bundle extras = new Bundle();
			nextActivity.putExtras(extras);
			Home.this.startActivity(nextActivity);
			finish();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.option_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.updateMenuItem:
	    	if (phoneDB == null)
	    		phoneDB = new PhoneOpenHelper(this);
	    	if (database == null)
	    		database = phoneDB.getWritableDatabase();
	    	API phoneApi = new API(this);
	    	String[] params = {"0"};
			if(!phoneApi.callMethod(Methods.phone, this, params))
			{
				Toast msg = Toast.makeText(this, "Error: Calling phone", Toast.LENGTH_LONG);
				msg.show();
			}
	        return true;
	    case R.id.updateApp:
	    	API updateAppApi = new API(this);
	    	if(!updateAppApi.callMethod(Methods.checkAppVersion, this))
	    	{
	    		Toast msg = Toast.makeText(this,"Error: Couldn't get app version", Toast.LENGTH_LONG);
	    		msg.show();
	    	}
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch(position){
		case 0:
			// launch service report activity
			Intent reportIntent = new Intent(Home.this, Report.class);
			Home.this.startActivity(reportIntent);
			break;
		case 1:
			// launch Contract activity
			Intent contractIntent = new Intent(Home.this, Contract.class);
			Home.this.startActivity(contractIntent);
			break;
		/*
		case 2:
			// launch profile activity
			Intent profileIntent = new Intent(Home.this, Profile.class);
			Home.this.startActivity(profileIntent);
			break;
		*/
		case 2:
			// launch directory activity
			Intent directoryIntent = new Intent(Home.this, Directory.class);
			Home.this.startActivity(directoryIntent);
			break;
		default:
			//do nothing
		}
	}
	
	
	
	/*@Override
	protected void onResume()
	{
		super.onResume();
		Auth.loadKeys(getPreferences(MODE_PRIVATE));
	}*/
	
	@Override
	protected void onPause()
	{
		super.onPause();
		Auth.saveKeys(getSharedPreferences(APO.PREF_FILE_NAME, MODE_PRIVATE));
	}

	public void onRestRequestComplete(Methods method, JSONObject result) 
	{
		if(method == Methods.phone)
		{
			if(result != null)
			{
				try {
					String requestStatus = result.getString("requestStatus");
					if(requestStatus.compareTo("success") == 0)
					{
						SharedPreferences.Editor editor = getSharedPreferences(APO.PREF_FILE_NAME, MODE_PRIVATE).edit();
						editor.putLong("updateTime", result.getLong("updateTime"));
						editor.commit();
						int numbros = result.getInt("numBros");
						JSONArray caseID = result.getJSONArray("caseID");
						JSONArray first = result.getJSONArray("first");
						JSONArray last = result.getJSONArray("last");
						JSONArray phone = result.getJSONArray("phone");
						JSONArray family = result.getJSONArray("family");
						ContentValues values;
						for(int i = 0; i < numbros; i++)
						{
							values = new ContentValues();
							values.put("_id", caseID.getString(i));
							values.put("first", first.getString(i));
							values.put("last", last.getString(i));
							values.put("phone", phone.getString(i));
							values.put("family", family.getString(i));
							database.replace("phoneDB", null, values);
						}
					}
					else if(requestStatus.compareTo("timestamp invalid") == 0)
					{
						Toast msg = Toast.makeText(this, "Invalid timestamp.  Please try again.", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(requestStatus.compareTo("HMAC invalid") == 0)
					{
						Auth.loggedIn = false;
						Toast msg = Toast.makeText(this, "You have been logged out by the server.  Please log in again.", Toast.LENGTH_LONG);
						msg.show();
						finish();
					}
					else
					{
						Toast msg = Toast.makeText(this, "Invalid requestStatus", Toast.LENGTH_LONG);
						msg.show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
		else if(method == Methods.checkAppVersion)
		{
			if(result != null)
			{
				try{
					String requestStatus = result.getString("requestStatus");
					if(requestStatus.compareTo("success") == 0)
					{
						String appVersion = result.getString("version");
						String appDate = result.getString("date");
						final String appUrl = result.getString("url");
						PackageInfo pinfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);;
						if(appVersion.compareTo(pinfo.versionName) != 0)
						{
							AlertDialog.Builder builder = new AlertDialog.Builder(this);
							builder.setTitle("Upgrade");
							builder.setMessage("Update available, ready to upgrade?");
							builder.setIcon(R.drawable.icon);
							builder.setCancelable(false);
							builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Intent promptInstall = new Intent("android.intent.action.VIEW",Uri.parse("https://apo.case.edu:8090/app/" + appUrl)); 
									startActivity(promptInstall);
								}
							});
							builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
							AlertDialog alert = builder.create();
							alert.show();
						}
						else
						{
							Toast msg = Toast.makeText(this, "No updates found", Toast.LENGTH_LONG);
							msg.show();
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
