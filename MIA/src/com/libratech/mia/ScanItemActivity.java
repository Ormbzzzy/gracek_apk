package com.libratech.mia;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.zxing.client.android.CaptureActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class ScanItemActivity extends CaptureActivity implements
		iRibbonMenuCallback {

	RibbonMenuView rbmView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_scan);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
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
	// @Override
	// public void handleDecode(Result rawResult, Bitmap barcode) {
	// Result lastResult = rawResult;
	// String scan = lastResult.getText().toString();
	// final Bundle b = new Bundle();
	// Log.d("code", scan);
	// if (scan.split(":")[0].equals("ARPK")) {
	// b.putString("code", scan.split(":")[1]);
	// b.putString("paperID", getIntent().getStringExtra("paperID"));
	// b.putString("empID", getIntent().getStringExtra("empID"));
	// b.putStringArray("delivINFO",
	// getIntent().getStringArrayExtra("delivINFO"));
	// b.putString("driver", getIntent().getStringExtra("driver"));
	// b.putString("delivID", getIntent().getStringExtra("delivID"));
	// b.putStringArray("IDarray",
	// getIntent().getStringArrayExtra("IDarray"));
	// Intent login = new Intent(ScannerActivity.this, VendorLogin.class);
	// login.putExtras(b);
	// MainActivityTab.thisMap.finish();
	// startActivity(login);
	// } else {
	// new AlertDialog.Builder(this)
	// .setTitle("Invalid QR Code")
	// .setMessage(
	// "The code you have scanned is not a valid Company QR code.")
	// .setPositiveButton("Rescan",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog,
	// int which) {
	// b.putString("paperID", getIntent()
	// .getStringExtra("paperID"));
	// b.putString("empID", getIntent()
	// .getStringExtra("empID"));
	// b.putStringArray("delivINFO", getIntent()
	// .getStringArrayExtra("delivINFO"));
	// b.putString("driver", getIntent()
	// .getStringExtra("driver"));
	// b.putString("delivID", getIntent()
	// .getStringExtra("delivID"));
	// b.putStringArray("IDarray", getIntent()
	// .getStringArrayExtra("IDarray"));
	// Intent reScan = new Intent(
	// ScannerActivity.this,
	// ScannerActivity.class);
	// reScan.putExtras(b);
	// startActivity(reScan);
	// }
	// })
	// .setNegativeButton("Cancel Delivery",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog,
	// int which) {
	// finish();
	// }
	// }).show();
	// }
	// }
}
