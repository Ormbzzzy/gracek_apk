package com.libratech.mia;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class Promotions extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_promotions);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.promotions, menu);
		return true;
	}
	
	public void newProductNav(View view)
	{
		Intent intent = new Intent(this, NewProductsControl.class);	    
	    startActivity(intent);
	}
	
	public void discProductNav(View view)
	{
		Intent intent = new Intent(this, DiscountedProductsControl.class);	    
	    startActivity(intent);
	}
	
	public void bandedProductNav(View view)
	{
		Intent intent = new Intent(this, BandedProductsControl.class);	    
	    startActivity(intent);
	}
	
	public void instoreSampleNav(View view)
	{
		Intent intent = new Intent(this, InstoreSamplingControl.class);	    
	    startActivity(intent);
	}

}
