package com.sunyh.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

import com.sunyh.gprs.MainActivity;
import com.sunyh.gprs.R;

public class MessageUtil {

	public static void showErr4Activity(final Activity ctx, String tag,
			final String info) {
		ctx.runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
				builder.setTitle("错误提示");
				builder.setMessage(info);
				builder.setNegativeButton("确定", null);
				builder.show();
			}
		});
	}

	public static void showErr4Activity(final Activity ctx, String tag,
			Throwable e) {
		showErr4Activity(ctx, tag, e.toString());
	}

	public static void showInfo4Activity(Activity ctx, String tag, String info) {
		Vibrator vib = (Vibrator) ctx
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(60 * 3);

		Toast.makeText(ctx, info, Toast.LENGTH_LONG).show();
	}

	public static void showInfo4Service(Service ctx, CharSequence contentTitle,
			CharSequence contentText) {
		NotificationManager mNotificationManager = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.icon;
		Notification notification = new Notification(icon, "国产GPRS流量监控",
				System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		long[] vibrate = { 0, 100 };
		notification.vibrate = vibrate;

		// 在程序代码中使用RemoteViews的方法来定义image和text。然后把RemoteViews对象传到contentView字段
		// RemoteViews remoteViews = new RemoteViews(ctx.getPackageName(),
		// R.layout.view);
		// remoteViews.setImageViewResource(R.id.image, R.drawable.ic_launcher);
		// remoteViews.setTextViewText(R.id.text, "content");
		// notification.contentView = remoteViews;

		// 为Notification的contentIntent字段定义一个Intent(注意，使用自定义View不需要setLatestEventInfo()方法)
		Intent notificationIntent = new Intent(ctx, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// notification.contentIntent = contentIntent;

		notification.setLatestEventInfo(ctx, contentTitle, contentText,
				contentIntent);

		// 发送通知
		mNotificationManager.notify(R.id.text, notification);
	}
}