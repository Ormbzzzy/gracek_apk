package com.libratech.mia.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.adapters.HomeAdapter;
import com.libratech.mia.adapters.ReviewAdapter;
import com.libratech.mia.models.Product;
import com.libratech.mia.models.Products;
import com.libratech.mia.models.Scanned;
import com.libratech.mia.models.Store;
import com.libratech.mia.utilities.ActivityControl;
import com.libratech.mia.utilities.DatabaseConnector;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.libratech.mia.R;
import com.libratech.mia.utilities.DownloadImage;

public class StoreReviewActivity extends Activity implements
        iRibbonMenuCallback {


    boolean scanned = true;
    boolean unscanned = false;
    boolean storeSelected = false;
    boolean productSelected = false;
    int selection = 0;
    String dateString = "";
    String upcCode = "";
    ArrayList<Products> products = new ArrayList<Products>();

    ListView lv;
    RibbonMenuView rbmView;
    ReviewAdapter adapter = new ReviewAdapter(this);
    Button bS, bU;
    ProgressBar pb;
    ListView listview;
    ImageView img;
    Button edit;
    TextView upc;
    Button update;
    TextView brand;
    TextView name;
    EditText price;
    CheckBox gctBox;
    TextView weight;
    TextView uom;
    File image;
    private ProgressBar listLoad;

    public static ArrayList<Product> pList = new ArrayList<Product>();
    ArrayList<Store> storeList = new ArrayList<Store>();

    DatabaseConnector db = new DatabaseConnector();
    DownloadImage imgDown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_review);
        Date date = new Date();
        dateString = new SimpleDateFormat("yyyy-MM-dd").format(date);
        listLoad = (ProgressBar) findViewById(R.id.allstorereview_progress);
        setupList();
        setupMenu();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        new getInfo()
                .execute(DatabaseConnector.getDomain() + "/MIA_mysql.php?allStores=yes");

    }

    private void setupMenu() {
        rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
        rbmView.setMenuClickCallback(this);
        rbmView.setMenuItems(R.menu.manager_menu);
    }

    private void setupList() {
        lv = (ListView) findViewById(R.id.reviewListview);
        lv.setAdapter(adapter);
        lv.setFastScrollEnabled(true);
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                storeSelected = true;
                selection = position;
                setupStore(position);
            }
        });
    }

    private void setupStore(final int position) {
        setContentView(R.layout.home_activity);
        setupMenu();
        int s, u, max;
        s = products.get(position).getScanned().size();
        u = products.get(position).getUnscanned().size();
        max = u + s;
        ((TextView) findViewById(R.id.ScanProgressText)).setText("" + s + "/"
                + max + " products scanned at "
                + storeList.get(position).getCompanyName());
        bS = (Button) findViewById(R.id.scannedbutton);
        bU = (Button) findViewById(R.id.unscannedbutton);
        pb = (ProgressBar) findViewById(R.id.scanProgressBar);
        listview = (ListView) findViewById(R.id.mainlistview);
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                productSelected = true;
                setContentView(R.layout.update);
                Scanned p = (Scanned) parent.getItemAtPosition(position);
                img = (ImageView) findViewById(R.id.updateImage);
                edit = (Button) findViewById(R.id.edit);
                edit.setText("Cancel");
                edit.setVisibility(View.GONE);
                upc = (TextView) findViewById(R.id.upc);
                update = (Button) findViewById(R.id.update);
                brand = (TextView) findViewById(R.id.Brand);
                name = (TextView) findViewById(R.id.Name);
                price = (EditText) findViewById(R.id.Price);
                gctBox = (CheckBox) findViewById(R.id.gct);
                weight = (TextView) findViewById(R.id.weight);
                uom = (TextView) findViewById(R.id.uom);
                update.setVisibility(View.GONE);
                upc.setText(p.getUpcCode());
                upc.setFocusable(false);
                name.setFocusable(false);
                price.setFocusable(false);
                gctBox.setClickable(false);
                weight.setFocusable(false);
                uom.setFocusable(false);
                gctBox.setChecked(p.getGct().equalsIgnoreCase("yes"));
                brand.setText(p.getBrand());
                name.setText(p.getProductName());
                price.setText(String.valueOf(p.getPrice()));
                weight.setText(p.getWeight());
                uom.setText(p.getUom());
                upcCode = (String) upc.getText();
                while (upcCode.charAt(0) == '0') {
                    upcCode = upcCode.replaceFirst("0", "");
                }

                Log.d("url", "http://ma.holycrosschurchjm.com/" + upcCode
                        + ".jpg");
                System.gc();
                img.setImageDrawable(getResources().getDrawable(
                        R.drawable.image_loading));
                File f = new File(Environment.getExternalStorageDirectory()
                        .toString() + "/MIA/images", ".nomedia");
                if (!f.exists()) {
                    f.getParentFile().mkdirs();
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                image = new File(Environment.getExternalStorageDirectory()
                        .toString() + "/MIA/images", upc.getText() + ".jpg");
                if (!image.exists()) {
                    if (isConnected()) {
                        imgDown = new DownloadImage(img, image, getApplicationContext());
                        imgDown.cancel(true);
                        imgDown.execute("http://ma.holycrosschurchjm.com/"
                                + upcCode + ".jpg");
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Image cannot be loaded. Please check your connection.",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Bitmap b = BitmapFactory.decodeFile(image.getAbsolutePath());
                    int nh = (int) (b.getHeight() / (b.getWidth() / 200));
                    Bitmap scaled = Bitmap.createScaledBitmap(b, 200, nh, true);
                    img.setImageBitmap(scaled);
                }
            }
        });
        bS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                listview.setAdapter(new HomeAdapter(StoreReviewActivity.this,
                        products.get(position).getScanned()));
                bU.setBackgroundColor(bS.getSolidColor());
                bS.setBackgroundColor(bS.getHighlightColor());
            }

        });

        bU.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                listview.setAdapter(new HomeAdapter(StoreReviewActivity.this,
                        products.get(position).getUnscanned()));
                bS.setBackgroundColor(bU.getSolidColor());
                bU.setBackgroundColor(bU.getHighlightColor());
            }
        });
        bS.performClick();

    }

    private class getInfo extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... url) {

            return db.dbPull(url[0]);

        }

        @Override
        protected void onPreExecute() {
            storeList.clear();
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
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    storeList.add(new Store(code, storeName, addr, city));
                }
                new getAllProducts()
                        .execute(DatabaseConnector.getDomain() + "/MIA_mysql.php?allproducts=yes");
            }
        }
    }

    class getAllProducts extends AsyncTask<String, Void, JSONArray> {
        protected JSONArray doInBackground(String... url) {
            return db.dbPull(url[0]);
        }

        @Override
        protected void onPreExecute() {
            pList.clear();
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
                    photo = result.getJSONArray(i).getString(7);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                pList.add(new Product(upc, weight, name, desc, brand, category,
                        uom, price, gct, photo));
            }

            new getProducts().execute(storeList);
        }
    }

    class getProducts extends AsyncTask<ArrayList<Store>, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            products.clear();
        }

        @Override
        protected JSONArray doInBackground(ArrayList<Store>... stores) {
            float price = (float) 0.00;
            String storeName, upc, gct;
            storeName = upc = gct = "";
            JSONArray result = db
                    .dbPull(DatabaseConnector.getDomain() + "/MIA_mysql.php?rec_date=" + dateString + "&allscannedproducts=yes");

            for (int j = 0; j < stores[0].size(); j++) {
                Store s = stores[0].get(j);
                products.add(j, new Products());
                for (Product p : pList) {
                    products.get(j).getUnscanned().add(new Scanned(p, false));
                }
                for (int i = 0; i < result.length(); i++) {
                    try {
                        storeName = result.getJSONArray(i).getString(0);
                        upc = result.getJSONArray(i).getString(1);
                        price = (float) result.getJSONArray(i).getDouble(8);
                        gct = result.getJSONArray(i).getString(9);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (storeName.equals(s.getCompanyName())) {
                        for (Product p : pList) {
                            if (p.getUpcCode().equals(upc)) {
                                int temp = 0;
                                for (int k = 0; k < products.get(j).getUnscanned().size(); k++) {
                                    if (products.get(j).getUnscanned().get(k).getUpcCode().equals(p.getUpcCode()))
                                        temp = k;
                                    break;
                                }
                                products.get(j).getUnscanned().remove(temp);
                                p.setPrice(price);
                                p.setGct(gct);
                                products.get(j)
                                        .getScanned()
                                        .add(new Scanned(p, true));
                            }
                        }
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            adapter = new ReviewAdapter(StoreReviewActivity.this, products,
                    storeList);
            lv.setAdapter(adapter);
            listLoad.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (storeList.size() != 0)
            listLoad.setVisibility(ProgressBar.GONE);
    }

    @Override
    protected void onPause() {

        
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        if (productSelected) {
            productSelected = false;
            imgDown.cancel(true);

            setContentView(R.layout.home_activity);
            LayoutInflater inflater = (LayoutInflater) StoreReviewActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.home_activity, null);
            setupMenu();
            setupStore(selection);
            bS.performClick();
        } else if (storeSelected) {
            storeSelected = false;
            setContentView(R.layout.store_review);
            LayoutInflater inflater = (LayoutInflater) StoreReviewActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.store_review, null);
            listLoad = (ProgressBar) findViewById(R.id.allstorereview_progress);
            if (storeList.size() != 0) {
                listLoad.setVisibility(View.GONE);
            }
            setupMenu();
            setupList();
            lv.setAdapter(adapter);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
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
                products.clear();
                
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

    class downloadImage extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... fileUrl) {
            URL myFileUrl = null;

            try {
                if (isCancelled())
                    cancel(true);
                myFileUrl = new URL(fileUrl[0]);
                if (isCancelled())
                    cancel(true);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                HttpURLConnection conn = (HttpURLConnection) myFileUrl
                        .openConnection();
                conn.setDoInput(true);
                if (isCancelled())
                    cancel(true);
                conn.connect();
                if (isCancelled())
                    cancel(true);
                InputStream is = conn.getInputStream();
                if (isCancelled())
                    cancel(true);
                Log.i("im connected", "Downloading image");
                return BitmapFactory.decodeStream(is);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            if (isCancelled())
                cancel(true);

        }

        @Override
        protected void onPostExecute(Bitmap result) {

            if (result == null) {
                img.setImageDrawable(getResources().getDrawable(
                        R.drawable.no_image));
                Toast.makeText(getApplicationContext(), "Image not found.",
                        Toast.LENGTH_SHORT).show();
            } else {
                img.setImageBitmap(result);
                int nh = (int) (result.getHeight() / (result.getWidth() / 200));
                Bitmap scaled = Bitmap
                        .createScaledBitmap(result, 200, nh, true);
                img.setImageBitmap(scaled);

                image.getParentFile().mkdirs();
                try {
                    FileOutputStream out = new FileOutputStream(image);
                    result.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                    // MediaStore.Images.Media.insertImage(getContentResolver(),
                    // image.getAbsolutePath(), image.getName(),
                    // image.getName());
                    Toast.makeText(getApplicationContext(), "Image saved",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
