package edu.cwru.apo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;


public class APO extends Activity {
	 private static final int STOPSPLASH = 0;
     //time in milliseconds
     private static final long SPLASHTIME = 3000;
     
     private Boolean loggedIn = false; // hard coded to true until login check code is implemented
    
     //handler for splash screen
     private Handler splashHandler = new Handler() {
             @Override
             public void handleMessage(Message msg) {
                     switch (msg.what) {
                     case STOPSPLASH:
                             if(loggedIn) {
                            	 // start main home activity
                             }
                             else {
                            	 // start login activity
                            	 Intent loginIntent = new Intent(APO.this, Login.class);
                            	 APO.this.startActivity(loginIntent);
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