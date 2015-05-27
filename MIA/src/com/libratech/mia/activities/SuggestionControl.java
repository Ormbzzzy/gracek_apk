package com.libratech.mia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import com.libratech.mia.R;
public class SuggestionControl extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggestion_control);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.suggestion_control, menu);
		return true;
	}
	
	public void addSuggestionNav(View view)
	{		
		Intent intent = new Intent(this, SuggestionBox.class);	    
	    startActivity(intent); 
	}
	
	public void updateSuggestionNav(View view)
	{
		/*We need the listing of the suggestions before this screen.
		Just here for navigational flow right now */
		Intent intent = new Intent(this, SuggestionBox.class);	    
	    startActivity(intent); 
	}
	
	public void deleteSuggestionNav(View view)
	{
		/*We need the listing of the suggestions before this screen.
		Just here for navigational flow right now */
		
		Intent intent = new Intent(this, DeleteSuggestionBox.class);	    
	    startActivity(intent); 
	}
	
	public void viewAllSuggestionsNav(View view)
	{
		/*We need the listing of the sampled products before this screen.
		Just here for navigational flow right now */
	}


}
