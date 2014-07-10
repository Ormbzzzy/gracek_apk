package com.libratech.mia;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class AddUser extends Activity implements OnItemSelectedListener{
	
	ArrayAdapter<CharSequence> roleAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Spinner roleSpinner = (Spinner) findViewById(R.id.userRole);
		roleAdapter = ArrayAdapter.createFromResource(this,R.array.userRoles_array, 
						android.R.layout.simple_spinner_item);
		roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		roleSpinner.setAdapter(roleAdapter);
		roleSpinner.setOnItemSelectedListener(this);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_user);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.add_user, menu);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}
