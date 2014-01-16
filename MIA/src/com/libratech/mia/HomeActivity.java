package com.libratech.mia;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.ListView;

public class HomeActivity extends Activity implements iRibbonMenuCallback {

	private RibbonMenuView rbmView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		ListView listview = (ListView) findViewById(R.id.mainlistview);
		listview.setAdapter(new HomeAdapter());

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {

			rbmView.toggleMenu();

			return true;

		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	public void RibbonMenuItemClick(int itemId, int position) {
		// TODO Auto-generated method stub

		String classes[] = { "HomeActivity", "ScanItemActivity",
				"FeedbackActivity" };
		if (position != 0) {
			try {
				startActivity(new Intent(HomeActivity.this,
						Class.forName("com.libratech.mia." + classes[position])));
			} catch (Exception e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		finish();
		super.onPause();
	}
	

}
