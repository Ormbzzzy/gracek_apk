package com.libratech.mia;

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

public class ScanItemActivity extends Activity implements iRibbonMenuCallback {

	RibbonMenuView rbmView;
	Button scanButton;
	TextView barcode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_scan);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		scanButton = (Button) findViewById(R.id.scanButton);
		barcode = (TextView) findViewById(R.id.barcodeText);
		if (getIntent().hasExtra("code"))
			barcode.setText(getIntent().getStringExtra("code"));

		getActionBar().setDisplayHomeAsUpEnabled(true);

		scanButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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

	@Override
	public void RibbonMenuItemClick(int itemId, int position) {
		// TODO Auto-generated method stub
		String classes[] = { "HomeActivity", "ScanItemActivity",
				"FeedbackActivity" };
		if (position != 1) {
			try {
				startActivity(new Intent(ScanItemActivity.this,
						Class.forName("com.libratech.mia." + classes[position])));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

}