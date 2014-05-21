package com.libratech.mia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.libratech.mia.models.Product;
import com.libratech.mia.models.Scanned;

public class AllProductsActivity extends Activity implements
		iRibbonMenuCallback {
	private RibbonMenuView rbmView;
	EditText search;
	ExpandableListView listview;
	ArrayList<Product> products = HomeActivity.aProducts;
	ArrayList<Scanned> scanned = HomeActivity.sProducts;
	boolean updated = false;
	boolean nSort, bSort, cSort;
	TextView name, brand;
	AllAdapter ad = new AllAdapter(this, products);
	String compId = HomeActivity.storeID;
	String dateString = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all);
		Date date = new Date();
		dateString = new SimpleDateFormat("yyyy-MM-dd").format(date);
		nSort = bSort = cSort = true;
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		if (getIntent().hasExtra("parent")
				&& getIntent().getStringExtra("parent").equalsIgnoreCase(
						"StoreReviewActivity")) {
			rbmView.setMenuItems(R.menu.manager_menu);
		} else {
			rbmView.setMenuItems(R.menu.home);
		}
		search = (EditText) findViewById(R.id.inputSearch);
		listview = (ExpandableListView) findViewById(R.id.alllistview);
		listview.setFastScrollEnabled(true);
		listview.setAdapter(ad);
		listview.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				boolean found = false;
				Bundle b = new Bundle();
				Scanned s = null;
				Product p = ((AllAdapter) listview.getExpandableListAdapter())
						.getProduct(groupPosition, childPosition);
				for (int i = 0; i < scanned.size(); i++) {
					s = scanned.get(i);
					if (s.getUpcCode().equals(p.getUpcCode())) {
						p.setPrice(s.getPrice());
						p.setGct(s.getGct());
						String[] product = { p.getUpcCode(),
								p.getProductName(), p.getBrand(),
								String.valueOf(p.getPrice()), p.getWeight(),
								p.getUom(), p.getGct(), p.getCategory() };
						b.putStringArray("product", product);
						b.putString("mode", "update");
						b.putString("parent",
								getIntent().getStringExtra("parent"));
						startActivityForResult(new Intent(
								AllProductsActivity.this,
								UpdateProductActivity.class).putExtras(b), 1);
						found = true;
						break;
					}
				}
				if (!found) {
					b.putString("mode", "view");
					String[] product = { p.getUpcCode(), p.getProductName(),
							p.getBrand(), String.valueOf(p.getPrice()),
							p.getWeight(), p.getUom(), p.getGct(),
							p.getCategory() };
					b.putStringArray("product", product);
					b.putString("parent", getIntent().getStringExtra("parent"));
					startActivityForResult(new Intent(AllProductsActivity.this,
							UpdateProductActivity.class).putExtras(b), 1);
				}
				return false;
			}
		});

		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				((AllAdapter) listview.getExpandableListAdapter())
						.filterData(cs.toString());
			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

		});
		if (products == null || products.size() == 0) {
			new getProducts()
					.execute("http://holycrosschurchjm.com/MIA_mysql.php?allproducts=yes");
		}
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	class getProducts extends AsyncTask<String, Void, JSONArray> {
		protected JSONArray doInBackground(String... url) {
			return new DatabaseConnector().dbPull(url[0]);
		}

		@Override
		protected void onPreExecute() {

			Toast.makeText(AllProductsActivity.this, "Loading products.",
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
				products.add(new Product(upc, weight, name, desc, brand,
						category, uom, price, gct, photo));

			}
			Toast.makeText(AllProductsActivity.this, "Products loaded.",
					Toast.LENGTH_SHORT).show();
			updated = true;
			listview.setAdapter(new AllAdapter(AllProductsActivity.this,
					products));
		}
	}

	class updateScanned extends AsyncTask<String, Void, JSONArray> {
		protected JSONArray doInBackground(String... url) {
			return new DatabaseConnector().dbPull(url[0]);
		}

		@Override
		protected void onPreExecute() {

			Toast.makeText(AllProductsActivity.this, "Loading products.",
					Toast.LENGTH_SHORT).show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			String upc, name, desc, brand, category, uom, gct, photo, weight;
			name = desc = brand = category = uom = gct = photo = upc = weight = "";
			float price = (float) 0.00;
			if (result == null)
				result = new JSONArray();
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
					price = (float) result.getJSONArray(i).getDouble(7);
					gct = result.getJSONArray(i).getString(8);
					photo = result.getJSONArray(i).getString(9);// price
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				scanned.add(new Scanned(upc, weight, name, desc, brand,
						category, uom, price, gct, photo, true));
			}
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

			// case R.id.name:
			// if (nSort) {
			// nSort = false;
			// Collections.sort(products, Product.ascNameComparator);
			// } else {
			// nSort = true;
			// Collections.sort(products, Product.desNameComparator);
			// }
			// ad = new AllAdapter(AllProductsActivity.this, products);
			// listview.setAdapter(new AllAdapter(AllProductsActivity.this,
			// new ArrayList<Product>()));
			// listview.setAdapter(ad);
			// break;
			//
			// case R.id.brand:
			// if (bSort) {
			// bSort = false;
			// Collections.sort(products, Product.ascBrandComparator);
			// } else {
			// bSort = true;
			// Collections.sort(products, Product.desBrandComparator);
			// }
			// ad = new AllAdapter(AllProductsActivity.this, products);
			// listview.setAdapter(new AllAdapter(AllProductsActivity.this,
			// new ArrayList<Product>()));
			// listview.setAdapter(ad);
			// break;
			//
			// case R.id.category:
			// if (cSort) {
			// cSort = false;
			// Collections.sort(products, Product.ascCatComparator);
			// } else {
			// cSort = true;
			// Collections.sort(products, Product.desCatComparator);
			// }
			// ad = new AllAdapter(AllProductsActivity.this, products);
			// listview.setAdapter(ad);
			// break;

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
		// TODO Auto-generated method stub
		Bundle b = new Bundle();
		Intent i = new Intent();
		switch (itemId) {
		case R.id.HomeActivity:
			i = new Intent(this, HomeActivity.class);
			break;
		case R.id.AllProducts:
			i = new Intent(this, AllProductsActivity.class);
			break;
		case R.id.ScanItemActivity:
			i = new Intent(this, ScanActivity.class);
			break;
		// case R.id.Feedback:
		// i = new Intent(this, FeedbackActivity.class);
		// break;
		case R.id.StoreReviewActivity:
			i = new Intent(this, StoreReviewActivity.class);
			break;
		 case R.id.delProduct:
		 i = new Intent(this, DeleteProduct.class);
		 break;
		case R.id.addUser:
			i = new Intent(this, AddUser.class);
			break;
		case R.id.addProduct:
			i = new Intent(this, AddProduct.class);
			break;
		// case R.id.delUser:
		// i = new Intent(this, DeleteUser.class);
		// break;

		default:
			break;
		}
		b.putString("parent", getIntent().getStringExtra("parent"));
		i.putExtras(b);
		startActivityForResult(i, 1);
	}

	@Override
	protected void onPause() {

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

		String text = search.getText().toString();
		search.setText("");
		search.setText(text);
		if (updated) {
			updated = !updated;
			if (isConnected()) {
				products.clear();
				new getProducts()
						.execute("http://holycrosschurchjm.com/MIA_mysql.php?allproducts=yes");
			} else {
				Toast.makeText(getApplicationContext(),
						"Please check your connection.", Toast.LENGTH_SHORT)
						.show();
			}
		}
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (data.hasExtra("updated")) {
				setResult(RESULT_OK, new Intent().putExtra("updated", true));
				if (isConnected()) {
					scanned.clear();
					new updateScanned()
							.execute("http://holycrosschurchjm.com/MIA_mysql.php?comp_id="
									+ compId
									+ "&rec_date="
									+ dateString
									+ "&scannedproducts=yes");
				} else {
					Toast.makeText(getApplicationContext(),
							"Please check your connection.", Toast.LENGTH_SHORT)
							.show();
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.allprod_actionbar, menu);
		return true;
	}
}