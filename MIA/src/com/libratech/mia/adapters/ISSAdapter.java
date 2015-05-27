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
import com.libratech.mia.models.InStoreSample;

import java.util.ArrayList;

public class ISSAdapter extends BaseAdapter {

	ArrayList<InStoreSample> data;
	Context ctx;

	public ISSAdapter(ArrayList<InStoreSample> data, Context ctx) {
		this.data = data;
		this.ctx = ctx;
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
			vi = inflater.inflate(R.layout.iss_adapter, null);
		}
		TextView name = (TextView) vi.findViewById(R.id.issLabel);
		name.setText(data.get(position).getProductName());
		return vi;
	}
}
