package com.libratech.mia;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class HomeAdapter extends BaseAdapter {

	Context context;
	JSONArray data;// = new ArrayList<String>();

	public HomeAdapter(Context context, JSONArray data) {
		this.context = context;
		this.data = data;
		Log.d("from call", data.toString());
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.length();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		try {
			return data.get(arg0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
		// TextView itemNum = (TextView) vi.findViewById(R.id.itemNum);
		// itemNum.setText("" + (position+1));
		TextView itemName = (TextView) vi.findViewById(R.id.itemName);
		try {
			itemName.setText(data.getJSONArray(position).getString(0));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		TextView itemBrand = (TextView) vi.findViewById(R.id.itemBrand);
		try {
			itemBrand.setText(data.getJSONArray(position).getString(1));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TextView itemPrice = (TextView) vi.findViewById(R.id.itemPrice);
		try {
			itemPrice.setText(data.getJSONArray(position).getString(4));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CheckBox itemScanned = (CheckBox) vi.findViewById(R.id.itemScanned);
		try {
			itemScanned.setChecked(data.getJSONArray(position).getBoolean(6));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return vi;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
}
