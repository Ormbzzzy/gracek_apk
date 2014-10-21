package com.libratech.mia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.libratech.mia.models.Product;

public class InStoreSampling extends Activity {

	ImageView img;
	TextView upc, name, weight, brand;
	DatePicker datePick;
	EditText cmt;
	Button cancel, add, dpb;
	Dialog dg;
	ImageButton scan;
	ArrayList<Product> aProd = HomeActivity.aProducts;
	String empID = HomeActivity.empID;
	String compID = HomeActivity.storeID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_in_store_sampling);
		img = (ImageView) findViewById(R.id.updateImage);
		upc = (TextView) findViewById(R.id.upc);
		dg = new Dialog(InStoreSampling.this);
		dg.setContentView(R.layout.date_picker);
		dg.setTitle("Select Date");
		dg.setCanceledOnTouchOutside(true);
		name = (TextView) findViewById(R.id.name);
		scan = (ImageButton) findViewById(R.id.sampleScan);
		brand = (TextView) findViewById(R.id.brand);
		datePick = (DatePicker) dg.findViewById(R.id.DateButton);
		dpb = (Button) findViewById(R.id.dpButton);
		cmt = (EditText) findViewById(R.id.comments);
		cancel = (Button) findViewById(R.id.cancel);
		add = (Button) findViewById(R.id.add);
		dpb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dg.show();
			}

		});

		scan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle b = new Bundle();
				b.putString("parent", "AddProduct");
				Intent i = new Intent(InStoreSampling.this, ScanActivity.class);
				i.putExtras(b);
				startActivityForResult(i, 1);
			}
		});

		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!brand.getText().equals("Brand")) {
					String dateString = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss").format(new Date());
					new SendISS()
							.execute(("http://holycrosschurchjm.com/MIA_mysql.php?addFeedback=yes&merch_id="
									+ empID
									+ "&upc_code="
									+ upc.getText()
									+ "&comments="
									+ cmt.getText()
									+ "&comp_id=" + compID + "&rec_date=" + dateString)
									.replace(" ", "%20"));
				} else {
					Toast.makeText(getApplicationContext(),
							"Please enter a valid product", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private class SendISS extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... url) {
			return new DatabaseConnector().DBPush(url[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			Toast.makeText(getApplicationContext(),
					"In store sample submitted.", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (data.hasExtra("code"))
				upc.setText(data.getStringExtra("code"));
			String s = upc.getText().toString();
			upc.setText("");
			upc.setText(s);
			for (Product p : aProd) {
				if (p.getUpcCode().equals(s)) {
					brand.setText(p.getBrand());
					name.setText(p.getProductName());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		finish();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.in_store_sampling, menu);
		return true;
	}

}