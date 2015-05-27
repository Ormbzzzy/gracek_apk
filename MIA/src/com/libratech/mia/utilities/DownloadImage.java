package com.libratech.mia.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.libratech.mia.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
	ImageView img = null;
	File image = null;
	Context ctx;

	public DownloadImage(ImageView img, File image, Context ctx) {
		this.img = img;
		this.image = image;
		this.ctx = ctx;
	}

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
			img.setImageDrawable(ctx.getResources().getDrawable(
					R.drawable.no_image));
			// Toast.makeText(getApplicationContext(), "Image not found.",
			// Toast.LENGTH_SHORT).show();
		} else {
			img.setImageBitmap(result);
			int nh = (int) (result.getHeight() / (result.getWidth() / 200));
			Bitmap scaled = Bitmap.createScaledBitmap(result, 200, nh, true);
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
				Toast.makeText(ctx.getApplicationContext(), "Image saved",
						Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}