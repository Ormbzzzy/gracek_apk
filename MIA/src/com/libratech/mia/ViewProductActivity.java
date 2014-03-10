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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.libratech.mia.models.Product;

public class ViewProductActivity extends Activity implements
		iRibbonMenuCallback {

	EditText price;
	ImageView img;
	File image;
	TextView brand, weight, name, upc, uom;
	RibbonMenuView rbmView;
	Button scan, confirm;
	DatabaseConnector db = new DatabaseConnector();
	String gct, category;
	CheckBox gctBox;
	boolean found = false;
	ArrayList<Product> products = HomeActivity.aProducts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product);
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		scan = (Button) findViewById(R.id.scan);
		upc = (TextView) findViewById(R.id.upc);
		confirm = (Button) findViewById(R.id.confirm);
		brand = (TextView) findViewById(R.id.Brand);
		name = (TextView) findViewById(R.id.Name);
		price = (EditText) findViewById(R.id.Price);
		gctBox = (CheckBox) findViewById(R.id.gct);
		img = (ImageView) findViewById(R.id.image);
		weight = (TextView) findViewById(R.id.weight);
		uom = (TextView) findViewById(R.id.uom);
		scan.setText("Cancel");
		if (getIntent().hasExtra("code")) {
			for (int i = 0; i < products.size(); i++) {
				if (getIntent().getStringExtra("code").contains(
						products.get(i).getUpcCode())) {
					upc.setText(products.get(i).getUpcCode());
					brand.setText(products.get(i).getBrand());
					name.setText(products.get(i).getProductName());
					price.setText(String.valueOf(products.get(i).getPrice()));
					weight.setText(products.get(i).getWeight());
					found = true;
					break;
				}
			}
			if (!found) {
				Toast.makeText(ViewProductActivity.this,
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

		// char[] urlPath = category.toCharArray();
		String upcCode = (String) upc.getText();
		while (upcCode.charAt(0) == '0') {
			upcCode = upcCode.replaceFirst("0", "");
		}

		Log.d("url", "http://ma.holycrosschurchjm.com/" + upcCode + ".jpg");
		System.gc();
		image = new File(Environment.getExternalStorageDirectory().toString()
				+ "/MIA/images", upcCode + ".jpg");
		if (!image.exists()) {
			if (isConnected()) {
				new downloadImage().execute("http://ma.holycrosschurchjm.com/"
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
		gctBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (gctBox.isChecked())
					gct = "yes";
				else
					gct = "no";
			}
		});
		scan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Double.parseDouble(price.getText().toString()) == 0.0) {
					Toast.makeText(ViewProductActivity.this,
							"Price cannot be zero.", Toast.LENGTH_LONG).show();
				} else {

					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					Date date = new Date();
					String dateString = new SimpleDateFormat(
							"yyyy-MM-dd hh:mm:ss").format(date); // 9:00
					String product[] = { upc.getText().toString(), "MER-00001",
							"COMP-00001", dateString,
							price.getText().toString(), gct };
					Log.d("to DB", product[0] + " " + product[1] + " "
							+ product[2] + " " + product[3] + " " + product[4]
							+ " " + product[5]);
					nameValuePairs.add(new BasicNameValuePair(
							"addScannedProduct", "yes"));
					nameValuePairs.add(new BasicNameValuePair("upc_code", upc
							.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("merch_id",
							"MER-00001"));
					nameValuePairs.add(new BasicNameValuePair("comp_id",
							"COMP-00001"));
					nameValuePairs.add(new BasicNameValuePair("rec_date",
							"2013-11-01 11:30:28"));
					nameValuePairs.add(new BasicNameValuePair("price", price
							.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("gct", gct));
					// "http://holycrosschurchjm.com/MIA_mysql.php?addScannedProduct=yes&upc_code="+upc.getText().toString()+"&merch_id=MER-00001&comp_id=COMP-00001&rec_date=2014-02-09&price=345.00&gct=yes"
					if (isConnected()) {
						new pushProduct()
								.execute("http://holycrosschurchjm.com/MIA_mysql.php?addScannedProduct=yes&upc_code="
										+ upc.getText()
										+ "&merch_id=MER-00001&comp_id=COMP-00001&rec_date=2013-11-01&price="
										+ price.getText() + "&gct=" + gct);
					} else {
						Toast.makeText(getApplicationContext(),
								"Please check your connection and try again.",
								Toast.LENGTH_SHORT).show();
						// new pushProduct().execute(nameValuePairs);
					}
				}
			}
		});
	}

	class pushProduct extends AsyncTask<String, Void, Boolean> {
		protected Boolean doInBackground(String... fields) {
			return db.DBPush(fields[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			String message;
			if (result) {
				message = "Product updated.";
				setResult(RESULT_OK, new Intent().putExtra("updated", true));
			} else {
				message = "Error while updating product.";
			}
			Toast.makeText(ViewProductActivity.this, message,
					Toast.LENGTH_SHORT).show();
			try {
				startActivity(new Intent(ViewProductActivity.this,
						Class.forName("com.libratech.mia."
								+ getIntent().getStringExtra("parent"))));
			} catch (Exception e) {
				e.printStackTrace();
			}
			finish();
		}
	}

	class downloadImage extends AsyncTask<String, Void, Bitmap> {
		protected Bitmap doInBackground(String... fileUrl) {
			URL myFileUrl = null;
			try {
				myFileUrl = new URL(fileUrl[0]);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				HttpURLConnection conn = (HttpURLConnection) myFileUrl
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				Log.i("im connected", "Downloading image");
				return BitmapFactory.decodeStream(is);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			if (result == null) {
				img.setImageDrawable(getResources().getDrawable(
						R.drawable.no_image));
				Toast.makeText(getApplicationContext(), "Image not found.",
						Toast.LENGTH_SHORT).show();
			} else {
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
					MediaStore.Images.Media.insertImage(getContentResolver(),
							image.getAbsolutePath(), image.getName(),
							image.getName());
					Toast.makeText(getApplicationContext(),
							"File is Saved in  " + image, 1000).show();
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
	public void RibbonMenuItemClick(int itemId, int position) {
		// TODO Auto-generated method stub
		String classes[] = { "HomeActivity", "ScanActivity",
				"AllProductsActivity", "FeedbackActivity" };
		try {
			startActivity(new Intent(ViewProductActivity.this,
					Class.forName("com.libratech.mia." + classes[position])));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
