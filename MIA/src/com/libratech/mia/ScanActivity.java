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
		super.onCreate(icicle);
		EasyTracker.getInstance(this).activityStart(this);
		setContentView(R.layout.capture);

	}

	@Override
	public void onPause() {
		super.onPause();
		EasyTracker.getInstance(this).activityStop(this);
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
				}
			} else {
				startActivity(new Intent(
						ScanActivity.this,
						Class.forName("com.libratech.mia.UpdateProductActivity"))
						.putExtras(b));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (data.hasExtra("updated")) {
				Toast.makeText(getApplicationContext(), "passed",
						Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK, new Intent().putExtra("updated", true));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finish();
	}
}