package com.libratech.mia.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.adapters.SuggestionAdapter;
import com.libratech.mia.models.Suggestion;
import com.libratech.mia.utilities.ActivityControl;
import com.libratech.mia.utilities.DatabaseConnector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.libratech.mia.R;
public class SuggestionBox extends Activity implements iRibbonMenuCallback {

	TextView title, viewTitle, viewComment;
	EditText comment;
	View lv, dv, addV;
	Button submit, cancel;
	ListView list;
	RibbonMenuView rbmView;
	ArrayList<Suggestion> suggs = new ArrayList<Suggestion>();
	String empID = HomeActivity.empID;
	String compID = HomeActivity.storeID;
	MenuItem add, del;
	boolean sugSelected = false;
	boolean newSug = false;
	Builder dg;
	Suggestion s;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggestion_box);
		dg = new AlertDialog.Builder(this);
		dg.setTitle("Are you sure?");
		dg.setMessage("Doing this will permanently remove this entry from the application!");
		dg.setPositiveButton("Yes, I'm Sure.",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						Date date = new Date();
						String dateString = new SimpleDateFormat("yyyy-MM-dd")
								.format(date);
						new Delete()
								.execute((DatabaseConnector.getDomain()+"/MIA_mysql.php?deleteSuggestions=yes&merch_id="
										+ empID
										+ "&comp_id="
										+ compID
										+ "&rec_date="
										+ dateString
										+ "&stored_rec_date=" + s.getDate())
										.replace(" ", "%20"));
					}
				});
		dg.setNegativeButton("Cancel", null);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		lv = (View) findViewById(R.id.sListView);
		list = (ListView) lv.findViewById(R.id.suggList);
		dv = (View) findViewById(R.id.viewSugg);
		dv.setVisibility(View.GONE);
		addV = (View) findViewById(R.id.sugEntry);
		lv.setVisibility(View.VISIBLE);
		addV.setVisibility(View.GONE);
		viewTitle = (TextView) dv.findViewById(R.id.title);
		viewComment = (TextView) dv.findViewById(R.id.comment);
		title = (TextView) addV.findViewById(R.id.title);
		comment = (EditText) addV.findViewById(R.id.comments);
		cancel = (Button) addV.findViewById(R.id.cancel);
		submit = (Button) addV.findViewById(R.id.add);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				finish();
			}

		});
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (title.getText().length() > 0
						|| comment.getText().length() > 0) {
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

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				lv.setVisibility(View.GONE);
				dv.setVisibility(View.VISIBLE);
				sugSelected = true;
				invalidateOptionsMenu();
				s = suggs.get(position);
				viewTitle.setText(s.getTitle());
				viewComment.setText(s.getComment());
			}

		});
		Date date = new Date();
		String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(date);
		new GetSuggestion()
				.execute((DatabaseConnector.getDomain()+"/MIA_mysql.php?merchSuggestions=yes&merch_id="
						+ empID + "&comp_id=" + compID + "&rec_date=" + dateString)
						.replace(" ", "%20"));
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

	public class GetSuggestion extends AsyncTask<String, Void, JSONArray> {

		protected JSONArray doInBackground(String... params) {
			return new DatabaseConnector().dbPull(params[0]);
		}

		protected void onPostExecute(JSONArray result) {
			String title, content, date;
			title = content = date = "";
			suggs.clear();
			for (int i = 0; i < result.length(); i++) {
				try {

					title = result.getJSONArray(i).getString(0);
					content = result.getJSONArray(i).getString(1);
					date = result.getJSONArray(i).getString(2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				suggs.add(new Suggestion(title, content, date));
			}
			list.setAdapter(new SuggestionAdapter(getApplicationContext(),
					suggs));
		}
	}

	public class Delete extends AsyncTask<String, Void, Boolean> {

		protected Boolean doInBackground(String... params) {
			return new DatabaseConnector().DBPush(params[0]);
		}

		protected void onPostExecute(Boolean result) {
			if (result) {
				Toast.makeText(getApplicationContext(),
						"Item successfully deleted.", Toast.LENGTH_SHORT)
						.show();
				finish();
			} else {
				Toast.makeText(getApplicationContext(),
						"An error has occured.", Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.suggestion_box, menu);
		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		// super.onPrepareOptionsMenu(menu);
		del = menu.findItem(R.id.removeSug);
		add = menu.findItem(R.id.newSug);
		del.setVisible(sugSelected);
		add.setVisible(!sugSelected);
		if (newSug) {
			del.setVisible(false);
			add.setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.newSug:
			lv.setVisibility(View.GONE);
			addV.setVisibility(View.VISIBLE);
			invalidateOptionsMenu();
			return true;

		case R.id.removeSug:
			dg.show();
			invalidateOptionsMenu();
			return true;

		case android.R.id.home:
			rbmView.toggleMenu();
			return true;

		case R.id.logout:
			
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

		ActivityControl.changeActivity(this, itemId, getIntent()
				.getStringExtra("parent"));
	}
}
