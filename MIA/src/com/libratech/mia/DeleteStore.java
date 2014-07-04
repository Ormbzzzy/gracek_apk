package com.libratech.mia;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class DeleteStore extends Activity implements iRibbonMenuCallback {

	RibbonMenuView rbmView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_store);
		setupMenu();
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

	private void setupMenu() {
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.manager_menu);
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
