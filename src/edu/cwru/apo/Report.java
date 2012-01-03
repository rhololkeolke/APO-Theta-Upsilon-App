package edu.cwru.apo;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.TableLayout;

public class Report extends Activity{
	EditText txtProjName, txtProjLoc, txtTravelTime, txtHours, txtMinutes;
	DatePicker datePicker;
	RadioGroup groupTypeService, groupTypeProject;
	CheckBox chkOut, chkDriver;
	MultiAutoCompleteTextView txtName;
	Button btnAdd, btnSubmit;
	TableLayout tblBrothers;
	private PhoneOpenHelper phoneDB;
	private static SQLiteDatabase database = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report);
		txtProjName = (EditText)findViewById(R.id.txtProjectName);
		txtProjLoc = (EditText)findViewById(R.id.txtProjectLocation);
		txtTravelTime = (EditText)findViewById(R.id.txtTravelTime);
		txtHours = (EditText)findViewById(R.id.txtHours);
		txtMinutes = (EditText)findViewById(R.id.txtMinutes);
		datePicker = (DatePicker)findViewById(R.id.datePicker);
		groupTypeService = (RadioGroup)findViewById(R.id.groupTypeService);
		groupTypeProject = (RadioGroup)findViewById(R.id.groupTypeProject);
		chkOut = (CheckBox)findViewById(R.id.chkOut);
		chkDriver = (CheckBox)findViewById(R.id.chkDriver);
		txtName = (MultiAutoCompleteTextView)findViewById(R.id.txtName);
		btnAdd = (Button)findViewById(R.id.btnAdd);
		btnSubmit = (Button)findViewById(R.id.btnSubmit);
		tblBrothers = (TableLayout)findViewById(R.id.tblBros);
		
		if (database == null)
		{
			phoneDB = new PhoneOpenHelper(this);
			database = phoneDB.getWritableDatabase();
		}
		
		loadUsers();
	}
	
	private void loadUsers()
	{
		if (database == null)
		{
			phoneDB = new PhoneOpenHelper(this);
			database = phoneDB.getWritableDatabase();
		}
		
		Cursor results = database.query("phoneDB", new String[] {"first","last","_id"}, null, null, null, null, "first");
		int numResults = results.getCount();
		
		String [] users = new String[numResults];
		if (!results.moveToFirst())
			return;
		int x = 0;
		while (!results.isAfterLast())
		{
			users[x] = results.getString(0) + " " + results.getString(1) + " [" + results.getString(2) + "]";
			x++;
			results.moveToNext();
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,users);
		txtName.setAdapter(adapter);
		txtName.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
	}

}
