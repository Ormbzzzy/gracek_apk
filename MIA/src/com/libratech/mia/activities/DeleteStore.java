package com.libratech.mia.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.adapters.DeleteStoreAdapter;
import com.libratech.mia.models.Store;
import com.libratech.mia.utilities.ActivityControl;
import com.libratech.mia.utilities.DatabaseConnector;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import com.libratech.mia.R;

public class DeleteStore extends Activity implements iRibbonMenuCallback {

    RibbonMenuView rbmView;
    View list, details;
    ListView lv;
    TextView name, city, addr, storeID;
    Button delete, cancel;
    ArrayList<Store> stores = new ArrayList<Store>();
    DatabaseConnector db = new DatabaseConnector();
    Store s;
    AlertDialog.Builder dg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_store);
        dg = new AlertDialog.Builder(this);
        dg.setTitle("Are you sure?");
        dg.setMessage("Doing this will remove all data related to this store from the application!");
        dg.setPositiveButton("Yes, I'm Sure.",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (DatabaseConnector.isConnected(getApplicationContext())) {
                            new delStore()
                                    .execute(DatabaseConnector.getDomain() + "/MIA_mysql.php?deleteStore=yes&comp_id="
                                            + s.getStoreID());
                        } else {
                            Toast.makeText(getApplicationContext(), "Please check your connection and try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        dg.setNegativeButton("Cancel", null);
        setupMenu();
        list = (View) findViewById(R.id.delListView);
        details = (View) findViewById(R.id.details);
        details.setVisibility(View.GONE);
        setupDetails();
        new getInfo()
                .execute(DatabaseConnector.getDomain() + "/MIA_mysql.php?allStores=yes");
    }

    public void setupList() {
        lv = (ListView) list.findViewById(R.id.delList);
        lv.setAdapter(new DeleteStoreAdapter(this, stores));
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                list.setVisibility(View.GONE);
                details.setVisibility(View.VISIBLE);
                s = stores.get(position);
                name.setText(stores.get(position).getCompanyName());
                city.setText(stores.get(position).getCity());
                addr.setText(stores.get(position).getAddress());
                storeID.setText(stores.get(position).getStoreID());
            }

        });
    }

    public void setupDetails() {
        name = (TextView) details.findViewById(R.id.compName);
        city = (TextView) details.findViewById(R.id.city);
        storeID = (TextView) details.findViewById(R.id.compID);
        addr = (TextView) details.findViewById(R.id.addressLine1);
        delete = (Button) details.findViewById(R.id.addStore);
        delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                dg.show();
            }

        });
        cancel = (Button) details.findViewById(R.id.cancel);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                details.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
            }

        });
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {

        if (details.getVisibility() == View.VISIBLE) {
            details.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout, menu);
        return true;
    }

    private void setupMenu() {
        rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
        rbmView.setMenuClickCallback(this);
        rbmView.setMenuItems(R.menu.manager_menu);
    }

    private class delStore extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... url) {

            return db.DBPush(url[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {

            Toast.makeText(getApplicationContext(),
                    "Store successfully deleted.", Toast.LENGTH_SHORT).show();
            details.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }
    }

    private class getInfo extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... url) {

            return db.dbPull(url[0]);

        }

        @Override
        protected void onPreExecute() {
            /*
             * Toast.makeText(getApplicationContext(),
			 * "Gathering store information.", Toast.LENGTH_SHORT).show();
			 */
            stores.clear();
        }

        @Override
        protected void onPostExecute(JSONArray result) {

            String code, storeName, addr, city;
            code = storeName = addr = city = "";
            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    try {
                        code = result.getJSONArray(i).getString(0);
                        storeName = result.getJSONArray(i).getString(1);
                        addr = result.getJSONArray(i).getString(2);
                        city = result.getJSONArray(i).getString(3);
                        Log.d("Store", storeName);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    stores.add(new Store(code, storeName, addr, city));
                }
                setupList();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                rbmView.toggleMenu();
                return true;

            case R.id.logout:
                
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void RibbonMenuItemClick(int itemId, int position) {

        ActivityControl.changeActivity(this, itemId, "StoreReviewActivity");
    }
}