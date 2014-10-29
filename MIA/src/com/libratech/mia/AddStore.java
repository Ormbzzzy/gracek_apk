package com.libratech.mia;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;

public class AddStore extends Activity implements iRibbonMenuCallback {

	EditText name, address;
	Spinner city;
	Button confirm, cancel;
	RibbonMenuView rbmView;
	ArrayAdapter<CharSequence> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_store);
		name = (EditText) findViewById(R.id.compName);
		address = (EditText) findViewById(R.id.addressLine1);
		city = (Spinner) findViewById(R.id.citySpinner);
		cancel = (Button) findViewById(R.id.cancel);
		confirm = (Button) findViewById(R.id.confirmaddStore);
		adapter = ArrayAdapter.createFromResource(this, R.array.capitals_array,
				android.R.layout.simple_spinner_item);
		city.setAdapter(adapter);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new pushStore()
						.execute(("http://holycrosschurchjm.com/MIA_mysql.php?addStore=yes&comp_id=COMP-00003&company_name="
								+ name + "&address=" + address + "&city=" + city
								.getItemIdAtPosition(city
										.getSelectedItemPosition())).replace(
								" ", "%20"));
			}
		});
		setupMenu();
	}

	private void setupMenu() {
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.manager_menu);
	}

	class pushStore extends AsyncTask<String, Void, JSONArray> {
		protected JSONArray doInBackground(String... url) {
			return new DatabaseConnector().dbPull(url[0]);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.logout, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

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

	@Override
	public void RibbonMenuItemClick(int itemId, int position) {

		ActivityControl.changeActivity(this, itemId, "StoreReviewActivity");
	}
	// @Override
	// public void RibbonMenuItemClick(int itemId, int position) {
	//
	// Bundle b = new Bundle();
	// Intent i = new Intent();
	// switch (itemId) {
	// case R.id.HomeActivity:
	// i = new Intent(this, HomeActivity.class);
	// b.putString("parent", "StoreReviewActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// case R.id.AllProducts:
	// i = new Intent(this, AllProductsActivity.class);
	// b.putString("parent", "StoreReviewActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// case R.id.ScanItemActivity:
	// i = new Intent(this, ScanActivity.class);
	// b.putString("parent", "StoreReviewActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// // case R.id.Feedback:
	// // i = new Intent(this, FeedbackActivity.class);
	// // break;
	// case R.id.StoreReviewActivity:
	// rbmView.toggleMenu();
	// break;
	// case R.id.delProduct:
	// i = new Intent(this, DeleteProduct.class);
	// b.putString("parent", "StoreReviewActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// case R.id.addUser:
	// i = new Intent(this, AddUser.class);
	// b.putString("parent", "StoreReviewActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// case R.id.addProduct:
	// i = new Intent(this, AddProduct.class);
	// b.putString("parent", "StoreReviewActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// case R.id.delUser:
	// i = new Intent(this, DeleteUser.class);
	// b.putString("parent", "StoreReviewActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// case R.id.addStore:
	// rbmView.toggleMenu();
	// break;
	//
	// case R.id.delStore:
	// i = new Intent(this, DeleteStore.class);
	// b.putString("parent", "StoreReviewActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// default:
	// break;
	// }
	// }
}
