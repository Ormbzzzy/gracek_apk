package com.libratech.mia;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
	float downX = 0;
	int numScanned, numUnscanned;
	boolean all = true;
	boolean scanned = false;
	boolean unscanned = false;
	boolean done = false;

	View.OnTouchListener gestureListener;

	public static ArrayList<Product> aProducts = new ArrayList<Product>();
	ArrayList<Scanned> uProducts = new ArrayList<Scanned>();
	public static ArrayList<Scanned> sProducts = new ArrayList<Scanned>();
	HomeAdapter sAdapter = new HomeAdapter(this, sProducts);
	HomeAdapter uAdapter = new HomeAdapter(this, uProducts);

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
						p.getWeight(), p.getUom(), p.getGct(), p.getCategory() };
				b.putBoolean("scanned", p.getScanned());
				b.putStringArray("product", product);
				b.putString("parent", "com.libratech.mia.HomeActivity");
				if (!p.getScanned()) {
					startActivityForResult(new Intent(HomeActivity.this,
							ViewProductActivity.class).putExtras(b), 1);
				} else {
					startActivityForResult(new Intent(HomeActivity.this,
							UpdateProductActivity.class).putExtras(b), 1);
				}
			}
		});
		listview.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_UP) {
					try {
						((HomeAdapter) listview.getAdapter())
								.areAllItemsEnabled(true);
						return false;
					} catch (Exception e) {
						e.printStackTrace();
					}
					return false;
				}
				final int HORIZONTAL_MIN_DISTANCE = 300;
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					downX = event.getX();
				}
				float upX;
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					downX = event.getX();
					return false;
				}
				case MotionEvent.ACTION_MOVE: {
					upX = event.getX();
					float deltaX = downX - upX;
					if (Math.abs(deltaX) > HORIZONTAL_MIN_DISTANCE) {
						if (deltaX < 0) {
							bS.performClick();
							return true;
						}
						if (deltaX > 0) {
							bU.performClick();
							return true;
						}
						return false;
					}
				}
				}
				return false;
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
				listview.setAdapter(sAdapter);
				bU.setBackgroundColor(bS.getSolidColor());
				bS.setBackgroundColor(bS.getHighlightColor());
			}

		});

		bU.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				listview.setAdapter(uAdapter);
				bS.setBackgroundColor(bU.getSolidColor());
				// bA.setBackgroundColor(bU.getSolidColor());
				bU.setBackgroundColor(bU.getHighlightColor());
			}
		});
		if (isConnected()) {
			aProducts.clear();
			new getProducts()
					.execute("http://holycrosschurchjm.com/MIA_mysql.php?allproducts=yes");
		} else {
			Toast.makeText(
					HomeActivity.this,
					"No network connection, please check your connection and reload the application",
					Toast.LENGTH_LONG).show();
		}
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	class getProducts extends AsyncTask<String, Void, JSONArray> {
		protected JSONArray doInBackground(String... url) {
			return db.DBPull(url[0]);
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
					// Log.d("UPC from DB", upc);
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
					// Log.d("weight from DB", weight);
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
				} else if (unscanned) {
					uProducts.add(new Scanned(upc, weight, name, desc, brand,
							category, uom, price, gct, photo, false));
				}
			}

			if (all) {
				sProducts.clear();
				all = false;
				scanned = true;
				new getProducts()
						.execute("http://holycrosschurchjm.com/MIA_mysql.php?comp_id=COMP-00001&rec_date=2013-11-01&scannedproducts=yes");
				sAdapter.notifyDataSetChanged();
			} else if (scanned) {
				uProducts.clear();
				scanned = false;
				unscanned = true;
				new getProducts()
						.execute("http://holycrosschurchjm.com/MIA_mysql.php?comp_id=COMP-00001&rec_date=2013-11-01&unscannedproducts=yes");
				uAdapter.notifyDataSetChanged();
			} else if (unscanned) {
				unscanned = false;
				all = true;
				done = true;
				numScanned = sProducts.size();
				listview.setAdapter(sAdapter);
				bS.callOnClick();
				pb.setMax(sProducts.size() + uProducts.size());
				pb.setProgress(sProducts.size());
				tv.setText("" + sProducts.size() + "\\"
						+ (sProducts.size() + uProducts.size())
						+ " items scanned.");
				if (aProducts.size() == 0) {
					Toast.makeText(HomeActivity.this,
							"No products were found.", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(HomeActivity.this,
							"All products have been loaded.",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	public void RibbonMenuItemClick(int itemId, int position) {
		// TODO Auto-generated method stub
		String classes[] = { "HomeActivity", "ScanActivity",
				"AllProductsActivity", "FeedbackActivity" };
		if (position != 0) {
			try {
				if (classes[position].equalsIgnoreCase("homeactivity")) {
					Bundle b = new Bundle();
					b.putString("parent", "AllProductsActivity");
					startActivity(new Intent(HomeActivity.this,
							Class.forName("com.libratech.mia."
									+ classes[position])));
				} else {
					startActivity(new Intent(HomeActivity.this,
							Class.forName("com.libratech.mia."
									+ classes[position])));
				}
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

	// @Override
	// protected void onResume() {
	// // TODO Auto-generated method stub
	// super.onResume();
	// if (done) {
	// done = false;
	// if (isConnected()) {
	// aProducts.clear();
	// new getProducts()
	// .execute("http://holycrosschurchjm.com/MIA_mysql.php?allproducts=yes");
	// } else {
	// Toast.makeText(getApplicationContext(),
	// "Please check your connection.", Toast.LENGTH_SHORT)
	// .show();
	// ;
	// }
	// }
	// }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (data.hasExtra("updated")) {
				if (isConnected()) {
					all = true;
					aProducts.clear();
					new getProducts()
							.execute("http://holycrosschurchjm.com/MIA_mysql.php?allproducts=yes");
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
}
