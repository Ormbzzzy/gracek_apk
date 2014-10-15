package com.libratech.mia;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;

public class FeedbackActivity extends Activity implements iRibbonMenuCallback {

	RibbonMenuView rbmView;
	TextView name, brand, upc;
	ImageButton img;
	DatePicker dp;
	CheckBox urgent;
	Button cancel, confirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		name = (TextView) findViewById(R.id.Name);
		brand = (TextView) findViewById(R.id.Brand);
		upc = (TextView) findViewById(R.id.upc);
		img = (ImageButton) findViewById(R.id.feedbackScan);
		dp = (DatePicker) findViewById(R.id.sampleDate);
		urgent = (CheckBox) findViewById(R.id.urgent);
		cancel = (Button) findViewById(R.id.cancel);
		confirm = (Button) findViewById(R.id.sendFB);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}

		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int day, month, year;
				day = dp.getDayOfMonth();
				month = dp.getMonth();
				year = dp.getYear();
				Date d = new Date();
				// new SendFB().execute("");
			}

		});
		getActionBar().setDisplayHomeAsUpEnabled(true);
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
		Bundle b = new Bundle();
		Intent i = new Intent();
		switch (itemId) {
		case R.id.HomeActivity:
			rbmView.toggleMenu();
			break;
		case R.id.AllProducts:
			i = new Intent(this, AllProductsActivity.class);
			b.putString("parent", "HomeActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		case R.id.ScanItemActivity:
			i = new Intent(this, ScanActivity.class);
			b.putString("parent", "HomeActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		case R.id.Feedback:
			rbmView.toggleMenu();
			break;
		case R.id.StoreReviewActivity:
			i = new Intent(this, StoreReviewActivity.class);
			b.putString("parent", "HomeActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		// case R.id.delProduct:
		// i = new Intent(this, DeleteProduct.class);
		// b.putString("parent", "HomeActivity");
		// i.putExtras(b);
		// startActivityForResult(i, 1);
		// break;
		case R.id.addUser:
			i = new Intent(this, AddUser.class);
			b.putString("parent", "HomeActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		case R.id.addProduct:
			i = new Intent(this, AddProduct.class);
			b.putString("parent", "HomeActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		// case R.id.delUser:
		// i = new Intent(this, DeleteUser.class);
		// b.putString("parent", "HomeActivity");
		// i.putExtras(b);
		// startActivityForResult(i, 1);
		// break;
		case R.id.AddBanded:
			i = new Intent(this, AddBandedOffer.class);
			// rbmView.toggleMenu();
			b.putString("parent", "HomeActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
		case R.id.AddDiscount:
			i = new Intent(this, AddDiscountProductActivity.class);
			// rbmView.toggleMenu();
			b.putString("parent", "HomeActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		finish();
		super.onPause();
	}

}
