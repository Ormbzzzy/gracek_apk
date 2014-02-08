package com.libratech.mia;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public class DatabaseConnector {

	InputStream is;
	String result;
	JSONArray jArray;

	public DatabaseConnector() {
		is = null;
		result = "";
		jArray = null;
	}

	public JSONArray DBPull(String url) {

		// Download JSON data from URL
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
		}

		// Convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
		}

		try {

			jArray = new JSONArray(result);
		} catch (JSONException e) {
			Log.e("log_tag", "Error parsing data " + e.toString());
		}

		return jArray;
	}

	public boolean DBPush(String[] fields) {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("holycrosschurchjm.com/MIA_mysql.php");
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		try {
			nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("updateScannedProduct",
					"yes"));
			nameValuePairs.add(new BasicNameValuePair("upc_code", fields[0]));
			nameValuePairs.add(new BasicNameValuePair("merch_id", fields[1]));
			nameValuePairs.add(new BasicNameValuePair("comp_id", fields[2]));
			nameValuePairs.add(new BasicNameValuePair("rec_date", fields[3]));
			nameValuePairs.add(new BasicNameValuePair("price", fields[4]));
			nameValuePairs.add(new BasicNameValuePair("gct", fields[5]));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			httpclient.execute(httppost);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}