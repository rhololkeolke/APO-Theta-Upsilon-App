package edu.cwru.apo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener{
	private Button login_btn;
	private Button forgot_btn;
	private EditText username;
	private EditText password;
	
	public static final String PREF_FILE_NAME = "PrefFile";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		// map the login button to the layout
		login_btn = (Button)findViewById(R.id.login);
		login_btn.setOnClickListener(this);
		// map the forgot password button to the layout
		forgot_btn = (Button)findViewById(R.id.forgot_password);
		forgot_btn.setOnClickListener(this);
		
		username = (EditText)findViewById(R.id.username);
		password = (EditText)findViewById(R.id.password);
		
	}

	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.login:
			/* need to implement checking of login credentials
			 currently saves whatever values are input into username and password
			 If there are input values saved when the app starts then it will bypass the login screen
			 */
			HttpClient httpClient = new TrustAPOHttpClient(getApplicationContext()); // create the client so that https connects on 8090
			// add the parameters to the post
			Map<String, String> kvPairs = new HashMap<String, String>();
			kvPairs.put("method", "login");
			kvPairs.put("user", username.getText().toString());
			kvPairs.put("pass", password.getText().toString());
			kvPairs.put("submitLogin", "1");
			try {
				HttpResponse httpResponse = doPost(httpClient, "https://apo.case.edu/api/api.php", kvPairs);
				HttpEntity httpEntity = httpResponse.getEntity();
				String result = EntityUtils.toString(httpEntity);
				JSONObject jObject = new JSONObject(result);
				Toast toast = Toast.makeText(getApplicationContext(), jObject.getString("loginResult"), Toast.LENGTH_LONG);
				toast.show();
			} catch (ClientProtocolException e) {
				Log.e("ClientProtocolException", e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e("IOException", e.getMessage());
				e.printStackTrace();
			} catch (JSONException e) {
				Log.e("JSONException", e.getMessage());
				e.printStackTrace();
			}
			
			/*SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
			SharedPreferences.Editor prefEditor = preferences.edit();
			prefEditor.putString("username", username.getText().toString());
			prefEditor.putString("passHash", password.getText().toString());
			prefEditor.commit();
			
			Intent homeIntent = new Intent(Login.this, Home.class);
			Login.this.startActivity(homeIntent);
			finish();*/
			break;
		case R.id.forgot_password:
			//start forgot password activity
			Intent forgotPasswordIntent = new Intent(Login.this, ForgotPassword.class);
			Login.this.startActivity(forgotPasswordIntent);
			break;
		default:
			//something went wrong add a throw here
		}
	}
	
/*	private HttpClient createHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	    HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
	    HttpProtocolParams.setUseExpectContinue(params, true);

	    SchemeRegistry schReg = new SchemeRegistry();
	    schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	    schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 8090));
	    ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

	    return new TrustAPOHttpClient(conMgr, params);
	}*/
	
	public static HttpResponse doPost(HttpClient httpClient, String url, Map<String, String> kvPairs)
		throws ClientProtocolException, IOException
		{
		   HttpPost httppost = new HttpPost(url); 

		   if (kvPairs != null && kvPairs.isEmpty() == false) {
		       List<NameValuePair> nameValuePairs =
		                new ArrayList<NameValuePair>(kvPairs.size());
		       String k, v;
		       Iterator<String> itKeys = kvPairs.keySet().iterator(); 

		       while (itKeys.hasNext()) {
		              k = itKeys.next();
		              v = kvPairs.get(k);
		              nameValuePairs.add(new BasicNameValuePair(k, v));
		       } 

		       httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		   } 

		   HttpResponse response;
		   response = httpClient.execute(httppost); return response;
		}

}
