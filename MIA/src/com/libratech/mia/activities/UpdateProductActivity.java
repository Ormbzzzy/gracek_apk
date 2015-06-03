package com.libratech.mia.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.libratech.mia.R;
import com.libratech.mia.models.Product;
import com.libratech.mia.models.Scanned;
import com.libratech.mia.utilities.DatabaseConnector;
import com.libratech.mia.utilities.DownloadImage;

import org.apache.http.NameValuePair;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProductActivity extends Activity {

    String gct, category;
    String storeID = HomeActivity.storeID;
    String empID = HomeActivity.empID;
    String upcCode;
    String mode = "";
    DatabaseConnector db = new DatabaseConnector();
    boolean found = false;
    boolean scan = false;
    DownloadImage imgDown;
    ArrayList<Product> products = HomeActivity.aProducts;
    ArrayList<Scanned> scanned = HomeActivity.sProducts;

    EditText price;
    TextView brand, weight, name, upc, uom;
    RibbonMenuView rbmView;
    Button cancel, update;
    CheckBox gctBox;
    ImageView img;
    File image;
    AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.update);
        img = (ImageView) findViewById(R.id.updateImage);
        cancel = (Button) findViewById(R.id.edit);
        cancel.setText("Cancel");
        upc = (TextView) findViewById(R.id.upc);
        update = (Button) findViewById(R.id.update);
        brand = (TextView) findViewById(R.id.Brand);
        name = (TextView) findViewById(R.id.Name);
        price = (EditText) findViewById(R.id.Price);
        gctBox = (CheckBox) findViewById(R.id.gct);
        weight = (TextView) findViewById(R.id.weight);
        uom = (TextView) findViewById(R.id.uom);
        gct = "";
        price.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        if (getIntent().hasExtra("mode"))
            mode = getIntent().getStringExtra("mode");
        if (getIntent().hasExtra("code")) {
            for (Product p : products) {
                if (getIntent().getStringExtra("code").contains(p.getUpcCode())) {
                    mode = "update";
                    upc.setText(p.getUpcCode());
                    brand.setText(p.getBrand());
                    name.setText(p.getProductName());
                    price.setText(String.valueOf(p.getPrice()));
                    weight.setText(p.getWeight());
                    uom.setText(p.getUom());
                    found = true;
                    Log.d("product", p.toString());
                    break;
                }
            }
            if (found) {
                for (Scanned s : scanned) {
                    if (getIntent().getStringExtra("code").contains(
                            s.getUpcCode())) {
                        scan = true;
                        price.setText(String.valueOf(s.getPrice()));
                        gct = s.getGct();
                        break;
                    }
                }
            }
            if (scan) {
                mode = "update";
            } else {
                mode = "view";
            }
            if (!found) {
                dialog = new AlertDialog.Builder(getApplicationContext());
                dialog.setTitle("New Product Detected");
                dialog.setMessage("This product was not found in the system, would you like to add it?");
                dialog.setPositiveButton("Add Product",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Bundle b = new Bundle();
                                b.putString("code", getIntent().getStringExtra("code"));
                                Intent i = new Intent(getApplicationContext(), AddNewProduct.class).putExtras(b);
                                startActivity(i);
                            }
                        });
                finish();
            }
        } else {
            if (getIntent().hasExtra("product")) {
                upc.setText(getIntent().getStringArrayExtra("product")[0]);
                name.setText(getIntent().getStringArrayExtra("product")[1]);
                brand.setText(getIntent().getStringArrayExtra("product")[2]);
                price.setText(getIntent().getStringArrayExtra("product")[3]);
                weight.setText(getIntent().getStringArrayExtra("product")[4]);
                uom.setText(getIntent().getStringArrayExtra("product")[5]);
                gctBox.setChecked(getIntent().getStringArrayExtra("product")[6]
                        .equalsIgnoreCase("yes"));
                category = getIntent().getStringArrayExtra("product")[7]
                        .toLowerCase();
            }
        }
        if (mode.equals("view")) {
            update.setText("Confirm");
        } else if (mode.equals("update")) {
            update.setText("Update");
        }
        gctBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (gctBox.isChecked()) {
                    gct = "yes";
                } else {
                    gct = "no";
                }
            }
        });
        gctBox.callOnClick();
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                imgDown.cancel(true);
                finish();
            }
        });

        update.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Double.parseDouble(price.getText().toString()) == 0.0) {
                    Toast.makeText(UpdateProductActivity.this,
                            "Price cannot be zero.", Toast.LENGTH_LONG).show();
                } else {
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    Date date = new Date();
                    String dateString = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss").format(date);
                    String product[] = {upc.getText().toString(), "MER-00001",
                            "COMP-00001", dateString,
                            price.getText().toString(), gct};
                    Log.d("to DB", product[0] + " " + product[1] + " "
                            + product[2] + " " + product[3] + " " + product[4]
                            + " " + product[5]);
                    if (DatabaseConnector.isConnected(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(),
                                "Saving changes.", Toast.LENGTH_SHORT).show();
                        update.setClickable(false);
                        update.setTextColor(getResources().getColor(
                                R.color.gray));
                        if (mode.equals("update")) {
                            Log.d("url",
                                    DatabaseConnector.getDomain()
                                            + "/MIA_mysql.php?updateScannedProduct=yes&upc_code="
                                            + upc.getText() + "&merch_id="
                                            + empID + "&comp_id=" + storeID
                                            + "&rec_date="
                                            + dateString.replace(" ", "%20")
                                            + "&price=" + price.getText()
                                            + "&gct=" + gct);
                            new pushProduct().execute(DatabaseConnector
                                    .getDomain()
                                    + "/MIA_mysql.php?updateScannedProduct=yes&upc_code="
                                    + upc.getText()
                                    + "&merch_id="
                                    + empID
                                    + "&comp_id="
                                    + storeID
                                    + "&rec_date="
                                    + dateString.replace(" ", "%20")
                                    + "&price="
                                    + price.getText()
                                    + "&gct="
                                    + gct);
                        } else if (mode.equals("view")) {
                            Log.d("url",
                                    DatabaseConnector.getDomain()
                                            + "/MIA_mysql.php?addScannedProduct=yes&upc_code="
                                            + upc.getText() + "&merch_id="
                                            + empID + "&comp_id=" + storeID
                                            + "&rec_date="
                                            + dateString.replace(" ", "%20")
                                            + "&price=" + price.getText()
                                            + "&gct=" + gct);
                            new pushProduct().execute(DatabaseConnector
                                    .getDomain()
                                    + "/MIA_mysql.php?addScannedProduct=yes&upc_code="
                                    + upc.getText()
                                    + "&merch_id="
                                    + empID
                                    + "&comp_id="
                                    + storeID
                                    + "&rec_date="
                                    + dateString.replace(" ", "%20")
                                    + "&price="
                                    + price.getText()
                                    + "&gct="
                                    + gct);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please check your connection and try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                    // new pushProduct().execute(nameValuePairs);
                }
            }
        });

        // char[] urlPath = category.toCharArray();
        upcCode = (String) upc.getText();
        while (upcCode.charAt(0) == '0') {
            upcCode = upcCode.replaceFirst("0", "");
        }

        System.gc();
        img.setImageDrawable(getResources().getDrawable(
                R.drawable.image_loading));
        File f = new File(Environment.getExternalStorageDirectory().toString()
                + "/MIA/images", ".nomedia");
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        image = new File(Environment.getExternalStorageDirectory().toString()
                + "/MIA/images", upc.getText() + ".jpg");
        if (!image.exists()) {
            if (DatabaseConnector.isConnected(getApplicationContext())) {
                imgDown = new DownloadImage(img, image, getApplicationContext());
                imgDown.execute("http://ma.holycrosschurchjm.com/" + upcCode
                        + ".jpg");
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
        if (getIntent().getStringExtra("parent").equalsIgnoreCase(
                "storereviewactivity")) {
            price.setFocusable(false);
            cancel.setVisibility(View.GONE);
            update.setVisibility(View.GONE);
        }

    }

    class pushProduct extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... fields) {
            imgDown.cancel(true);
            return db.DBPush(fields[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            String message;
            if (result) {
                message = "Product updated.";
                HomeActivity.updated = true;
                setResult(RESULT_OK, new Intent().putExtra("updated", true));
            } else {
                message = "Error while updating product.";
            }
            Toast.makeText(UpdateProductActivity.this, message,
                    Toast.LENGTH_SHORT).show();

            finish();
        }
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

            case R.id.logout:
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
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

}
