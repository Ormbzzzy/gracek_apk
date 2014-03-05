package com.libratech.mia;

import org.json.JSONArray;
import org.json.JSONException;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	// Values for email and password at the time of the login attempt.
	private DatabaseConnector db = new DatabaseConnector();
	private String empID;
	private String empPass;
	// UI references.
	private EditText id;
	private EditText pass;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		setupLoginForm();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

	public void attemptLogin() {

		// Reset errors.
		id.setError(null);
		pass.setError(null);

		// Store values at the time of the login attempt.
		empID = id.getText().toString();
		empPass = pass.getText().toString();
		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(empPass)) {
			pass.setError("Password is required.");
			focusView = pass;
			cancel = true;
		} else if (empPass.length() < 4) {
			pass.setError("Password too short.");
			focusView = pass;
			cancel = true;
		}
		if (TextUtils.isEmpty(empID)) {
			id.setError("ID is required");
			focusView = id;
			cancel = true;
		}
		if (cancel) {
			focusView.requestFocus();
		} else {
			mLoginStatusMessageView.setText("Logging in...");
			showProgress(true);
			try {
				new UserLoginTask()
						.execute("http://www.holycrosschurchjm.com/MIA_mysql.php?userLogin=yes&username="
								+ empID + "&password=" + empPass);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public class UserLoginTask extends AsyncTask<String, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(String... params) {

			JSONArray result = new JSONArray();
			result = db.DBPull(params[0]);
			return db.DBPull(params[0]);
		}

		@Override
		protected void onPostExecute(final JSONArray success) {
			showProgress(false);
			Log.d("emp", success.toString());
			String empID, fName, lName, role;
			empID = fName = lName = role = "";
			try {
				if (!success.getJSONArray(0).getString(0).equals("false")) {
					try {
						empID = success.getJSONArray(0).getString(0);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						fName = success.getJSONArray(0).getString(1);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						lName = success.getJSONArray(0).getString(2);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						role = success.getJSONArray(0).getString(2);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Bundle b = new Bundle();
					String[] info = { empID, fName, lName, role };
					b.putStringArray("info", info);
					try {
						startActivity(new Intent(LoginActivity.this,
								Class.forName("com.libratech.mia.HomeActivity")));
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					pass.setError("Invalid password.");
					pass.requestFocus();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		protected void onCancelled() {
			showProgress(false);
		}
	}

	private void setupLoginForm() {
		id = (EditText) findViewById(R.id.empID);
		pass = (EditText) findViewById(R.id.password);
		pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					if (!isConnected()) {
						Toast.makeText(
								LoginActivity.this,
								"No network connection. Please check you connection and try again.",
								Toast.LENGTH_LONG).show();
					} else {
						attemptLogin();
					}
					return true;
				}
				return false;
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (!isConnected()) {
							Toast.makeText(
									LoginActivity.this,
									"No network connection. Please check you connection and try again.",
									Toast.LENGTH_LONG).show();
						} else {

							attemptLogin();
						}
					}
				});
	}

	public boolean isConnected() {
		ConnectivityManager connectivity = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}
		return false;
	}
}
