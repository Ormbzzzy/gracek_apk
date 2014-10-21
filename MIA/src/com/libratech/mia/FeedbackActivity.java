package com.libratech.mia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.models.Product;

public class FeedbackActivity extends Activity implements iRibbonMenuCallback {

	RibbonMenuView rbmView;
	TextView name, brand, upc;
	ImageButton scan;
	DatePicker dp;
	CheckBox urgent;
	Button cancel, confirm, xDate, cDate;
	String urg = "";
	String empId = HomeActivity.empID;
	String compID = HomeActivity.storeID;
	Button dpb;
	Dialog dg;
	Calendar c = Calendar.getInstance();
	int day, month, year;
	ArrayList<Product> aProd = HomeActivity.aProducts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		day = month = year = 0;
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		dg = new Dialog(FeedbackActivity.this);
		dg.setContentView(R.layout.date_picker);
		dg.setTitle("Select Date");
		dg.setCanceledOnTouchOutside(true);
		name = (TextView) findViewById(R.id.Name);
		brand = (TextView) findViewById(R.id.Brand);
		upc = (TextView) findViewById(R.id.upc);
		scan = (ImageButton) findViewById(R.id.feedbackScan);
		dp = (DatePicker) dg.findViewById(R.id.sampleDate);
		dpb = (Button) findViewById(R.id.dpButton);
		urgent = (CheckBox) findViewById(R.id.urgent);
		xDate = (Button) dg.findViewById(R.id.cancelDate);
		cDate = (Button) dg.findViewById(R.id.confirmDate);
		cancel = (Button) findViewById(R.id.cancel);
		confirm = (Button) findViewById(R.id.sendFB);
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
				finish();
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (dp.getVisibility() == View.VISIBLE) {
			dp.setVisibility(View.GONE);
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

		ActivityControl.changeActivity(this, itemId, position, rbmView,
				"HomewActivity");
	}

	// @Override
	// public void RibbonMenuItemClick(int itemId, int position) {
	// Bundle b = new Bundle();
	// Intent i = new Intent();
	// switch (itemId) {
	// case R.id.HomeActivity:
	// rbmView.toggleMenu();
	// break;
	// case R.id.AllProducts:
	// i = new Intent(this, AllProductsActivity.class);
	// b.putString("parent", "HomeActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// case R.id.ScanItemActivity:
	// i = new Intent(this, ScanActivity.class);
	// b.putString("parent", "HomeActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// case R.id.Feedback:
	// rbmView.toggleMenu();
	// break;
	// case R.id.StoreReviewActivity:
	// i = new Intent(this, StoreReviewActivity.class);
	// b.putString("parent", "HomeActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// // case R.id.delProduct:
	// // i = new Intent(this, DeleteProduct.class);
	// // b.putString("parent", "HomeActivity");
	// // i.putExtras(b);
	// // startActivityForResult(i, 1);
	// // break;
	// case R.id.addUser:
	// i = new Intent(this, AddUser.class);
	// b.putString("parent", "HomeActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// case R.id.addProduct:
	// i = new Intent(this, AddProduct.class);
	// b.putString("parent", "HomeActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// // case R.id.delUser:
	// // i = new Intent(this, DeleteUser.class);
	// // b.putString("parent", "HomeActivity");
	// // i.putExtras(b);
	// // startActivityForResult(i, 1);
	// // break;
	// case R.id.AddBanded:
	// i = new Intent(this, AddBandedOffer.class);
	// // rbmView.toggleMenu();
	// b.putString("parent", "HomeActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// case R.id.AddDiscount:
	// i = new Intent(this, AddDiscountProductActivity.class);
	// // rbmView.toggleMenu();
	// b.putString("parent", "HomeActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// default:
	// break;
	// }
	// }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		finish();
		super.onPause();
	}

}
