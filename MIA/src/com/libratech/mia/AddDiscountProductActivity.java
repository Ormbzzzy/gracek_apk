package com.libratech.mia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.libratech.mia.models.DiscountedProduct;
import com.libratech.mia.models.Product;
import com.libratech.mia.models.Scanned;

public class AddDiscountProductActivity extends Activity implements
		iRibbonMenuCallback {

	View listView, details, discounts;
	ExpandableListView list;
	ImageView img;
	TextView name, brand, upc, weight, uom;
	EditText price, value;
	Product p;
	CheckBox gct;
	Button confirm, cancel;
	Spinner sp;
	RibbonMenuView rbmView;
	ArrayList<Product> products = HomeActivity.aProducts;
	ArrayList<Scanned> scanned = HomeActivity.sProducts;
	ArrayList<DiscountedProduct> data = new ArrayList<DiscountedProduct>();
	ListView dList;
	String compID = HomeActivity.storeID;
	String empID = HomeActivity.empID;
	Scanned s;
	MenuItem add, del, scan;
	public boolean itemSelected = false;
	public boolean discountSelected = false;
	Builder dg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_discount_product);
		dg = new AlertDialog.Builder(this);
		dg.setTitle("Are you sure?");
		dg.setMessage("Doing this will remove the record of this discount");
		dg.setPositiveButton("Yes, I'm Sure.",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						Date date = new Date();
						String dateString = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss").format(date);
						new delDiscount()
								.execute(("http://holycrosschurchjm.com/MIA_mysql.php?deleteDiscountedProduct=yes&comp_id="
										+ compID
										+ "&rec_date="
										+ dateString
										+ "&upc_code=" + upc.getText())
										.replace(" ", "%20"));
					}
				});
		dg.setNegativeButton("Cancel", null);
		listView = (View) findViewById(R.id.AllDiscountListView);
		list = (ExpandableListView) listView.findViewById(R.id.AllDiscountList);
		listView.setVisibility(View.GONE);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		details = (View) findViewById(R.id.discountDetail);
		discounts = (View) findViewById(R.id.discounts);
		discounts.setVisibility(View.VISIBLE);
		upc = (TextView) details.findViewById(R.id.upc);
		dList = (ListView) discounts.findViewById(R.id.discountList);
		name = (TextView) details.findViewById(R.id.Name);
		brand = (TextView) details.findViewById(R.id.Brand);
		weight = (TextView) details.findViewById(R.id.weight);
		uom = (TextView) details.findViewById(R.id.uom);
		gct = (CheckBox) details.findViewById(R.id.gct);
		value = (EditText) details.findViewById(R.id.Value);
		sp = (Spinner) details.findViewById(R.id.discSpinner);
		cancel = (Button) details.findViewById(R.id.cancel);
		confirm = (Button) details.findViewById(R.id.addProd);
		List<String> type = new ArrayList<String>();
		type.add("$ Off item");
		type.add("% Off item");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, type);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(dataAdapter);
		// add.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// discounts.setVisibility(View.GONE);
		// listView.setVisibility(View.VISIBLE);
		// }
		//
		// });
		list.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				p = (Product) ((AllAdapter) list.getExpandableListAdapter())
						.getProduct(groupPosition, childPosition);
				listView.setVisibility(View.GONE);
				details.setVisibility(View.VISIBLE);
				itemSelected = true;
				invalidateOptionsMenu();
				upc.setText(p.getUpcCode());
				weight.setText(p.getWeight());
				name.setText(p.getProductName());
				brand.setText(p.getBrand());
				uom.setText(p.getUom());
				for (Scanned sc : scanned) {
					if (p.getUpcCode().equals(sc.getUpcCode())) {
						s = sc;
						break;
					}
				}
				// try {
				// price.setText(String.valueOf(s.getPrice()));
				// } catch (NullPointerException e) {
				// price.setText("0.00");
				// }
				return false;
			}

		});
		details.setVisibility(View.GONE);
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!value.getText().equals("")) {
					Date date = new Date();
					String dateString = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss").format(date);

					ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
					if (confirm.getText().equals("Update")) {
						nvp.add(new BasicNameValuePair(
								"updateDiscountedProduct", "yes"));
					} else {
						nvp.add(new BasicNameValuePair("addDiscountedProduct",
								"yes"));
					}
					nvp.add(new BasicNameValuePair("merch_id", empID));
					nvp.add(new BasicNameValuePair("comp_id", compID));
					nvp.add(new BasicNameValuePair("rec_date", dateString));
					nvp.add(new BasicNameValuePair("upc_code", upc.getText()
							.toString()));
					nvp.add(new BasicNameValuePair("discValue", value.getText()
							.toString()));
					nvp.add(new BasicNameValuePair("discType", (String) sp
							.getSelectedItem()));
					new AddDiscount().execute(nvp);
				} else {
					Toast.makeText(getApplicationContext(),
							"Please enter a discount value.", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		dList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				discounts.setVisibility(View.GONE);
				details.setVisibility(View.VISIBLE);
				discountSelected = true;
				invalidateOptionsMenu();
				confirm.setText("Update");
				DiscountedProduct d = data.get(position);
				upc.setText(d.getUpc());
				weight.setText(d.getWeight());
				name.setText(d.getName());
				brand.setText(d.getBrand());
				uom.setText(d.getUom());
				value.setText(d.getDiscValue());
				if (d.getDiscType().contains("%")) {
					sp.setSelection(1);
				} else {
					sp.setSelection(0);
				}
			}

		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				details.setVisibility(View.GONE);
				discounts.setVisibility(View.VISIBLE);
				discountSelected = false;
				invalidateOptionsMenu();
			}
		});
		Date date = new Date();
		String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(date);
		new getDiscounts()
				.execute(("http://holycrosschurchjm.com/MIA_mysql.php?discountedProducts=yes&comp_id="
						+ compID + "&rec_date=" + dateString).replace(" ",
						"%20"));
		list.setAdapter(new AllAdapter(AddDiscountProductActivity.this,
				products));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	class AddDiscount extends AsyncTask<ArrayList<NameValuePair>, Void, String> {
		protected String doInBackground(ArrayList<NameValuePair>... url) {
			new DatabaseConnector().DBSubmit(url[0]);
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Toast.makeText(getApplicationContext(), "Discount added",
					Toast.LENGTH_SHORT).show();
			finish();
		}

	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		// super.onPrepareOptionsMenu(menu);
		del = menu.findItem(R.id.removeDiscount);
		add = menu.findItem(R.id.newDiscount);
		scan = menu.findItem(R.id.discountScan);
		del.setVisible(discountSelected);
		add.setVisible(!discountSelected);
		scan.setVisible(itemSelected);
		if (itemSelected) {
			scan.setVisible(true);
			del.setVisible(false);
			add.setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_discount_product, menu);

		return true;
	}

	class delDiscount extends AsyncTask<String, Void, Boolean> {
		protected Boolean doInBackground(String... url) {
			return new DatabaseConnector().DBPush(url[0]);
		}

		@Override
		protected void onPreExecute() {

			Toast.makeText(getApplicationContext(), "Loading discounts.",
					Toast.LENGTH_SHORT).show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				Toast.makeText(getApplicationContext(),
						"An error has occured.", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(),
						"Item Successfully Deleted.", Toast.LENGTH_SHORT)
						.show();
				finish();
			}
		}
	}

	class getDiscounts extends AsyncTask<String, Void, JSONArray> {
		protected JSONArray doInBackground(String... url) {
			return new DatabaseConnector().dbPull(url[0]);
		}

		@Override
		protected void onPreExecute() {

			Toast.makeText(getApplicationContext(), "Loading discounts.",
					Toast.LENGTH_SHORT).show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			String upc, name, brand, weight, uom, photo, value, type;
			name = brand = value = uom = type = photo = upc = weight = "";
			float price = (float) 0.00;
			for (int i = 0; i < result.length(); i++) {
				try {
					upc = result.getJSONArray(i).getString(0);
					name = result.getJSONArray(i).getString(1);
					brand = result.getJSONArray(i).getString(2);
					weight = result.getJSONArray(i).getString(3);
					uom = result.getJSONArray(i).getString(4);
					value = result.getJSONArray(i).getString(5);
					type = result.getJSONArray(i).getString(6);
					photo = result.getJSONArray(i).getString(7);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				data.add(new DiscountedProduct(upc, name, brand, weight, uom,
						value, type, photo));
			}
			dList.setAdapter(new DiscountAdapter(data, getApplicationContext()));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
			rbmView.toggleMenu();
			return true;

		case R.id.discountScan:
			Bundle b = new Bundle();
			b.putString("parent", "AddProduct");
			Intent n = new Intent(AddDiscountProductActivity.this,
					ScanActivity.class);
			n.putExtras(b);
			startActivityForResult(n, 1);
			return true;
		case R.id.newDiscount:
			itemSelected = true;
			invalidateOptionsMenu();
			discounts.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			return true;

		case R.id.removeDiscount:
			dg.show();
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
	public void onBackPressed() {
		if (discountSelected || itemSelected) {
			discountSelected = false;
			itemSelected = false;
			details.setVisibility(View.GONE);
			discounts.setVisibility(View.VISIBLE);
			confirm.setText("Add");
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (data.hasExtra("code"))
				upc.setText(data.getStringExtra("code"));
			listView.setVisibility(View.GONE);
			details.setVisibility(View.VISIBLE);
			String s = upc.getText().toString();
			upc.setText("");
			upc.setText(s);
			for (Product p : products) {
				if (p.getUpcCode().equals(s)) {
					brand.setText(p.getBrand());
					name.setText(p.getProductName());
					weight.setText(p.getWeight());
					uom.setText(p.getUom());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void RibbonMenuItemClick(int itemId, int position) {

		ActivityControl.changeActivity(this, itemId, "HomeActivity");
	}

}