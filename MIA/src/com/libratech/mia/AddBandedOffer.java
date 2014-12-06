package com.libratech.mia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.models.BandedProduct;
import com.libratech.mia.models.Product;

public class AddBandedOffer extends Activity implements iRibbonMenuCallback {

	RibbonMenuView rbmView;
	View allList, list, details, bandedList;
	ListView lv, blv;
	ExpandableListView exlv;
	Button imgB;
	TextView name, price, weight, brand, uom, upc;
	ArrayList<Product> bandedProd = new ArrayList<Product>();
	ArrayList<Product> allProd = HomeActivity.aProducts;
	ArrayList<BandedProduct> bList = new ArrayList<BandedProduct>();
	ArrayList<BandedProduct> temp = new ArrayList<BandedProduct>();
	BandedProduct currentBanded = new BandedProduct();
	Button remove, submit, addB;
	Product p = new Product();;
	String compID = HomeActivity.storeID;
	String empID = HomeActivity.empID;
	boolean newBanded = false;
	boolean bandedSelected = false;
	boolean bandedItemSelected = false;
	BandedAdapter adapter;
	MenuItem add, del, scan;
	BandedProduct bp = new BandedProduct();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_banded_offer);
		allList = (View) findViewById(R.id.AllbandedListView);
		exlv = (ExpandableListView) allList.findViewById(R.id.AllBandedList);
		list = (View) findViewById(R.id.bandedListView);
		lv = (ListView) list.findViewById(R.id.bandedList);
		imgB = (Button) list.findViewById(R.id.add);
		submit = (Button) list.findViewById(R.id.submit);
		details = (View) findViewById(R.id.bandedDetail);
		bandedList = (View) findViewById(R.id.bandedOffers);
		blv = (ListView) bandedList.findViewById(R.id.banded);
		name = (TextView) details.findViewById(R.id.name);
		brand = (TextView) details.findViewById(R.id.brand);
		upc = (TextView) details.findViewById(R.id.upc);
		uom = (TextView) details.findViewById(R.id.uom);
		remove = (Button) details.findViewById(R.id.cancel);
		lv.setAdapter(adapter);
		exlv.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				p = ((AllAdapter) exlv.getExpandableListAdapter()).getProduct(
						groupPosition, childPosition);
				Toast.makeText(getApplicationContext(), p.getProductName(),
						Toast.LENGTH_SHORT).show();
				allList.setVisibility(View.GONE);
				list.setVisibility(View.VISIBLE);
				if (newBanded) {
					currentBanded.getProducts().add(p);
					adapter = new BandedAdapter(getApplicationContext(),
							currentBanded.getProducts());
					lv.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				} else {
					currentBanded.getProducts().add(p);
					adapter = new BandedAdapter(getApplicationContext(),
							currentBanded.getProducts());
					lv.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				}

				return false;
			}

		});

		lv.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				bandedItemSelected = true;
				invalidateOptionsMenu();
				list.setVisibility(View.GONE);
				details.setVisibility(View.VISIBLE);
				Product p = currentBanded.getProducts().get(position);
				name.setText(p.getProductName());
				upc.setText(p.getUpcCode());
				uom.setText(p.getUom());
				brand.setText(p.getBrand());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		remove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (bandedProd.remove(p)) {
					details.setVisibility(View.GONE);
					list.setVisibility(View.VISIBLE);
				} else {
					details.setVisibility(View.GONE);
					allList.setVisibility(View.VISIBLE);
				}
			}
		});
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder dg = new Builder(getApplicationContext());
				dg.setTitle("Confirm Submission");
				String s = "Confirm bundle of:\n";
				for (Product p : bandedProd) {
					s += p.getProductName() + "\n";
				}
				dg.setMessage(s);
				dg.setPositiveButton("Confirm",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								String s = bandedProd.get(0).getUpcCode();
								for (int i = 1; i < bandedProd.size(); i++) {
									s += "-" + bandedProd.get(i).getUpcCode();
								}
								new PushBanded().execute("");
							}
						});
			}
		});
		blv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				bandedProd = bList.get(position).getProducts();
				lv.setAdapter(new BandedAdapter(AddBandedOffer.this, bandedProd));
				bandedList.setVisibility(View.GONE);
				list.setVisibility(View.VISIBLE);
			}
		});
		lv.setAdapter(new BandedAdapter(this, bandedProd));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				list.setVisibility(View.GONE);
				details.setVisibility(View.VISIBLE);
				p = bandedProd.get(position);
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
				.execute(("http://holycrosschurchjm.com/MIA_mysql.php?bandedProducts=yes&comp_id="
						+ compID + "&rec_date=" + dateString).replace(" ",
						"%20"));
		exlv.setAdapter(new AllAdapter(getApplicationContext(), allProd));
		getActionBar().setHomeButtonEnabled(true);
	}

	class PushBanded extends AsyncTask<String, Void, Boolean> {
		protected Boolean doInBackground(String... url) {
			return new DatabaseConnector().DBPush(url[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

	}

	class getBandedProducts extends AsyncTask<String, Void, JSONArray> {
		protected JSONArray doInBackground(String... url) {
			return new DatabaseConnector().dbPull(url[0]);
		}

		@Override
		protected void onPreExecute() {

			Toast.makeText(AddBandedOffer.this, "Loading banded products.",
					Toast.LENGTH_SHORT).show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			String upc, name, desc, brand, category, uom, gct, photo, weight;
			name = desc = brand = category = uom = gct = photo = upc = weight = "";
			float price = (float) 0.00;
			try {

				for (int i = 0; i < result.length(); i++) {
					BandedProduct b = new BandedProduct();
					for (int j = 0; j < result.getJSONArray(i).length(); j++) {
						price = Float.parseFloat(result.getJSONArray(i)
								.getJSONArray(j).getString(0));
						upc = result.getJSONArray(i).getJSONArray(j)
								.getString(0);
						for (Product p : allProd) {
							if (p.getUpcCode().equals(upc))
								b.getProducts().add(p);
						}
						b.settotalPrice(price);

					}
					bList.add(b);
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			blv.setAdapter(new BandedOfferAdapter(getApplicationContext(),
					bList));
			Toast.makeText(AddBandedOffer.this, "Banded products loaded.",
					Toast.LENGTH_SHORT).show();

		}
	}

	class getAllProducts extends AsyncTask<String, Void, JSONArray> {
		protected JSONArray doInBackground(String... url) {
			return new DatabaseConnector().dbPull(url[0]);
		}

		@Override
		protected void onPreExecute() {

			Toast.makeText(AddBandedOffer.this, "Loading all products.",
					Toast.LENGTH_SHORT).show();
			super.onPreExecute();
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
				allProd.add(new Product(upc, weight, name, desc, brand,
						category, uom, price, gct, photo));

			}
			exlv.setAdapter(new AllAdapter(getApplicationContext(), allProd));
			Toast.makeText(AddBandedOffer.this, "All roducts loaded.",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_banded_offer, menu);
		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		// super.onPrepareOptionsMenu(menu);
		del = menu.findItem(R.id.removeBanded);
		add = menu.findItem(R.id.newBanded);
		scan = menu.findItem(R.id.bandedScan);
		scan.setVisible(false);
		del.setVisible(bandedSelected);
		add.setVisible(!bandedSelected);
		if (newBanded) {
			if (list.getVisibility() == View.VISIBLE) {
				scan.setVisible(false);
				add.setVisible(true);
				del.setVisible(false);
			} else {
				scan.setVisible(true);
				del.setVisible(false);
				add.setVisible(false);
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.newBanded:
			if (newBanded) {
				allList.setVisibility(View.VISIBLE);
			} else {
				newBanded = true;
				invalidateOptionsMenu();
				bandedList.setVisibility(View.GONE);
				list.setVisibility(View.VISIBLE);
			}
			return true;
		case R.id.removeBanded:
			return true;
		case R.id.bandedScan:
			return true;
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