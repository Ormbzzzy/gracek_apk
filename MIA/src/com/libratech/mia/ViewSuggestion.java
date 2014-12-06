package com.libratech.mia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

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
import com.libratech.mia.ViewNewProduct.Delete;
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
	Builder dg;
	Suggestion s;

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
		list = (ListView) lv.findViewById(R.id.suggList);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		title = (TextView) addV.findViewById(R.id.title);
		comment = (EditText) addV.findViewById(R.id.comment);
		vtitle = (TextView) dv.findViewById(R.id.title);
		vcomment = (TextView) dv.findViewById(R.id.comment);
		cancel = (Button) addV.findViewById(R.id.cancel);
		submit = (Button) addV.findViewById(R.id.add);
		submit.setText("Delete");
		add = (Button) addV.findViewById(R.id.add);
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
				s = suggs.get(position);
				vtitle.setText(s.getTitle());
				vcomment.setText(s.getComment());
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
		// new GetSuggestion()
		// .execute(("http://holycrosschurchjm.com/MIA_mysql.php?merchSuggestions=yes&merch_id="
		// + empID + "&comp_id=" + compID + "&rec_date=" + dateString)
		// .replace(" ", "%20"));
		getActionBar().setDisplayHomeAsUpEnabled(true);
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

		ActivityControl.changeActivity(this, itemId, getIntent()
				.getStringExtra("parent"));
	}

}
