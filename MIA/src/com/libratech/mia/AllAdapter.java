package com.libratech.mia;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.libratech.mia.models.Product;

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
				Log.d("category", p.getCategory());
			} else {
				pList.get(parentList.indexOf(p.getCategory())).add(p);
			}
		}
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vi = convertView;
		if (vi == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.all_row, null);
		}
		TextView name = (TextView) vi.findViewById(R.id.allName);
		TextView brand = (TextView) vi.findViewById(R.id.allBrand);
		name.setText(pList.get(groupPosition).getList().get(childPosition)
				.getProductName());
		brand.setText(pList.get(groupPosition).getList().get(childPosition)
				.getBrand());
		Log.d("name", (String) name.getText());
		return vi;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		int i = 0;
		for (Product p : childList) {
			if (p.getCategory().equalsIgnoreCase(parentList.get(groupPosition)))
				i++;
		}
		return i;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	public Product getProduct(int group, int child) {
		return pList.get(group).getList().get(child);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return parentList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vi = convertView;
		if (vi == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.all_cat, null);
		}

		TextView tv = (TextView) vi.findViewById(R.id.allCat);
		tv.setText(parentList.get(groupPosition));
		Log.d("cat", (String) tv.getText());
		return tv;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
}
