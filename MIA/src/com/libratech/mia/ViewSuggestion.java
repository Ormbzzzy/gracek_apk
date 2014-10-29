package com.libratech.mia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.HomeActivity.getStoreInfo;
import com.libratech.mia.models.Suggestion;

public class ViewSuggestion extends Activity implements iRibbonMenuCallback {

	TextView title, vtitle, vcomment;
	View lv, dv, addV;
	ListView list;
	EditText comment;
	Button submit, cancel, add;
	RibbonMenuView rbmView;
	ArrayList<Suggestion> suggs = new ArrayList<Suggestion>();
	String empID = HomeActivity.empID;
	String compID = HomeActivity.storeID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggestion_box);
		lv = (View) findViewById(R.id.sListView);
		dv = (View) findViewById(R.id.viewSugg);
		dv.setVisibility(View.GONE);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		addV = (View) findViewById(R.id.sugEntry);
		addV.setVisibility(View.GONE);
		add = (Button) lv.findViewById(R.id.addSugg);
		list = (ListView) lv.findViewById(R.id.suggList);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		title = (TextView) addV.findViewById(R.id.title);
		comment = (EditText) addV.findViewById(R.id.comment);
		vtitle = (TextView) dv.findViewById(R.id.title);
		vcomment = (TextView) dv.findViewById(R.id.comment);
		dv.setFocusable(false);
		cancel = (Button) addV.findViewById(R.id.cancel);
		submit = (Button) addV.findViewById(R.id.add);
		submit.setText("Delete");
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(),
						SuggestionBox.class));
			}

		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}

		});
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				lv.setVisibility(View.GONE);
				dv.setVisibility(View.VISIBLE);
				vtitle.setText(suggs.get(position).getTitle());
				vcomment.setText(suggs.get(position).getComment());
			}

		});
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		Date date = new Date();
		String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(date);
		new GetSuggestion()
				.execute(("http://holycrosschurchjm.com/MIA_mysql.php?merchSuggestions=yes&merch_id="
						+ empID + "&comp_id=" + compID + "&rec_date=" + dateString)
						.replace(" ", "%20"));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public class GetSuggestion extends AsyncTask<String, Void, JSONArray> {

		protected JSONArray doInBackground(String... params) {
			return new DatabaseConnector().dbPull(params[0]);
		}

		protected void onPostExecute(JSONArray result) {
			String title, comment, date;
			title = comment = date = "";
			float price = (float) 0.00;
			for (int i = 0; i < result.length(); i++) {
				try {
					title = result.getJSONArray(i).getString(0);
					comment = result.getJSONArray(i).getString(1);
					date = result.getJSONArray(i).getString(2);

				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				suggs.add(new Suggestion(title, comment, date));
				list.setAdapter(new SuggestionAdapter(getApplicationContext(),
						suggs));
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		EasyTracker.getInstance(this).activityStop(this);
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
			rbmView.toggleMenu();
			return true;

		case R.id.logout:
			EasyTracker.getInstance(this).activityStop(this);
			Intent i = new Intent(getApplicationContext(), LoginActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.suggestion_box, menu);
		return true;
	}

	@Override
	public void RibbonMenuItemClick(int itemId, int position) {

		ActivityControl.changeActivity(this, itemId, getIntent().getStringExtra("parent"));
	}

}
