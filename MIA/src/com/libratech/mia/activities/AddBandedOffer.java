package com.libratech.mia.activities;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.R;
import com.libratech.mia.adapters.AllAdapter;
import com.libratech.mia.adapters.BandedAdapter;
import com.libratech.mia.adapters.BandedOfferAdapter;
import com.libratech.mia.models.BandedProduct;
import com.libratech.mia.models.Product;
import com.libratech.mia.utilities.ActivityControl;
import com.libratech.mia.utilities.DatabaseConnector;
import com.libratech.mia.utilities.ScanActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddBandedOffer extends Activity implements iRibbonMenuCallback {

    RibbonMenuView rbmView;
    View allListView, bandedListView, detailView, offerListView;
    ListView bandedList, offerList;
    ExpandableListView exlv;
    Button imgB;
    TextView name, price, weight, brand, uom, upc;
    EditText bPrice, search;
    ArrayList<Product> bandedProd = new ArrayList<Product>();
    ArrayList<Product> allProd = HomeActivity.aProducts;
    ArrayList<BandedProduct> bList = new ArrayList<BandedProduct>();
    BandedProduct currentBanded = new BandedProduct();
    Button remove, submit;
    String compID = HomeActivity.storeID;
    String empID = HomeActivity.empID;
    boolean newBanded = false;
    boolean bandedSelected = false;
    boolean bandedItemSelected = false;
    int selectedBanded = 0;
    BandedAdapter adapter;
    MenuItem add, del, scan;
    BandedProduct bp = new BandedProduct();
    Builder delDG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_banded_offer);

        rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
        rbmView.setMenuClickCallback(this);
        rbmView.setMenuItems(R.menu.home);
        allListView = (View) findViewById(R.id.AllbandedListView);
        exlv = (ExpandableListView) allListView.findViewById(R.id.AllBandedList);
        search = (EditText) allListView.findViewById(R.id.inputSearch);
        allListView.setVisibility(View.GONE);
        bandedListView = (View) findViewById(R.id.bandedListView);
        bandedList = (ListView) bandedListView.findViewById(R.id.bandedList);
        bPrice = (EditText) bandedListView.findViewById(R.id.bandedPrice);
        bPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        imgB = (Button) bandedListView.findViewById(R.id.add);
        submit = (Button) bandedListView.findViewById(R.id.submit);
        detailView = (View) findViewById(R.id.bandedDetail);
        offerListView = (View) findViewById(R.id.bandedOffers);
        offerList = (ListView) offerListView.findViewById(R.id.banded);
        name = (TextView) detailView.findViewById(R.id.name);
        brand = (TextView) detailView.findViewById(R.id.brand);
        weight = (TextView) detailView.findViewById(R.id.weight);
        upc = (TextView) detailView.findViewById(R.id.upc);
        uom = (TextView) detailView.findViewById(R.id.uom);
        remove = (Button) detailView.findViewById(R.id.cancel);
        bandedList.setAdapter(adapter);

        delDG = new Builder(AddBandedOffer.this);
        delDG.setTitle("Confirm Delete");
        delDG.setMessage("Doing this will remove the banded offfer from the system!");
        delDG.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Date date = new Date();
                        String dateString = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss").format(date);
                        new PushBanded()
                                .execute((DatabaseConnector.getDomain() + "/MIA_mysql.php?deleteBandedProduct=yes&comp_id="
                                        + compID
                                        + "&rec_date="
                                        + dateString
                                        + "&band_prods=" + currentBanded
                                        .getBandedID()).replace(" ", "%20"));
                    }
                });
        exlv.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                Product p = ((AllAdapter) exlv.getExpandableListAdapter()).getProduct(
                        groupPosition, childPosition);
                allListView.setVisibility(View.GONE);
                bandedListView.setVisibility(View.VISIBLE);
                if (newBanded) {
                    currentBanded.getProducts().add(p);
                    adapter = new BandedAdapter(getApplicationContext(),
                            currentBanded.getProducts());
                    bandedList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    currentBanded.getProducts().add(p);
                    adapter = new BandedAdapter(getApplicationContext(),
                            currentBanded.getProducts());
                    bandedList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                invalidateOptionsMenu();

                return false;
            }

        });

        bandedList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                bandedItemSelected = true;
                selectedBanded = position;
                bandedListView.setVisibility(View.GONE);
                detailView.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
                Product p = currentBanded.getProducts().get(position);
                name.setText(p.getProductName());
                upc.setText(p.getUpcCode());
                uom.setText(p.getUom());
                weight.setText(p.getWeight());
                brand.setText(p.getBrand());
            }
        });
        remove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                for (Product p : allProd) {
                    if (p.getUpcCode().equals(upc.getText())) {
                        currentBanded.getProducts().remove(selectedBanded);
                        adapter.notifyDataSetChanged();
                        detailView.setVisibility(View.GONE);
                        bandedListView.setVisibility(View.VISIBLE);
                        bandedItemSelected = false;
                        invalidateOptionsMenu();
                    }
                }
            }
        });
        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Builder dg = new Builder(AddBandedOffer.this);
                dg.setTitle("Confirm Submission");
                String s = "Confirm banded offer of:\n\n";
                for (Product p : currentBanded.getProducts()) {
                    s += p.getProductName() + "\n";
                }
                s += "\nFor $" + bPrice.getText();
                dg.setMessage(s);
                dg.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                String prods = currentBanded.getProducts()
                                        .get(0).getUpcCode();
                                for (int i = 1; i < currentBanded.getProducts()
                                        .size(); i++) {
                                    prods += "-"
                                            + currentBanded.getProducts()
                                            .get(i).getUpcCode();
                                }
                                Date date = new Date();
                                String dateString = new SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss").format(date);
                                if (submit.getText().equals("Update")) {
                                    new PushBanded()
                                            .execute((DatabaseConnector.getDomain() + "/MIA_mysql.php?updateBandedProduct=yes&merch_id="
                                                    + empID
                                                    + "&comp_id="
                                                    + compID
                                                    + "&rec_date="
                                                    + dateString
                                                    + "&band_prods="
                                                    + currentBanded.getBandedID()
                                                    + "&band_price=" + bPrice
                                                    .getText() + "&new_band_prods=" + prods).replace(" ",
                                                    "%20"));
                                    submit.setText("Submit");
                                } else {
                                    new PushBanded()
                                            .execute((DatabaseConnector.getDomain() + "/MIA_mysql.php?addBandedProduct=yes&merch_id="
                                                    + empID
                                                    + "&comp_id="
                                                    + compID
                                                    + "&rec_date="
                                                    + dateString
                                                    + "&band_prods="
                                                    + prods
                                                    + "&band_price=" + bPrice
                                                    .getText()).replace(" ",
                                                    "%20"));
                                }
                            }
                        });
                try {
                    if (Float.parseFloat(bPrice.getText().toString()) <= 0.00) {
                        Toast.makeText(getApplicationContext(),
                                "That price is not valid.", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        dg.show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "That price is not valid.", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        offerList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                bandedSelected = true;
                invalidateOptionsMenu();
                submit.setText("Update");
                currentBanded = bList.get(position);
                adapter = new BandedAdapter(getApplicationContext(),
                        currentBanded.getProducts());
                bPrice.setText(String.valueOf(currentBanded.getTotalPrice()));
                bandedList.setAdapter(adapter);
                offerListView.setVisibility(View.GONE);
                bandedListView.setVisibility(View.VISIBLE);
            }
        });
        bandedList.setAdapter(new BandedAdapter(this, bandedProd));

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                ((AllAdapter) exlv.getExpandableListAdapter()).filterData(cs
                        .toString());
            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

        });
        Date date = new Date();
        String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(date);
        new getBandedProducts()
                .execute((DatabaseConnector.getDomain() + "/MIA_mysql.php?bandedProducts=yes&comp_id="
                        + compID + "&rec_date=" + dateString).replace(" ",
                        "%20"));
        exlv.setAdapter(new AllAdapter(getApplicationContext(), allProd));
        getActionBar().setHomeButtonEnabled(true);
    }

    class PushBanded extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... url) {
            return new DatabaseConnector().DBPush(url[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                if (del.isVisible()) {
                    Toast.makeText(getApplicationContext(),
                            "Offer successfully removed.", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Offer successfully submitted.", Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "An error as occurred",
                        Toast.LENGTH_SHORT).show();
            }
            bandedListView.setVisibility(View.GONE);
            offerListView.setVisibility(View.VISIBLE);
            newBanded = false;
            bandedSelected = false;
            bandedItemSelected = false;
            invalidateOptionsMenu();
            Date date = new Date();
            String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(date);
            new getBandedProducts()
                    .execute((DatabaseConnector.getDomain() + "/MIA_mysql.php?bandedProducts=yes&comp_id="
                            + compID + "&rec_date=" + dateString).replace(" ",
                            "%20"));
            super.onPostExecute(result);
        }

    }

    class getBandedProducts extends AsyncTask<String, Void, JSONArray> {
        protected JSONArray doInBackground(String... url) {
            return new DatabaseConnector().dbPull(url[0]);
        }

        @Override
        protected void onPreExecute() {

            Toast.makeText(AddBandedOffer.this, "Loading banded products.",
                    Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            String prods = "";
            float price = (float) 0.00;
            bList.clear();
            try {
                for (int i = 0; i < result.length(); i++) {
                    BandedProduct b = new BandedProduct();
                    price = Float.parseFloat(result.getJSONArray(i)
                            .getString(1));
                    prods = result.getJSONArray(i).getString(0);
                    for (String upc : prods.split("[-]")) {
                        for (Product p : allProd) {
                            if (p.getUpcCode().equals(upc))
                                b.getProducts().add(p);
                        }
                    }
                    b.setBandedID(prods);
                    b.settotalPrice(price);
                    bList.add(b);
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            offerList.setAdapter(new BandedOfferAdapter(getApplicationContext(),
                    bList));
            Toast.makeText(AddBandedOffer.this, "Banded products loaded.",
                    Toast.LENGTH_SHORT).show();

        }
    }

    class getAllProducts extends AsyncTask<String, Void, JSONArray> {
        protected JSONArray doInBackground(String... url) {
            return new DatabaseConnector().dbPull(url[0]);
        }

        @Override
        protected void onPreExecute() {

            Toast.makeText(AddBandedOffer.this, "Loading all products.",
                    Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            String upc, name, desc, brand, category, uom, gct, photo, weight;
            name = desc = brand = category = uom = gct = photo = upc = weight = "";
            float price = (float) 0.00;
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
                allProd.add(new Product(upc, weight, name, desc, brand,
                        category, uom, price, gct, photo));

            }
            exlv.setAdapter(new AllAdapter(getApplicationContext(), allProd));
            Toast.makeText(AddBandedOffer.this, "All roducts loaded.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (data.hasExtra("code"))
                upc.setText(data.getStringExtra("code"));
            String s = upc.getText().toString();
            for (Product p : allProd) {
                if (p.getUpcCode().equals(s)) {
                    currentBanded.getProducts().add(p);
                    allListView.setVisibility(View.GONE);
                    bandedListView.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_banded_offer, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        del = menu.findItem(R.id.removeBanded);
        add = menu.findItem(R.id.newBanded);
        scan = menu.findItem(R.id.bandedScan);
        scan.setVisible(false);
        del.setVisible(bandedSelected);
        if (offerListView.getVisibility() == View.VISIBLE) {
            del.setVisible(false);
            add.setVisible(true);
        }
        if (bandedListView.getVisibility() == View.VISIBLE) {
            add.setVisible(true);
            del.setVisible(true);
            if (currentBanded.getProducts().size() == 0) {
                del.setVisible(false);
            }
        }
        if (allListView.getVisibility() == View.VISIBLE) {
            scan.setVisible(true);
        }
        if (detailView.getVisibility() == View.VISIBLE) {
            del.setVisible(true);
            add.setVisible(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (detailView.getVisibility() == View.VISIBLE) {
            bandedItemSelected = false;
            detailView.setVisibility(View.GONE);
            bandedListView.setVisibility(View.VISIBLE);
            invalidateOptionsMenu();
        } else {
            if (allListView.getVisibility() == View.VISIBLE) {
                if (newBanded) {
                    bandedSelected = newBanded = bandedItemSelected = false;
                    allListView.setVisibility(View.GONE);
                    offerListView.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                } else {
                    allListView.setVisibility(View.GONE);
                    bandedListView.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                }
            } else {
                if (bandedListView.getVisibility() == View.VISIBLE) {
                    bandedListView.setVisibility(View.GONE);
                    offerListView.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                } else if (offerListView.getVisibility() == View.VISIBLE) {

                    finish();
                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.newBanded:
                if (newBanded) {
                    allListView.setVisibility(View.VISIBLE);
                } else {
                    newBanded = true;
                    invalidateOptionsMenu();
                    offerListView.setVisibility(View.GONE);
                    bandedListView.setVisibility(View.VISIBLE);
                }
                return true;
            case R.id.removeBanded:
                if (bandedItemSelected) {
                    currentBanded.getProducts().remove(selectedBanded);
                    adapter.notifyDataSetChanged();
                    bandedItemSelected = false;
                    detailView.setVisibility(View.GONE);
                    bandedList.setVisibility(View.VISIBLE);
                } else {
                    delDG.show();
                }
                return true;
            case R.id.bandedScan: {
                Bundle b = new Bundle();
                b.putString("parent", "AddBandedOffer");
                Intent i = new Intent(getApplicationContext(), ScanActivity.class);
                i.putExtras(b);
                startActivityForResult(i, 1);
            }
            return true;
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

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero,
                                        int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1)
                    + "}+((\\.[0-9]{0," + (digitsAfterZero - 1)
                    + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    }

    @Override
    public void RibbonMenuItemClick(int itemId, int position) {
        ActivityControl.changeActivity(this, itemId, "HomeActivity");
    }

}