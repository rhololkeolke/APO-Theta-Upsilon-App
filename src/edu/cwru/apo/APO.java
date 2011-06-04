package edu.cwru.apo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;


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
                         
                         String username = preferences.getString("username", null);
                         String passHash = preferences.getString("passHash", null);
                         
                         if(username == null || passHash == null)
                         {
                        	 Intent loginIntent = new Intent(APO.this, Login.class);
                        	 APO.this.startActivity(loginIntent);
                        	 finish();
                         }
                         else
                         {
                        	 secretKey = appKey + passHash;
                        	 user = username;
                        	 Intent homeIntent = new Intent(APO.this, Home.class);
                        	 APO.this.startActivity(homeIntent);
                        	 finish();
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