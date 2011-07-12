package edu.cwru.apo;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.client.HttpClient;
import org.json.JSONObject;

import edu.cwru.apo.RestClient.RequestMethod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class API extends Activity{
	
	private static HttpClient httpClient = null;
	private static String url = "https://apo.case.edu:8090/api/api.php";
	
	private Context context;
	
	public enum Methods {login, checkCredentials, logout, resetPassword, getContract, phone, serviceReport, checkAES, aesServerEncryption, aesServerDecryption, testHMAC};
	
	public API(Context context)
	{
		this.context = context;
		if(httpClient == null)
			httpClient = new TrustAPOHttpClient(this.context); // context leak?
	}
	
	public boolean callMethod(Methods method, AsyncRestRequestListener<Methods, JSONObject> callback, String...params)
	{
		String hmac;
		boolean result = false;
		switch(method)
		{
		case aesServerEncryption:
			ApiCall serverEncryptionCall = new ApiCall(context, callback, method, "Testing", "Please Wait");
			RestClient serverEncryptionClient = new RestClient(url, httpClient, RequestMethod.POST);
			serverEncryptionClient.AddParam("method", "aesServerEncryption");
			serverEncryptionClient.AddParam("plain", "Hello World");
			
			serverEncryptionCall.execute(serverEncryptionClient);
			result=true;
			break;
		case login:
			if(params.length != 2)
				break;
			if(params[0] == null || params[1] == null)
				break;
			
			// set up a login request
			ApiCall loginCall = new ApiCall(context, callback, method, "Logging In", "Please Wait");
			RestClient loginClient = new RestClient(url, httpClient, RequestMethod.POST);
			String passHash =  Auth.md5(params[1]);		//only used to see that the md5 is not working correctly
			Auth.generateAesKey(256);
			String aes = Auth.getAesKey(context.getApplicationContext());
			loginClient.AddParam("method", "login");
			loginClient.AddParam("user", params[0]);
			loginClient.AddParam("pass", passHash);		//temp solution for password it to not use the md5 method and just copy in the password hash string literal
			loginClient.AddParam("AESkey", aes);
			loginClient.AddParam("installID", Installation.id(context.getApplicationContext()));
			
			//execute the call
			loginCall.execute(loginClient);
			result = true;
			break;
		case checkCredentials:
			// set up a checkCredentials request
			ApiCall checkCredentialsCall = new ApiCall(context, callback, method);
			RestClient checkCredentialsClient = new RestClient(url, httpClient, RequestMethod.POST);
			
			// check if HMAC key and AES key exist
			if(!Auth.HmacKeyExists() || !Auth.AesKeyExists())
			{
				break;
			}
			
			// if both exist add parameters to call and execute
			checkCredentialsClient.AddParam("method", "checkCredentials");
			checkCredentialsClient.AddParam("installID", Installation.id(context.getApplicationContext()));
			checkCredentialsClient.AddParam("timestamp", Long.toString(Auth.getTimestamp()));
			hmac = Auth.getHmac(checkCredentialsClient);
			checkCredentialsClient.AddParam("hmac", hmac);
			checkCredentialsClient.AddParam("otp", Auth.getOtp(hmac));
			
			//execute the call
			checkCredentialsCall.execute(checkCredentialsClient);
			result = true;
			break;
		case logout:
			// set up a logout request
			ApiCall logoutCall = new ApiCall(context, callback, method);
			RestClient logoutClient = new RestClient(url, httpClient, RequestMethod.POST);
			
			// clear all the keys in memory and preference file
			Auth.clearKeys(getSharedPreferences(APO.PREF_FILE_NAME, MODE_PRIVATE));
			
			// add the parameters to the call
			logoutClient.AddParam("method", "logout");
			logoutClient.AddParam("installID", Installation.id(context.getApplicationContext()));
			
			// execute the call
			logoutCall.execute(logoutClient);
			
			result = true;
			break;
		case resetPassword:
			// set up a resetPassword request
			ApiCall resetPasswordCall = new ApiCall(context, callback, method);
			RestClient resetPasswordClient = new RestClient(url, httpClient, RequestMethod.POST);
			
			//add parameters to the call
			resetPasswordClient.AddParam("method", "resetPassword");
			resetPasswordClient.AddParam("caseID", params[0]);
			
			//execute the call
			resetPasswordCall.execute(resetPasswordClient);
			
			result = true;
			break;
		case getContract:
			// set up a getContract request
			ApiCall getContractCall = new ApiCall(context, callback, method);
			RestClient getContractClient = new RestClient(url, httpClient, RequestMethod.POST);
			
			// add parameters to the call
			getContractClient.AddParam("method", "getContract");
			getContractClient.AddParam("installID", Installation.id(context.getApplicationContext()));
			getContractClient.AddParam("timestamp", Long.toString(Auth.getTimestamp()));
			hmac = Auth.getHmac(getContractClient);
			getContractClient.AddParam("hmac", hmac);
			getContractClient.AddParam("otp", Auth.getOtp(hmac));
			
			//execute request
			getContractCall.execute(getContractClient);
			
			result = true;
			break;
		case phone:
			// set up a phone request
			break;
		case serviceReport:
			// set up a serviceReport request
			break;
		case checkAES:
			ApiCall checkAESCall = new ApiCall(context, callback, method);
			RestClient checkAESClient = new RestClient(url, httpClient, RequestMethod.POST);
			Auth.generateAesKey(256);
			String key = Auth.getAesKeyInsecure();
			String encrypted = Auth.AesEncrypt("Hello World", key, "fedcba9876543210");
			checkAESClient.AddParam("method", "checkAES");
			checkAESClient.AddParam("plain", "Hello World");
			checkAESClient.AddParam("encrypted", encrypted);
			checkAESClient.AddParam("key", key);
			checkAESClient.AddParam("iv", "fedcba9876543210");
			//execute request
			checkAESCall.execute(checkAESClient);
			
			result = true;
			break;
		case testHMAC:
			SecretKeySpec sk;
			try {
				sk = new SecretKeySpec("secret".getBytes(), "HmacMD5");
				Mac mac;
				mac = Mac.getInstance("HmacMD5");
			    mac.init(sk);

			    String testhmac = Auth.bytesToHex(mac.doFinal("testdata".getBytes()));
			    
			    ApiCall testHMACCall = new ApiCall(context, callback, method);
				RestClient testHMACClient = new RestClient(url, httpClient, RequestMethod.POST);
				
				testHMACClient.AddParam("method", "testHMAC");
				testHMACClient.AddParam("data", "testdata");
				testHMACClient.AddParam("secret", "secret");
				testHMACClient.AddParam("hmac", testhmac);
				//execute request
				testHMACCall.execute(testHMACClient);
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			result = true;
			break;
		}
		return result;
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
