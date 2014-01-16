package com.libratech.mia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class HomeAdapter extends BaseAdapter {

	Context context;
	String[] data;
	private static LayoutInflater inflater = null;

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vi = convertView;
		if (vi == null)
			vi = inflater.inflate(R.layout.row, null);
		TextView itemNum = (TextView) vi.findViewById(R.id.itemNum);
		itemNum.setText(position);
		TextView itemName = (TextView) vi.findViewById(R.id.itemName);
		itemName.setText(data[position]);
		TextView itemBrand = (TextView) vi.findViewById(R.id.itemBrand);
		itemBrand.setText(data[position]);
		TextView itemPrice = (TextView) vi.findViewById(R.id.itemPrice);
		itemPrice.setText(data[position]);
		CheckBox itemScanned = (CheckBox) vi.findViewById(R.id.itemScanned);

		return vi;
	}
}
