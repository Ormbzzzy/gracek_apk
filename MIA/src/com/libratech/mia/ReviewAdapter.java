package com.libratech.mia;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.libratech.mia.models.Products;
import com.libratech.mia.models.Store;

public class ReviewAdapter extends BaseAdapter {

	Context context;
	ArrayList<Products> data = new ArrayList<Products>();
	ArrayList<Store> stores = new ArrayList<Store>();

	public ReviewAdapter(Context context) {
		this.context = context;
	}

	public ReviewAdapter(Context context, ArrayList<Products> data,
			ArrayList<Store> stores) {
		this.data = data;
		this.context = context;
		this.stores = stores;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
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
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.review_row, null);
		}
		((TextView) vi.findViewById(R.id.reviewName)).setText(stores.get(
				position).getCompanyName());
		ProgressBar pb = (ProgressBar) vi.findViewById(R.id.reviewProgress);
		pb.setMax(data.get(position).getScanned().size()
				+ data.get(position).getUnscanned().size());
		pb.setProgress(data.get(position).getScanned().size());
		((TextView) vi.findViewById(R.id.reviewText)).setText(""
				+ data.get(position).getScanned().size() + "/" + pb.getMax()
				+ " items scanned at " + stores.get(position).getCompanyName());
		return vi;
	}
}
