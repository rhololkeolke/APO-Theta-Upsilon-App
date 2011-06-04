package edu.cwru.apo;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


public class APO extends Activity {
	 public static final String PREF_FILE_NAME = "PrefFile";
	
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
                        	 String requestStatus = API.login(getApplicationContext(), preferences).getString("requestStatus"); // run the login to reestablish session
                        	 Bundle extras = new Bundle();
								if(requestStatus.compareTo("valid login") == 0)
								{
									secretKey = appKey;
									user = preferences.getString("username", null);
		                        	Intent homeIntent = new Intent(APO.this, Home.class);
		                        	APO.this.startActivity(homeIntent);
		                        	finish();
								}
								else if(requestStatus.compareTo("missing username or passHash") == 0)
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
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						 }
	                     break;
                     }
                     super.handleMessage(msg);
             }
     };
     
    /** Called when the activity is first created. */
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

    }
	
}