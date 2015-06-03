package com.libratech.mia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.R;
import com.libratech.mia.utilities.ActivityControl;

public class Promotions extends Activity implements iRibbonMenuCallback {

	Button newProd, discount, sample, banded;
	RibbonMenuView rbmView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_promotions);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);

		newProd = (Button) findViewById(R.id.newProducts);
		discount = (Button) findViewById(R.id.discountedProducts);
		sample = (Button) findViewById(R.id.instoreSampling);
		banded = (Button) findViewById(R.id.bandedProducts);
		newProd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				startActivity(new Intent(getApplicationContext(),
						ViewNewProduct.class));
				// ActivityControl.changeActivity(Promotions.this, R.id.newProd,
				// getIntent().getStringExtra("parent"));
			}

		});
		discount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				startActivity(new Intent(getApplicationContext(),
						AddDiscountProductActivity.class));
				// ActivityControl.changeActivity(Promotions.this,
				// R.id.AddDiscount, getIntent().getStringExtra("parent"));
			}

		});
		sample.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				startActivity(new Intent(getApplicationContext(),
						InStoreSampling.class));
				// ActivityControl.changeActivity(Promotions.this, R.id.Sample,
				// getIntent().getStringExtra("parent"));
			}

		});
		banded.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				startActivity(new Intent(getApplicationContext(),
						AddBandedOffer.class));
				// ActivityControl.changeActivity(Promotions.this, R.id.Sample,
				// getIntent().getStringExtra("parent"));
			}

		});
	}

	public void newProductNav(View view) {
		Intent intent = new Intent(this, NewProductsControl.class);
		startActivity(intent);
	}

	public void discProductNav(View view) {
		Intent intent = new Intent(this, DiscountedProductsControl.class);
		startActivity(intent);
	}

	public void bandedProductNav(View view) {
		Intent intent = new Intent(this, BandedProductsControl.class);
		startActivity(intent);
	}

	public void instoreSampleNav(View view) {
		Intent intent = new Intent(this, InstoreSamplingControl.class);
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
			rbmView.toggleMenu();

			return true;

		case R.id.logout:
			
			Intent i = new Intent(getApplicationContext(), LoginActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.logout_with_change, menu);
		return true;
	}

	@Override
	protected void onPause() {
		
		finish();
		super.onPause();
	}

	@Override
	public void RibbonMenuItemClick(int itemId, int position) {
		
		ActivityControl.changeActivity(this, itemId, getIntent()
				.getStringExtra("parent"));
	}

}
