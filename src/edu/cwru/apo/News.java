package edu.cwru.apo;

import android.app.Activity;
import android.os.Bundle;

// displays most recent news from website
// will have a news ticker with latest twitter updates
public class News extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news);
	}

}
