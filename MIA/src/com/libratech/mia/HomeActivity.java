package com.libratech.mia;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

public class HomeActivity extends Activity implements iRibbonMenuCallback {

	private RibbonMenuView rbmView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == android.R.id.home) {

			rbmView.toggleMenu();

			return true;

		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void RibbonMenuItemClick(int itemId) {
		// TODO Auto-generated method stub

	}

}
