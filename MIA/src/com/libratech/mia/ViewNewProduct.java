package com.libratech.mia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.models.Product;

public class ViewNewProduct extends Activity implements iRibbonMenuCallback {
	private RibbonMenuView rbmView;
	ListView listview;
	View lv, details;
	ArrayList<Product> products = new ArrayList<Product>();
	boolean updated = false;
	ImageView img;
	boolean nSort, bSort, cSort;
	TextView name, brand, upc, uom, weight;
	String compId = HomeActivity.storeID;
	String dateString = "";
	Button cancel, delete;
	Builder dg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_new_product);
		Date date = new Date();
		details = (View) findViewById(R.id.newDetails);
		lv = (View) findViewById(R.id.newList);
		details.setVisibility(View.GONE);
		img = (ImageView) details.findViewById(R.id.updateImage);
		upc = (TextView) details.findViewById(R.id.upc);
		brand = (TextView) details.findViewById(R.id.Brand);
		name = (TextView) details.findViewById(R.id.Name);
		weight = (TextView) details.findViewById(R.id.weight);
		uom = (TextView) details.findViewById(R.id.uom);
		cancel = (Button) details.findViewById(R.id.cancel);
		delete = (Button) details.findViewById(R.id.delete);
		dateString = new SimpleDateFormat("yyyy-MM-dd").format(date);
		nSort = bSort = cSort = true;
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		dg = new AlertDialog.Builder(this);
		dg.setTitle("Are you sure?");
		dg.setMessage("Doing this will permanently remove all data related to this store from the database!");
		dg.setPositiveButton("Yes, I'm Sure.",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						new Delete()
								.execute("http://holycrosschurchjm.com/MIA_mysql.php?deleteStore=yes&comp_id="
										+ compId);
					}
				});
		dg.setNegativeButton("Cancel", null);
		if (getIntent().hasExtra("parent")
				&& getIntent().getStringExtra("parent").equalsIgnoreCase(
						"StoreReviewActivity")) {
			rbmView.setMenuItems(R.menu.manager_menu);
		} else {
			rbmView.setMenuItems(R.menu.home);
		}
		listview = (ListView) lv.findViewById(R.id.alllistview);
		listview.setFastScrollEnabled(true);
		listview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				lv.setVisibility(View.GONE);
				details.setVisibility(View.VISIBLE);
				Product p = products.get(position);
				upc.setText(p.getUpcCode());
				name.setText(p.getProductName());
				brand.setText(p.getBrand());
				weight.setText(p.getWeight());
				uom.setText(p.getUom());
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				details.setVisibility(View.GONE);
				lv.setVisibility(View.VISIBLE);
			}

		});

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				details.setVisibility(View.GONE);
				lv.setVisibility(View.VISIBLE);
			}

		});
		if (products == null || products.size() == 0) {
			new getProducts()
					.execute("http://holycrosschurchjm.com/MIA_mysql.php?newProducts=yes&comp_id="
							+ compId + "&rec_date=" + dateString);
		}
		EasyTracker.getInstance(this).activityStart(this);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	class Delete extends AsyncTask<String, Void, Boolean> {
		protected Boolean doInBackground(String... url) {
			return new DatabaseConnector().DBPush(url[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Toast.makeText(getApplicationContext(), "Item deleted",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "An error occured.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	class getProducts extends AsyncTask<String, Void, JSONArray> {
		protected JSONArray doInBackground(String... url) {
			return new DatabaseConnector().dbPull(url[0]);
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			String upc, name, desc, brand, category, uom, gct, photo, weight;
			name = desc = brand = category = uom = gct = photo = upc = weight = "";
			float price = (float) 0.00;
			for (int i = 0; i < result.length(); i++) {
				try {
					upc = result.getJSONArray(i).getString(0);
					name = result.getJSONArray(i).getString(1);
					desc = result.getJSONArray(i).getString(2);
					brand = result.getJSONArray(i).getString(3);
					category = result.getJSONArray(i).getString(4);
					weight = result.getJSONArray(i).getString(5);
					uom = result.getJSONArray(i).getString(6);
					photo = result.getJSONArray(i).getString(7);// price
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				products.add(new Product(upc, weight, name, desc, brand,
						category, uom, price, gct, photo));

			}
			Toast.makeText(ViewNewProduct.this, "Products loaded.",
					Toast.LENGTH_SHORT).show();
			updated = true;
			listview.setAdapter(new NewAdapter(ViewNewProduct.this, products));
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

		ActivityControl.changeActivity(this, itemId, getIntent()
				.getStringExtra("parent"));
	}

	@Override
	protected void onPause() {
		EasyTracker.getInstance(this).activityStop(this);
		super.onPause();
	}

	public boolean isConnected() {
		ConnectivityManager connectivity = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}
		return false;
	}

	@Override
	protected void onResume() {

		if (updated) {
			updated = !updated;
			if (isConnected()) {
				products.clear();
				new getProducts()
						.execute("http://holycrosschurchjm.com/MIA_mysql.php?newProducts=yes&comp_id="
								+ compId + "&rec_date=" + dateString);
			} else {
				Toast.makeText(getApplicationContext(),
						"Please check your connection.", Toast.LENGTH_SHORT)
						.show();
			}
		}
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.allprod_actionbar, menu);
		return true;
	}
}