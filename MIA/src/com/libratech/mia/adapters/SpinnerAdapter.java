package com.libratech.mia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.libratech.mia.R;
import com.libratech.mia.R.id;
import com.libratech.mia.R.layout;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<String> {

	Context ctx;
	ArrayList<String> data = new ArrayList<String>();

	public SpinnerAdapter(Context context, int textViewResourceId,
			ArrayList<String> objects) {
		super(context, textViewResourceId, objects);
		ctx = context;
		data = objects;

	}

	@Override
	public int getCount() {
		
		return data.size();
	}

	@Override
	public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
		return getCustomView(position, cnvtView, prnt);
	}

	@Override
	public View getView(int pos, View cnvtView, ViewGroup prnt) {

		LayoutInflater inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mySpinner = inflater.inflate(R.layout.spinner, prnt, false);
		TextView text = (TextView) mySpinner.findViewById(R.id.promptText);
		text.setText(data.get(pos));
		return getCustomView(pos, cnvtView, prnt);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mySpinner = inflater.inflate(R.layout.spinner_row, parent, false);
		TextView text = (TextView) mySpinner.findViewById(R.id.spinText);
		text.setText(data.get(position));
		return mySpinner;
	}

}
