package com.libratech.mia;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AddStore extends Activity implements OnItemSelectedListener {

	ArrayAdapter<CharSequence> cityAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Spinner citySpinner = (Spinner) findViewById(R.id.citySpinner);
		cityAdapter = ArrayAdapter.createFromResource(this,R.array.capitals_array, 
						android.R.layout.simple_spinner_item);
		cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		citySpinner.setAdapter(cityAdapter);
		citySpinner.setOnItemSelectedListener(this);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_store);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_store, menu);
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
