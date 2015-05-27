package com.libratech.mia.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import com.libratech.mia.R;
public class DeleteInStoreSample extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_in_store_sample);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.delete_in_store_sample, menu);
		return true;
	}

}
