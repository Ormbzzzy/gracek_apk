package com.libratech.mia;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;

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
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.models.Product;
import com.libratech.mia.models.Scanned;

public class UpdateProductActivity extends Activity {

	String gct, category;
	String storeID = HomeActivity.storeID;
	String empID = HomeActivity.empID;
	String upcCode;
	String mode = "";
	DatabaseConnector db = new DatabaseConnector();
	boolean found = false;
	boolean scan = false;
	downloadImage imgDown = new downloadImage();
	ArrayList<Product> products = HomeActivity.aProducts;
	ArrayList<Scanned> scanned = HomeActivity.sProducts;

	EditText price;
	TextView brand, weight, name, upc, uom;
	RibbonMenuView rbmView;
	Button edit, update;
	CheckBox gctBox;
	ImageView img;
	File image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update);
		img = (ImageView) findViewById(R.id.updateImage);
		edit = (Button) findViewById(R.id.edit);
		edit.setText("Cancel");
		upc = (TextView) findViewById(R.id.upc);
		update = (Button) findViewById(R.id.update);
		brand = (TextView) findViewById(R.id.Brand);
		name = (TextView) findViewById(R.id.Name);
		price = (EditText) findViewById(R.id.Price);
		gctBox = (CheckBox) findViewById(R.id.gct);
		weight = (TextView) findViewById(R.id.weight);
		uom = (TextView) findViewById(R.id.uom);
		gct = "";
		price.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(8, 2) });
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
						gct=s.getGct();
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
				Toast.makeText(getApplicationContext(),
						"That product is not in the system.",
						Toast.LENGTH_SHORT).show();
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
		edit.setOnClickListener(new OnClickListener() {

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
					String product[] = { upc.getText().toString(), "MER-00001",
							"COMP-00001", dateString,
							price.getText().toString(), gct };
					Log.d("to DB", product[0] + " " + product[1] + " "
							+ product[2] + " " + product[3] + " " + product[4]
							+ " " + product[5]);
					if (isConnected()) {
						Toast.makeText(getApplicationContext(),
								"Saving changes.", Toast.LENGTH_SHORT).show();
						update.setClickable(false);
						update.setTextColor(getResources().getColor(
								R.color.gray));
						if (mode.equals("update")) {
							Log.d("url",
									"http://holycrosschurchjm.com/MIA_mysql.php?updateScannedProduct=yes&upc_code="
											+ upc.getText() + "&merch_id="
											+ empID + "&comp_id=" + storeID
											+ "&rec_date="
											+ dateString.replace(" ", "%20")
											+ "&price=" + price.getText()
											+ "&gct=" + gct);
							new pushProduct()
									.execute("http://holycrosschurchjm.com/MIA_mysql.php?updateScannedProduct=yes&upc_code="
											+ upc.getText()
											+ "&merch_id="
											+ empID
											+ "&comp_id="
											+ storeID
											+ "&rec_date="
											+ dateString.replace(" ", "%20")
											+ "&price="
											+ price.getText()
											+ "&gct=" + gct);
						} else if (mode.equals("view")) {
							Log.d("url",
									"http://holycrosschurchjm.com/MIA_mysql.php?addScannedProduct=yes&upc_code="
											+ upc.getText() + "&merch_id="
											+ empID + "&comp_id=" + storeID
											+ "&rec_date="
											+ dateString.replace(" ", "%20")
											+ "&price=" + price.getText()
											+ "&gct=" + gct);
							new pushProduct()
									.execute("http://holycrosschurchjm.com/MIA_mysql.php?addScannedProduct=yes&upc_code="
											+ upc.getText()
											+ "&merch_id="
											+ empID
											+ "&comp_id="
											+ storeID
											+ "&rec_date="
											+ dateString.replace(" ", "%20")
											+ "&price="
											+ price.getText()
											+ "&gct=" + gct);
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

		Log.d("url", "http://ma.holycrosschurchjm.com/" + upcCode + ".jpg");
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
			if (isConnected()) {
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
			edit.setVisibility(View.GONE);
			update.setVisibility(View.GONE);
		}
		EasyTracker.getInstance(this).activityStart(this);
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
				setResult(RESULT_OK,new Intent().putExtra("updated", true));
			} else {
				message = "Error while updating product.";
			}
			Toast.makeText(UpdateProductActivity.this, message,
					Toast.LENGTH_SHORT).show();

			finish();
		}
	}

	@Override
	public void onBackPressed() {
		
		imgDown.cancel(true);
		super.onBackPressed();
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
				if (isCancelled())
					cancel(true);
				HttpURLConnection conn = (HttpURLConnection) myFileUrl
						.openConnection();
				if (isCancelled())
					cancel(true);
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
