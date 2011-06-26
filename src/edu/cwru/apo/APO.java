package edu.cwru.apo;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cwru.apo.API.Methods;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


public class APO extends Activity implements AsyncRestRequestListener<API.Methods,JSONObject>{
	/* public static final String PREF_FILE_NAME = "PrefFile";
	
	 private static final int STOPSPLASH = 0;
     //time in milliseconds
     private static final long SPLASHTIME = 3000;
     
     public static final String appKey = "changeThisKeyInFinalApp";

	public static String secretKey;

	public static String user;
         
     //handler for splash screen
     private Handler splashHandler = new Handler() {
             @Override
             public void handleMessage(Message msg) {
                     switch (msg.what) {
                     case STOPSPLASH:
                         SharedPreferences preferences  = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
                         
                         try {
                        	 JSONObject jObject = API.login(getApplicationContext(), preferences);
                        	 if(jObject == null) // if there was a problem with the request
                        	 {
                        		Toast webError = Toast.makeText(getApplicationContext(), "Error contacting webserver.  Check your connection.  If problem persists contact webmaster", Toast.LENGTH_SHORT);
                        	 	webError.show();
                        	 	break;
                        	 }
                        	 String requestStatus = jObject.getString("requestStatus"); // run the login to reestablish session
                        	 Bundle extras = new Bundle();
								if(requestStatus.compareTo("valid login") == 0)
								{
									secretKey = appKey;
									user = preferences.getString("username", null);
		                        	Intent homeIntent = new Intent(APO.this, Home.class);
		                        	APO.this.startActivity(homeIntent);
		                        	finish();
								} 
								else
								{
									if(requestStatus.compareTo("missing username or passHash") == 0)
									{
										extras.putString("message", null);
									}
									else
									{
										extras.putString("message", "There was an error with your saved username/password. Please login again.");
									}
									Intent loginIntent = new Intent(APO.this,Login.class);
									loginIntent.putExtras(extras);
									APO.this.startActivity(loginIntent);
									finish();
								}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						 }
	                     break;
                     }
                     super.handleMessage(msg);
             }
     };
     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // removes title bar on app, making image full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.splash_screen);
        
        // eventually there will be code here determining if the user is logged in or not
        Message msg = new Message();
        msg.what = STOPSPLASH;
        splashHandler.sendMessageDelayed(msg, SPLASHTIME);

    }*/
	
	private static final long SPLASHTIME = 3000;
	private static long STARTTIME = 0;
	
	public static final String PREF_FILE_NAME = "PrefFile";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        
        // removes title bar on app, making image full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.splash_screen);
        
        // load keys if they exist
		Auth.loadKeys(getSharedPreferences(APO.PREF_FILE_NAME, MODE_PRIVATE));
        
        //start Async Web Call here
        STARTTIME = Auth.getTimestamp();
        API api = new API(getApplicationContext());
        if(!api.callMethod(Methods.checkCredentials, this, (String[])null))
        {
        	Intent loginIntent = new Intent(APO.this, Login.class);
        	Bundle extras = new Bundle();
        	extras.putString("msg", "No saved credentials");
        	loginIntent.putExtras(extras);
        	APO.this.startActivity(loginIntent);
        	finish();
        }
        
	}
	
	public void onRestRequestComplete(API.Methods method, JSONObject result)
	{
		// check and see if the splash screen has been shown for the minimum amount of time
		// NOTE: This would probably be better done with a timer.  Need to change this
		while(Auth.getTimestamp()-STARTTIME < SPLASHTIME)
		{
			// do nothing.  This is probably not the right way to do this
		}
		
		// set the next activity to default Login
		Intent nextActivity = new Intent(APO.this, Login.class);
		Bundle extras = new Bundle();
		
		if(method == Methods.checkCredentials)
		{
			if(result != null)
			{
				try {
					if(result.getString("requestStatus").compareTo("valid") == 0)
					{
						//change the nextActivity to Home
						nextActivity = new Intent(APO.this, Home.class);
					}
					else if(result.getString("requestStatus").compareTo("No response") == 0)
					{
						extras.putString("msg", "Could not contact web server. Please check your connection");
					}
					else
					{
						extras.putString("msg", "Invalid credentials");
					}
					
				} catch (JSONException e) {
					extras.putString("msg", "JSON error: Invalid JSON response");
					e.printStackTrace();
				}
			}
			else
			{
				extras.putString("msg", "JSON error: No JSON Object to read");
			}
		}
		else
		{
			extras.putString("msg", "Invalid method called");
		}
		
		nextActivity.putExtras(extras);
		
		// start the next activity
		APO.this.startActivity(nextActivity);
		finish();
	}
	
}