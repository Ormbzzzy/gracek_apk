package com.libratech.mia;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.libratech.mia.models.Product;

public class AllAdapter extends BaseAdapter {

	public Context context;
	public ArrayList<Product> data = new ArrayList<Product>();

	public AllAdapter(Context context, ArrayList<Product> aProducts) {
		this.context = context;
		this.data = aProducts;
		// for (int i = 0; i < data.size(); i++) {
		// Log.d("UPC to Adapter", "" + data.get(i).getUpcCode());
		// }
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vi = convertView;
		if (vi == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.all_row, null);
		}
		TextView name = (TextView) vi.findViewById(R.id.allName);
		TextView brand = (TextView) vi.findViewById(R.id.allBrand);
		TextView category = (TextView) vi.findViewById(R.id.allCat);
		name.setText(data.get(position).getProductName());
		brand.setText(data.get(position).getBrand());
		category.setText(data.get(position).getCategory());
		return vi;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
}
