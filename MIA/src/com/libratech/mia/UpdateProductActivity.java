package com.libratech.mia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;

public class UpdateProductActivity extends Activity implements
		iRibbonMenuCallback {

	EditText price, weight;
	TextView brand, name, upc,uom;
	RibbonMenuView rbmView;
	Button edit, update;
	DatabaseConnector db = new DatabaseConnector();
	String gct;
	CheckBox gctBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		edit = (Button) findViewById(R.id.edit);
		upc = (TextView) findViewById(R.id.upc);
		update = (Button) findViewById(R.id.update);
		brand = (TextView) findViewById(R.id.Brand);
		name = (TextView) findViewById(R.id.Name);
		price = (EditText) findViewById(R.id.Price);
		gctBox = (CheckBox) findViewById(R.id.gct);
		weight = (EditText) findViewById(R.id.weight);
		uom = (TextView) findViewById(R.id.uom);
		if (gctBox.isChecked())
			gct = "yes";
		else
			gct = "no";
		if (getIntent().hasExtra("product")) {
			upc.setText(getIntent().getStringArrayExtra("product")[0]);
			name.setText(getIntent().getStringArrayExtra("product")[1]);
			brand.setText(getIntent().getStringArrayExtra("product")[2]);
			price.setText(getIntent().getStringArrayExtra("product")[3]);
			weight.setText(getIntent().getStringArrayExtra("product")[4]);
			uom.setText(getIntent().getStringArrayExtra("product")[5]);
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
		edit.setOnClickListener(new OnClickListener() {

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
					startActivity(new Intent(UpdateProductActivity.this, Class
							.forName("com.libratech.mia.ScanActivity"))
							.putExtras(b));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				Date date = new Date();
				String dateString = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
						.format(date); // 9:00
				String product[] = { upc.getText().toString(), "MER-00001",
						"COMP-00001", dateString, price.getText().toString(),
						gct };
				Log.d("to DB", product[0] + " " + product[1] + " " + product[2]
						+ " " + product[3] + " " + product[4] + " "
						+ product[5]);
				nameValuePairs.add(new BasicNameValuePair(
						"updateScannedProduct", "yes"));
				nameValuePairs.add(new BasicNameValuePair("upc_code", upc
						.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("merch_id",
						"MER-00001"));
				nameValuePairs.add(new BasicNameValuePair("comp_id",
						"COMP-00001"));
				nameValuePairs.add(new BasicNameValuePair("rec_date",
						"2013-11-01 11:30:28"));
				nameValuePairs.add(new BasicNameValuePair("price", price
						.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("gct", gct));
				// "http://holycrosschurchjm.com/MIA_mysql.php?addScannedProduct=yes&upc_code="+upc.getText().toString()+"&merch_id=MER-00001&comp_id=COMP-00001&rec_date=2014-02-09&price=345.00&gct=yes"
				new pushProduct()
						.execute("http://holycrosschurchjm.com/MIA_mysql.php?updateScannedProduct=yes&upc_code="
								+ upc.getText()
								+ "&merch_id=MER-00001&comp_id=COMP-00001&rec_date=2013-11-01&price="
								+ price.getText() + "&gct=yes");
				// new pushProduct().execute(nameValuePairs);
			}
		});
	}

	class pushProduct extends AsyncTask<String, Void, Boolean> {
		protected Boolean doInBackground(String... fields) {
			return db.DBPush(fields[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			String message;
			if (result) {
				message = "Product updated.";
			} else {
				message = "Error while updating product.";
			}
			Toast.makeText(UpdateProductActivity.this, message,
					Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	public void RibbonMenuItemClick(int itemId, int position) {
		// TODO Auto-generated method stub
		String classes[] = { "HomeActivity", "ScanActivity",
				"FeedbackActivity" };
		if (position != 0) {
			try {
				startActivity(new Intent(UpdateProductActivity.this,
						Class.forName("com.libratech.mia." + classes[position])));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
