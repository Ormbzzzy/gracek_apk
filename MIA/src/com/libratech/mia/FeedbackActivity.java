package com.libratech.mia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.models.Feedback;
import com.libratech.mia.models.Product;
import com.libratech.mia.models.Scanned;

public class FeedbackActivity extends Activity implements iRibbonMenuCallback {

	RibbonMenuView rbmView;
	View lv, dv;
	TextView name, brand, upc;
	ImageButton scan;
	DatePicker dp;
	ListView list;
	CheckBox urgent;
	Button cancel, confirm, xDate, cDate;
	String urg = "";
	String empId = HomeActivity.empID;
	String compID = HomeActivity.storeID;
	Button dpb, add;
	Dialog dg;
	Calendar c = Calendar.getInstance();
	int day, month, year;
	ArrayList<Feedback> fb = new ArrayList<Feedback>();
	ArrayList<Product> aProd = HomeActivity.aProducts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		day = month = year = 0;
		lv = (View) findViewById(R.id.fbList);
		dv = (View) findViewById(R.id.fbDetails);
		dv.setVisibility(View.VISIBLE);
		lv.setVisibility(View.VISIBLE);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		dg = new Dialog(FeedbackActivity.this);
		dg.setContentView(R.layout.date_picker);
		dg.setTitle("Select Date");
		dg.setCanceledOnTouchOutside(true);
		add = (Button) lv.findViewById(R.id.addFB);
		list = (ListView) lv.findViewById(R.id.feedbackList);
		name = (TextView) dv.findViewById(R.id.Name);
		brand = (TextView) dv.findViewById(R.id.Brand);
		upc = (TextView) dv.findViewById(R.id.upc);
		scan = (ImageButton) dv.findViewById(R.id.feedbackScan);
		dp = (DatePicker) dg.findViewById(R.id.sampleDate);
		dpb = (Button) findViewById(R.id.dpButton);
		urgent = (CheckBox) findViewById(R.id.urgent);
		xDate = (Button) dg.findViewById(R.id.cancelDate);
		cDate = (Button) dg.findViewById(R.id.confirmDate);
		cancel = (Button) dv.findViewById(R.id.cancel);
		confirm = (Button) dv.findViewById(R.id.sendFB);
		dpb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dg.show();
			}

		});
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				lv.setVisibility(View.GONE);
				dv.setVisibility(View.VISIBLE);
			}

		});
		scan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle b = new Bundle();
				b.putString("parent", "FeedbackActivity");
				Intent i = new Intent(FeedbackActivity.this, ScanActivity.class);
				i.putExtras(b);
				startActivityForResult(i, 1);
			}
		});
		urgent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (urgent.isChecked()) {
					urg = "yes";
				} else {
					urg = "no";
				}
			}

		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (cancel.getText().equals("Delete")) {

				} else {
					finish();
				}

			}

		});
		cDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				day = dp.getDayOfMonth();
				month = dp.getMonth();
				year = dp.getYear();
				c.set(day, month, year);
				dg.cancel();
			}

		});
		xDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dg.cancel();
			}

		});
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (day != 0 && !brand.getText().equals("Brand")) {
					if (urg.equals("")) {
						if (!urgent.isChecked())
							urg = "no";
					}
					String dateString = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss").format(new Date());
					String expString = new SimpleDateFormat("yyyy-MM-dd")
							.format(c.getTime());
					new SendFB()
							.execute(("http://holycrosschurchjm.com/MIA_mysql.php?addFeedback=yes&merch_id="
									+ empId
									+ "&upc_code="
									+ upc.getText()
									+ "&comp_id="
									+ compID
									+ "&rec_date="
									+ dateString
									+ "&expiry_date="
									+ expString
									+ "&urgent="
									+ urg
									+ "&photo="
									+ upc.getText() + ".jpg").replace(" ",
									"%20"));
				} else {
					Toast.makeText(getApplicationContext(),
							"Please enter a valid date and product",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date());
		String expString = new SimpleDateFormat("yyyy-MM-dd").format(c
				.getTime());
		new getFB()
				.execute(("http://holycrosschurchjm.com/MIA_mysql.php?expiryFeedback=yes&comp_id="
						+ compID + "&rec_date=" + dateString).replace(" ",
						"%20"));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private class SendFB extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... url) {
			return new DatabaseConnector().DBPush(url[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			Toast.makeText(getApplicationContext(),
					"Expiration feedback submitted.", Toast.LENGTH_SHORT)
					.show();
			finish();
		}
	}

	private class getFB extends AsyncTask<String, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(String... url) {
			return new DatabaseConnector().dbPull(url[0]);
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			String upc, name, brand, date, urg;
			name = brand = date = urg = upc = "";
			float price = (float) 0.00;
			if (result == null)
				result = new JSONArray();
			for (int i = 0; i < result.length(); i++) {
				try {
					upc = result.getJSONArray(i).getString(0);
					name = result.getJSONArray(i).getString(1);
					brand = result.getJSONArray(i).getString(2);
					date = result.getJSONArray(i).getString(3);
					urg = result.getJSONArray(i).getString(4);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				fb.add(new Feedback(upc, name, brand, date, urg));
				list.setAdapter(new FeedbackAdapter(getApplicationContext(), fb));
			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (dp.getVisibility() == View.VISIBLE) {
			dp.setVisibility(View.GONE);
		} else {
			if (dv.getVisibility() == View.VISIBLE) {
				dv.setVisibility(View.GONE);
				lv.setVisibility(View.VISIBLE);

			}
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			rbmView.toggleMenu();
			return true;

		case R.id.logout:
			EasyTracker.getInstance(this).activityStop(this);
			Intent i = new Intent(getApplicationContext(), LoginActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void RibbonMenuItemClick(int itemId, int position) {

		ActivityControl.changeActivity(this, itemId, "HomewActivity");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		finish();
		super.onPause();
	}

}
