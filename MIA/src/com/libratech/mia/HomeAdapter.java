package com.libratech.mia;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.libratech.mia.models.Scanned;

public class HomeAdapter extends BaseAdapter {

	public Context context;
	public ArrayList<Scanned> data = new ArrayList<Scanned>();

	public HomeAdapter(Context context, ArrayList<Scanned> aProducts) {
		this.context = context;
		this.data = aProducts;
//		for (int i = 0; i < data.size(); i++) {
//			Log.d("UPC to Adapter", ""+data.get(i).getUpcCode());
//		}
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
			vi = inflater.inflate(R.layout.row, null);
		}
		TextView itemName = (TextView) vi.findViewById(R.id.itemName);
		itemName.setText(data.get(position).getProductName());
		TextView itemBrand = (TextView) vi.findViewById(R.id.itemBrand);
		itemBrand.setText(data.get(position).getBrand());
		TextView itemPrice = (TextView) vi.findViewById(R.id.itemPrice);
		itemPrice.setText(String.valueOf(data.get(position).getPrice()));
		CheckBox itemScanned = (CheckBox) vi.findViewById(R.id.itemScanned);
		itemScanned.setChecked(data.get(position).getScanned());
		return vi;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
}
