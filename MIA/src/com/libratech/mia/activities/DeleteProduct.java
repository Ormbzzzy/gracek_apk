package com.libratech.mia.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.adapters.AllAdapter;
import com.libratech.mia.models.Product;
import com.libratech.mia.utilities.ActivityControl;
import com.libratech.mia.utilities.DatabaseConnector;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import com.libratech.mia.R;

public class DeleteProduct extends Activity implements iRibbonMenuCallback {

    ArrayList<Product> pList = StoreReviewActivity.pList;
    RibbonMenuView rbmView;
    ExpandableListView listview;
    View details, list;
    AllAdapter ad = new AllAdapter(this, pList);
    EditText search, price;
    TextView upc, brand, name, weight, uom;
    Button update, cancel;
    String gct;
    CheckBox gctBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all);
        setupMenu();
        setupList();

        if (pList == null || pList.size() == 0) {
            new getProducts()
                    .execute(DatabaseConnector.getDomain() + "/MIA_mysql.php?allproducts=yes");
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupMenu() {
        rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
        rbmView.setMenuClickCallback(this);
        rbmView.setMenuItems(R.menu.manager_menu);
    }

    private void setupList() {
        search = (EditText) findViewById(R.id.inputSearch);
        listview = (ExpandableListView) findViewById(R.id.alllistview);
        details = (View) findViewById(R.id.details);
        listview.setFastScrollEnabled(true);
        listview.setAdapter(ad);
        listview.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                setContentView(R.layout.product);
                Product p = ((AllAdapter) listview.getExpandableListAdapter())
                        .getProduct(groupPosition, childPosition);
                upc = (TextView) findViewById(R.id.upc);
                update = (Button) findViewById(R.id.confirm);
                cancel = (Button) findViewById(R.id.scan);
                cancel.setText("Cancel");
                update.setText("Delete");
                brand = (TextView) findViewById(R.id.Brand);
                name = (TextView) findViewById(R.id.Name);
                price = (EditText) findViewById(R.id.Price);
                gctBox = (CheckBox) findViewById(R.id.gct);
                weight = (TextView) findViewById(R.id.weight);
                uom = (TextView) findViewById(R.id.uom);
                gct = "";
                gctBox.setClickable(false);
                upc.setText(p.getUpcCode());
                brand.setText(p.getBrand());
                name.setText(p.getProductName());
                price.setText(String.valueOf(p.getPrice()));
                weight.setText(p.getWeight());
                uom.setText(p.getUom());
                cancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                update.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                DeleteProduct.this);
                        builder.setTitle("Warning");
                        builder.setMessage("Doing this will remove the product from the database. Do you want to continue?");
                        builder.setPositiveButton("Delete",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialogInterface,
                                            int i) {
                                        if (DatabaseConnector.isConnected(getApplicationContext())) {
                                            new delete()
                                                    .execute(DatabaseConnector.getDomain() + "/MIA_mysql.php?deleteProduct=yes&upc_code="
                                                            + upc.getText());
                                            setContentView(R.layout.all);
                                            setupMenu();
                                            setupList();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Please check your connection and try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        builder.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialogInterface,
                                            int i) {
                                        setContentView(R.layout.all);
                                        setupMenu();
                                        setupList();
                                    }
                                });
                        builder.create().show();
                    }

                });
                return false;
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                ((AllAdapter) listview.getExpandableListAdapter())
                        .filterData(cs.toString());
            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

        });
    }

    class delete extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... url) {
            new DatabaseConnector().DBPush(url[0]);
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            Toast.makeText(getApplicationContext(),
                    "Product successfuly deleted", Toast.LENGTH_LONG).show();

            new getProducts()
                    .execute(DatabaseConnector.getDomain() + "/MIA_mysql.php?allproducts=yes");
        }

    }

    class getProducts extends AsyncTask<String, Void, JSONArray> {
        protected JSONArray doInBackground(String... url) {
            return new DatabaseConnector().dbPull(url[0]);
        }

        @Override
        protected void onPreExecute() {

            Toast.makeText(DeleteProduct.this, "Loading products.",
                    Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            String upc, name, desc, brand, category, uom, gct, photo, weight;
            name = desc = brand = category = uom = gct = photo = upc = weight = "";
            float price = (float) 0.00;
            pList.clear();
            // ad = new AllAdapter(DeleteProduct.this, pList);
            // listview.setAdapter(ad);
            // ad.notifyDataSetChanged();
            for (int i = 0; i < result.length(); i++) {
                try {
                    upc = result.getJSONArray(i).getString(0);
                    name = result.getJSONArray(i).getString(1);
                    desc = result.getJSONArray(i).getString(2);
                    brand = result.getJSONArray(i).getString(3);
                    category = result.getJSONArray(i).getString(4);
                    weight = result.getJSONArray(i).getString(5);
                    uom = result.getJSONArray(i).getString(6);
                    photo = result.getJSONArray(i).getString(7);// price
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                pList.add(new Product(upc, weight, name, desc, brand, category,
                        uom, price, gct, photo));
            }
            Toast.makeText(DeleteProduct.this, "Products loaded.",
                    Toast.LENGTH_SHORT).show();
            ad = new AllAdapter(DeleteProduct.this, pList);
            listview.setAdapter(ad);
            // ad.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delete_product, menu);
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
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        
        finish();
    }

    @Override
    public void RibbonMenuItemClick(int itemId, int position) {

        ActivityControl.changeActivity(this, itemId, "StoreReviewActivity");
    }

}