package com.libratech.mia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private DatabaseConnector db;
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
		// getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

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
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
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

	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			db.DBLogin(id.getText().toString(),pass.getText().toString());
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {

				finish();
			} else {
				pass.setError("Invalid password.");
				pass.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	private String getEmpId() {
		Bundle fromScanner = getIntent().getExtras();
		return fromScanner.getString("code");
	}

	private void setupLoginForm() {
		empID = getIntent().getStringExtra(EXTRA_EMAIL);
		id = (EditText) findViewById(R.id.empID);
		id.setVisibility(View.GONE);
		id.setText(getEmpId());

		pass = (EditText) findViewById(R.id.password);
		pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
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
						attemptLogin();
					}
				});
	}
}
