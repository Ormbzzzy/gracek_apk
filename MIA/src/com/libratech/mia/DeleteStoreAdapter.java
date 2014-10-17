package com.libratech.mia;

import java.text.NumberFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.libratech.mia.models.Store;

public class DeleteStoreAdapter extends BaseAdapter {

	Context ctx;
	ArrayList<Store> data;

	public DeleteStoreAdapter(Context ctx, ArrayList<Store> data) {
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
			vi = inflater.inflate(R.layout.delete_row, null);
		}
		TextView storeName = (TextView) vi.findViewById(R.id.itemName);
		storeName.setText(data.get(position).getCompanyName());
		return vi;
	}

}
