package com.libratech.mia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.darvds.ribbonmenu.RibbonMenuView;

public class ActivityControl {

	public static void changeActivity(Context ctx, int itemId, int position,
			RibbonMenuView rbmView, String parent) {
		Bundle b = new Bundle();
		Intent i = new Intent();
		switch (itemId) {
		case R.id.HomeActivity:
			i = new Intent(ctx.getApplicationContext(), HomeActivity.class);
			b.putString("parent", parent);
			i.putExtras(b);
			ctx.startActivity(i);
			break;
		case R.id.AllProducts:
			i = new Intent(ctx.getApplicationContext(),
					AllProductsActivity.class);
			b.putString("parent", parent);
			i.putExtras(b);
			ctx.startActivity(i);
			break;
		case R.id.Scan:
			i = new Intent(ctx.getApplicationContext(), ScanActivity.class);
			b.putString("parent", parent);
			i.putExtras(b);
			ctx.startActivity(i);
			break;
		case R.id.Feedback:
			i = new Intent(ctx.getApplicationContext(), FeedbackActivity.class);
			b.putString("parent", parent);
			i.putExtras(b);
			ctx.startActivity(i);
			break;
		case R.id.StoreReviewActivity:
			rbmView.toggleMenu();
			break;
		case R.id.delProduct:
			i = new Intent(ctx.getApplicationContext(), DeleteProduct.class);
			b.putString("parent", parent);
			i.putExtras(b);
			ctx.startActivity(i);
			break;
		case R.id.addUser:
			i = new Intent(ctx.getApplicationContext(), AddUser.class);
			b.putString("parent", parent);
			i.putExtras(b);
			ctx.startActivity(i);
			break;
		case R.id.addProduct:
			i = new Intent(ctx.getApplicationContext(), AddProduct.class);
			b.putString("parent", parent);
			i.putExtras(b);
			ctx.startActivity(i);
			break;
		case R.id.addNewProduct:
			i = new Intent(ctx.getApplicationContext(), AddProduct.class);
			b.putString("parent", parent);
			i.putExtras(b);
			ctx.startActivity(i);
			break;
		case R.id.delUser:
			i = new Intent(ctx.getApplicationContext(), DeleteUser.class);
			b.putString("parent", parent);
			i.putExtras(b);
			ctx.startActivity(i);
			break;
		case R.id.addStore:
			i = new Intent(ctx.getApplicationContext(), AddStore.class);
			b.putString("parent", parent);
			i.putExtras(b);
			ctx.startActivity(i);
			break;

		case R.id.delStore:
			i = new Intent(ctx.getApplicationContext(), DeleteStore.class);
			b.putString("parent", parent);
			i.putExtras(b);
			ctx.startActivity(i);
			break;
		case R.id.AddDiscount:
			i = new Intent(ctx.getApplicationContext(),
					AddDiscountProductActivity.class);
			b.putString("parent", parent);
			i.putExtras(b);
			ctx.startActivity(i);
			break;
		case R.id.AddBanded:
			i = new Intent(ctx.getApplicationContext(), AddBandedOffer.class);
			b.putString("parent", parent);
			i.putExtras(b);
			ctx.startActivity(i);
			break;
		case R.id.Suggestion:
			i = new Intent(ctx.getApplicationContext(), SuggestionBox.class);
			b.putString("parent", parent);
			i.putExtras(b);
			ctx.startActivity(i);
			break;
		// case R.id.PromoNav:
		// i = new Intent(ctx.getApplicationContext(), DeleteStore.class);
		// b.putString("parent", parent);
		// i.putExtras(b);
		// ctx.startActivity(i);
		// break;
		// case R.id.ExpiryNav:
		// i = new Intent(ctx.getApplicationContext(), DeleteStore.class);
		// b.putString("parent", parent);
		// i.putExtras(b);
		// ctx.startActivity(i);
		// break;
		// case R.id.SuggestionNav:
		// i = new Intent(ctx.getApplicationContext(), DeleteStore.class);
		// b.putString("parent", parent);
		// i.putExtras(b);
		// ctx.startActivity(i);
		// break;
		case R.id.settings:
			// i = new Intent(ctx.getApplicationContext(), DeleteStore.class);
			// b.putString("parent", parent);
			// i.putExtras(b);
			// ctx.startActivity(i);
			break;
		default:
			break;
		}
	}
}
