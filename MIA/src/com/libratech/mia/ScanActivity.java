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
		while (scan.charAt(0) == '0') {
			scan = scan.replaceFirst("0", "");
		}
		b.putString("code", scan);
		try {
			b.putString("parent", getIntent().getStringExtra("parent"));
			startActivity(new Intent(ScanActivity.this,
					Class.forName("com.libratech.mia.ViewProductActivity"))
					.putExtras(b));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}