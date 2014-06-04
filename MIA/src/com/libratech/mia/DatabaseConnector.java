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
		jArray = new JSONArray();
	}

	public void clear() {
		is = null;
		result = "";
		jArray = new JSONArray();
	}

	public JSONArray dbPull(String url) {
		// Download JSON data from URLs
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection" + e.toString());
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
			return new JSONArray();
		}

		try {

			jArray = new JSONArray(result);
		} catch (JSONException e) {
			Log.e("log_tag", "Error parsing data " + e.toString());
			return new JSONArray();
		}

		return jArray;
	}

	public boolean DBPush(String fields) {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(fields);
		// HttpPost httppost = new HttpPost(
		// "http://www.holycrosschurchjm.com/MIA_mysql.php");
		try {
			// httppost.setEntity(new UrlEncodedFormEntity(fields));
			httpclient.execute(httppost);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public JSONArray DBLogin(String id, String pass) {
		// Download JSON data from URLs
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userLogin", "yes"));
		nameValuePairs.add(new BasicNameValuePair("username", "" + id));
		nameValuePairs.add(new BasicNameValuePair("password", "" + pass));
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://holycrosschurchjm.com/MIA_mysql.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection" + e.toString());
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
			return new JSONArray();
		}
		if (result != null && result.equals("false")) {

		} else {
			try {
				jArray = new JSONArray(result);
			} catch (JSONException e) {
				Log.e("log_tag", "Error parsing data " + e.toString());
				return new JSONArray();
			}
		}
		return jArray;
	}
}