package com.libratech.mia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
	ListView dList;
	String compID = HomeActivity.storeID;
	String empID = HomeActivity.empID;
	Scanned s;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_discount_product);
		listView = (View) findViewById(R.id.AllDiscountListView);
		list = (ExpandableListView) listView.findViewById(R.id.AllDiscountList);
		details = (View) findViewById(R.id.discountDetail);
		discounts = (View) findViewById(R.id.discounts);
		discounts.setVisibility(View.GONE);
		upc = (TextView) details.findViewById(R.id.upc);
		dList = (ListView) discounts.findViewById(R.id.discountList);
		name = (TextView) details.findViewById(R.id.Name);
		brand = (TextView) details.findViewById(R.id.Brand);
		weight = (TextView) details.findViewById(R.id.weight);
		uom = (TextView) details.findViewById(R.id.uom);
		gct = (CheckBox) details.findViewById(R.id.gct);
		// price = (EditText) details.findViewById(R.id.Price);
		value = (EditText) details.findViewById(R.id.Value);
		sp = (Spinner) details.findViewById(R.id.discSpinner);
		cancel = (Button) details.findViewById(R.id.cancel);
		confirm = (Button) details.findViewById(R.id.addProd);
		List<String> type = new ArrayList<String>();
		type.add("$");
		type.add("%");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, type);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(dataAdapter);

		list.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				p = (Product) ((AllAdapter) list.getExpandableListAdapter())
						.getProduct(groupPosition, childPosition);
				list.setVisibility(View.GONE);
				details.setVisibility(View.VISIBLE);
				weight.setText(p.getWeight());
				name.setText(p.getProductName());
				brand.setText(p.getBrand());
				uom.setText(p.getUom());
				for (Scanned sc : scanned) {
					if (p.getUpcCode().equals(s.getUpcCode())) {
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
		new getProducts()
				.execute("http://holycrosschurchjm.com/MIA_mysql.php?allproducts=yes");
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!value.getText().equals("")) {
					Date date = new Date();
					String dateString = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss").format(date);
					new AddDiscount()
							.execute(("http://holycrosschurchjm.com/MIA_mysql.php?addDiscountedProduct=yes&merch_id="
									+ empID
									+ "&comp_id="
									+ compID
									+ "&rec_date="
									+ dateString
									+ "&upc_code="
									+ upc.getText()
									+ "&discValue="
									+ value.getText() + "&disType=" + (String) sp
									.getSelectedItem()).replace(" ", "%20"));
				} else {
					Toast.makeText(getApplicationContext(),
							"Please enter a discount value.", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		getActionBar().setHomeButtonEnabled(true);
	}

	class AddDiscount extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... url) {
			new DatabaseConnector().DBPush(url[0]);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_discount_product, menu);

		return true;
	}

	class getProducts extends AsyncTask<String, Void, JSONArray> {
		protected JSONArray doInBackground(String... url) {
			return new DatabaseConnector().dbPull(url[0]);
		}

		@Override
		protected void onPreExecute() {

			Toast.makeText(AddDiscountProductActivity.this,
					"Loading products.", Toast.LENGTH_SHORT).show();
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
			Toast.makeText(AddDiscountProductActivity.this, "Products loaded.",
					Toast.LENGTH_SHORT).show();
			list.setAdapter(new AllAdapter(AddDiscountProductActivity.this,
					products));
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

		ActivityControl.changeActivity(this, itemId, position, rbmView,
				"HomeActivity");
	}
	// @Override
	// public void RibbonMenuItemClick(int itemId, int position) {
	// Bundle b = new Bundle();
	// Intent i = new Intent();
	// switch (itemId) {
	// case R.id.HomeActivity:
	// rbmView.toggleMenu();
	// break;
	// case R.id.AllProducts:
	// i = new Intent(this, AllProductsActivity.class);
	// b.putString("parent", "HomeActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// case R.id.ScanItemActivity:
	// i = new Intent(this, ScanActivity.class);
	// b.putString("parent", "HomeActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// // case R.id.Feedback:
	// // i = new Intent(this, FeedbackActivity.class);
	// // break;
	// case R.id.StoreReviewActivity:
	// i = new Intent(this, StoreReviewActivity.class);
	// b.putString("parent", "HomeActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// // case R.id.delProduct:
	// // i = new Intent(this, DeleteProduct.class);
	// // b.putString("parent", "HomeActivity");
	// // i.putExtras(b);
	// // startActivityForResult(i, 1);
	// // break;
	// case R.id.addUser:
	// i = new Intent(this, AddUser.class);
	// b.putString("parent", "HomeActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// case R.id.addProduct:
	// i = new Intent(this, AddProduct.class);
	// b.putString("parent", "HomeActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// // case R.id.delUser:
	// // i = new Intent(this, DeleteUser.class);
	// // b.putString("parent", "HomeActivity");
	// // i.putExtras(b);
	// // startActivityForResult(i, 1);
	// // break;
	// case R.id.AddBanded:
	// i = new Intent(this, AddBandedOffer.class);
	// // rbmView.toggleMenu();
	// b.putString("parent", "HomeActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// case R.id.AddDiscount:
	// i = new Intent(this, AddDiscountProductActivity.class);
	// // rbmView.toggleMenu();
	// b.putString("parent", "HomeActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// default:
	// break;
	// }
	// }
}