package com.libratech.mia;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.models.User;

public class DeleteUser extends Activity implements iRibbonMenuCallback {

	ListView lv;
	View details;
	TextView idTv, fName, lName, role;
	Button confirm, cancel;
	RibbonMenuView rbmView;
	ArrayList<User> emp = new ArrayList<User>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_user);
		setupMenu();
		lv = (ListView) findViewById(R.id.allUsers);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				User u = (User) lv.getAdapter().getItem(position);
				lv.setVisibility(View.GONE);
				details.setVisibility(View.VISIBLE);
				idTv.setText(u.getId());
				fName.setText(u.getfName());
				lName.setText(u.getlName());
				role.setText(u.getRole());
			}

		});
		details = (View) findViewById(R.id.userDetails);
		details.setVisibility(View.GONE);
		idTv = (TextView) details.findViewById(R.id.userID);
		fName = (TextView) details.findViewById(R.id.firstName);
		lName = (TextView) details.findViewById(R.id.lastName);
		role = (TextView) details.findViewById(R.id.userRole);
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		confirm = (Button) findViewById(R.id.deleteUser);
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				details.setVisibility(View.GONE);
				lv.setVisibility(View.VISIBLE);
			}
		});
		confirm = (Button) findViewById(R.id.deleteUser);
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(
						DeleteUser.this);
				builder.setTitle("Warning");
				builder.setMessage("Doing this will remove the user from the database. Do you want to continue?");
				builder.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialogInterface, int i) {
								new deleteUser()
										.execute("http://holycrosschurchjm.com/MIA_mysql.php?deleteUser=yes&emp_id="
												+ idTv.getText());
								details.setVisibility(View.GONE);
								lv.setVisibility(View.VISIBLE);

							}
						});
				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialogInterface, int i) {
								details.setVisibility(View.GONE);
								lv.setVisibility(View.VISIBLE);
							}
						});
				builder.create().show();
			}

		});
		getActionBar().setDisplayHomeAsUpEnabled(true);
		new getUsers()
				.execute("http://holycrosschurchjm.com/MIA_mysql.php?allUsers=yes");

	}

	private void setupMenu() {
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.manager_menu);
	}

	private class deleteUser extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... url) {
			new DatabaseConnector().DBPush(url[0]);
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			Toast.makeText(getApplicationContext(), "User successfuly deleted",
					Toast.LENGTH_SHORT).show();
		}
	}

	private class getUsers extends AsyncTask<String, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(String... url) {
			return new DatabaseConnector().dbPull(url[0]);
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			String id, fName, lName, role;
			id = fName = lName = role = "";
			emp.clear();
			for (int i = 0; i < result.length(); i++) {
				try {
					id = result.getJSONArray(i).getString(0);
					fName = result.getJSONArray(i).getString(1);
					lName = result.getJSONArray(i).getString(2);
					role = result.getJSONArray(i).getString(3);

				} catch (Exception e) {
					e.printStackTrace();
				}
				emp.add(new User(id, fName, lName, role));
			}
			lv.setAdapter(new UserAdapter(DeleteUser.this, emp));
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
	// i = new Intent(this, AddStore.class);
	// b.putString("parent", "StoreReviewActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
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
