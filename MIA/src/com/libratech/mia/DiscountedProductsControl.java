package com.libratech.mia;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class DiscountedProductsControl extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discounted_products_control);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.discounted_products_control, menu);
		return true;
	}
	
	public void addDiscProdNav(View view)
	{
		/* All products list to select product/s should be before this screen*/
		Intent intent = new Intent(this, AddDiscountProductActivity.class);	    
	    startActivity(intent); 
	}
	
	public void updateDiscProdNav(View view)
	{
		/*We need the listing of the discounted products before this screen.
		Just here for navigational flow right now */
		Intent intent = new Intent(this, AddDiscountProductActivity.class);	    
	    startActivity(intent); 
	}
	
	public void deleteDiscProdNav(View view)
	{
		/*We need the listing of the discounted products before this screen.
		Just here for navigational flow right now */
		Intent intent = new Intent(this, DeleteDiscountProduct.class);	    
	    startActivity(intent); 
	}
	
	public void viewAllDiscProdsNav(View view)
	{
		/*We need the listing of the discounted products before this screen.
		Just here for navigational flow right now */
	}


}
