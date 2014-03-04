package com.libratech.mia;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.libratech.mia.models.Product;
import com.libratech.mia.models.Scanned;

public class HomeActivity extends Activity implements iRibbonMenuCallback {

	private RibbonMenuView rbmView;
	protected GestureDetector gest;
	DatabaseConnector db = new DatabaseConnector();
	boolean listReady = false;
	ListView listview;
	TextView tv;
	Button bS, bU;
	ProgressBar pb;
	int numScanned, numUnscanned;
	boolean all = true;
	boolean scanned = false;
	boolean unscanned = false;
	boolean done = false;

	public static ArrayList<Product> aProducts = new ArrayList<Product>();
	ArrayList<Scanned> uProducts = new ArrayList<Scanned>();
	public static ArrayList<Scanned> sProducts = new ArrayList<Scanned>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		numScanned = numUnscanned = 0;
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		listview = (ListView) findViewById(R.id.mainlistview);
		listview.setOnItemClickListener(new OnItemClickListener() {
			// @Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Bundle b = new Bundle();
				Scanned p = (Scanned) arg0.getItemAtPosition(arg2);
				String[] product = { p.getUpcCode(), p.getProductName(),
						p.getBrand(), String.valueOf(p.getPrice()),
						p.getWeight(), p.getUom(), p.getGct() };
				b.putBoolean("scanned", p.getScanned());
				b.putStringArray("product", product);
				b.putString("parent", "com.libratech.mia.HomeActivity");
				Log.d("product", product[0] + product[1] + product[2]
						+ product[3]);
				if (!p.getScanned()) {
					startActivity(new Intent(HomeActivity.this,
							ViewProductActivity.class).putExtras(b));
				} else {
					startActivity(new Intent(HomeActivity.this,
							UpdateProductActivity.class).putExtras(b));
				}
			}
		});
		listview.setOnTouchListener(new OnSwipeTouchListener(this) {
			public void onSwipeRight() {
				rbmView.toggleMenu();
				Toast.makeText(HomeActivity.this, "right", Toast.LENGTH_SHORT)
						.show();
			}
		});
		tv = (TextView) findViewById(R.id.ScanProgressText);
		bS = (Button) findViewById(R.id.scannedbutton);
		bU = (Button) findViewById(R.id.unscannedbutton);
		pb = (ProgressBar) findViewById(R.id.scanProgressBar);

		bS.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				listview.setAdapter(new HomeAdapter(HomeActivity.this,
						sProducts));
				bU.setBackgroundColor(bS.getSolidColor());
				bS.setBackgroundColor(bS.getHighlightColor());
			}

		});

		bU.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				listview.setAdapter(new HomeAdapter(HomeActivity.this,
						uProducts));
				bS.setBackgroundColor(bU.getSolidColor());
				// bA.setBackgroundColor(bU.getSolidColor());
				bU.setBackgroundColor(bU.getHighlightColor());
			}
		});
		new getProducts()
				.execute("http://holycrosschurchjm.com/MIA_mysql.php?allproducts=yes");
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	class getProducts extends AsyncTask<String, Void, JSONArray> {
		protected JSONArray doInBackground(String... url) {
			return db.DBPull(url[0]);
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			String upc, name, desc, brand, category, uom, gct, photo, weight;
			name = desc = brand = category = uom = gct = photo = upc = weight = "";
			String message = "Nothing";
			float price = (float) 0.00;
			if (result==null)
				result=new JSONArray();
			for (int i = 0; i < result.length(); i++) {
				try {
					upc = result.getJSONArray(i).getString(0);
				//	Log.d("UPC from DB", upc);
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
				//	Log.d("weight from DB", weight);
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
				if (all) {
					aProducts.add(new Product(upc, weight, name, desc, brand,
							category, uom, price, gct, photo));
					message = "All Products";
				} else if (scanned) {
					try {
						price = (float) result.getJSONArray(i).getDouble(7);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						gct = result.getJSONArray(i).getString(8);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						photo = result.getJSONArray(i).getString(9);// price
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					sProducts.add(new Scanned(upc, weight, name, desc, brand,
							category, uom, price, gct, photo, true));
					message = "Scanned Products";
				} else if (unscanned) {
					uProducts.add(new Scanned(upc, weight, name, desc, brand,
							category, uom, price, gct, photo, false));
					message = "Unscanned Products";
				}
			}
			Toast.makeText(HomeActivity.this, message + " Loaded.",
					Toast.LENGTH_SHORT).show();
			if (all) {
				all = false;
				scanned = true;
				new getProducts()
						.execute("http://holycrosschurchjm.com/MIA_mysql.php?comp_id=COMP-00001&rec_date=2013-11-01&scannedproducts=yes");
			} else if (scanned) {
				scanned = false;
				unscanned = true;
				new getProducts()
						.execute("http://holycrosschurchjm.com/MIA_mysql.php?comp_id=COMP-00001&rec_date=2013-11-01&unscannedproducts=yes");
			} else if (unscanned) {
				unscanned = false;
				all = true;
				done = true;
				numScanned = sProducts.size();
				listview.setAdapter(new HomeAdapter(HomeActivity.this,
						sProducts));
				bS.callOnClick();
				pb.setMax(sProducts.size() + uProducts.size());
				pb.setProgress(sProducts.size());
				tv.setText("" + sProducts.size() + "\\"
						+ (sProducts.size() + uProducts.size())
						+ " items scanned.");
			}
		}
	}

	public void RibbonMenuItemClick(int itemId, int position) {
		// TODO Auto-generated method stub
		String classes[] = { "HomeActivity", "ScanActivity",
				"AllProductsActivity", "FeedbackActivity" };
		if (position != 0) {
			try {
				startActivity(new Intent(HomeActivity.this,
						Class.forName("com.libratech.mia." + classes[position])));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (done) {
			done = false;
			unscanned = false;
			all = true;
			aProducts.clear();
			sProducts.clear();
			uProducts.clear();
			new getProducts()
					.execute("http://holycrosschurchjm.com/MIA_mysql.php?allproducts=yes");
		}
	}
}
