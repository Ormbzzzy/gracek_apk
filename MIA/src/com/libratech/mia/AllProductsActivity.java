package com.libratech.mia;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.libratech.mia.models.Product;

public class AllProductsActivity extends Activity implements
		iRibbonMenuCallback {
	private RibbonMenuView rbmView;

	ListView listview;
	ArrayList<Product> products = HomeActivity.aProducts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		listview = (ListView) findViewById(R.id.alllistview);
		listview.setAdapter(new AllAdapter(this, products));
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Bundle b = new Bundle();
				Product p = (Product) arg0.getItemAtPosition(arg2);
				String[] product = { p.getUpcCode(), p.getProductName(),
						p.getBrand(), String.valueOf(p.getPrice()) };
				b.putStringArray("product", product);
				b.putString("parent", "com.libratech.mia.AllProductsActivity");
				Log.d("product", product[0] + product[1] + product[2]);
				startActivity(new Intent(AllProductsActivity.this,
						UpdateProductActivity.class).putExtras(b));
			}
		});
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

	@Override
	public void RibbonMenuItemClick(int itemId, int position) {
		// TODO Auto-generated method stub
		String classes[] = { "HomeActivity", "ScanItemActivity",
				"AllProductsActivity", "FeedbackActivity" };
		if (position != 2) {
			try {
				startActivity(new Intent(AllProductsActivity.this,
						Class.forName("com.libratech.mia." + classes[position])));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
