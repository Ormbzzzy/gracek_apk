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
import com.libratech.mia.models.Product;

import java.text.NumberFormat;
import java.util.ArrayList;

public class NewAdapter extends BaseAdapter implements ListAdapter {

	Context ctx;
	ArrayList<Product> data;

	public NewAdapter(Context ctx, ArrayList<Product> products) {
		// TODO Auto-generated constructor stub
		this.ctx = ctx;
		data = products;
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
			vi = inflater.inflate(R.layout.new_product, null);
		}
		TextView itemName = (TextView) vi.findViewById(R.id.itemName);
		itemName.setText(data.get(position).getProductName());
		// TextView itemBrand = (TextView) vi.findViewById(R.id.itemBrand);
		// itemBrand.setText(data.get(position).getBrand());
		TextView itemPrice = (TextView) vi.findViewById(R.id.itemPrice);
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
		itemPrice.setText(format.format(data.get(position).getPrice()));
		TextView itemBrand = (TextView) vi.findViewById(R.id.itemBrand);
		itemBrand.setText(data.get(position).getBrand());

		return vi;
	}
}
