package com.libratech.mia.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.adapters.AllAdapter;
import com.libratech.mia.models.Product;
import com.libratech.mia.utilities.ActivityControl;
import com.libratech.mia.utilities.DatabaseConnector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.libratech.mia.R;
import com.libratech.mia.utilities.ScanActivity;

public class InStoreSampling extends Activity implements iRibbonMenuCallback {

	View list, details, all;
	ImageView img;
	TextView name, weight, brand;
	DatePicker dp;
	ListView lv;
	ExpandableListView exlv;
	EditText cmt, upc, search;
	Button cancel, submit, dpb, xDate, cDate;
	Dialog dg;
	ArrayList<Product> aProd = HomeActivity.aProducts;
	String empID = HomeActivity.empID;
	String compID = HomeActivity.storeID;
	int day, month, year;
	Calendar c = Calendar.getInstance();
	Builder delDg;
	RibbonMenuView rbmView;
	boolean issSelected = false;
	boolean newIss = false;
	MenuItem add, del, scan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_in_store_sampling);
		dg = new Dialog(InStoreSampling.this);
		dg.setContentView(R.layout.date_picker);
		dg.setTitle("Select Date");
		delDg = new AlertDialog.Builder(this);
		delDg.setTitle("Are you sure?");
		delDg.setMessage("Doing this will remove the record of this discount");
		delDg.setPositiveButton("Yes, I'm Sure.",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						Date date = new Date();
						String dateString = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss").format(date);
						new delISS()
								.execute((DatabaseConnector.getDomain()+"/MIA_mysql.php?deleteInStoreSample=yes&comp_id="
										+ compID
										+ "&rec_date="
										+ dateString
										+ "&upc_code=" + upc.getText())
										.replace(" ", "%20"));
					}
				});
		delDg.setNegativeButton("Cancel", null);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		all = (View) findViewById(R.id.issAllListView);
		exlv = (ExpandableListView) all.findViewById(R.id.issAllList);
		exlv.setAdapter(new AllAdapter(InStoreSampling.this, aProd));
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
		all.setVisibility(View.GONE);
		search = (EditText) all.findViewById(R.id.inputSearch);
		list = (View) findViewById(R.id.issListView);
		details = (View) findViewById(R.id.issDetails);
		details.setVisibility(View.GONE);
		lv = (ListView) list.findViewById(R.id.issList);
		dg.setCanceledOnTouchOutside(true);
		img = (ImageView) details.findViewById(R.id.updateImage);
		upc = (EditText) details.findViewById(R.id.upc);
		name = (TextView) details.findViewById(R.id.name);
		brand = (TextView) details.findViewById(R.id.brand);
		dp = (DatePicker) dg.findViewById(R.id.sampleDate);
		dpb = (Button) details.findViewById(R.id.DateButton);
		xDate = (Button) dg.findViewById(R.id.cancelDate);
		cDate = (Button) dg.findViewById(R.id.confirmDate);
		cmt = (EditText) findViewById(R.id.comments);
		cancel = (Button) details.findViewById(R.id.cancel);
		submit = (Button) details.findViewById(R.id.add);
		upc.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
				for (Product p : aProd) {
					if (upc.getText().equals(p.getUpcCode())) {
						name.setText(p.getProductName());
						brand.setText(p.getBrand());
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				

			}

		});
		dpb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				dg.show();
			}

		});

		cDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				day = dp.getDayOfMonth();
				month = dp.getMonth();
				year = dp.getYear();
				c.set(day, month, year);
				dg.cancel();
				// Date d = new Date(day,month, year);
			}

		});
		xDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				dg.cancel();
			}

		});
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				issSelected = true;
				list.setVisibility(View.GONE);
				details.setVisibility(View.VISIBLE);
				submit.setText("Update");
				invalidateOptionsMenu();
			}

		});
		exlv.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				all.setVisibility(View.GONE);
				details.setVisibility(View.VISIBLE);
				Product p = ((AllAdapter) exlv.getExpandableListAdapter())
						.getProduct(groupPosition, childPosition);
				name.setText(p.getProductName());
				brand.setText(p.getBrand());
				return false;
			}

		});
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (!brand.getText().equals("Brand")) {

					String dateString = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss").format(new Date());
					if (submit.getText().equals("Add")) {
						new SendISS()
								.execute((DatabaseConnector.getDomain()+"/MIA_mysql.php?addInStoreSample=yes&merch_id="
										+ empID
										+ "&comp_id="
										+ compID
										+ "&rec_date="
										+ dateString
										+ "&upc_code="
										+ upc.getText()
										+ "&comments=" + cmt.getText())
										.replace(" ", "%20"));
					} else {
						new SendISS()
								.execute((DatabaseConnector.getDomain()+"/MIA_mysql.php?updateInStoreSample=yes&merch_id="
										+ empID
										+ "&comp_id="
										+ compID
										+ "&rec_date="
										+ dateString
										+ "&upc_code="
										+ upc.getText()
										+ "&comments=" + cmt.getText())
										.replace(" ", "%20"));
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Please enter a valid product", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	class delISS extends AsyncTask<String, Void, Boolean> {
		protected Boolean doInBackground(String... url) {
			return new DatabaseConnector().DBPush(url[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Toast.makeText(getApplicationContext(), "Item deleted",
						Toast.LENGTH_SHORT).show();
				details.setVisibility(View.GONE);
				lv.setVisibility(View.VISIBLE);
				issSelected = false;
				invalidateOptionsMenu();
			} else {
				Toast.makeText(getApplicationContext(), "An error occured.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class SendISS extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... url) {
			return new DatabaseConnector().DBPush(url[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			Toast.makeText(getApplicationContext(),
					"In store sample submitted.", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	public void RibbonMenuItemClick(int itemId, int position) {

		ActivityControl.changeActivity(this, itemId, "HomeActivity");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.issScan:
			Bundle b = new Bundle();
			b.putString("parent", "AddProduct");
			Intent n = new Intent(InStoreSampling.this, ScanActivity.class);
			n.putExtras(b);
			startActivityForResult(n, 1);
			return true;
		case R.id.newIss:
			newIss = true;
			list.setVisibility(View.GONE);
			getActionBar().setTitle("All Products");
			all.setVisibility(View.VISIBLE);
			invalidateOptionsMenu();
			break;
		case R.id.removeIss:
			delDg.show();
			break;
		case android.R.id.home:
			rbmView.toggleMenu();
			break;

		case R.id.logout:
			
			Intent i = new Intent(getApplicationContext(), LoginActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (data.hasExtra("code"))
				upc.setText(data.getStringExtra("code"));
			all.setVisibility(View.GONE);
			details.setVisibility(View.VISIBLE);
			String s = upc.getText().toString();
			upc.setText("");
			upc.setText(s);
			for (Product p : aProd) {
				if (p.getUpcCode().equals(s)) {
					brand.setText(p.getBrand());
					name.setText(p.getProductName());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		
		super.onPause();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.in_store_sampling, menu);
		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		// super.onPrepareOptionsMenu(menu);
		del = menu.findItem(R.id.removeIss);
		add = menu.findItem(R.id.newIss);
		scan = menu.findItem(R.id.issScan);
		del.setVisible(issSelected);
		add.setVisible(!issSelected);
		scan.setVisible(newIss);
		if (newIss) {
			scan.setVisible(true);
			del.setVisible(false);
			add.setVisible(false);
		}
		return true;
	}

}