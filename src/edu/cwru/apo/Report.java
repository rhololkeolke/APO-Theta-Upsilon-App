package edu.cwru.apo;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cwru.apo.API.Methods;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Report extends Activity implements AsyncRestRequestListener<Methods, JSONObject>, View.OnFocusChangeListener, RadioGroup.OnCheckedChangeListener, View.OnClickListener
{
	EditText txtProjName, txtProjLoc, txtTravelTime, txtHours, txtMinutes, txtComments;
	DatePicker datePicker;
	RadioGroup groupTypeService, groupTypeProject, groupOnOffCampus;
	RadioButton radOn, radOff, radIn, radService1, radService2, radService3;
	CheckBox chkDriver;
	MultiAutoCompleteTextView txtName;
	Button btnAdd, btnSubmit;
	LinearLayout layoutBrothers;
	String [] users;
	private PhoneOpenHelper phoneDB;
	private static SQLiteDatabase database = null;
	ArrayList<Brother> brothers = new ArrayList<Brother>();
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRequestedOrientation(1);
		setContentView(R.layout.report);
		txtProjName = (EditText)findViewById(R.id.txtProjectName);
		txtProjLoc = (EditText)findViewById(R.id.txtProjectLocation);
		txtTravelTime = (EditText)findViewById(R.id.txtTravelTime);
		txtHours = (EditText)findViewById(R.id.txtHours);
		txtMinutes = (EditText)findViewById(R.id.txtMinutes);
		txtComments = (EditText)findViewById(R.id.txtComments);
		datePicker = (DatePicker)findViewById(R.id.datePicker);
		groupTypeService = (RadioGroup)findViewById(R.id.groupTypeService);
		groupTypeProject = (RadioGroup)findViewById(R.id.groupTypeProject);
		groupOnOffCampus = (RadioGroup)findViewById(R.id.groupOnOffCampus);
		radOn = (RadioButton)findViewById(R.id.radioOn);
		radOff = (RadioButton)findViewById(R.id.radioOff);
		radIn = (RadioButton)findViewById(R.id.radioIn);
		radService1 = (RadioButton)findViewById(R.id.radioService1);
		radService2 = (RadioButton)findViewById(R.id.radioService2);
		radService3 = (RadioButton)findViewById(R.id.radioService3);
		chkDriver = (CheckBox)findViewById(R.id.chkDriver);
		txtName = (MultiAutoCompleteTextView)findViewById(R.id.txtName);
		btnAdd = (Button)findViewById(R.id.btnAdd);
		btnSubmit = (Button)findViewById(R.id.btnSubmit);
		layoutBrothers = (LinearLayout)findViewById(R.id.layoutBros);
		
		txtTravelTime.setEnabled(false);
		chkDriver.setChecked(false);
		txtName.setOnFocusChangeListener(this);
		groupOnOffCampus.setOnCheckedChangeListener(this);
		btnAdd.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
		
		if (database == null)
		{
			phoneDB = new PhoneOpenHelper(this);
			database = phoneDB.getWritableDatabase();
		}
		
		loadUsers();
		loadBrothers();
	}
	
	public void loadBrothers()
	{
		layoutBrothers.removeAllViews();
		LinearLayout row;
		TextView name;
		TextView hours;
		TextView minutes;
		TextView driver;
		Button remove;
		android.view.Display display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		
		row = new LinearLayout(this);
		name = new TextView(this);
		hours = new TextView(this);
		minutes = new TextView(this);
		driver = new TextView(this);
		TextView removeText = new TextView(this);
		
		name.setText("Name");
		name.setWidth((int)(display.getWidth()*.4));
		hours.setText("Hours");
		hours.setWidth((int)(display.getWidth()*.15));
		minutes.setText("Minutes");
		minutes.setWidth((int)(display.getWidth()*.15));
		driver.setText("Driver?");
		driver.setWidth((int)(display.getWidth()*.15));
		removeText.setText("Remove");
		removeText.setWidth((int)(display.getWidth()*.15));
		
		row.addView(name);
		row.addView(hours);
		row.addView(minutes);
		row.addView(driver);
		row.addView(removeText);
		
		layoutBrothers.addView(row);
		
		for (int x = 0; x < brothers.size(); x++)
		{
    		row = new LinearLayout(this);
			name = new TextView(this);
			hours = new TextView(this);
			minutes = new TextView(this);
			driver = new TextView(this);
			remove = new Button(this);
			
			name.setText(brothers.get(x).name);
			name.setWidth((int)(display.getWidth()*.4));
			hours.setText(brothers.get(x).hours+"");
			hours.setWidth((int)(display.getWidth()*.15));
			minutes.setText(brothers.get(x).minutes+"");
			minutes.setWidth((int)(display.getWidth()*.15));
			if (brothers.get(x).driver)
				driver.setText("Yes");
			else
				driver.setText("No");
			driver.setWidth((int)(display.getWidth()*.15));
			remove.setText("X");
			remove.setOnClickListener(this);
			remove.setWidth((int)(display.getWidth()*.15));
			
			row.addView(name);
			row.addView(hours);
			row.addView(minutes);
			row.addView(driver);
			row.addView(remove);
			
			layoutBrothers.addView(row);
		}
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
		
		users = new String[numResults];
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
	
	public void onRestRequestComplete(Methods method, JSONObject result) 
	{
		if(method == Methods.serviceReport)
		{
			if(result != null)
			{
				try {
					String requestStatus = result.getString("requestStatus");
					if(requestStatus.compareTo("success") == 0)
					{
						Toast msg = Toast.makeText(getApplicationContext(), "Service Report successfully submitted!", Toast.LENGTH_LONG);
						msg.show();
						finish();
					}
					else if(requestStatus.compareTo("timestamp invalid") == 0)
					{
						Toast msg = Toast.makeText(getApplicationContext(), "Invalid timestamp.  Please try again.", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(requestStatus.compareTo("HMAC invalid") == 0)
					{
						Auth.loggedIn = false;
						Toast msg = Toast.makeText(getApplicationContext(), "You have been logged out by the server.  Please log in again.", Toast.LENGTH_LONG);
						msg.show();
						finish();
					}
					else if(requestStatus.compareTo("invalid date") == 0)
					{
						Toast msg = Toast.makeText(getApplicationContext(), "Invalid date entered.  Please contact webmaster if problem persists", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(requestStatus.compareTo("missing project name") == 0)
					{
						Toast msg = Toast.makeText(getApplicationContext(), "Missing project name.  Please contact webmaster if problem persists", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(requestStatus.compareTo("missing location") == 0)
					{
						Toast msg = Toast.makeText(getApplicationContext(), "Missing project location.  Please contact webmaster if problem persists", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(requestStatus.compareTo("inside or outside") == 0)
					{
						Toast msg = Toast.makeText(getApplicationContext(), "Missing inside or outside project information.  Please contact webmaster if problem persists", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(requestStatus.compareTo("on or off campus") == 0)
					{
						Toast msg = Toast.makeText(getApplicationContext(), "Mission on or off campus information.  Please contact webmaster if problem persists", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(requestStatus.compareTo("missing service type") == 0)
					{
						Toast msg = Toast.makeText(getApplicationContext(), "Missing service type information.  Please contact webmaster if problem persists", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(requestStatus.compareTo("no brothers added") == 0)
					{
						Toast msg = Toast.makeText(getApplicationContext(), "No brothers added to service report.  Please contact webmaster if problem persists", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(requestStatus.compareTo("missing travel time") == 0)
					{
						Toast msg = Toast.makeText(getApplicationContext(), "Missing Travel Time.  Please contact webmaster if problem persists", Toast.LENGTH_LONG);
						msg.show();
					}
					else if(requestStatus.compareTo("invalid date") == 0)
					{
						Toast msg = Toast.makeText(getApplicationContext(), "Invalid date entered.  Please contact webmaster if problem persists", Toast.LENGTH_LONG);
						msg.show();
					}
					else
					{
						Toast msg = Toast.makeText(getApplicationContext(), "Invalid requestStatus", Toast.LENGTH_LONG);
						msg.show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				Toast msg = Toast.makeText(getApplicationContext(), "No feedback recieved from server.  Please contact webmaster if problem persists", Toast.LENGTH_LONG);
				msg.show();
				finish();
			}
		}
		
	}

	public void onFocusChange(View v, boolean hasFocus) 
    {
        if (v.getId() == R.id.txtName && !hasFocus) 
        {
        	String text = ((AutoCompleteTextView)v).getText().toString();
        	if (text.length() < 3) 		//specifies minimum length for a valid string, this is done because the substring in the following line cannot be done if the string is too short
        	{
        		((AutoCompleteTextView)v).setText("");
        		return;
        	}
        	text = text.substring(0, text.length()-2);
            if(isValid(text))
            {
            	((AutoCompleteTextView)v).setText(text);
            }
            else
            	((AutoCompleteTextView)v).setText("");
        }
    }
    
    public boolean isValid(CharSequence text) 
    {
        Arrays.sort(users);
        if (Arrays.binarySearch(users, text.toString()) > 0) 
        {
            return true;
        }
        return false;
    }
    
    public void onCheckedChanged(RadioGroup group, int checkedId) 
	{
		if(group.getId() == R.id.groupOnOffCampus)
		{
			if(checkedId == radOn.getId())
			{
				txtTravelTime.setText("");
				txtTravelTime.setEnabled(false);
				chkDriver.setEnabled(false);
			}
			else if (checkedId == radOff.getId())
			{
				txtTravelTime.setText("0");
				txtTravelTime.setEnabled(true);
				chkDriver.setEnabled(true);
			}
		}
	}

    public String getId(String brother)
	{
		int begin = brother.indexOf('[');
		int end = brother.indexOf(']');
		if (begin == -1 || end == -1)
			return null;
		else
			return brother.substring(begin+1, end);
	}
	
	public void onClick(View v) 
	{
		if (v.getId() == R.id.btnAdd)
		{
			txtMinutes.requestFocus();
			if (txtName.getText().toString().compareTo("")==0)
			{
				Toast.makeText(this, "No name inserted.", Toast.LENGTH_SHORT).show();
				return;
			}
			else if((Integer.parseInt(txtHours.getText().toString())==0) && (Integer.parseInt(txtMinutes.getText().toString())==0))
			{
				Toast.makeText(this, "Time must be greater than 0 minutes.", Toast.LENGTH_SHORT).show();
				return;
			}
			String brother = getId(txtName.getText().toString());
			if (brother == null)
			{
				Toast.makeText(this, "Invalid name entered.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			boolean driver;
			if (chkDriver.isChecked())
				driver=true;
			else
				driver=false;
			Brother bro = new Brother(txtName.getText().toString(), getId(txtName.getText().toString()), Integer.parseInt(txtHours.getText().toString()), Integer.parseInt(txtMinutes.getText().toString()), driver);
			brothers.add(bro);
			
			loadBrothers();
		}
		else if (v.getId() == R.id.btnSubmit)
		{
			if (txtProjName.getText().toString().compareTo("") == 0)
			{
				Toast.makeText(this, "Project Name cannot be empty.", Toast.LENGTH_SHORT).show();
				return;
			}
			else if (txtProjLoc.getText().toString().compareTo("") == 0)
			{
				Toast.makeText(this, "Project Location cannot be empty.", Toast.LENGTH_SHORT).show();
				return;
			}
			else if (brothers.size() == 0)
			{
				Toast.makeText(this, "No brothers in service report.", Toast.LENGTH_SHORT).show();
				return;
			}
			try 
			{
				JSONObject json = new JSONObject();
				
				for (int x = 0; x < brothers.size(); x++)
				{
					JSONObject jsonBro = new JSONObject();
					jsonBro.put("uid", brothers.get(x).id);
					jsonBro.put("hrs", brothers.get(x).hours);
					jsonBro.put("min", brothers.get(x).minutes);
					if (brothers.get(x).driver)
						jsonBro.put("driver", 1);
					else
						jsonBro.put("driver", 0);
					
					jsonBro.put("min", brothers.get(x).minutes);
					if (brothers.get(x).driver)
						jsonBro.put("driver", 1);
					else
						jsonBro.put("driver", 0);
					json.put(x+"", jsonBro);
				}
				
				String[] params = new String[9];
				
				String month, day;
				if (datePicker.getMonth() < 10)
					month = "0" + datePicker.getMonth();
				else
					month = "" + datePicker.getMonth();
				if (datePicker.getDayOfMonth() < 10)
					day = "0" + datePicker.getDayOfMonth();
				else
					day = "" + datePicker.getDayOfMonth();
				params[0] = datePicker.getYear() + "-" + month + "-" + day;
				params[1] = txtProjName.getText().toString();
				params[2] = txtProjLoc.getText().toString();
				if (radIn.isChecked())
					params[3] = "in";
				else
					params[3] = "out";
				if (radOn.isChecked())
					params[4] = "on";
				else
					params[4] = "off";
				if (radService1.isChecked())
					params[5] = "0";
				else if (radService2.isChecked())
					params[5] = "1";
				else if (radService3.isChecked())
					params[5] = "2";
				else
					params[5] = "3";
				if (txtTravelTime.getText().toString().compareTo("") == 0)
					params[6] = "0";
				else
					params[6] = "" + Integer.parseInt(txtTravelTime.getText().toString());
				params[7] = txtComments.getText().toString();
				params[8] = "" + brothers.size();
				
				API api = new API(this);
				if(!api.callMethod(Methods.serviceReport, this, json, params))
				{
					Toast msg = Toast.makeText(getApplicationContext(), "Error: Calling getContract", Toast.LENGTH_LONG);
					msg.show();
				}
			} 
			catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			Button remove = (Button)v;
			if (remove.getText().toString().compareTo("X")!=0)
				return;
			LinearLayout row = (LinearLayout) remove.getParent();
			int count = layoutBrothers.getChildCount();
			int match = -1;
			for (int x = 1; x <= count; x++)						//start with index 1 because index 0 is the header
			{
				if(layoutBrothers.getChildAt(x) == row)
				{
					match = x;
					break;
				}
			}
			if (match == -1)										//no match occurred, this should never happen but if it does, it just returns to exit the method
				return;
			else
			{
				brothers.remove(match-1);							//'-1' accounts for the header throwing off the indices
				loadBrothers();
			}
		}
	}
    
    private class Brother
    {
    	String name, id;
    	int hours, minutes;
    	boolean driver;
    	
    	public Brother(String name, String id, int hours, int minutes, boolean driver)
    	{
    		this.name = name;
    		this.id = id;
    		this.hours = hours;
    		this.minutes = minutes;
    		this.driver = driver;
    	}
    }
}
