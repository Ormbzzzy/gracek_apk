package com.libratech.mia;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class AddProduct extends Activity implements iRibbonMenuCallback {

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
	File photoFile = null;

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
				Intent i = new Intent(AddProduct.this, ScanActivity.class);
				i.putExtras(b);
				startActivityForResult(i, 1);
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
									REQUEST_TAKE_PHOTO);
						}
					}
				} else {
					Toast.makeText(AddProduct.this,
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
							AddProduct.this,
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
					Toast.makeText(AddProduct.this, "No field can be empty.",
							Toast.LENGTH_SHORT).show();
				} else {
					new addProduct()
							.execute("http:/holycrosschurchjm.com/MIA_mysql.php?addProduct=yes&upc_code="
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
									+ uom.getText() + "&photo=no_image.jpg");
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
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			new DatabaseConnector().DBPush(params[0]);
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
			Bundle extras = data.getExtras();
			Bitmap b = (Bitmap) extras.get("data");
			image.setImageBitmap(b);
			
		}
	}

	private File createImageFile() throws IOException {
		// Create an image file name

		img = new File(Environment.getExternalStorageDirectory().toString()
				+ "/MIA/images", upc.getText() + ".jpg");

		// Save a file: path for use with ACTION_VIEW intents
		// mCurrentPhotoPath = "file:" + image.getAbsolutePath();
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
		Bundle b = new Bundle();
		Intent i = new Intent();
		switch (itemId) {
		case R.id.HomeActivity:
			i = new Intent(this, HomeActivity.class);
			b.putString("parent", "StoreReviewActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		case R.id.AllProducts:
			i = new Intent(this, AllProductsActivity.class);
			b.putString("parent", "StoreReviewActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		case R.id.ScanItemActivity:
			i = new Intent(this, ScanActivity.class);
			b.putString("parent", "StoreReviewActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		// case R.id.Feedback:
		// i = new Intent(this, FeedbackActivity.class);
		// b.putString("parent", "StoreReviewActivity");
		// i.putExtras(b);
		// startActivityForResult(i, 1);
		// break;
		case R.id.StoreReviewActivity:
			i = new Intent(this, StoreReviewActivity.class);
			b.putString("parent", "StoreReviewActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		// case R.id.delProduct:
		// i = new Intent(this, DeleteProduct.class);
		// b.putString("parent", "StoreReviewActivity");
		// i.putExtras(b);
		// startActivityForResult(i, 1);
		// break;
		case R.id.addUser:
			i = new Intent(this, AddUser.class);
			b.putString("parent", "StoreReviewActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		case R.id.addProduct:
			i = new Intent(this, AddProduct.class);
			b.putString("parent", "StoreReviewActivity");
			i.putExtras(b);
			startActivityForResult(i, 1);
			break;
		// case R.id.delUser:
		// i = new Intent(this, DeleteUser.class);
		// b.putString("parent", "StoreReviewActivity");
		// i.putExtras(b);
		// startActivityForResult(i, 1);
		// break;
		default:
			break;
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
