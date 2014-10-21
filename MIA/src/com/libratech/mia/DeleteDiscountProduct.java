package com.libratech.mia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.libratech.mia.models.DiscountedProduct;
import com.libratech.mia.models.Product;
import com.libratech.mia.models.Scanned;

public class DeleteDiscountProduct extends Activity implements
		iRibbonMenuCallback {

	View details, discounts;
	ImageView img;
	TextView name, brand, upc, weight, uom, value, sp;
	Product p;
	Button delete, cancel;
	ImageButton add;
	RibbonMenuView rbmView;
	ArrayList<Product> products = HomeActivity.aProducts;
	ArrayList<Scanned> scanned = HomeActivity.sProducts;
	ArrayList<DiscountedProduct> list = new ArrayList<DiscountedProduct>();
	ListView dList;
	String compID = HomeActivity.storeID;
	String empID = HomeActivity.empID;
	Scanned s;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_discount_product);
		details = (View) findViewById(R.id.delDiscDetail);
		discounts = (View) findViewById(R.id.discounts);
		discounts.setVisibility(View.VISIBLE);
		upc = (TextView) details.findViewById(R.id.upc);
		dList = (ListView) discounts.findViewById(R.id.delDiscounts);
		name = (TextView) details.findViewById(R.id.Name);
		brand = (TextView) details.findViewById(R.id.Brand);
		weight = (TextView) details.findViewById(R.id.weight);
		uom = (TextView) details.findViewById(R.id.uom);
		value = (TextView) details.findViewById(R.id.Value);
		sp = (TextView) details.findViewById(R.id.type);
		cancel = (Button) details.findViewById(R.id.cancel);
		delete = (Button) details.findViewById(R.id.addProd);
		details.setVisibility(View.GONE);
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Date date = new Date();
				String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(date);
				new DelDiscount()
						.execute(("http://holycrosschurchjm.com/MIA_mysql.php?deleteDiscountedProduct=yes&comp_id="
								+ compID
								+ "&rec_date="
								+ dateString
								+ "&upc_code=" + upc.getText()).replace(" ",
								"%20"));
			}
		});
		Date date = new Date();
		String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(date);
		new getDiscounts()
				.execute(("http://holycrosschurchjm.com/MIA_mysql.php?discountedProducts=yes&comp_id="
						+ compID + "&rec_date=" + dateString).replace(" ",
						"%20"));
		getActionBar().setHomeButtonEnabled(true);
	}

	class DelDiscount extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... url) {
			new DatabaseConnector().DBPush(url[0]);
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Toast.makeText(getApplicationContext(), "Discount added",
					Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_discount_product, menu);

		return true;
	}

	class getDiscounts extends AsyncTask<String, Void, JSONArray> {
		protected JSONArray doInBackground(String... url) {
			return new DatabaseConnector().dbPull(url[0]);
		}

		@Override
		protected void onPreExecute() {

			Toast.makeText(getApplicationContext(), "Loading products.",
					Toast.LENGTH_SHORT).show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			String upc, name, brand, weight, uom, photo, value, type;
			name = brand = value = uom = type = photo = upc = weight = "";
			float price = (float) 0.00;
			for (int i = 0; i < result.length(); i++) {
				try {
					upc = result.getJSONArray(i).getString(0);
					name = result.getJSONArray(i).getString(1);
					brand = result.getJSONArray(i).getString(2);
					weight = result.getJSONArray(i).getString(3);
					uom = result.getJSONArray(i).getString(4);
					value = result.getJSONArray(i).getString(5);
					type = result.getJSONArray(i).getString(6);
					photo = result.getJSONArray(i).getString(7);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				list.add(new DiscountedProduct(upc, name, brand, weight, uom,
						value, type, photo));
			}
			dList.setAdapter(new DiscountAdapter(list, getApplicationContext()));
			Toast.makeText(getApplicationContext(), "Products loaded.",
					Toast.LENGTH_SHORT).show();
		}
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
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void RibbonMenuItemClick(int itemId, int position) {

		ActivityControl.changeActivity(this, itemId, position, rbmView,
				"HomeActivity");
	}
}