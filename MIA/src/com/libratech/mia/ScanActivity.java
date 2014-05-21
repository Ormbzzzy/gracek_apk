package com.libratech.mia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;

public class ScanActivity extends CaptureActivity {

	@Override
	public void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		EasyTracker.getInstance(this).activityStart(this);
		super.onCreate(icicle);
		setContentView(R.layout.capture);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		EasyTracker.getInstance(this).activityStop(this);
		super.onPause();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void handleDecode(Result rawResult, Bitmap barcode) {
		String scan = rawResult.getText().toString();
		Bundle b = new Bundle();
		b.putString("code", scan);
		if (getIntent().hasExtra("parent"))
			b.putString("parent", getIntent().getStringExtra("parent"));
		try {
			if (getIntent().hasExtra("parent")) {
				if (getIntent().getStringExtra("parent").equals("AddProduct")) {
					setResult(RESULT_OK, new Intent().putExtras(b));
					finish();
				} else {
					startActivity(new Intent(
							ScanActivity.this,
							Class.forName("com.libratech.mia.UpdateProductActivity"))
							.putExtras(b));
					finish();
				}
			} else {
				startActivity(new Intent(
						ScanActivity.this,
						Class.forName("com.libratech.mia.UpdateProductActivity"))
						.putExtras(b));
				finish();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}