package edu.cwru.apo;

import java.net.URLDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cwru.apo.API.Methods;
import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

// allows a user to view and edit their profile information
public class Profile extends Activity implements AsyncRestRequestListener<Methods, JSONObject>{
	TextView text;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		text = (TextView)this.findViewById(R.id.profileText);
		API api = new API(this);
		if(!api.callMethod(Methods.checkAES, this, (String[])null))
		{
			Toast msg = Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG);
			msg.show();
		}
	}
	
	public void onRestRequestComplete(Methods method, JSONObject result) {
		if(method == Methods.checkAES)
		{
			if(result != null)
			{
				try {
					byte[] encrypted = Auth.hexToBytes(result.getString("encrypted"));
					byte[] iv = "fedcba9876543210".getBytes();
					String decrypted = Auth.bytesToHex(Auth.AesDecrypt(encrypted, iv));
					text.setText(decrypted);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
}
