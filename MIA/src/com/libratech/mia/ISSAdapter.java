package com.libratech.mia;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.libratech.mia.models.InStoreSample;

public class ISSAdapter extends BaseAdapter {

	ArrayList<InStoreSample> data;
	Context ctx;

	public ISSAdapter(ArrayList<InStoreSample> data, Context ctx) {
		this.data = data;
		this.ctx = ctx;
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
		View vi = convertView;
		if (vi == null) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.iss_adapter, null);
		}
		TextView name = (TextView) vi.findViewById(R.id.issLabel);
		name.setText(data.get(position).getProductName());
		return vi;
	}
}
