package com.libratech.mia.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.libratech.mia.R;
import com.libratech.mia.R.id;
import com.libratech.mia.R.layout;
import com.libratech.mia.models.Product;

import java.util.ArrayList;

public class AllAdapter extends BaseExpandableListAdapter {

	public Context context;
	static ArrayList<String> parentList = new ArrayList<String>();
	static ArrayList<Product> childList = new ArrayList<Product>();
	static ArrayList<Child> pList = new ArrayList<Child>();

	private class Child {
		private ArrayList<Product> list;

		public Child() {
			list = new ArrayList<Product>();
		}

		public void add(Product p) {
			list.add(p);
		}

		public ArrayList<Product> getList() {
			return list;
		}
	}

	public AllAdapter(Context context, ArrayList<Product> aProducts) {
		this.context = context;
		childList = aProducts;
		for (Product p : aProducts) {
			if (!parentList.contains(p.getCategory())) {
				parentList.add(p.getCategory());
				pList.add(new Child());
				pList.get(parentList.indexOf(p.getCategory())).add(p);
			} else {
				pList.get(parentList.indexOf(p.getCategory())).add(p);
			}
		}
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		View vi = convertView;
		if (vi == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.all_row, null);
		}
		TextView name = (TextView) vi.findViewById(R.id.allName);
		TextView brand = (TextView) vi.findViewById(R.id.allBrand);
		brand.setVisibility(View.GONE);
		name.setText(pList.get(groupPosition).getList().get(childPosition)
				.getProductName());
		brand.setText(pList.get(groupPosition).getList().get(childPosition)
				.getBrand());
		return vi;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		

		return pList.get(groupPosition).getList().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		
		return null;
	}

	public Product getProduct(int group, int child) {
		return pList.get(group).getList().get(child);
	}

	@Override
	public int getGroupCount() {
		
		return parentList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		
		return 0;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		View vi = convertView;
		if (vi == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.all_cat, null);
		}

		TextView tv = (TextView) vi.findViewById(R.id.allCat);
		tv.setText(parentList.get(groupPosition));
		tv.setTextColor(Color.BLACK);
		return tv;
	}

	@Override
	public boolean hasStableIds() {
		
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		
		return true;
	}

	public void filterData(String query) {
		query = query.toLowerCase();
		pList.clear();
		parentList.clear();
		for (Product p : childList) {
			if (p.getBrand().toLowerCase().contains(query)
					|| p.getProductName().toLowerCase().contains(query)
					|| p.getCategory().toLowerCase().contains(query)) {
				if (!parentList.contains(p.getCategory())) {
					parentList.add(p.getCategory());
					pList.add(new Child());
					pList.get(parentList.indexOf(p.getCategory())).add(p);
				} else {
					pList.get(parentList.indexOf(p.getCategory())).add(p);
				}
			}
		}
		notifyDataSetChanged();
	}
}