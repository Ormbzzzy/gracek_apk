package com.libratech.mia;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;

public class ViewProductActivity extends Activity implements
		iRibbonMenuCallback {

	EditText price;
	TextView brand, name;
	RibbonMenuView rbmView;
	Button scan, confirm;
	DatabaseConnector db = new DatabaseConnector();
	String gct;
	CheckBox gctBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		scan = (Button) findViewById(R.id.scan);
		confirm = (Button) findViewById(R.id.confirm);
		brand = (TextView) findViewById(R.id.Brand);
		name = (TextView) findViewById(R.id.Name);
		price = (EditText) findViewById(R.id.Price);
		gctBox = (CheckBox) findViewById(R.id.gct);
		if (gctBox.isChecked())
			gct = "yes";
		else
			gct = "no";
		if (getIntent().hasExtra("product")) {
			name.setText(getIntent().getStringArrayExtra("product")[0]);
			brand.setText(getIntent().getStringArrayExtra("product")[1]);
			price.setText(getIntent().getStringArrayExtra("product")[2]);
		}
		gctBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (gctBox.isChecked())
					gct = "yes";
				else
					gct = "no";
			}

		});
		scan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					Bundle b = new Bundle();
					// String[] product = { name.getText(), brand.getText(),
					// price.getText() };
					// b.putStringArray("product", product);
					b.putString("parent",
							"com.libratech.mia.ViewProductActivity");
					startActivity(new Intent(ViewProductActivity.this, Class
							.forName("com.libratech.mia.ScanActivity"))
							.putExtras(b));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String product[] = { "00000000000", "MER-00001", "COMP-00001",
						"2013-11-01 11:30:28", price.getText().toString(), gct };
				new pushProduct().execute(product);
			}

		});
	}

	class pushProduct extends AsyncTask<String, Void, Boolean> {
		protected Boolean doInBackground(String... fields) {

			return db.DBPush(fields);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			String message;
			if (result) {
				message = "Product updated.";
			} else {
				message = "Error while updating product.";
			}
			Toast.makeText(ViewProductActivity.this, message,
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void RibbonMenuItemClick(int itemId, int position) {
		// TODO Auto-generated method stub

		String classes[] = { "HomeActivity", "ScanItemActivity",
				"FeedbackActivity" };
		if (position != 0) {
			try {
				startActivity(new Intent(ViewProductActivity.this,
						Class.forName("com.libratech.mia." + classes[position])));
			} catch (Exception e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}
		}
	}

}
