package edu.cwru.apo;

import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cwru.apo.RestClient.RequestMethod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class API extends Activity{
	/*private static HttpClient httpClient = null;
	
	public static JSONObject login(Context context, String user, String pass) // used for initial logins
	{
		//add code for Login here
		if(httpClient == null)
			httpClient = new TrustAPOHttpClient(context);
		
		Map<String, String> kvPairs = new HashMap<String, String>();
		kvPairs.put("method","login");
		kvPairs.put("user",user);
		kvPairs.put("pass", Auth.md5(pass));
		kvPairs.put("submitLogin", "1");
		try{
			HttpResponse httpResponse = doPost(httpClient, "https://apo.case.edu/api/api.php", kvPairs);
			HttpEntity httpEntity = httpResponse.getEntity();
			String result = EntityUtils.toString(httpEntity);
			JSONObject jObject = new JSONObject(result);
			return jObject;
		} catch(ClientProtocolException e) {
			//Log.e("ClientProtocolException", ((Object) e).gotMessage());
			e.printStackTrace();
		} catch(IOException e) {
			//Log.e("IOException", ((Object) e).gotMessage());
			e.printStackTrace();
		} catch(JSONException e) {
			//Log.e("JSONException", ((Object) e).gotMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public static JSONObject login(Context context, SharedPreferences preferences) // used when restoring 
	{
		if(httpClient == null)
			httpClient = new TrustAPOHttpClient(context);
        
        String username = preferences.getString("username", null);
        String passHash = preferences.getString("passHash", null);
        if(username == null || passHash == null)
        {
        	JSONObject jObject = new JSONObject();
        	try {
				jObject.put("requestStatus", "missing username or passHash");
				return jObject;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		Map<String, String> kvPairs = new HashMap<String, String>();
		kvPairs.put("method", "login");
		kvPairs.put("user", username);
		kvPairs.put("pass", passHash);
		kvPairs.put("submitLogin", "1");
		try{
			HttpResponse httpResponse = doPost(httpClient, "https://apo.case.edu/api/api.php", kvPairs);
			HttpEntity httpEntity = httpResponse.getEntity();
			String result = EntityUtils.toString(httpEntity);
			JSONObject jObject = new JSONObject(result);
			return jObject;
		} catch(ClientProtocolException e) {
			//Log.e("ClientProtocolException", ((Object) e).gotMessage());
			e.printStackTrace();
		} catch(IOException e) {
			//Log.e("IOException", ((Object) e).gotMessage());
			e.printStackTrace();
		} catch(JSONException e) {
			//Log.e("JSONException", ((Object) e).gotMessage());
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject getContract(Context context)
	{
		if(httpClient == null)
			httpClient = new TrustAPOHttpClient(context);
		Map<String, String> kvPairs = new HashMap<String, String>();
		kvPairs.put("method", "getContract");
		//kvPairs.put("user", APO.user);
		Calendar cal = Calendar.getInstance();
		kvPairs.put("timestamp", String.valueOf(cal.getTimeInMillis()));
		//kvPairs.put("HMAC", Auth.HMAC(kvPairs, APO.secretKey));
		try{
			HttpResponse httpResponse = doPost(httpClient, "https://apo.case.edu/api/api.php", kvPairs);
			HttpEntity httpEntity = httpResponse.getEntity();
			String result = EntityUtils.toString(httpEntity);
			JSONObject jObject = new JSONObject(result);
			return jObject;
		} catch(ClientProtocolException e){
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static JSONObject HMACTest(Context context)
	{
		if(httpClient == null)
			httpClient = new TrustAPOHttpClient(context);
		Map<String, String> kvPairs = new HashMap<String, String>();
		kvPairs.put("method", "HMACTest");
		//kvPairs.put("user", APO.user);
		Calendar cal = Calendar.getInstance();
		kvPairs.put("timestamp", String.valueOf(cal.getTimeInMillis()));
		//kvPairs.put("HMAC", Auth.HMAC(kvPairs, APO.secretKey));
		try{
			HttpResponse httpResponse = doPost(httpClient, "https://apo.case.edu/api/api.php", kvPairs);
			HttpEntity httpEntity = httpResponse.getEntity();
			String result = EntityUtils.toString(httpEntity);
			JSONObject jObject = new JSONObject(result);
			return jObject;
		} catch(ClientProtocolException e){
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static HttpResponse doPost(HttpClient httpClient, String url, Map<String, String> kvPairs) throws ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost(url);
		
		if(kvPairs != null && kvPairs.isEmpty() == false) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(kvPairs.size());
			String k, v;
			Iterator<String> itKeys = kvPairs.keySet().iterator();
			
			while(itKeys.hasNext()) {
				k = itKeys.next();
				v = kvPairs.get(k);
				nameValuePairs.add(new BasicNameValuePair(k,v));
			}

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		}

		return httpClient.execute(httpPost);
	}*/
	
	private static HttpClient httpClient = null; // this is static so that the session isn't broken
	private static String url = "https://apo.case.edu/api/api.php";
	
	private Context context;
	
	public enum Methods {login, checkCredentials, getContract, phone, serviceReport};
	
	public API(Context context)
	{
		this.context = context;
		if(httpClient == null)
			httpClient = new TrustAPOHttpClient(this.context); // context leak?
	}
	
	public void callMethod(Methods method, AsyncRestRequestListener<Methods, JSONObject> callback, String...params)
	{
		switch(method)
		{
		case login:
			// set up a login request
			break;
		case checkCredentials:
			// set up a checkCredentials request
			ApiCall call = new ApiCall(context, callback, method);
			RestClient restClient = new RestClient(url, httpClient, RequestMethod.POST);
			
			// see if HMAC and AES key exist
			if(!Auth.HmacKeyExists() || !Auth.AesKeyExists())
			{
				call.execute(restClient);
				break;
			}
			
			// if both exist add parameters to call and execute
			restClient.AddParam("method", "checkCredentials");
			restClient.AddParam("installID", Installation.id(context));
			restClient.AddParam("timestamp", Long.toString(Auth.getTimestamp()));
			restClient.AddParam("hmac", Auth.getHmac(restClient));
			restClient.AddParam("otp", Auth.getOtp());
			call.execute(restClient);
			
			// execute call
			break;
		case getContract:
			// set up a getContract request
			break;
		case phone:
			// set up a phone request
			break;
		case serviceReport:
			// set up a serviceReport request
			break;
		}
	}
	
	private class ApiCall extends AsyncTask<RestClient, Void, JSONObject>
	{
		
		private Context context;
		private AsyncRestRequestListener<Methods,JSONObject> callback;
		private String progTitle;
		private String progMsg;
		private Methods method;
		
		private ProgressDialog progDialog;
		
		// constructor used when no progress dialog is desired
		public ApiCall(Context context, AsyncRestRequestListener<Methods,JSONObject> cb, Methods method)
		{
			this.context = context;
			this.callback = cb;
			this.method = method;
		}
		
		// constructor used when progress dialog is desired
		public ApiCall(Context context, AsyncRestRequestListener<Methods,JSONObject> cb, Methods method, String title, String message)
		{
			this.context = context;
			this.callback = cb;
			this.method = method;
			this.progTitle = title;
			this.progMsg = message;
		}
		
		@Override
		protected void onPreExecute()
		{
			// if message and title were specified start progress dialog
			if((progTitle != null) && (progMsg != null))
			{
				progDialog = ProgressDialog.show(context, progTitle, progMsg);
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
