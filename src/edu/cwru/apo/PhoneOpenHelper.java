package edu.cwru.apo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cwru.apo.API.Methods;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class PhoneOpenHelper extends SQLiteOpenHelper implements AsyncRestRequestListener<Methods, JSONObject>{

    private static final int DATABASE_VERSION = 1;
    private static final String PHONE_TABLE_NAME = "phoneDB";
    private static final String PHONE_TABLE_CREATE =
                "CREATE TABLE " + PHONE_TABLE_NAME + " (_id TEXT PRIMARY KEY, first TEXT, last TEXT, phone TEXT, family TEXT);";
    private static SQLiteDatabase database = null;
    private static Context context;
    
    PhoneOpenHelper(Context context) {
        super(context, PHONE_TABLE_NAME, null, DATABASE_VERSION);
        PhoneOpenHelper.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	database = db;
    	database.execSQL(PHONE_TABLE_CREATE);
    }

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	public void loadData() 
	{
		if (database == null)
			database = this.getWritableDatabase();
		API api = new API(context);
		if(!api.callMethod(Methods.phone, this, (String[])null))
		{
			Toast msg = Toast.makeText(context, "Error: Calling phone", Toast.LENGTH_LONG);
			msg.show();
		}

	}
	
	public void onRestRequestComplete(Methods method, JSONObject result) {
		if(method == Methods.phone)
		{
			if(result != null)
			{
				try {
					String requestStatus = result.getString("requestStatus");
					if(requestStatus.compareTo("success") == 0)
					{
						int numbros = result.getInt("numBros");
						JSONArray caseID = result.getJSONArray("caseID");
						JSONArray first = result.getJSONArray("first");
						JSONArray last = result.getJSONArray("last");
						JSONArray phone = result.getJSONArray("phone");
						JSONArray family = result.getJSONArray("family");
						ContentValues values; 
						for(int i = 0; i < numbros; i++)
						{
							values = new ContentValues();
							values.put("_id", caseID.getString(i));
							values.put("first", first.getString(i));
							values.put("last", last.getString(i));
							values.put("phone", phone.getString(i));
							values.put("family", family.getString(i));
							database.replace("phoneDB", null, values);
						}
					}
					else if(requestStatus.compareTo("timestamp invalid") == 0)
					{
						Toast msg = Toast.makeText(context, "Invalid timestamp.  Please try again.", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(requestStatus.compareTo("HMAC invalid") == 0)
					{
						Toast msg = Toast.makeText(context, "You have been logged out by the server.  Please log in again.", Toast.LENGTH_LONG);
						msg.show();
					}
					else
					{
						Toast msg = Toast.makeText(context, "Invalid requestStatus", Toast.LENGTH_LONG);
						msg.show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
}
