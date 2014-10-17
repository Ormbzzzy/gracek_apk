package com.libratech.mia;

import java.util.ArrayList;

import com.libratech.mia.models.BandedProduct;
import com.libratech.mia.models.Product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class BandedOfferAdapter extends BaseAdapter implements ListAdapter {

	Context ctx;
	ArrayList<BandedProduct> data;

	public BandedOfferAdapter(Context ctx, ArrayList<BandedProduct> bList) {
		this.ctx = ctx;
		this.data = bList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		View vi = convertView;
		if (vi == null) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.banded, null);
		}
		TextView itemName = (TextView) vi.findViewById(R.id.bandedMain);
		TextView extra = (TextView) vi.findViewById(R.id.bandedExtra);
		TextView bandedPrice = (TextView) vi.findViewById(R.id.bandedPrice);
		itemName.setText(data.get(position).getProducts().get(0)
				.getProductName());
		extra.setText("and " + (data.get(position).getProducts().size() - 1)
				+ " more products.");
		bandedPrice.setText(""+data.get(position).gettotalPrice());
		return vi;
	}

}
