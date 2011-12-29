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

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cwru.apo.RestClient.RequestMethod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

public class API extends Activity{
	
	private static HttpClient httpClient = null;
	private static String secureUrl = "https://apo.case.edu:8090/api/api.php";
	
	private Context context;
	
	public enum Methods {login, checkCredentials, logout, resetPassword, getContract, phone, serviceReport, checkAES, decryptRSA};
	
	public API(Context context)
	{
		this.context = context;
		if(httpClient == null)
			httpClient = new TrustAPOHttpClient(this.context); // context leak?
		if(Auth.Hmac == null)
			Auth.Hmac = new DynamicHmac();
	}
	
	public boolean callMethod(Methods method, AsyncRestRequestListener<Methods, JSONObject> callback, String...params)
	{
		boolean result = false;
		switch(method)
		{
		case login:
			if(params.length != 2)
				break;
			if(params[0] == null || params[1] == null)
				break;
			
			// set up a login request NOTE: Must be HTTPS!!
			ApiCall loginCall = new ApiCall(context, callback, method, "Logging In", "Please Wait");
			RestClient loginClient = new RestClient(secureUrl, httpClient, RequestMethod.POST);
			loginClient.AddParam("method", "login");
			loginClient.AddParam("user", params[0]);
			loginClient.AddParam("pass", Auth.md5(params[1]).toString());
			loginClient.AddParam("installID", URLEncoder.encode(Installation.id(context.getApplicationContext())));
			loginClient.AddParam("secretKey", Auth.Hmac.getSecretKey().toString());
			
			//execute the call
			loginCall.execute(loginClient);
			result = true;
			break;
		case checkCredentials:
			// set up a checkCredentials request
			ApiCall checkCredentialsCall = new ApiCall(context, callback, method);
			RestClient checkCredentialsClient = new RestClient(secureUrl, httpClient, RequestMethod.POST);
			
			// if both exist add parameters to call and execute
			String installID = Installation.id(context.getApplicationContext());
			String timestamp = Long.toString(Auth.getTimestamp());
			String data = "checkCredentials" + timestamp + installID;
			checkCredentialsClient.AddParam("method", "checkCredentials");
			checkCredentialsClient.AddParam("installID", installID);
			checkCredentialsClient.AddParam("timestamp", timestamp);
			try {
				checkCredentialsClient.AddParam("HMAC", Auth.Hmac.generate(data).toString());
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
			//execute the call
			checkCredentialsCall.execute(checkCredentialsClient);
			result = true;
			break;
		case logout:
			// set up a logout request
			break;
		case resetPassword:
			// set up a resetPassword request
			break;
		case getContract:
			// set up a getContract request
			ApiCall getContractCall = new ApiCall(context, callback, method, "Loading", "Please Wait");
			RestClient getContractClient = new RestClient(secureUrl, httpClient, RequestMethod.POST);
			
			// if both exist add parameters to call and execute
			String contractinstallID = Installation.id(context.getApplicationContext());
			String contracttimestamp = Long.toString(Auth.getTimestamp());
			String contractdata = "getContract" + contracttimestamp + contractinstallID;
			getContractClient.AddParam("method", "getContract");
			getContractClient.AddParam("installID", contractinstallID);
			getContractClient.AddParam("timestamp", contracttimestamp);
			try {
				getContractClient.AddParam("HMAC", Auth.Hmac.generate(contractdata).toString());
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
			//execute the call
			getContractCall.execute(getContractClient);
			result = true;
			break;

		case phone:
			// set up a phone request
			ApiCall phoneCall = new ApiCall(context, callback, method, "Updating User Directory", "Please Wait");
			RestClient phoneClient = new RestClient(secureUrl, httpClient, RequestMethod.POST);
			
			// if both exist add parameters to call and execute
			String phoneInstallID = Installation.id(context.getApplicationContext());
			String phoneTimestamp = Long.toString(Auth.getTimestamp());
			
			JSONObject phoneUserData = new JSONObject();
			try {
				//phoneUserData.put("updateTime", getUpdateTime(getSharedPreferences(APO.PREF_FILE_NAME, MODE_PRIVATE)));
				phoneUserData.put("updateTime", "0");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String phoneUD = URLEncoder.encode(phoneUserData.toString());
			String phoneData = "phone" + phoneTimestamp + phoneInstallID + phoneUserData.toString();
			phoneClient.AddParam("method", "phone");
			phoneClient.AddParam("installID", phoneInstallID);
			phoneClient.AddParam("timestamp", phoneTimestamp);
			phoneClient.AddParam("userData", phoneUD);
			try {
				phoneClient.AddParam("HMAC", Auth.Hmac.generate(phoneData).toString());
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
			//execute the call
			phoneCall.execute(phoneClient);
			result = true;
			break;
		case serviceReport:
			// set up a phone request
			ApiCall reportCall = new ApiCall(context, callback, method, "Updating User Directory", "Please Wait");
			RestClient reportClient = new RestClient(secureUrl, httpClient, RequestMethod.POST);
			
			// if both exist add parameters to call and execute
			String reportInstallID = Installation.id(context.getApplicationContext());
			String reportTimestamp = Long.toString(Auth.getTimestamp());
			
			JSONObject reportUserData = new JSONObject();
			try {
				reportUserData.put("date", params[0]);
				reportUserData.put("projectName", params[1]);
				reportUserData.put("projectLocation", params[2]);
				reportUserData.put("inOut", params[3]);
				reportUserData.put("offCampus", params[4]);
				reportUserData.put("serviceType", params[5]);
				reportUserData.put("travelTime", params[6]);
				reportUserData.put("comments", params[7]);
				reportUserData.put("numBros", params[8]);
				reportUserData.put("brothers", params[9]);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String reportUD = URLEncoder.encode(reportUserData.toString());
			String reportData = "phone" + reportTimestamp + reportInstallID + reportUserData.toString();
			reportClient.AddParam("method", "serviceReport");
			reportClient.AddParam("installID", reportInstallID);
			reportClient.AddParam("timestamp", reportTimestamp);
			reportClient.AddParam("userData", reportUD);
			try {
				reportClient.AddParam("HMAC", Auth.Hmac.generate(reportData).toString());
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
			//execute the call
			reportCall.execute(reportClient);
			result = true;
			break;
		}
		return result;
	}
	
	private long getUpdateTime(SharedPreferences prefs)
	{
		return prefs.getLong("updateTime", 0);
	}
	
	private class ApiCall extends AsyncTask<RestClient, Void, JSONObject>
	{
		
		private Context context;
		private AsyncRestRequestListener<Methods,JSONObject> callback;
		private Context progContext;
		private String progTitle;
		private String progMsg;
		private Methods method;
		
		private ProgressDialog progDialog;
		
		// constructor used when no progress dialog is desired
		public ApiCall(Context context, AsyncRestRequestListener<Methods,JSONObject> cb, Methods method)
		{
			this.context = context.getApplicationContext();
			this.callback = cb;
			this.method = method;
		}
		
		// constructor used when progress dialog is desired
		public ApiCall(Context context, AsyncRestRequestListener<Methods,JSONObject> cb, Methods method, String title, String message)
		{
			this.context = context.getApplicationContext();
			this.callback = cb;
			this.method = method;
			this.progContext = context;
			this.progTitle = title;
			this.progMsg = message;
		}
		
		@Override
		protected void onPreExecute()
		{
			// if message and title were specified start progress dialog
			if((progTitle != null) && (progMsg != null))
			{
				progDialog = ProgressDialog.show(progContext, progTitle, progMsg, false);
			}
		}

		@Override
		protected JSONObject doInBackground(RestClient... params) {
			// start the requests
			JSONObject jObject = null;
			try {
				params[0].Execute(); // only ever expect one param.  If there are more ignore them
				jObject = new JSONObject(params[0].getResponse()); // convert the response to a JSON object
				
				// put a default response in the case that the web server returned nothing
				if(jObject == null)
					jObject.put("requestStatus", params[0].getErrorMessage());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return jObject;
		}
		
		@Override
		protected void onPostExecute(JSONObject result)
		{
			//dismiss the progress dialog
			if(progDialog != null)
				progDialog.cancel();
			
			// initiate the callback
			callback.onRestRequestComplete(method, result);
		}
		
	}
	
	
}
