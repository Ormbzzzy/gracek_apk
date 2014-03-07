package com.libratech.mia;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
	ListView listview;
	ArrayList<Product> products = HomeActivity.aProducts;
	ArrayList<Scanned> scanned = HomeActivity.sProducts;
	boolean updated = false;
	boolean nSort, bSort, cSort;
	TextView name, brand, category;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all);
		nSort = bSort = cSort = true;
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		name = (TextView) findViewById(R.id.allNameHead);
		brand = (TextView) findViewById(R.id.allBrandHead);
		category = (TextView) findViewById(R.id.allCatHead);
		search = (EditText) findViewById(R.id.inputSearch);
		name.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundColor(getResources().getColor(
							R.color.SkyBlue));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					v.setBackgroundColor(listview.getSolidColor());
				}
				return false;
			}
		});
		brand.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundColor(getResources().getColor(
							R.color.SkyBlue));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					v.setBackgroundColor(listview.getSolidColor());
				}
				return false;
			}
		});
		category.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundColor(getResources().getColor(
							R.color.SkyBlue));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					v.setBackgroundColor(listview.getSolidColor());
				}
				return false;
			}
		});
		name.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (nSort) {
					nSort = false;
					Collections.sort(products, Product.ascNameComparator);
				} else {
					nSort = true;
					Collections.sort(products, Product.desNameComparator);
				}
				listview.setAdapter(new AllAdapter(AllProductsActivity.this,
						products));
			}
		});
		brand.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (bSort) {
					bSort = false;
					Collections.sort(products, Product.ascBrandComparator);
				} else {
					bSort = true;
					Collections.sort(products, Product.desBrandComparator);
				}
				listview.setAdapter(new AllAdapter(AllProductsActivity.this,
						products));
			}
		});
		category.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (cSort) {
					cSort = false;
					Collections.sort(products, Product.ascCatComparator);
				} else {
					cSort = true;
					Collections.sort(products, Product.desCatComparator);
				}
				listview.setAdapter(new AllAdapter(AllProductsActivity.this,
						products));
			}
		});
		listview = (ListView) findViewById(R.id.alllistview);
		listview.setAdapter(new AllAdapter(this, products));
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				boolean found = false;
				Bundle b = new Bundle();
				Scanned s = null;
				Product p = (Product) arg0.getItemAtPosition(arg2);
				for (int i = 0; i < scanned.size(); i++) {
					s = scanned.get(i);
					if (s.getUpcCode().equals(p.getUpcCode())) {
						p.setPrice(s.getPrice());
						p.setGct(s.getGct());
						String[] product = { p.getUpcCode(),
								p.getProductName(), p.getBrand(),
								String.valueOf(p.getPrice()), p.getWeight(),
								p.getUom(), p.getGct() };
						b.putStringArray("product", product);
						b.putString("parent",
								"com.libratech.mia.AllProductsActivity");
						Log.d("product", product[0] + product[1] + product[2]
								+ product[3] + product[4]);
						startActivity(new Intent(AllProductsActivity.this,
								UpdateProductActivity.class).putExtras(b));
						found = true;
						break;
					}
				}
				if (!found) {
					String[] product = { p.getUpcCode(), p.getProductName(),
							p.getBrand(), String.valueOf(p.getPrice()),
							p.getWeight(), p.getUom(), p.getGct() };
					b.putStringArray("product", product);
					b.putString("parent",
							"com.libratech.mia.AllProductsActivity");
					Log.d("product", product[0] + product[1] + product[2]
							+ product[3] + product[4]);
					startActivity(new Intent(AllProductsActivity.this,
							ViewProductActivity.class).putExtras(b));
				}
			}
		});
		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text
				ArrayList<Product> filter = new ArrayList<Product>();
				for (int i = 0; i < products.size(); i++) {
					if (products.get(i).getBrand().toLowerCase()
							.contains(cs.toString().toLowerCase())
							|| products.get(i).getProductName().toLowerCase()
									.contains(cs.toString().toLowerCase())
							|| products.get(i).getCategory()
									.contains(cs.toString().toLowerCase()))
						filter.add(products.get(i));
				}
				listview.setAdapter(new AllAdapter(AllProductsActivity.this,
						filter));
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

		});
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	class getProducts extends AsyncTask<String, Void, JSONArray> {
		protected JSONArray doInBackground(String... url) {
			return new DatabaseConnector().DBPull(url[0]);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			Toast.makeText(AllProductsActivity.this, "Updating products.",
					Toast.LENGTH_SHORT).show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			String upc, name, desc, brand, category, uom, gct, photo, weight;
			name = desc = brand = category = uom = gct = photo = upc = weight = "";
			float price = (float) 0.00;
			products.clear();
			for (int i = 0; i < result.length(); i++) {
				try {
					upc = result.getJSONArray(i).getString(0);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					name = result.getJSONArray(i).getString(1);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					desc = result.getJSONArray(i).getString(2);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					brand = result.getJSONArray(i).getString(3);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					category = result.getJSONArray(i).getString(4);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					weight = result.getJSONArray(i).getString(5);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					uom = result.getJSONArray(i).getString(6);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					photo = result.getJSONArray(i).getString(7);// price
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				products.add(new Product(upc, weight, name, desc, brand,
						category, uom, price, gct, photo));
				Toast.makeText(AllProductsActivity.this, "Products Updated",
						Toast.LENGTH_SHORT).show();
				updated = true;
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			rbmView.toggleMenu();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void RibbonMenuItemClick(int itemId, int position) {
		// TODO Auto-generated method stub
		String classes[] = { "HomeActivity", "ScanActivity",
				"AllProductsActivity", "FeedbackActivity" };
		if (position != 2) {
			try {
				startActivity(new Intent(AllProductsActivity.this,
						Class.forName("com.libratech.mia." + classes[position])));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		String text = search.getText().toString();
		search.setText("");
		search.setText(text);
		if (updated) {
			updated = !updated;
			products = new ArrayList<Product>();
			new getProducts()
					.execute("http://holycrosschurchjm.com/MIA_mysql.php?allproducts=yes");
		}
	}

}