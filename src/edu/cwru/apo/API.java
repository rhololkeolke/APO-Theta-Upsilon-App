package edu.cwru.apo;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class API extends Activity{
	private static HttpClient httpClient = null;
	
	public static JSONObject login(Context context, String user, String pass) // used for initial logins
	{
		//add code for Login here
		if(httpClient == null)
			httpClient = new TrustAPOHttpClient(context);
		
		Map<String, String> kvPairs = new HashMap<String, String>();
		kvPairs.put("method","login");
		kvPairs.put("user",user);
		kvPairs.put("pass", md5(pass));
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
		kvPairs.put("user", APO.user);
		Calendar cal = Calendar.getInstance();
		kvPairs.put("timestamp", String.valueOf(cal.getTimeInMillis()));
		kvPairs.put("HMAC", HMAC(kvPairs));
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
		kvPairs.put("user", APO.user);
		Calendar cal = Calendar.getInstance();
		kvPairs.put("timestamp", String.valueOf(cal.getTimeInMillis()));
		kvPairs.put("HMAC", HMAC(kvPairs));
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

	public static String md5(String in)
	{
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(in.getBytes());
			byte[] a = digest.digest();
			int len = a.length;
			StringBuilder sb = new StringBuilder(len << 1);
			for(int i = 0; i<len; i++)
			{
				sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
				sb.append(Character.forDigit(a[i] & 0x0f, 16));
			}
			return sb.toString();
		} catch(NoSuchAlgorithmException e) {
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
	} 
		
	public static String HMAC(Map<String, String> kvPairs)
	{
		String v;
		String data = "";
		Iterator<String> itKeys = kvPairs.keySet().iterator();
		
		
		while(itKeys.hasNext()){
			v = kvPairs.get(itKeys.next());
			data = v + data;
		}        
        
	   SecretKeySpec sk;
		try {
			sk = new SecretKeySpec(APO.secretKey.getBytes(), "HmacMD5");
			Mac mac;
			mac = Mac.getInstance("HmacMD5");
		    mac.init(sk);

		    byte[] result = mac.doFinal(data.getBytes());
		    int len = result.length;
			StringBuilder sb = new StringBuilder(len << 1);
			for(int i = 0; i<len; i++)
			{
				sb.append(Character.forDigit((result[i] & 0xf0) >> 4, 16));
				sb.append(Character.forDigit(result[i] & 0x0f, 16));
			}
			return sb.toString();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return null;
	}
}
