package com.libratech.mia;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.libratech.mia.models.Product;

public class BandedAdapter extends BaseAdapter implements ListAdapter {

	Context ctx;
	ArrayList<Product> data;

	public BandedAdapter(Context ctx, ArrayList<Product> data) {
		this.ctx = ctx;
		this.data = data;
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
			vi = inflater.inflate(R.layout.banded_row, null);
		}
		TextView itemName = (TextView) vi.findViewById(R.id.itemName);
		TextView itemBrand = (TextView) vi.findViewById(R.id.itemBrand);
		itemName.setText(data.get(position).getProductName());
		itemBrand.setText(data.get(position).getBrand());
		return vi;
	}

}
