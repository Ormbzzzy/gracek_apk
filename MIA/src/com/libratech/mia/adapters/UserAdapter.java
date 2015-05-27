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
import com.libratech.mia.models.User;

import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {

	ArrayList<User> emp = new ArrayList<User>();
	Context context;

	public UserAdapter(Context ctx, ArrayList<User> emp) {
		this.emp = emp;
		context = ctx;
	}

	@Override
	public int getCount() {
		
		return emp.size();
	}

	@Override
	public Object getItem(int position) {
		
		return emp.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View vi = convertView;
		if (vi == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.user_layout, null);
		}
		TextView userID = (TextView) vi.findViewById(R.id.delID);
		TextView fName = (TextView) vi.findViewById(R.id.delFname);
		TextView lName = (TextView) vi.findViewById(R.id.delLname);
		userID.setText(emp.get(position).getId() + " - ");
		fName.setText(emp.get(position).getfName() + " ");
		lName.setText(emp.get(position).getlName());
		return vi;
	}

}
