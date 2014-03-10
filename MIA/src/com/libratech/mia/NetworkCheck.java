package com.libratech.mia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkCheck extends BroadcastReceiver {

	boolean status = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		final ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo net = connMgr.getActiveNetworkInfo();
		try {
			if (net.isConnected() && !status) {
				Toast.makeText(context, "Connection restored.",
						Toast.LENGTH_SHORT).show();
			} else {
				if (!net.isConnected() && status) {
					Toast.makeText(context, "Connection lost.",
							Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
