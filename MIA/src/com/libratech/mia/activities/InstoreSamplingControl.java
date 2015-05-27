package com.libratech.mia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import com.libratech.mia.R;
public class InstoreSamplingControl extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instore_sampling_control);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.instore_sampling_control, menu);
		return true;
	}
	
	public void addSampleProdNav(View view)
	{
		/* All products list to select product/s should be before this screen*/
		Intent intent = new Intent(this, InStoreSampling.class);	    
	    startActivity(intent); 
	}
	
	public void updateSampleProdNav(View view)
	{
		/*We need the listing of the sampled products before this screen.
		Just here for navigational flow right now */
		Intent intent = new Intent(this, InStoreSampling.class);	    
	    startActivity(intent); 
	}
	
	public void deleteSampleProdNav(View view)
	{
		/*We need the listing of the sampled products before this screen.
		Just here for navigational flow right now */
		
		Intent intent = new Intent(this, DeleteInStoreSample.class);	    
	    startActivity(intent); 
	}
	
	public void viewAllSampleProdsNav(View view)
	{
		/*We need the listing of the sampled products before this screen.
		Just here for navigational flow right now */
	}

}
