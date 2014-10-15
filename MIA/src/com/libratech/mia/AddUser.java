package com.libratech.mia;

import java.util.ArrayList;
import java.util.List;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddUser extends Activity implements
iRibbonMenuCallback {

	EditText fName, lName, id, pw, pw2;
	Button cancel, confirm;
	String userRole;
	Spinner role;
	
	RibbonMenuView rbmView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_user);
		fName = (EditText) findViewById(R.id.firstName);
		lName = (EditText) findViewById(R.id.lastName);
		id = (EditText) findViewById(R.id.userID);
		pw = (EditText) findViewById(R.id.password);
		pw2 = (EditText) findViewById(R.id.confirmPassword);
		cancel = (Button) findViewById(R.id.cancel);
		confirm = (Button) findViewById(R.id.confirmUser);
		role = (Spinner) findViewById(R.id.userRole);
		List<String> list = new ArrayList<String>();
		list.add("Merchandiser");
		list.add("Manager");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
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
				}
				if (position == 1) {
					userRole = "manager";
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});
		pw2.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (!pw2.getText().equals("")) {
					if (!pw.getText().equals(pw2.getText())) {
						pw2.setError("Passowrds do not match");
						pw2.requestFocus();
					}
				}
			}
		});
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!pw.getText().equals(pw2.getText())) {
					pw2.setError("Passowrds do not match");
					pw2.requestFocus();
				} else {
					new pushUser()
							.execute("http://holycrosschurchjm.com/MIA_mysql.php?addUser=yes&emp_id="
									+ id.getText()
									+ "&fname="
									+ fName.getText()
									+ "&lname="
									+ lName.getText()
									+ "&role="
									+ userRole
									+ "&password=" + pw.getText());
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
		setupMenu();
	}

	class pushUser extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... url) {
			new DatabaseConnector().DBPush(url[0]);
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Toast.makeText(getApplicationContext(), "User successfully added",
					Toast.LENGTH_SHORT).show();
			finish();
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
	public void RibbonMenuItemClick(int itemId, int position) {

		Bundle b = new Bundle();
		Intent i = new Intent();
		switch (itemId) {
		case R.id.HomeActivity:
			i = new Intent(this, HomeActivity.class);
			b.putString("parent", "StoreReviewActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		case R.id.AllProducts:
			i = new Intent(this, AllProductsActivity.class);
			b.putString("parent", "StoreReviewActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		case R.id.ScanItemActivity:
			i = new Intent(this, ScanActivity.class);
			b.putString("parent", "StoreReviewActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		// case R.id.Feedback:
		// i = new Intent(this, FeedbackActivity.class);
		// break;
		case R.id.StoreReviewActivity:
			rbmView.toggleMenu();
			break;
		case R.id.delProduct:
			i = new Intent(this, DeleteProduct.class);
			b.putString("parent", "StoreReviewActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		case R.id.addUser:
			i = new Intent(this, AddUser.class);
			b.putString("parent", "StoreReviewActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		case R.id.addProduct:
			i = new Intent(this, AddProduct.class);
			b.putString("parent", "StoreReviewActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		case R.id.delUser:
			i = new Intent(this, DeleteUser.class);
			b.putString("parent", "StoreReviewActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		case R.id.addStore:
			i = new Intent(this, AddStore.class);
			b.putString("parent", "StoreReviewActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;

		case R.id.delStore:
			i = new Intent(this, DeleteStore.class);
			b.putString("parent", "StoreReviewActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		default:
			break;
		}
	}
	
}
