package com.libratech.mia.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.adapters.NewAdapter;
import com.libratech.mia.models.Product;
import com.libratech.mia.utilities.ActivityControl;
import com.libratech.mia.utilities.DatabaseConnector;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
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
public class ViewNewProduct extends Activity implements iRibbonMenuCallback {
	private RibbonMenuView rbmView;
	ListView listview;
	View lv, details;
	ArrayList<Product> products = new ArrayList<Product>();
	boolean updated = false;
	ImageView image;
	boolean nSort, bSort, cSort;
	EditText name, brand, upc, uom, weight, price, cat, desc;
	CheckBox gctBox;
	String compId = HomeActivity.storeID;
	String dateString = "";
	Button cancel, update;
	File img;
	Bitmap bmp, scaled;
	Builder dg;
	ProgressDialog prog;
	String gct = "";
	String empId = HomeActivity.empID;
	MenuItem add, del;
	boolean prodSelected = false;
	boolean newProd = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_new_product);
		Date date = new Date();
		dateString = new SimpleDateFormat("yyyy-MM-dd").format(date);
		details = (View) findViewById(R.id.newDetails);
		lv = (View) findViewById(R.id.newList);
		details.setVisibility(View.GONE);
		image = (ImageView) details.findViewById(R.id.image);
		upc = (EditText) details.findViewById(R.id.upc);
		brand = (EditText) details.findViewById(R.id.Brand);
		name = (EditText) details.findViewById(R.id.Name);
		weight = (EditText) details.findViewById(R.id.weight);
		price = (EditText) details.findViewById(R.id.price);
		gctBox = (CheckBox) details.findViewById(R.id.gct);
		// gctBox.setClickable(false);
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
		uom = (EditText) details.findViewById(R.id.uom);
		cat = (EditText) details.findViewById(R.id.Category);
		desc = (EditText) details.findViewById(R.id.Description);
		cancel = (Button) details.findViewById(R.id.cancel);
		cancel.setText("Cancel");
		update = (Button) details.findViewById(R.id.delete);
		update.setText("update");
		nSort = bSort = cSort = true;
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		dg = new AlertDialog.Builder(this);
		dg.setTitle("Are you sure?");
		dg.setMessage("Doing this will permanently remove this entry from the application!");
		dg.setPositiveButton("Yes, I'm Sure.",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						Date date = new Date();
						dateString = new SimpleDateFormat("yyyy-MM-dd")
								.format(date);
						FTPClient con = null;

						try {
							con = new FTPClient();
							con.connect("ftp.holycrosschurchjm.com");

							if (con.login("picupload@holycrosschurchjm.com",
									"picupload123")) {
								con.enterLocalPassiveMode();
								boolean result = con
										.deleteFile("/New Products/"
												+ upc.getText());
								if (result)
									con.logout();
								con.disconnect();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						new Delete()
								.execute((DatabaseConnector.getDomain()+"/MIA_mysql.php?deleteNewProduct=yes&comp_id=comp-00001&rec_date="
										+ dateString + "&upc_code=" + upc
										.getText()).replace(" ", "%20"));
					}
				});
		dg.setNegativeButton("Cancel", null);
		if (getIntent().hasExtra("parent")
				&& getIntent().getStringExtra("parent").equalsIgnoreCase(
						"StoreReviewActivity")) {
			rbmView.setMenuItems(R.menu.manager_menu);
		} else {
			rbmView.setMenuItems(R.menu.home);
		}
		listview = (ListView) lv.findViewById(R.id.newlistview);
		listview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				lv.setVisibility(View.GONE);
				details.setVisibility(View.VISIBLE);
				prodSelected = true;
				new downloadImage()
						.execute("http://ma.holycrosschurchjm.com/New Products/"
								+ upc.getText() + ".jpg");
				invalidateOptionsMenu();
				Product p = products.get(position);
				upc.setText(p.getUpcCode());
				name.setText(p.getProductName());
				brand.setText(p.getBrand());
				weight.setText(p.getWeight());
				price.setText("" + p.getPrice());
				uom.setText(p.getUom());
				if (p.getGct().equals("yes")) {
					gctBox.setChecked(true);
				} else
					gctBox.setChecked(false);
			}
		});

		update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (upc.getText().length() == 0 || name.getText().length() == 0
						|| desc.getText().length() == 0
						|| cat.getText().length() == 0
						|| weight.getText().length() == 0
						|| uom.getText().length() == 0
						|| brand.getText().length() == 0
						|| upc.getText().length() == 0) {
					Toast.makeText(ViewNewProduct.this,
							"No field can be empty.", Toast.LENGTH_SHORT)
							.show();
				} else {
					Date date = new Date();
					String dateString = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss").format(date);
					new addProduct()
							.execute((DatabaseConnector.getDomain()+"/MIA_mysql.php?updateNewProduct=yes&merch_id="
									+ empId
									+ "&comp_id="
									+ compId
									+ "&rec_date="
									+ dateString
									+ "&upc_code="
									+ upc.getText()
									+ "&product_name="
									+ name.getText()
									+ "&description="
									+ desc.getText()
									+ "&brand="
									+ brand.getText()
									+ "&category="
									+ cat.getText()
									+ "&price="
									+ price.getText()
									+ "&gct="
									+ gct
									+ "&weight="
									+ weight.getText()
									+ "&uom="
									+ uom.getText() + "&photo=" + upc.getText() + ".jpg")
									.replace(" ", "%20"));
				}
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				details.setVisibility(View.GONE);
				lv.setVisibility(View.VISIBLE);
				prodSelected = !prodSelected;
				invalidateOptionsMenu();
			}

		});
		if (products == null || products.size() == 0) {
			new getProducts()
					.execute(DatabaseConnector.getDomain()+"/MIA_mysql.php?newProducts=yes&comp_id="
							+ compId + "&rec_date=" + dateString);
		}
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	class Delete extends AsyncTask<String, Void, Boolean> {
		protected Boolean doInBackground(String... url) {
			return new DatabaseConnector().DBPush(url[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Toast.makeText(getApplicationContext(), "Item deleted",
						Toast.LENGTH_SHORT).show();
				details.setVisibility(View.GONE);
				lv.setVisibility(View.VISIBLE);
				new getProducts()
						.execute(DatabaseConnector.getDomain()+"/MIA_mysql.php?newProducts=yes&comp_id="
								+ compId + "&rec_date=" + dateString);
			} else {
				Toast.makeText(getApplicationContext(), "An error occured.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	class getProducts extends AsyncTask<String, Void, JSONArray> {
		protected JSONArray doInBackground(String... url) {
			return new DatabaseConnector().dbPull(url[0]);
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			String upc, name, desc, brand, category, uom, gct, photo, weight;
			name = desc = brand = category = uom = gct = photo = upc = weight = gct = "";
			float price = (float) 0.00;
			products.clear();
			for (int i = 0; i < result.length(); i++) {
				try {
					upc = result.getJSONArray(i).getString(0);
					name = result.getJSONArray(i).getString(1);
					desc = result.getJSONArray(i).getString(2);
					brand = result.getJSONArray(i).getString(3);
					category = result.getJSONArray(i).getString(4);
					weight = result.getJSONArray(i).getString(5);
					uom = result.getJSONArray(i).getString(6);
					price = (float) result.getJSONArray(i).getDouble(7);
					gct = result.getJSONArray(i).getString(8);
					photo = result.getJSONArray(i).getString(9);// price
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				products.add(new Product(upc, weight, name, desc, brand,
						category, uom, price, gct, photo));

			}
			Toast.makeText(ViewNewProduct.this, "Products loaded.",
					Toast.LENGTH_SHORT).show();
			updated = true;
			listview.setAdapter(new NewAdapter(ViewNewProduct.this, products));
		}
	}

	private class addProduct extends AsyncTask<String, Void, String> {

		@Override
		protected void onPostExecute(String result) {
			
			super.onPostExecute(result);
			prog.dismiss();
			Toast.makeText(getApplicationContext(),
					"Product successfully added", Toast.LENGTH_LONG).show();
			bmp.recycle();
			scaled.recycle();
			bmp = scaled = null;
			ViewNewProduct.this.finish();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			prog = ProgressDialog.show(ViewNewProduct.this, "Wait",
					"Adding Product...");
		}

		@Override
		protected String doInBackground(String... params) {
			
			new DatabaseConnector().DBPush(params[0]);
			FTPClient con = null;

			try {
				con = new FTPClient();
				con.connect("ftp.holycrosschurchjm.com");

				if (con.login("picupload@holycrosschurchjm.com", "picupload123")) {
					con.enterLocalPassiveMode();
					con.setFileType(FTP.BINARY_FILE_TYPE);
					String data = img.getAbsolutePath();

					FileInputStream in = new FileInputStream(new File(data));
					boolean result = con.storeFile(
							"/New Products/" + img.getName(), in);
					in.close();
					if (result)
						con.logout();
					con.disconnect();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

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
				image.setImageDrawable(getResources().getDrawable(
						R.drawable.no_image));
				// Toast.makeText(getApplicationContext(), "Image not found.",
				// Toast.LENGTH_SHORT).show();
			} else {
				image.setImageBitmap(result);
				int nh = (int) (result.getHeight() / (result.getWidth() / 200));
				Bitmap scaled = Bitmap
						.createScaledBitmap(result, 200, nh, true);
				image.setImageBitmap(scaled);

				img.getParentFile().mkdirs();
				try {
					FileOutputStream out = new FileOutputStream(img);
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.newProd:
			newProd = true;
			startActivity(new Intent(getApplicationContext(),
					AddNewProduct.class));
			break;
		case R.id.removeProd:
			dg.show();
			break;
		case android.R.id.home:
			rbmView.toggleMenu();
			return true;

		case R.id.logout:
			Intent i = new Intent(getApplicationContext(), LoginActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void RibbonMenuItemClick(int itemId, int position) {

		ActivityControl.changeActivity(this, itemId, getIntent()
				.getStringExtra("parent"));
	}

	@Override
	protected void onPause() {
		
		super.onPause();
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
	protected void onResume() {

		if (updated) {
			updated = !updated;
			if (isConnected()) {
				products.clear();
				new getProducts()
						.execute(DatabaseConnector.getDomain()+"/MIA_mysql.php?newProducts=yes&comp_id="
								+ compId + "&rec_date=" + dateString);
			} else {
				Toast.makeText(getApplicationContext(),
						"Please check your connection.", Toast.LENGTH_SHORT)
						.show();
			}
		}
		super.onResume();
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		del = menu.findItem(R.id.removeProd);
		add = menu.findItem(R.id.newProd);
		del.setVisible(prodSelected);
		add.setVisible(!prodSelected);
		if (newProd) {
			del.setVisible(false);
			add.setVisible(false);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.new_products_control, menu);
		return true;
	}
}