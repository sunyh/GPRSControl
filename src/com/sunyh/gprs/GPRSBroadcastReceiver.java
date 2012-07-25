package com.sunyh.gprs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 设置服务开机自动启动
 * @author sunyh
 *
 */
public class GPRSBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent arg1) {
		Log.e("GPRSBroadcastReceiver ", " onReceive");
		Intent intent= new Intent(ctx, GPRSService.class);
		ctx.startService(intent);
	}
}