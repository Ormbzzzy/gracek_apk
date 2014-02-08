package com.libratech.mia;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.libratech.mia.models.Product;

@SuppressWarnings("deprecation")
public class HomeActivity extends FragmentActivity implements
		iRibbonMenuCallback {

	private RibbonMenuView rbmView;
	DatabaseConnector db = new DatabaseConnector();
	JSONArray sProducts = new JSONArray();
	JSONArray uProducts = new JSONArray();
	JSONArray aProducts = new JSONArray();
	boolean listReady = false;
	ListView listview;
	TabHost tabHost;
	TextView tv;
	Button bS, bU;
	ProgressBar pb;
	int numScanned, numUnscanned;
	boolean scanned = true;
	ArrayList products = new ArrayList<Product>();

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
				String name = (String) ((TextView) arg1
						.findViewById(R.id.itemName)).getText();
				String brand = (String) ((TextView) arg1
						.findViewById(R.id.itemBrand)).getText();
				String price = (String) ((TextView) arg1
						.findViewById(R.id.itemPrice)).getText();
				String[] product = { name, brand, price };
				b.putStringArray("product", product);
				b.putString("parent", "com.libratech.mia.HomeActivity");
				Log.d("product", product[0]+product[1]+product[2]);
				startActivity(new Intent(HomeActivity.this, ViewProductActivity.class)
						.putExtras(b));
			}

		});
		tv = (TextView) findViewById(R.id.ScanProgressText);
//		bA = (Button) findViewById(R.id.allbutton);
		bS = (Button) findViewById(R.id.scannedbutton);
		bU = (Button) findViewById(R.id.unscannedbutton);
		pb = (ProgressBar) findViewById(R.id.scanProgressBar);
//		bA.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				listview.setAdapter(new HomeAdapter(HomeActivity.this,
//						aProducts));
//				bU.setBackgroundColor(bA.getSolidColor());
//				bS.setBackgroundColor(bA.getSolidColor());
//				bA.setBackgroundColor(bA.getHighlightColor());
//			}
//
//		});

		bS.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				listview.setAdapter(new HomeAdapter(HomeActivity.this,
						sProducts));
				bU.setBackgroundColor(bS.getSolidColor());
			//	bA.setBackgroundColor(bS.getSolidColor());
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
				//bA.setBackgroundColor(bU.getSolidColor());
				bU.setBackgroundColor(bU.getHighlightColor());
			}
		});
		new getProducts()
				.execute("http://holycrosschurchjm.com/MIA_mysql.php?comp_id=COMP-00001&rec_date=2013-11-01&scannedproducts=yes");
		getActionBar().setDisplayHomeAsUpEnabled(true);
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

	class getProducts extends AsyncTask<String, Void, JSONArray> {
		protected JSONArray doInBackground(String... url) {

			return db.DBPull(url[0]);
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			for (int i = 0; i < result.length(); i++) {
				try {
					aProducts.put(result.getJSONArray(i).put(6, scanned));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			listview.setAdapter(new HomeAdapter(HomeActivity.this, aProducts));
			if (scanned) {
				sProducts = result;
				numScanned = result.length();
				scanned = false;
				new getProducts()
						.execute("http://holycrosschurchjm.com/MIA_mysql.php?comp_id=COMP-00001&rec_date=2013-11-02&unscannedproducts=yes");
			} else {
				uProducts = result;
				numUnscanned = result.length();
			}
			pb.setMax(numScanned + numUnscanned);
			pb.setProgress(numScanned);
			tv.setText("" + numScanned + "\\" + (numScanned + numUnscanned)
					+ " items scanned.");
		}
	}

	public void RibbonMenuItemClick(int itemId, int position) {
		// TODO Auto-generated method stub

		String classes[] = { "HomeActivity", "ScanItemActivity",
				"FeedbackActivity" };
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
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
}
