package com.libratech.mia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.HomeActivity.getStoreInfo;

public class SuggestionBox extends Activity implements iRibbonMenuCallback {

	TextView title;
	EditText comment;
	View lv, dv, addV;
	Button submit, cancel;
	RibbonMenuView rbmView;
	String empID = HomeActivity.empID;
	String compID = HomeActivity.storeID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggestion_box);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		lv = (View) findViewById(R.id.sListView);
		dv = (View) findViewById(R.id.viewSugg);
		dv.setVisibility(View.GONE);
		addV = (View) findViewById(R.id.sugEntry);
		lv.setVisibility(View.GONE);
		addV.setVisibility(View.VISIBLE);
		title = (TextView) addV.findViewById(R.id.title);
		comment = (EditText) addV.findViewById(R.id.comments);
		cancel = (Button) addV.findViewById(R.id.cancel);
		submit = (Button) addV.findViewById(R.id.add);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}

		});
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (title.getText().length() == 0
						|| comment.getText().length() == 0) {
					Date date = new Date();
					String dateString = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss").format(date);
					String params[] = { empID, compID,
							title.getText().toString(),
							comment.getText().toString(), dateString,
							"no_photo.jpg" };
					new AddSuggestion().execute(params);

				} else {
					Toast.makeText(getApplicationContext(),
							"Title and comment fields cannot be empty.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public class AddSuggestion extends AsyncTask<String, Void, JSONArray> {

		protected JSONArray doInBackground(String... params) {
			ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
			nvp.add(new BasicNameValuePair("addSuggestions", "yes"));
			nvp.add(new BasicNameValuePair("merch_id", "" + params[0]));
			nvp.add(new BasicNameValuePair("comp_id", "" + params[1]));
			nvp.add(new BasicNameValuePair("title", "" + params[2]));
			nvp.add(new BasicNameValuePair("comments", "" + params[3]));
			nvp.add(new BasicNameValuePair("rec_date", "" + params[4]));
			nvp.add(new BasicNameValuePair("photo", "" + params[5]));
			return new DatabaseConnector().DBSubmit(nvp);
		}

		protected void onPostExecute(JSONArray success) {

			if (success.optBoolean(0)) {
				Toast.makeText(getApplicationContext(),
						"Suggestion successfully submitted", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(getApplicationContext(), "An error occured",
						Toast.LENGTH_SHORT).show();
			}
			finish();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		EasyTracker.getInstance(this).activityStop(this);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.suggestion_box, menu);
		return true;
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
	public void RibbonMenuItemClick(int itemId, int position) {

		ActivityControl.changeActivity(this, itemId, getIntent().getStringExtra("parent"));
	}

}
