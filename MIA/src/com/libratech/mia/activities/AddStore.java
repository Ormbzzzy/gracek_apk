package com.libratech.mia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.adapters.SpinnerAdapter;
import com.libratech.mia.utilities.ActivityControl;
import com.libratech.mia.utilities.DatabaseConnector;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import com.libratech.mia.R;

public class AddStore extends Activity implements iRibbonMenuCallback {

	EditText name, address, id;
	Spinner city;
	Button confirm, cancel;
	RibbonMenuView rbmView;
	ArrayList<String> CityArrayList = new ArrayList<String>();

	// ArrayAdapter<CharSequence> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_store);
		name = (EditText) findViewById(R.id.compName);
		address = (EditText) findViewById(R.id.addressLine1);
		city = (Spinner) findViewById(R.id.citySpinner);
		cancel = (Button) findViewById(R.id.cancel);
		confirm = (Button) findViewById(R.id.confirmaddStore);
		id = (EditText) findViewById(R.id.compID);
		Collections.addAll(CityArrayList,
				getResources().getStringArray(R.array.capitals_array));
		city.setAdapter(new SpinnerAdapter(this,
				android.R.layout.simple_spinner_item, CityArrayList));
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (name.getText().toString().matches("")
						|| address.getText().toString().matches("")
						|| id.getText().toString().matches("")) {
					Toast.makeText(getApplicationContext(),
							"Some information is missing.", Toast.LENGTH_SHORT)
							.show();
				} else {
					new pushStore().execute((DatabaseConnector.getDomain()
							+ "/MIA_mysql.php?addStore=yes&comp_id="
							+ id.getText() + "&company_name=" + name.getText()
							+ "&address=" + address.getText() + "&city=" + city
							.getItemIdAtPosition(city.getSelectedItemPosition()))
							.replace(" ", "%20"));
				}
			}
		});
		setupMenu();
		getActionBar().setDisplayHomeAsUpEnabled(true);
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
}
