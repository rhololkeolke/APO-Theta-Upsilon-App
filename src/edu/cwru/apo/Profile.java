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
					String encrypted = result.getString("encrypted");
					String key = result.getString("key");
					String iv = result.getString("iv");
					String decrypted = Auth.AesDecrypt(encrypted, key, iv);
					Toast msg = Toast.makeText(getApplicationContext(), decrypted, Toast.LENGTH_LONG);
					msg.show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Toast msg = Toast.makeText(getApplicationContext(), "Invalid JSON", Toast.LENGTH_LONG);
					msg.show();
					e.printStackTrace();
				}
			}
		}
		else
		{
			Toast msg = Toast.makeText(getApplicationContext(), "Invalid method", Toast.LENGTH_LONG);
			msg.show();
		}
	}
}
