package edu.cwru.apo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.util.Date;

public class APO extends Activity implements View.OnClickListener{
	Button btn;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btn = new Button(this);
        btn.setOnClickListener(this);
        updateTime();
        setContentView(btn);
        //setContentView(R.layout.main);
    }

	public void onClick(View v) {
		// TODO Auto-generated method stub
		updateTime();
	}
	
	private void updateTime() {
		btn.setText(new Date().toString());
	}
}