package com.libratech.mia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import com.libratech.mia.R;
public class BandedProductsControl extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_banded_products_control);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.banded_products_control, menu);
		return true;
	}
	
	public void addBandedProdNav(View view)
	{
		/* All products list to select product/s should be before this screen*/
		Intent intent = new Intent(this, AddBandedOffer.class);	    
	    startActivity(intent); 
	}
	
	public void updateBandedProdNav(View view)
	{
		/*We need the listing of the banded products before this screen.
		Just here for navigational flow right now */
		Intent intent = new Intent(this, AddBandedOffer.class);	    
	    startActivity(intent); 
	}
	
	public void deleteBandedProdNav(View view)
	{
		/*We need the listing of the banded products before this screen.
		Just here for navigational flow right now */
		
		//Intent intent = new Intent(this, DeleteDiscountProduct.class);	    
	    //startActivity(intent); 
	}
	
	public void viewAllBandedProdsNav(View view)
	{
		/*We need the listing of the discounted products before this screen.
		Just here for navigational flow right now */
	}


}
