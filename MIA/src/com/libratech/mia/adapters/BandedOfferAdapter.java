package com.libratech.mia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.libratech.mia.R;
import com.libratech.mia.R.id;
import com.libratech.mia.R.layout;
import com.libratech.mia.models.BandedProduct;

import java.util.ArrayList;

public class BandedOfferAdapter extends BaseAdapter implements ListAdapter {

	Context ctx;
	ArrayList<BandedProduct> data;

	public BandedOfferAdapter(Context ctx, ArrayList<BandedProduct> bList) {
		this.ctx = ctx;
		this.data = bList;
	}

	@Override
	public int getCount() {
		
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		
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
