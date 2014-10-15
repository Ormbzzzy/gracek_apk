package com.libratech.mia;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.libratech.mia.LoginActivity.getStoreInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SuggestionBox extends Activity {

	TextView title;
	EditText comment;
	Button submit, cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggestion_box);
		title = (TextView) findViewById(R.id.title);
		comment = (EditText) findViewById(R.id.comments);
		cancel = (Button) findViewById(R.id.cancel);
		submit = (Button) findViewById(R.id.add);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}

		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AddSuggestion().execute();
			}

		});
	}

	public class AddSuggestion extends AsyncTask<String, Void, JSONArray> {

		protected JSONArray doInBackground(String... params) {
			ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
			nvp.add(new BasicNameValuePair("merchSuggestions", "yes"));
			nvp.add(new BasicNameValuePair("username", "" + params[0]));
			nvp.add(new BasicNameValuePair("password", "" + params[1]));
			return new DatabaseConnector().DBSubmit(nvp);
		}

		protected void onPostExecute(JSONArray success) {
	
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.suggestion_box, menu);
		return true;
	}

}
