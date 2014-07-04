package com.libratech.mia;

import java.text.NumberFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.libratech.mia.models.Employee;
import com.libratech.mia.models.User;

public class UserAdapter extends BaseAdapter {

	ArrayList<User> emp = new ArrayList<User>();
	Context context;

	public UserAdapter(Context ctx, ArrayList<User> emp) {
		this.emp = emp;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return emp.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return emp.get(position);
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
			vi = inflater.inflate(R.layout.user_layout, null);
		}
		TextView userID = (TextView) vi.findViewById(R.id.delID);
		TextView fName = (TextView) vi.findViewById(R.id.delFname);
		TextView lName = (TextView) vi.findViewById(R.id.delLname);
		userID.setText(emp.get(position).getId());
		fName.setText(emp.get(position).getfName());
		lName.setText(emp.get(position).getlName());
		return vi;
	}

}
