package com.libratech.mia.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;
import com.libratech.mia.models.Product;
import com.libratech.mia.utilities.ActivityControl;
import com.libratech.mia.utilities.DatabaseConnector;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.libratech.mia.R;
import com.libratech.mia.utilities.ScanActivity;

public class AddNewProduct extends Activity implements iRibbonMenuCallback {

	ImageView image;
	EditText upc, name, desc, brand, cat, weight, uom, price;
	Button edit, update;
	ImageButton scan;
	RibbonMenuView rbmView;
	String path = "";
	File img;
	CheckBox gctBox;
	static final int REQUEST_IMAGE_CAPTURE = 3;
	static final int REQUEST_TAKE_PHOTO = 4;
	ArrayList<Product> aProd = HomeActivity.aProducts;
	ArrayList<Product> newProds = new ArrayList<Product>();
	String timeStamp = "";
	Boolean imageSet = false;
	File photoFile = null;
	Bitmap bmp, scaled;
	String gct = "no";
	String empId = HomeActivity.empID;
	String compId = HomeActivity.storeID;
	ProgressDialog prog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_product);
		// Show the Up button in the action bar.
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
		image = (ImageView) findViewById(R.id.addPhoto);
		upc = (EditText) findViewById(R.id.upc);
		scan = (ImageButton) findViewById(R.id.scanButton);
		name = (EditText) findViewById(R.id.Name);
		desc = (EditText) findViewById(R.id.Description);
		brand = (EditText) findViewById(R.id.Brand);
		cat = (EditText) findViewById(R.id.Category);
		weight = (EditText) findViewById(R.id.weight);
		weight.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(6, 3) });
		uom = (EditText) findViewById(R.id.uom);
		edit = (Button) findViewById(R.id.edit);
		edit.setText("Cancel");
		update = (Button) findViewById(R.id.update);
		price = (EditText) findViewById(R.id.price);
		gctBox = (CheckBox) findViewById(R.id.gct);
		price.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(8, 2) });
		upc.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				for (Product p : aProd) {
					if (upc.getText().equals(p.getUpcCode())) {
						Toast.makeText(getApplicationContext(),
								"That product is already in the system.",
								Toast.LENGTH_SHORT).show();
						upc.setText("");
					}
				}
			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

		});
		scan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle b = new Bundle();
				b.putString("parent", "AddProduct");
				Intent i = new Intent(AddNewProduct.this, ScanActivity.class);
				i.putExtras(b);
				startActivityForResult(i, 1);
			}
		});
		String s = "";
		gctBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				
				if (gctBox.isChecked()) {
					gct = "yes";
				} else {
					gct = "no";
				}
			}
		});
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (upc.getText().length() != 0) {
					Intent takePictureIntent = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

						try {
							photoFile = createImageFile();
						} catch (IOException ex) {
							ex.printStackTrace();
						}

						if (photoFile != null) {
							takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(photoFile));
							startActivityForResult(takePictureIntent,
									REQUEST_IMAGE_CAPTURE);
						}
					}
				} else {
					Toast.makeText(AddNewProduct.this,
							"Enter or scan a UPC before adding an image",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (photoFile == null) {
					Toast.makeText(
							AddNewProduct.this,
							"Photo cannot be empty. Tap placeholder to take a photo.",Toast.LENGTH_SHORT).show();
				} else if (upc.getText().length() == 0
						|| name.getText().length() == 0
						|| desc.getText().length() == 0
						|| cat.getText().length() == 0
						|| weight.getText().length() == 0
						|| uom.getText().length() == 0
						|| brand.getText().length() == 0
						|| upc.getText().length() == 0) {
					Toast.makeText(AddNewProduct.this,
							"No field can be empty.", Toast.LENGTH_SHORT)
							.show();
				} else {
					Date date = new Date();
					String dateString = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss").format(date);
					new addProduct()
							.execute((DatabaseConnector.getDomain()+"/MIA_mysql.php?addNewProduct=yes&merch_id="
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
		upc.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				img = new File(Environment.getExternalStorageDirectory()
						.toString() + "/MIA/images", upc.getText() + ".jpg");
				if (img.exists()) {
					Bitmap b = BitmapFactory.decodeFile(img.getAbsolutePath());
					int nh = (int) (b.getHeight() / (b.getWidth() / 200));
					Bitmap scaled = Bitmap.createScaledBitmap(b, 200, nh, true);
					image.setImageBitmap(scaled);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}

		});
		new downloadImage()
				.execute("http://ma.holycrosschurchjm.com/New Products/"
						+ upc.getText() + ".jpg");
		setupMenu();
		setupActionBar();
		Date date = new Date();
		String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(date);
		new GetNewProducts()
				.execute((DatabaseConnector.getDomain()+"/MIA_mysql.php?newProducts=yes&comp_id=comp-00001&rec_date=" + dateString)
						.replace(" ", "%20"));
		
	}

	private void setupMenu() {
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.home);
	}

	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	private class GetNewProducts extends AsyncTask<String, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(String... params) {
			
			return new DatabaseConnector().dbPull(params[0]);

		}

		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			
			super.onPostExecute(result);
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
			AddNewProduct.this.finish();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			prog = ProgressDialog.show(AddNewProduct.this, "Wait",
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_product, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (data.hasExtra("code"))
				upc.setText(data.getStringExtra("code"));
			String s = upc.getText().toString();
			upc.setText("");
			upc.setText(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

			BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
			bmpFactoryOptions.inJustDecodeBounds = false;
			bmp = BitmapFactory.decodeFile(photoFile.getAbsolutePath(),
					bmpFactoryOptions);

			// Bitmap bmp = (Bitmap) data.getExtras().get("data");
			int nh = (int) (bmp.getHeight() / (bmp.getWidth() / 200));
			scaled = Bitmap.createScaledBitmap(bmp, 200, nh, true);
			image.setImageBitmap(scaled);
		}
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		if (!img.getParentFile().exists()) {
			img.getParentFile().mkdirs();
			img.createNewFile();
		} else {
			img.createNewFile();
		}

		return img;
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
	public void RibbonMenuItemClick(int itemId, int position) {

		ActivityControl.changeActivity(this, itemId, "HomeActivity");
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
