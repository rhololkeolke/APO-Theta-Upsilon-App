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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cwru.apo.API.Methods;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Directory extends Activity implements OnClickListener, AsyncRestRequestListener<Methods, JSONObject>{
	
	private PhoneOpenHelper phoneDB;
	private TableLayout userTable;
	private static SQLiteDatabase database = null;
	private String lastPhone = "";
	private Dialog phoneDialog;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.directory);
		userTable = (TableLayout)findViewById(R.id.userTable);
		phoneDB = new PhoneOpenHelper(this);
		database = phoneDB.getWritableDatabase();
		Cursor results = database.query("phoneDB", new String[] {"first","last","_id"}, null, null, null, null, "first");
		if (results.getCount()<1)
		{
			API api = new API(this);
			if(!api.callMethod(Methods.phone, this, (String[])null))
			{
				Toast msg = Toast.makeText(this, "Error: Calling phone", Toast.LENGTH_LONG);
				msg.show();
			}
		}
		else
			loadTable();
	}
	
	
	private void loadTable()
	{
		ProgressDialog progDialog = ProgressDialog.show(this, "Loading", "Please Wait", false);
		userTable.removeAllViews();
		Cursor results = database.query("phoneDB", new String[] {"first","last","_id"}, null, null, null, null, "first");
		String rowText = "";
		TableRow row;
		TextView text;
		if (!results.moveToFirst())
			return;
		while (!results.isAfterLast())
		{
			rowText = results.getString(0) + " " + results.getString(1) + " [" + results.getString(2) + "]";
			row = new TableRow(this);
			text = new TextView(this);
			row.setPadding(0, 5, 0, 5);
			text.setClickable(true);
			text.setOnClickListener(this);
			//text.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			text.setText(rowText);
			userTable.addView(row);
			row.addView(text);
			results.moveToNext();
		}
		progDialog.cancel();
	}
	
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btnCall:
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:"+lastPhone));
			startActivity(intent);
			break;
		
		case R.id.btnText:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", lastPhone, null)));
			break;
			
		default:
			String text = ((TextView)v).getText().toString();
			int start = text.lastIndexOf('[');
			int end = text.lastIndexOf(']');
			String caseID = text.substring(start+1, end);
			Cursor results = database.query("phoneDB", new String[] {"first","last","_id","phone"}, "_id = ?", new String[] {caseID}, null, null, null);
			if (results.getCount() != 1)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Error Loading Phone Number")
				       .setCancelable(false)
				       .setNeutralButton("OK", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
			}
			else
			{
				results.moveToFirst();
				
				phoneDialog = new Dialog(this);
				phoneDialog.setContentView(R.layout.phone_dialog);
				phoneDialog.setTitle(results.getString(0) + " " + results.getString(1));
				
				TextView phoneText = (TextView) phoneDialog.findViewById((R.id.phoneText));
				String phoneNumber = removeNonDigits(results.getString(3));
				if (phoneNumber == null || phoneNumber.trim().equals("") || phoneNumber.trim().equals("null"))
				{
					((Button)phoneDialog.findViewById(R.id.btnCall)).setEnabled(false);
					((Button)phoneDialog.findViewById(R.id.btnText)).setEnabled(false);
					phoneNumber = "not available";
				}
				else
				{
					((Button)phoneDialog.findViewById(R.id.btnCall)).setEnabled(true);
					((Button)phoneDialog.findViewById(R.id.btnText)).setEnabled(true);
					((Button)phoneDialog.findViewById(R.id.btnCall)).setOnClickListener(this);
					((Button)phoneDialog.findViewById(R.id.btnText)).setOnClickListener(this);
				}
				lastPhone = phoneNumber;
				phoneText.setText("Phone Number: " + lastPhone);
				phoneDialog.show();
			}
			break;			
		}		
	}

	private String removeNonDigits(String phoneNumber)
	{
		StringBuilder ans = new StringBuilder();
		char currentChar;
		for (int i = 0; i < phoneNumber.length(); ++i) {
		    currentChar = phoneNumber.charAt(i);
		    if (Character.isDigit(currentChar)) {
		        ans.append(currentChar);
		    }
		}
		return ans.toString();
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
						loadTable();
					}
					else if(requestStatus.compareTo("timestamp invalid") == 0)
					{
						Toast msg = Toast.makeText(this, "Invalid timestamp.  Please try again.", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(requestStatus.compareTo("HMAC invalid") == 0)
					{
						Toast msg = Toast.makeText(this, "You have been logged out by the server.  Please log in again.", Toast.LENGTH_LONG);
						msg.show();
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
	}
}
