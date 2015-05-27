package com.libratech.mia.activities;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.adapters.BandedAdapter;
import com.libratech.mia.adapters.BandedOfferAdapter;
import com.libratech.mia.models.BandedProduct;
import com.libratech.mia.models.Product;
import com.libratech.mia.utilities.ActivityControl;
import com.libratech.mia.utilities.DatabaseConnector;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.libratech.mia.R;
public class ViewBanded extends Activity implements iRibbonMenuCallback {

	RibbonMenuView rbmView;
	View list, details, bandedList;
	ListView lv, blv;
	TextView name, price, weight, brand, uom, upc;
	EditText bPrice;
	ArrayList<Product> bandedProd = new ArrayList<Product>();
	ArrayList<Product> allProd = HomeActivity.aProducts;
	ArrayList<BandedProduct> bList = new ArrayList<BandedProduct>();
	ArrayList<BandedProduct> temp = new ArrayList<BandedProduct>();
	BandedProduct currentBanded = new BandedProduct();
	Button remove, submit, addB;
	Product p = new Product();;
	String compID = HomeActivity.storeID;
	String empID = HomeActivity.empID;
	BandedAdapter adapter;
	BandedProduct bp = new BandedProduct();
	Builder delDG;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_banded_offer);
		list = (View) findViewById(R.id.bandedListView);
		lv = (ListView) list.findViewById(R.id.bandedList);
		bPrice = (EditText) list.findViewById(R.id.bandedPrice);
		bPrice.setFocusable(false);
		submit = (Button) list.findViewById(R.id.submit);
		submit.setActivated(false);
		details = (View) findViewById(R.id.bandedDetail);
		bandedList = (View) findViewById(R.id.bandedOffers);
		blv = (ListView) bandedList.findViewById(R.id.banded);
		blv.setFocusable(true);
		name = (TextView) details.findViewById(R.id.name);
		brand = (TextView) details.findViewById(R.id.brand);
		weight = (TextView) details.findViewById(R.id.weight);
		upc = (TextView) details.findViewById(R.id.upc);
		uom = (TextView) details.findViewById(R.id.uom);
		remove = (Button) details.findViewById(R.id.cancel);
		remove.setActivated(false);
		lv.setAdapter(adapter);
		lv.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				list.setVisibility(View.GONE);
				details.setVisibility(View.VISIBLE);
				Product p = currentBanded.getProducts().get(position);
				name.setText(p.getProductName());
				upc.setText(p.getUpcCode());
				uom.setText(p.getUom());
				weight.setText(p.getWeight());
				brand.setText(p.getBrand());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				

			}
		});
		remove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				for (Product p : allProd) {
					if (p.getUpcCode().equals(upc.getText())) {
						currentBanded.getProducts().remove(p);
						adapter.notifyDataSetChanged();
						details.setVisibility(View.GONE);
						list.setVisibility(View.VISIBLE);
					}
				}
			}
		});
		blv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				submit.setText("Update");
				currentBanded = bList.get(position);
				bPrice.setText("" + currentBanded.getTotalPrice());
				bandedList.setVisibility(View.GONE);
				list.setVisibility(View.VISIBLE);
			}
		});
		lv.setAdapter(new BandedAdapter(this, bandedProd));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				list.setVisibility(View.GONE);
				details.setVisibility(View.VISIBLE);
				p = currentBanded.getProducts().get(position);
				name.setText(p.getProductName());
				brand.setText(p.getBrand());
				uom.setText(p.getUom());
				p = null;
			}
		});
		Date date = new Date();
		String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(date);
		new getBandedProducts()
				.execute((DatabaseConnector.getDomain()+"/MIA_mysql.php?bandedProducts=yes&comp_id="
						+ compID + "&rec_date=" + dateString).replace(" ",
						"%20"));
		getActionBar().setHomeButtonEnabled(true);
	}

	class getBandedProducts extends AsyncTask<String, Void, JSONArray> {
		protected JSONArray doInBackground(String... url) {
			return new DatabaseConnector().dbPull(url[0]);
		}

		@Override
		protected void onPreExecute() {

			Toast.makeText(ViewBanded.this, "Loading banded products.",
					Toast.LENGTH_SHORT).show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			String prods, name, desc, brand, category, uom, gct, photo, weight;
			name = desc = brand = category = uom = gct = photo = prods = weight = "";
			float price = (float) 0.00;
			bList.clear();
			try {
				for (int i = 0; i < result.length(); i++) {
					BandedProduct b = new BandedProduct();
					price = Float.parseFloat(result.getJSONArray(i)
							.getString(1));
					prods = result.getJSONArray(i).getString(0);
					for (String upc : prods.split("[-]")) {
						for (Product p : allProd) {
							if (p.getUpcCode().equals(upc))
								b.getProducts().add(p);
						}
					}
					b.setBandedID(prods);
					b.settotalPrice(price);
					bList.add(b);
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			blv.setAdapter(new BandedOfferAdapter(getApplicationContext(),
					bList));
			Toast.makeText(ViewBanded.this, "Banded products loaded.",
					Toast.LENGTH_SHORT).show();

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
			rbmView.toggleMenu();
			return true;

		case R.id.logout:
			EasyTracker.getInstance(this).activityStop(this);
			Intent i = new Intent(getApplicationContext(), LoginActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void RibbonMenuItemClick(int itemId, int position) {

		ActivityControl.changeActivity(this, itemId, "HomeActivity");
	}

}