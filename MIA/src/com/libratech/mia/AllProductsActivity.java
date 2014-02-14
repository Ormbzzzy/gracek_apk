package com.libratech.mia;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.libratech.mia.models.Product;

public class AllProductsActivity extends Activity implements
		iRibbonMenuCallback {
	private RibbonMenuView rbmView;
	EditText search;
	ListView listview;
	ArrayList<Product> products = HomeActivity.aProducts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		search = (EditText) findViewById(R.id.inputSearch);
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
						p.getBrand(), String.valueOf(p.getPrice()),
						p.getWeight(), p.getUom() };
				b.putStringArray("product", product);
				b.putString("parent", "com.libratech.mia.AllProductsActivity");
				Log.d("product", product[0] + product[1] + product[2]
						+ product[3] + product[4]);
				startActivity(new Intent(AllProductsActivity.this,
						UpdateProductActivity.class).putExtras(b));
			}
		});
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text
				ArrayList<Product> filter = new ArrayList<Product>();
				for (int i = 0; i < products.size(); i++) {
					if (products.get(i).getBrand().toLowerCase().contains(cs)
							|| products.get(i).getProductName().toLowerCase()
									.contains(cs)
							|| products.get(i).getBrand().contains(cs)
							|| products.get(i).getProductName().contains(cs))
						filter.add(products.get(i));
				}
				listview.setAdapter(new AllAdapter(AllProductsActivity.this,
						filter));
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

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
		String classes[] = { "HomeActivity", "ScanActivity",
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
