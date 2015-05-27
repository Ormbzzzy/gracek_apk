package com.libratech.mia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import com.libratech.mia.R;
public class FeedbackControl extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback_control);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.feedback_control, menu);
		return true;
	}
	
	public void addExpiredProdNav(View view)
	{
		/* All products list to select product/s should be before this screen*/
		Intent intent = new Intent(this, FeedbackActivity.class);	    
	    startActivity(intent); 
	}
	
	public void updateExpiredProdNav(View view)
	{
		/*We need the listing of the sampled products before this screen.
		Just here for navigational flow right now */
		Intent intent = new Intent(this, FeedbackActivity.class);	    
	    startActivity(intent); 
	}
	
	public void deleteExpiredProdNav(View view)
	{
		/*We need the listing of the sampled products before this screen.
		Just here for navigational flow right now */
		
		Intent intent = new Intent(this, FeedbackActivity.class);	    
	    startActivity(intent); 
	}
	
	public void viewAllExpiredProdsNav(View view)
	{
		/*We need the listing of the sampled products before this screen.
		Just here for navigational flow right now */
	}

}
