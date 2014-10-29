package com.libratech.mia;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.libratech.mia.models.Feedback;

public class FeedbackAdapter extends BaseAdapter {
	Context ctx;
	ArrayList<Feedback> data;

	public FeedbackAdapter(Context ctx, ArrayList<Feedback> data) {
		this.ctx = ctx;
		this.data = data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub

		View vi = arg1;
		if (vi == null) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.fbrow, null);
		}
		TextView name = (TextView) vi.findViewById(R.id.brand);
		TextView brand = (TextView) vi.findViewById(R.id.name);
		TextView date = (TextView) vi.findViewById(R.id.date);
		CheckBox urgent = (CheckBox) vi.findViewById(R.id.urgent);
		name.setText(data.get(arg0).getName());
		brand.setText(data.get(arg0).getBrand());
		date.setText(data.get(arg0).getDate());
		urgent.setChecked(data.get(arg0).isUrgent());
		return vi;
	}

}
