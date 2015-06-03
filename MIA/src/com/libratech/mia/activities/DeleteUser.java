package com.libratech.mia.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.libratech.mia.adapters.UserAdapter;
import com.libratech.mia.models.User;
import com.libratech.mia.utilities.ActivityControl;
import com.libratech.mia.utilities.DatabaseConnector;

import org.json.JSONArray;

import java.util.ArrayList;

import com.libratech.mia.R;

public class DeleteUser extends Activity implements iRibbonMenuCallback {

    ListView lv;
    View details;
    TextView idTv, fName, lName, role;
    Button confirm, cancel;
    RibbonMenuView rbmView;
    ArrayList<User> emp = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);
        setupMenu();
        lv = (ListView) findViewById(R.id.allUsers);
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                User u = (User) lv.getAdapter().getItem(position);
                lv.setVisibility(View.GONE);
                details.setVisibility(View.VISIBLE);
                idTv.setText(u.getId());
                fName.setText(u.getfName());
                lName.setText(u.getlName());
                role.setText(u.getRole());
            }

        });
        details = (View) findViewById(R.id.userDetails);
        details.setVisibility(View.GONE);
        idTv = (TextView) details.findViewById(R.id.userID);
        fName = (TextView) details.findViewById(R.id.firstName);
        lName = (TextView) details.findViewById(R.id.lastName);
        role = (TextView) details.findViewById(R.id.userRole);
        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        confirm = (Button) findViewById(R.id.deleteUser);
        confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                details.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
            }
        });
        confirm = (Button) findViewById(R.id.deleteUser);
        confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        DeleteUser.this);
                builder.setTitle("Warning");
                builder.setMessage("Doing this will remove the user from the database. Do you want to continue?");
                builder.setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialogInterface, int i) {
                                if (DatabaseConnector.isConnected(getApplicationContext())) {
                                    new deleteUser()
                                            .execute(DatabaseConnector.getDomain() + "/MIA_mysql.php?deleteUser=yes&emp_id="
                                                    + idTv.getText());
                                    details.setVisibility(View.GONE);
                                    lv.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Please check your connection and try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialogInterface, int i) {
                                details.setVisibility(View.GONE);
                                lv.setVisibility(View.VISIBLE);
                            }
                        });
                builder.create().show();
            }

        });
        getActionBar().setDisplayHomeAsUpEnabled(true);
        new getUsers()
                .execute(DatabaseConnector.getDomain() + "/MIA_mysql.php?allUsers=yes");

    }

    private void setupMenu() {
        rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
        rbmView.setMenuClickCallback(this);
        rbmView.setMenuItems(R.menu.manager_menu);
    }

    private class deleteUser extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... url) {
            new DatabaseConnector().DBPush(url[0]);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Toast.makeText(getApplicationContext(), "User successfuly deleted",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private class getUsers extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... url) {
            return new DatabaseConnector().dbPull(url[0]);
        }

        @Override
        protected void onPostExecute(JSONArray result) {

            super.onPostExecute(result);
            String id, fName, lName, role;
            id = fName = lName = role = "";
            emp.clear();
            for (int i = 0; i < result.length(); i++) {
                try {
                    id = result.getJSONArray(i).getString(0);
                    fName = result.getJSONArray(i).getString(1);
                    lName = result.getJSONArray(i).getString(2);
                    role = result.getJSONArray(i).getString(3);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                emp.add(new User(id, fName, lName, role));
            }
            lv.setAdapter(new UserAdapter(DeleteUser.this, emp));
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
