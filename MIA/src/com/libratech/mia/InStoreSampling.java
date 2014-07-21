package com.libratech.mia;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class InStoreSampling extends Activity {

	ImageView img;
	TextView upc, name, weight, uom;
	DatePicker datePick;
	EditText cmt;
	Button cancel, add;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_in_store_sampling);
		img = (ImageView) findViewById(R.id.updateImage);
		upc = (TextView) findViewById(R.id.upc);
		name = (TextView) findViewById(R.id.name);
		uom = (TextView) findViewById(R.id.uom);
		datePick = (DatePicker) findViewById(R.id.sampleDate);
		cmt = (EditText) findViewById(R.id.comments);
		cancel = (Button) findViewById(R.id.cancel);
		add = (Button) findViewById(R.id.add);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.in_store_sampling, menu);
		return true;
	}

}