package com.libratech.mia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.libratech.mia.R;
import com.libratech.mia.R.id;
import com.libratech.mia.R.layout;
import com.libratech.mia.models.Store;

import java.util.ArrayList;

public class DeleteStoreAdapter extends BaseAdapter {

	Context ctx;
	ArrayList<Store> data;

	public DeleteStoreAdapter(Context ctx, ArrayList<Store> data) {
		this.ctx = ctx;
		this.data = data;
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
			vi = inflater.inflate(R.layout.delete_row, null);
		}
		TextView storeName = (TextView) vi.findViewById(R.id.deleteText);
		TextView storeID = (TextView) vi.findViewById(R.id.deleteID);
		storeName.setText(data.get(position).getCompanyName());
		storeID.setText(data.get(position).getStoreID() + " -");
		return vi;
	}

}
