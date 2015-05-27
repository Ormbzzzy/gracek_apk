package com.libratech.mia.activities;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.adapters.AllAdapter;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.libratech.mia.R;

public class AddBandedOffer extends Activity implements iRibbonMenuCallback {

	RibbonMenuView rbmView;
	View allList, list, details, bandedList;
	ListView lv, blv;
	ExpandableListView exlv;
	Button imgB;
	TextView name, price, weight, brand, uom, upc;
	EditText bPrice, search;
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
	Builder delDG;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_banded_offer);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		allList = (View) findViewById(R.id.AllbandedListView);
		exlv = (ExpandableListView) allList.findViewById(R.id.AllBandedList);
		search = (EditText) allList.findViewById(R.id.inputSearch);
		list = (View) findViewById(R.id.bandedListView);
		lv = (ListView) list.findViewById(R.id.bandedList);
		bPrice = (EditText) list.findViewById(R.id.bandedPrice);
		bPrice.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(8, 2) });
		imgB = (Button) list.findViewById(R.id.add);
		submit = (Button) list.findViewById(R.id.submit);
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
		lv.setAdapter(adapter);

		delDG = new Builder(AddBandedOffer.this);
		delDG.setTitle("Confirm Delete");
		delDG.setMessage("Doing this will remove the banded offfer from the system!");
		delDG.setPositiveButton("Confirm",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						Date date = new Date();
						String dateString = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss").format(date);
						currentBanded = new BandedProduct();
						new PushBanded()
								.execute((DatabaseConnector.getDomain()+"/MIA_mysql.php?deleteBandedProduct=yes&comp_id="
										+ compID
										+ "&rec_date="
										+ dateString
										+ "&band_prods=" + currentBanded
										.getBandedID()).replace(" ", "%20"));
					}
				});
		exlv.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				p = ((AllAdapter) exlv.getExpandableListAdapter()).getProduct(
						groupPosition, childPosition);
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
				
				bandedItemSelected = true;
				invalidateOptionsMenu();
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
						bandedItemSelected = false;
						invalidateOptionsMenu();
					}
				}
			}
		});
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Builder dg = new Builder(AddBandedOffer.this);
				dg.setTitle("Confirm Submission");
				String s = "Confirm banded offer of:\n\n";
				for (Product p : currentBanded.getProducts()) {
					s += p.getProductName() + "\n";
				}
				s += "\nFor $" + bPrice.getText();
				dg.setMessage(s);
				dg.setPositiveButton("Confirm",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								
								String prods = currentBanded.getProducts()
										.get(0).getUpcCode();
								for (int i = 1; i < currentBanded.getProducts()
										.size(); i++) {
									prods += "-"
											+ currentBanded.getProducts()
													.get(i).getUpcCode();
								}
								Date date = new Date();
								String dateString = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss").format(date);
								if (submit.getText().equals("Update")) {
									new PushBanded()
											.execute((DatabaseConnector.getDomain()+"/MIA_mysql.php?updateBandedProduct=yes&merch_id="
													+ empID
													+ "&comp_id="
													+ compID
													+ "&rec_date="
													+ dateString
													+ "&band_prods="
													+ prods
													+ "&band_price=" + bPrice
													.getText()).replace(" ",
													"%20"));
									submit.setText("Submit");
								} else {
									new PushBanded()
											.execute((DatabaseConnector.getDomain()+"/MIA_mysql.php?addBandedProduct=yes&merch_id="
													+ empID
													+ "&comp_id="
													+ compID
													+ "&rec_date="
													+ dateString
													+ "&band_prods="
													+ prods
													+ "&band_price=" + bPrice
													.getText()).replace(" ",
													"%20"));
								}
							}
						});
				try {
					if (Float.parseFloat(bPrice.getText().toString()) <= 0.00) {
						Toast.makeText(getApplicationContext(),
								"That price is not valid.", Toast.LENGTH_SHORT)
								.show();
					} else {
						dg.show();
					}
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(),
							"That price is not valid.", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		blv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				bandedSelected = true;
				invalidateOptionsMenu();
				submit.setText("Update");
				currentBanded = bList.get(position);
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
		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				((AllAdapter) exlv.getExpandableListAdapter()).filterData(cs
						.toString());
			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

		});
		Date date = new Date();
		String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(date);
		new getBandedProducts()
				.execute((DatabaseConnector.getDomain()+"/MIA_mysql.php?bandedProducts=yes&comp_id="
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
			if (result) {
				if (del.isVisible()) {
					Toast.makeText(getApplicationContext(),
							"Offer successfully removed.", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(getApplicationContext(),
							"Offer successfully submitted.", Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "An error as occurred",
						Toast.LENGTH_SHORT).show();
			}
			list.setVisibility(View.GONE);
			bandedList.setVisibility(View.VISIBLE);
			newBanded = false;
			bandedSelected = false;
			bandedItemSelected = false;
			invalidateOptionsMenu();
			Date date = new Date();
			String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(date);
			new getBandedProducts()
					.execute((DatabaseConnector.getDomain()+"/MIA_mysql.php?bandedProducts=yes&comp_id="
							+ compID + "&rec_date=" + dateString).replace(" ",
							"%20"));
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
		if (newBanded && !bandedItemSelected) {
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
		if (bandedItemSelected) {
			del.setVisible(true);
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
			delDG.show();

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

	public class DecimalDigitsInputFilter implements InputFilter {

		Pattern mPattern;

		public DecimalDigitsInputFilter(int digitsBeforeZero,
				int digitsAfterZero) {
			mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1)
					+ "}+((\\.[0-9]{0," + (digitsAfterZero - 1)
					+ "})?)||(\\.)?");
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {

			Matcher matcher = mPattern.matcher(dest);
			if (!matcher.matches())
				return "";
			return null;
		}

	}

	@Override
	public void RibbonMenuItemClick(int itemId, int position) {

		ActivityControl.changeActivity(this, itemId, "HomeActivity");
	}

}