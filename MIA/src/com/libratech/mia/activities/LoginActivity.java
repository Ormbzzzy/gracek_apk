package com.libratech.mia.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.adapters.SpinnerAdapter;
import com.libratech.mia.models.Store;
import com.libratech.mia.utilities.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.libratech.mia.R;

public class LoginActivity extends Activity {

    private DatabaseConnector db = new DatabaseConnector();
    public static String empID;
    private String empPass;
    private ArrayList<Store> stores = new ArrayList<Store>();
    private ArrayList<Store> tempStores = new ArrayList<Store>();
    public ArrayList<String> spinList = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    public static String storeID = "";
    private Bundle b = new Bundle();
    private Location curLoc, storeLoc;
    private boolean locChange;
    public static String storeName = "";

    private EditText id;
    private EditText pass;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;
    private Dialog dg;
    private Button confirm;
    private Button cancel;
    private CustomSpinner sp;

    private LocationManager locMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.login_activity);
        startUp();
    }

    private boolean requestLocation() {
        locMan = (LocationManager) getApplicationContext().getSystemService(
                Context.LOCATION_SERVICE);
        if (!locMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("GPS", " disabled");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location is Disabled");
            builder.setMessage("Please enable location based services.");
            builder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface,
                                            int i) {
                            startActivityForResult(
                                    new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                                    1);
                        }
                    });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
            startUp();
        }
        return locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void startUp() {
        storeLoc = new Location(LocationManager.GPS_PROVIDER);
        curLoc = getBestLocation();
        adapter = new SpinnerAdapter(LoginActivity.this,
                android.R.layout.simple_spinner_item, spinList);
        adapter.setNotifyOnChange(true);
        // Log.d("curLoc", "" + curLoc.getLatitude() + " " +
        // curLoc.getLongitude());
        dg = new Dialog(LoginActivity.this);
        dg.setContentView(R.layout.dialog);
        dg.setTitle("Select Store");
        dg.setCanceledOnTouchOutside(false);
        sp = (CustomSpinner) dg.findViewById(R.id.storeSpinner);
        confirm = (Button) dg.findViewById(R.id.spinnerButton);
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!storeID.equals("")) {
                    for (Store s : stores) {
                        if (s.getStoreID().equals(storeID))
                            storeName = s.getCompanyName();
                    }

                    b.putString("name", storeName);
                    b.putString("store", storeID);
                    try {
                        dg.dismiss();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class)
                                .putExtras(b));
                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please select a store.", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        cancel = (Button) dg.findViewById(R.id.spinnerCancel);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dg.dismiss();
            }

        });
        sp.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (arg2 == 0) {
                    storeID = "";
                } else {
                    storeID = stores.get(arg2 - 1).getStoreID();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        setupLoginForm();

    }

    @Override
    protected void onPause() {
        dg.dismiss();
        
        pass.setText("");
        super.onPause();
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
                String[] s = {empID, empPass};
                new UserLoginTask().execute(s);
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

        protected JSONArray doInBackground(String... params) {
            ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair("userLogin", "yes"));
            nvp.add(new BasicNameValuePair("username", "" + params[0]));
            nvp.add(new BasicNameValuePair("password", "" + params[1]));
            return new DatabaseConnector().DBSubmit(nvp);
        }

        protected void onPostExecute(JSONArray success) {
            showProgress(false);
            String id, fName, lName, role;
            id = fName = lName = role = "";
            if (success.length() > 0) {
                Log.d("success", success.toString());
                try {
                    id = success.getString(0);
                    fName = success.getString(1);
                    lName = success.getString(2);
                    role = success.getString(3);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                success = new JSONArray();
                Log.d("user", id + " " + fName + " " + lName + " " + " " + role);
                String[] user = {id, fName, lName, role};
                b.putStringArray("user", user);
                Toast.makeText(getApplicationContext(), "Login successful",
                        Toast.LENGTH_SHORT).show();
                db.clear();
                showProgress(false);
                id = fName = lName = role = "";
                if (user[3].equals("manager")) {
                    try {
                        startActivity(new Intent(
                                getApplicationContext(), StoreReviewActivity.class)
                                .putExtras(b));
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                } else {
                    new getStoreInfo()
                            .execute(DatabaseConnector.getDomain() + "/MIA_mysql.php?workLoc=yes&merch_id="
                                    + user[0]);
                    dg.show();
                }
            } else {
                pass.setError("Invalid username or password.");
                pass.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }
    // GPS SERVICES (REMOVED UNTTIL A POSSIBLE FUTURE VERSION)
//    class getStores extends AsyncTask<String, Void, JSONArray> {
//        protected JSONArray doInBackground(String... url) {
//            return db.dbPull(url[0]);
//        }
//
//        @Override
//        protected void onPostExecute(JSONArray result) {
//            String code, name, addr, city;
//            code = name = addr = city = "";
//            if (result != null) {
//                for (int i = 0; i < result.length(); i++) {
//                    try {
//                        code = result.getJSONArray(i).getString(0);
//                        name = result.getJSONArray(i).getString(1);
//                        addr = result.getJSONArray(i).getString(2);
//                        city = result.getJSONArray(i).getString(3);
//                    } catch (JSONException e1) {
//                        e1.printStackTrace();
//                    }
//                    tempStores.add(new Store(code, name, addr, city));
//                }
//                new getLocation().execute(tempStores);
//            }
//        }
//    }
//
//    class getLocation extends
//            AsyncTask<ArrayList<Store>, Void, ArrayList<Store>> {
//        protected ArrayList<Store> doInBackground(ArrayList<Store>... store) {
//
//            List<Address> addresses;
//            Geocoder geocoder = new Geocoder(LoginActivity.this);
//            spinList.clear();
//            stores.clear();
//            try {
//                for (int i = 0; i < store[0].size(); i++) {
//                    Store s = store[0].get(i);
//                    addresses = geocoder.getFromLocationName(s.getAddress()
//                            + "," + s.getCity() + ", jamaica", 1);
//                    if (addresses.size() > 0) {
//                        double latitude = addresses.get(0).getLatitude();
//                        double longitude = addresses.get(0).getLongitude();
//                        storeLoc.setLatitude(latitude);
//                        storeLoc.setLongitude(longitude);
//                        Log.d("Store Location",
//                                s.getCompanyName() + " "
//                                        + storeLoc.getLatitude() + " "
//                                        + storeLoc.getLongitude()
//                                        + "Distance: "
//                                        + curLoc.distanceTo(storeLoc) + "m "
//                                        + "addr + city");
//                        if (curLoc.distanceTo(storeLoc) < 40000) {
//                            Log.d("Added", s.getCompanyName());
//                            spinList.add(s.getCompanyName());
//                            stores.add(s);
//                        }
//                    } else {
//                        addresses = geocoder.getFromLocationName(s.getAddress()
//                                + ",jamaica", 1);
//                        if (addresses.size() > 0) {
//                            double latitude = addresses.get(0).getLatitude();
//                            double longitude = addresses.get(0).getLongitude();
//                            storeLoc.setLatitude(latitude);
//                            storeLoc.setLongitude(longitude);
//                            Log.d("Store Location",
//                                    s.getCompanyName() + " "
//                                            + storeLoc.getLatitude() + " "
//                                            + storeLoc.getLongitude()
//                                            + "Distance: "
//                                            + curLoc.distanceTo(storeLoc)
//                                            + "m " + "addr");
//                            if (curLoc.distanceTo(storeLoc) < 40000) {
//                                Log.d("Added", s.getCompanyName());
//                                spinList.add(s.getCompanyName());
//                                stores.add(s);
//                            }
//                        }
//                    }
//                }
//            } catch (IOException e) {
//
//                e.printStackTrace();
//                new getLocation().execute(tempStores);
//            }
//            // spinList.add(0, "- Please select a store -");
//            return store[0];
//        }
//
//        protected void onPostExecute(ArrayList<Store> store) {
//            // adapter = new SpinnerAdapter(LoginActivity.this,
//            // android.R.layout.simple_spinner_item, spinList);
//            // sp.setAdapter(adapter);
//            // adapter.notifyDataSetChanged();
//        }
//    }

    class getStoreInfo extends AsyncTask<String, Void, JSONArray> {
        protected JSONArray doInBackground(String... url) {
            return db.dbPull(url[0]);
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            String tempID, tempName;
            tempID = tempName = "";
            if (result != null) {
                spinList.clear();
                stores.clear();
                spinList.add(0, "- Please select a store -");
                for (int i = 0; i < result.length(); i++) {
                    try {
                        empID = result.getJSONArray(i).getString(0);
                        tempID = result.getJSONArray(i).getString(1);
                        tempName = result.getJSONArray(i).getString(2);
                        spinList.add(tempName);
                        stores.add(new Store(tempID, tempName));
                        Log.d("Stores", "" + i + tempName + spinList.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                sp.setAdapter(adapter);
                sp.setSelection(0);
            }
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
                    if (!DatabaseConnector.isConnected(getApplicationContext())) {
                        Toast.makeText(
                                LoginActivity.this,
                                "No network connection. Please check your connection and try again.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        // if (requestLocation())
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
                        if (!DatabaseConnector.isConnected(getApplicationContext())) {
                            Toast.makeText(
                                    LoginActivity.this,
                                    "No network connection. Please check your connection and try again.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            // if (requestLocation())
                            attemptLogin();
                        }
                    }
                });
    }

    private Location getBestLocation() {
        Location gpslocation = null;
        Location networkLocation = null;

        if (locMan == null)
            locMan = (LocationManager) getApplicationContext()
                    .getSystemService(Context.LOCATION_SERVICE);
        try {
            if (locMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        100, 1, gpsListener);
                gpslocation = locMan
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (locMan.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        100, 1, gpsListener);
                networkLocation = locMan
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (gpslocation == null && networkLocation == null)
            return null;

        if (gpslocation != null && networkLocation != null) {
            if (gpslocation.getTime() < networkLocation.getTime())
                return networkLocation;
            else
                return gpslocation;
        }
        if (gpslocation == null) {
            return networkLocation;
        }
        if (networkLocation == null) {
            return gpslocation;
        }
        return null;
    }

    LocationListener gpsListener = new LocationListener() {
        public void onLocationChanged(Location loc) {
            if (curLoc == null) {
                curLoc = loc;
                locChange = true;
            } else if (curLoc.getLatitude() == loc.getLatitude()
                    && curLoc.getLongitude() == loc.getLongitude()) {
                locChange = false;
                return;
            } else
                locChange = true;
            curLoc = loc;
            if (locChange)
                locMan.removeUpdates(gpsListener);
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        spinList.clear();
        storeID = "";
        storeName = "";
        // sp.setAdapter(new SpinnerAdapter(LoginActivity.this,
        // android.R.layout.simple_spinner_item, spinList));
        // requestLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        // requestLocation();
    }

}