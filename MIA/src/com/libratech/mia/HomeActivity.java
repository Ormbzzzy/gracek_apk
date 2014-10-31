package com.libratech.mia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.google.analytics.tracking.android.EasyTracker;

import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.libratech.mia.LoginActivity.getStoreInfo;
import com.libratech.mia.models.Product;
import com.libratech.mia.models.Scanned;
import com.libratech.mia.models.Store;

public class HomeActivity extends Activity implements iRibbonMenuCallback {

	private RibbonMenuView rbmView;
	protected GestureDetector gest;
	DatabaseConnector db = new DatabaseConnector();
	ListView listview;
	TextView tv;
	Button bS, bU;
	ProgressBar pb;
	Spinner sp;
	Dialog dg;
	Button cancel, confirm;

	float downX = 0;
	boolean listReady = false;
	boolean all = true;
	boolean scanned = false;
	boolean unscanned = false;
	boolean done = false;
	String storeName = LoginActivity.storeName;
	public static String storeID = LoginActivity.storeID;
	public static String empID = LoginActivity.empID;
	String dateString = "";
	private ProgressBar listLoad;

	public static ArrayList<Product> aProducts = new ArrayList<Product>();
	public static ArrayList<Scanned> uProducts = new ArrayList<Scanned>();
	public static ArrayList<Scanned> sProducts = new ArrayList<Scanned>();
	public static ArrayList<Store> stores = new ArrayList<Store>();
	public static boolean updated = false;
	HomeAdapter sAdapter = new HomeAdapter(this, sProducts);
	HomeAdapter uAdapter = new HomeAdapter(this, uProducts);
	ArrayAdapter<String> adapter;
	String user = LoginActivity.empID;
	ArrayList<String> spinList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		dg = new Dialog(HomeActivity.this);
		dg.setContentView(R.layout.dialog);
		dg.setTitle("Select Store");
		dg.setCanceledOnTouchOutside(false);
		adapter = new SpinnerAdapter(HomeActivity.this,
				android.R.layout.simple_spinner_item, spinList);
		sp = (MySpinner) dg.findViewById(R.id.storeSpinner);
		confirm = (Button) dg.findViewById(R.id.spinnerButton);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!storeID.equals("")) {
					for (Store s : stores) {
						if (s.getStoreID().equals(storeID))
							storeName = s.getCompanyName();
					}
					LoginActivity.storeID = storeID;
					LoginActivity.storeName = storeName;
					aProducts.clear();
					sAdapter = uAdapter = new HomeAdapter(HomeActivity.this,
							new ArrayList<Scanned>());
					sAdapter.notifyDataSetChanged();
					uAdapter.notifyDataSetChanged();
					listLoad.setVisibility(View.VISIBLE);
					new getProducts()
							.execute("http://holycrosschurchjm.com/MIA_mysql.php?allproducts=yes");
					dg.dismiss();
				} else {
					Toast.makeText(getApplicationContext(),
							"Please select a store.", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		cancel = (Button) dg.findViewById(R.id.spinnerCancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dg.dismiss();
			}

		});
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (arg2 == 0) {
					storeID = "";
				} else {
					storeID = stores.get(arg2 - 1).getStoreID();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		empID = LoginActivity.empID;
		storeID = LoginActivity.storeID;
		Date date = new Date();
		dateString = new SimpleDateFormat("yyyy-MM-dd").format(date);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		listLoad = (ProgressBar) findViewById(R.id.mainlist_progress);
		listview = (ListView) findViewById(R.id.mainlistview);
		listview.setOnItemClickListener(new OnItemClickListener() {
			// @Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				Bundle b = new Bundle();
				Scanned p = (Scanned) arg0.getItemAtPosition(arg2);
				String[] product = { p.getUpcCode(), p.getProductName(),
						p.getBrand(), String.valueOf(p.getPrice()),
						p.getWeight(), p.getUom(), p.getGct(), p.getCategory() };
				b.putBoolean("scanned", p.getScanned());
				b.putString("store", storeID);
				b.putStringArray("product", product);
				b.putString("parent", "com.libratech.mia.HomeActivity");
				if (!p.getScanned()) {
					b.putString("mode", "view");
					startActivityForResult(new Intent(HomeActivity.this,
							UpdateProductActivity.class).putExtras(b), 1);
				} else {
					b.putString("mode", "update");
					startActivityForResult(new Intent(HomeActivity.this,
							UpdateProductActivity.class).putExtras(b), 1);
				}
			}
		});
		listview.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

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

				listview.setAdapter(sAdapter);
				bU.setBackgroundColor(bS.getSolidColor());
				bS.setBackgroundColor(bS.getHighlightColor());
			}

		});

		bU.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				listview.setAdapter(uAdapter);
				bS.setBackgroundColor(bU.getSolidColor());
				bU.setBackgroundColor(bU.getHighlightColor());
			}
		});
		if (isConnected()) {
			aProducts.clear();
			listLoad.setVisibility(View.VISIBLE);
			new getProducts()
					.execute("http://holycrosschurchjm.com/MIA_mysql.php?allproducts=yes");
		} else {
			Toast.makeText(
					HomeActivity.this,
					"No network connection, please check your connection and reload the application",
					Toast.LENGTH_LONG).show();
		}
		EasyTracker.getInstance(this).activityStart(this);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	class getProducts extends AsyncTask<String, Void, JSONArray> {
		protected JSONArray doInBackground(String... url) {
			return db.dbPull(url[0]);
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			String upc, name, desc, brand, category, uom, gct, photo, weight;
			name = desc = brand = category = uom = gct = photo = upc = weight = "";
			float price = (float) 0.00;
			if (result.length() == 0) {
				if (all)
					Toast.makeText(getApplicationContext(),
							"No products were found.", Toast.LENGTH_SHORT)
							.show();
				if (scanned)
					Toast.makeText(getApplicationContext(),
							"No scanned products were found.",
							Toast.LENGTH_SHORT).show();
				if (unscanned)
					Toast.makeText(getApplicationContext(),
							"No unscanned products were found.",
							Toast.LENGTH_SHORT).show();
			} else {
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
					if (all) {
						aProducts.add(new Product(upc, weight, name, desc,
								brand, category, uom, price, gct, photo));
					} else if (scanned) {
						try {
							price = (float) result.getJSONArray(i).getDouble(7);
							gct = result.getJSONArray(i).getString(8);
							photo = result.getJSONArray(i).getString(9);// price
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						sProducts.add(new Scanned(upc, weight, name, desc,
								brand, category, uom, price, gct, photo, true));
					} else if (unscanned) {
						uProducts
								.add(new Scanned(upc, weight, name, desc,
										brand, category, uom, price, gct,
										photo, false));
					}
				}
			}
			if (all) {
				sProducts.clear();
				all = false;
				scanned = true;
				new getProducts()
						.execute("http://holycrosschurchjm.com/MIA_mysql.php?comp_id="
								+ storeID
								+ "&rec_date="
								+ dateString
								+ "&scannedproducts=yes");
			} else if (scanned) {
				uProducts.clear();
				scanned = false;
				unscanned = true;
				new getProducts()
						.execute("http://holycrosschurchjm.com/MIA_mysql.php?comp_id="
								+ storeID
								+ "&rec_date="
								+ dateString
								+ "&unscannedproducts=yes");
			} else if (unscanned) {
				unscanned = false;
				all = true;
				done = true;
				listview.setAdapter(sAdapter);
				listview.setVisibility(View.VISIBLE);
				bS.callOnClick();
				pb.setMax(sProducts.size() + uProducts.size());
				pb.setProgress(sProducts.size());
				tv.setText("" + sProducts.size() + "\\"
						+ (sProducts.size() + uProducts.size())
						+ " items scanned at " + storeName + ".");
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
			listLoad.setVisibility(View.GONE);
			// listview.setVisibility(View.VISIBLE);
		}
		// EasyTracker.getInstance(this).activityStart(this);
	}

	class getStoreInfo extends AsyncTask<String, Void, JSONArray> {
		protected JSONArray doInBackground(String... url) {
			return db.dbPull(url[0]);
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			String tempID, tempName;
			tempID = tempName = "";
			if (result != null) {
				spinList.clear();
				stores.clear();
				spinList.add(0, "- Please select a store -");
				for (int i = 0; i < result.length(); i++) {
					try {
						empID = result.getJSONArray(i).getString(0);
						tempID = result.getJSONArray(i).getString(1);
						tempName = result.getJSONArray(i).getString(2);
						spinList.add(tempName);
						stores.add(new Store(tempID, tempName));
						Log.d("Stores", "" + i + tempName + spinList.size());
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				sp.setAdapter(adapter);
				sp.setSelection(0);
			}
		}
	}

	@Override
	public void RibbonMenuItemClick(int itemId, int position) {

		ActivityControl.changeActivity(this, itemId, "HomeActivity");
	}

	@Override
	public void onBackPressed() {

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		EasyTracker.getInstance(this).activityStop(this);
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
			rbmView.toggleMenu();
			return true;

		case R.id.change:
			new getStoreInfo()
					.execute("http://holycrosschurchjm.com/MIA_mysql.php?workLoc=yes&merch_id="
							+ user);
			dg.show();
			return true;

		case R.id.logout:
			EasyTracker.getInstance(this).activityStop(this);
			Intent i = new Intent(getApplicationContext(), LoginActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.logout_with_change, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (updated) {
			updated = !updated;
			if (isConnected()) {
				all = true;
				aProducts.clear();
				sProducts.clear();
				uProducts.clear();
				listview.setVisibility(View.GONE);
				listLoad.setVisibility(View.VISIBLE);
				new getProducts()
						.execute("http://holycrosschurchjm.com/MIA_mysql.php?allproducts=yes");
			} else {
				Toast.makeText(getApplicationContext(),
						"Please check your connection.", Toast.LENGTH_SHORT)
						.show();
			}
		}
		// try {
		// if (data.hasExtra("updated")) {
		// if (isConnected()) {
		// all = true;
		// aProducts.clear();
		// listLoad.setVisibility(View.VISIBLE);
		// listview.setVisibility(View.GONE);
		// Toast.makeText(getApplicationContext(), "That one",
		// Toast.LENGTH_SHORT).show();
		// new getProducts()
		// .execute("http://holycrosschurchjm.com/MIA_mysql.php?allproducts=yes");
		// } else {
		// Toast.makeText(getApplicationContext(),
		// "Please check your connection.", Toast.LENGTH_SHORT)
		// .show();
		// }
		// }
		// } catch (NullPointerException e) {
		// e.printStackTrace();
		// }
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
