package com.libratech.mia;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.jibble.simpleftp.SimpleFTP;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.analytics.tracking.android.EasyTracker;

public class AddNewProduct extends Activity implements iRibbonMenuCallback {

	ImageView image;
	EditText upc, name, desc, brand, cat, weight, uom;
	Button edit, update;
	ImageButton scan;
	RibbonMenuView rbmView;
	String path = "";
	File img;
	static final int REQUEST_IMAGE_CAPTURE = 3;
	static final int REQUEST_TAKE_PHOTO = 4;
	String timeStamp = "";
	Boolean imageSet = false;
	File photoFile = null;
	Bitmap bmp, scaled;
	String empId = HomeActivity.empID;
	String compId = HomeActivity.storeID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_product);
		// Show the Up button in the action bar.
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
		update = (Button) findViewById(R.id.update);
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
							"Photo cannot be empty. Tap placeholder to take a photo.",
							10000).show();
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
							.execute(("http://holycrosschurchjm.com/MIA_mysql.php?addNewProduct=yes&merch_id="
									+ empId
									+ "&comp_id="
									+ compId
									+ "&rec_date="
									+ dateString
									+ "upc_code="
									+ upc.getText()
									+ "&product_name="
									+ name.getText()
									+ "&description="
									+ desc.getText()
									+ "&brand="
									+ brand.getText()
									+ "&category="
									+ cat.getText()
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
		setupMenu();
		setupActionBar();
		EasyTracker.getInstance(this).activityStart(this);
	}

	private void setupMenu() {
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.manager_menu);
	}

	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	private class addProduct extends AsyncTask<String, Void, String> {

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Toast.makeText(getApplicationContext(),
					"Product successfully added", Toast.LENGTH_LONG).show();
			bmp.recycle();
			scaled.recycle();
			bmp = scaled = null;
			AddNewProduct.this.finish();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			SimpleFTP ftp = new SimpleFTP();
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
					boolean result = con.storeFile(img.getName(), in);
					in.close();
					if (result)
						Log.v("upload result", "succeeded");
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void RibbonMenuItemClick(int itemId, int position) {

		ActivityControl.changeActivity(this, itemId, position, rbmView,
				"HomeActivity");
	}

	// @Override
	// public void RibbonMenuItemClick(int itemId, int position) {
	//
	// Bundle b = new Bundle();
	// Intent i = new Intent();
	// switch (itemId) {
	// case R.id.HomeActivity:
	// i = new Intent(this, HomeActivity.class);
	// b.putString("parent", "StoreReviewActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// case R.id.AllProducts:
	// i = new Intent(this, AllProductsActivity.class);
	// b.putString("parent", "StoreReviewActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// case R.id.ScanItemActivity:
	// i = new Intent(this, ScanActivity.class);
	// b.putString("parent", "StoreReviewActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// // case R.id.Feedback:
	// // i = new Intent(this, FeedbackActivity.class);
	// // break;
	// case R.id.StoreReviewActivity:
	// rbmView.toggleMenu();
	// break;
	// case R.id.delProduct:
	// i = new Intent(this, DeleteProduct.class);
	// b.putString("parent", "StoreReviewActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// case R.id.addUser:
	// i = new Intent(this, AddUser.class);
	// b.putString("parent", "StoreReviewActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// case R.id.addProduct:
	// i = new Intent(this, AddProduct.class);
	// b.putString("parent", "StoreReviewActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// case R.id.delUser:
	// i = new Intent(this, DeleteUser.class);
	// b.putString("parent", "StoreReviewActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// case R.id.addStore:
	// i = new Intent(this, AddStore.class);
	// b.putString("parent", "StoreReviewActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	//
	// case R.id.delStore:
	// i = new Intent(this, DeleteStore.class);
	// b.putString("parent", "StoreReviewActivity");
	// i.putExtras(b);
	// startActivityForResult(i, 1);
	// break;
	// default:
	// break;
	// }
	// }

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
