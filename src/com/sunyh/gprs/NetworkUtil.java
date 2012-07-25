package com.sunyh.gprs;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

public class NetworkUtil {
	public static void closeNetWork(Context ctx, int type) {
		ConnectivityManager cw = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = cw.getActiveNetworkInfo();
		//
		// WifiManager mWiFiManager01 =
		// (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
		// mWiFiManager01.disconnect();
		if (netinfo != null && netinfo.isConnected())
			if (type == 1) {
				ctx.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			} else {
				ctx.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			}
		// for (NetworkInfo netinfo : netinfos) {

		// int i= cw.stopUsingNetworkFeature(netinfo.getType(),
		// netinfo.getTypeName());
		// }
		// return true;
	}
}