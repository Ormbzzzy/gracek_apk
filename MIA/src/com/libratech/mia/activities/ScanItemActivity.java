package com.libratech.mia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.libratech.mia.R;
public class ScanItemActivity extends Activity implements iRibbonMenuCallback {

	RibbonMenuView rbmView;
	Button scanButton;
	TextView barcode, brand, price, name;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_scan);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		scanButton = (Button) findViewById(R.id.scanButton);
		barcode = (TextView) findViewById(R.id.barcodeText);
		brand = (TextView) findViewById(R.id.itemDesc);
		name = (TextView) findViewById(R.id.ScanitemName);
		price = (TextView) findViewById(R.id.ScanitemPrice);
		if (getIntent().hasExtra("product")) {
			name.setText(getIntent().getStringArrayExtra("product")[0]);
			brand.setText(getIntent().getStringArrayExtra("product")[1]);
			price.setText(getIntent().getStringArrayExtra("product")[2]);
		}
		if (getIntent().hasExtra("code"))
			barcode.setText(getIntent().getStringExtra("code"));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		scanButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				try {
					startActivity(new Intent(ScanItemActivity.this, Class
							.forName("com.libratech.mia.ScanActivity")));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == android.R.id.home) {

			rbmView.toggleMenu();

			return true;

		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	public void RibbonMenuItemClick(int itemId, int position) {
		
		String classes[] = { "HomeActivity", "ScanActivity",
				"AllProductsActivity", "FeedbackActivity" };
		if (position != 0) {
			try {
				startActivity(new Intent(ScanItemActivity.this,
						Class.forName("com.libratech.mia." + classes[position])));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
