package com.libratech.mia;

import java.util.ArrayList;

import com.libratech.mia.models.Suggestion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

public class SuggestionAdapter extends BaseAdapter implements ListAdapter {

	Context ctx;
	ArrayList<Suggestion> data;

	public SuggestionAdapter(Context ctx, ArrayList<Suggestion> data) {
		// TODO Auto-generated constructor stub
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
		return data.get(position-1);
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
			vi = inflater.inflate(R.layout.suggestion, null);
		}
		TextView title = (TextView) vi.findViewById(R.id.title);
		TextView comment = (TextView) vi.findViewById(R.id.comment);
		TextView date = (TextView) vi.findViewById(R.id.date);
		String d = data.get(position).getDate();
		d = d.substring(0, d.indexOf(" ") + 1);
		date.setText(d);
		title.setText(data.get(position).getTitle());
		comment.setText(data.get(position).getComment());
		return vi;
	}
}
