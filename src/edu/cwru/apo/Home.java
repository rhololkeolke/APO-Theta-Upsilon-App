package edu.cwru.apo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;


//This is the main window of the application.  Every sub activity gets launched from here
public class Home extends Activity implements OnItemClickListener{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);
		
		GridView gridview = (GridView) findViewById(R.id.gridView);
		gridview.setAdapter(new ImageAdapter(this));
		
		gridview.setOnItemClickListener(this);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch(position){
		case 0:
			// launch news activity
			Intent newsIntent = new Intent(Home.this, News.class);
			Home.this.startActivity(newsIntent);
			break;
		case 1:
			// launch Contract activity
			Intent contractIntent = new Intent(Home.this, Contract.class);
			Home.this.startActivity(contractIntent);
			break;
		case 2:
			// launch profile activity
			Intent profileIntent = new Intent(Home.this, Profile.class);
			Home.this.startActivity(profileIntent);
			break;
		case 3:
			// launch directory activity
			Intent directoryIntent = new Intent(Home.this, Directory.class);
			Home.this.startActivity(directoryIntent);
			break;
		default:
			//do nothing
		}
	}
	
	/*@Override
	protected void onResume()
	{
		super.onResume();
		Auth.loadKeys(getPreferences(MODE_PRIVATE));
	}*/
	
	@Override
	protected void onPause()
	{
		super.onPause();
		Auth.saveKeys(getSharedPreferences(APO.PREF_FILE_NAME, MODE_PRIVATE));
	}

}
