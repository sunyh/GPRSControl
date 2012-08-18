package com.sunyh.gprs;

import java.io.IOException;
import java.util.Date;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.sunyh.util.MessageUtil;

public class GPRSService extends Service {

	@Override
	public void onStart(Intent intent, int startId) {
		final MyApp app = (MyApp) this.getApplication();
		Log.e("GPRSService ", " onStart");
		super.onStart(intent, startId);
		app.timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				Log.e("GPRSService ", " run");
				MainActivity.saveM(app);
				try {
					app.month = MainActivity.getMM();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Warning.checkData(app, Warning.warns2);
				String curDate = MyApp.format.format(new Date());
				if (Status.getInstance().isbNeedVib()) {
					// 如果是同一天，已经警告过的类型就不警告了，刚刚在activity中警告过了的也不警告
					String tmp = "";
					int i = 0;
					for (Warning warn : Warning.warns2) {
						if (warn.getDate() != null
								&& curDate.equals(MyApp.format
										.format(warn.getDate())))// 同一天已经警告过了就不警告了，重新设置了会清楚警告信息
							continue;

						if (warn.isNeedWarn() && !warn.isB()) {
							Warning activityWarn = Warning.warns1.get(i);
							if (!activityWarn.isB()
									|| (activityWarn.isB() && new Date()
											.getTime()
											- activityWarn.getDate().getTime() > 600)) {// 没有警告或者警告的时间已经超过一定时间了
								tmp += (warn.getWarnInfo() + "\n");
								warn.setWarnInfo(true, new Date());
								if (Boolean.valueOf(app.getProp("checkbox1")))
									NetworkUtil.closeNetWork(app,
											warn.netType);
							}
						}
						i++;
					}
					if (tmp.length() > 0)
						MessageUtil.showInfo4Service(GPRSService.this, "", tmp);

				}
			}
		}, 1000 * 60, 1000 * 60);// 一分钟执行一次
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}