package com.libratech.mia;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class NewProductsControl extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_products_control);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_products_control, menu);
		return true;
	}
	
	public void addNewProdNav(View view)
	{
		Intent intent = new Intent(this, AddProduct.class);	    
	    startActivity(intent); 
	}
	
	public void updateNewProdNav(View view)
	{
		/*We need the listing of the new products before this screen.
		Just here for navigational flow right now */
		Intent intent = new Intent(this, AddProduct.class);	    
	    startActivity(intent); 
	}
	
	public void deleteNewProdNav(View view)
	{
		/*We need the listing of the new products before this screen.
		Just here for navigational flow right now */
		Intent intent = new Intent(this, DeleteProduct.class);	    
	    startActivity(intent); 
	}
	
	public void viewAllNewProdsNav(View view)
	{
		/*We need the listing of the new products before this screen.
		Just here for navigational flow right now */
	}

}
