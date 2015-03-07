package com.libratech.mia;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.models.Store;

public class AddUser extends Activity implements iRibbonMenuCallback {

	EditText fName, lName, id, pw, pw2;
	Button cancel, confirm;
	String userRole;
	Spinner role, pickStore;
	View setStore;
	ArrayList<Store> allStores = new ArrayList<Store>();
	List<String> roleList = new ArrayList<String>();
	List<String> stores = new ArrayList<String>();
	String assignedStore = "";
	RibbonMenuView rbmView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_user);
		setStore = (View) findViewById(R.id.assignStore);
		setStore.setVisibility(View.GONE);
		pickStore = (Spinner) setStore.findViewById(R.id.setStore);
		fName = (EditText) findViewById(R.id.firstName);
		lName = (EditText) findViewById(R.id.lastName);
		id = (EditText) findViewById(R.id.userID);
		pw = (EditText) findViewById(R.id.password);
		pw2 = (EditText) findViewById(R.id.confirmPassword);
		cancel = (Button) findViewById(R.id.cancel);
		confirm = (Button) findViewById(R.id.confirmUser);
		role = (Spinner) findViewById(R.id.userRole);
		new getInfo()
				.execute("http://holycrosschurchjm.com/MIA_mysql.php?allStores=yes");

		roleList.add("Merchandiser");
		roleList.add("Manager");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, roleList);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		role.setAdapter(dataAdapter);
		role.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position == 0) {
					userRole = "merchandiser";
					setStore.setVisibility(View.VISIBLE);
				}
				if (position == 1) {
					userRole = "manager";
					setStore.setVisibility(View.GONE);
					assignedStore = "";
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		pickStore.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				assignedStore = allStores.get(position).getStoreID();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		// pw2.setOnFocusChangeListener(new OnFocusChangeListener() {
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// // TODO Auto-generated method stub
		//
		// if (!pw2.getText().equals(pw2.getText())) {
		// // if (!pw.getText().equals(pw2.getText())) {
		// pw2.setError("Passwords do not match");
		// pw2.requestFocus();
		// // }
		// }
		// }
		// });
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!pw.getText().toString().matches(pw2.getText().toString())) {
					pw2.setError("Passowrds do not match");
					pw2.requestFocus();
				} else {
					if (!userRole.equalsIgnoreCase("manager")) {
						new pushUser()
								.execute(("http://holycrosschurchjm.com/MIA_mysql.php?addMerchUser=yes&emp_id="
										+ id.getText()
										+ "&fname="
										+ fName.getText()
										+ "&lname="
										+ lName.getText()
										+ "&role=merchandiser&password="
										+ pw.getText() + "&comp_id=" + assignedStore)
										.replace(" ", "%20"));
					} else {
						new pushUser()
								.execute(("http://holycrosschurchjm.com/MIA_mysql.php?addUser=yes&emp_id="
										+ id.getText()
										+ "&fname="
										+ fName.getText()
										+ "&lname="
										+ lName.getText()
										+ "&role="
										+ userRole
										+ "&password=" + pw.getText()).replace(
										" ", "%20"));
					}
				}
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setupMenu();
	}

	private class getInfo extends AsyncTask<String, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(String... url) {

			return new DatabaseConnector().dbPull(url[0]);

		}

		@Override
		protected void onPreExecute() {
			/*
			 * Toast.makeText(getApplicationContext(),
			 * "Gathering store information.", Toast.LENGTH_SHORT).show();
			 */
			allStores.clear();
		}

		@Override
		protected void onPostExecute(JSONArray result) {

			String code, storeName, addr, city;
			code = storeName = addr = city = "";
			if (result != null) {
				for (int i = 0; i < result.length(); i++) {
					try {
						code = result.getJSONArray(i).getString(0);
						storeName = result.getJSONArray(i).getString(1);
						addr = result.getJSONArray(i).getString(2);
						city = result.getJSONArray(i).getString(3);
						Log.d("Store", storeName);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					allStores.add(new Store(code, storeName, addr, city));
					stores.add(storeName);
				}
			}
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
					AddUser.this, android.R.layout.simple_spinner_item, stores);
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			pickStore.setAdapter(dataAdapter);
		}
	}

	class pushUser extends AsyncTask<String, Void, Boolean> {
		protected Boolean doInBackground(String... url) {
			return new DatabaseConnector().DBPush(url[0]);

		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			if (result) {
				Toast.makeText(getApplicationContext(),
						"User successfully added", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(getApplicationContext(),
						"An error has occurred", Toast.LENGTH_SHORT).show();
			}
		}

	}

	private void setupMenu() {
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.manager_menu);
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
	public void onPause() {
		super.onPause();
		EasyTracker.getInstance(this).activityStop(this);
		finish();
	}

	@Override
	public void RibbonMenuItemClick(int itemId, int position) {

		ActivityControl.changeActivity(this, itemId, "HomeActivity");
	}

}
