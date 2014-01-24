package com.libratech.mia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;

public class ScanActivity extends CaptureActivity {

	@Override
	public void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.capture);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

	@Override
	public void handleDecode(Result rawResult, Bitmap barcode) {
		String scan = rawResult.getText().toString();
		final Bundle b = new Bundle();
		b.putString("code", scan);
		Intent toScan = new Intent(ScanActivity.this, ScanItemActivity.class);
		toScan.putExtras(b);
		startActivity(toScan);

	}
}