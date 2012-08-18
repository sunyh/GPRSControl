package com.sunyh.gprs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.youmi.android.AdManager;
import net.youmi.android.AdView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.gfan.sdk.statitistics.GFAgent;
import com.kuguo.ad.KuguoAdsManager;
import com.sunyh.util.MessageUtil;

public class MainActivity extends Activity {

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage("您确定要退出GPRS监控吗？");
			builder.setPositiveButton("退出",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 退出程序
							Intent exit = new Intent(Intent.ACTION_MAIN);
							exit.addCategory(Intent.CATEGORY_HOME);
							startActivity(exit);
							MainActivity.this.onDestroy();
						}
					});
			builder.setNegativeButton("取消", null);
			builder.show();
		} else if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
			Intent intent = new Intent(MainActivity.this.getApplicationContext(), SettingActivity.class);
			startActivity(intent);
		}
		return true;
	}

	protected void onPause() {
		super.onPause();
		try {
			this.unregisterReceiver(new GPRSBroadcastReceiver());
			WindowManager localWindowManager = (WindowManager)this.getApplicationContext().getSystemService("window");
			localWindowManager.removeViewImmediate(getCurrentFocus());
		} catch (Exception e) {

		}
		if (AD.GF)
			GFAgent.onPause(this);
	}
	
	protected void onResume() {
		super.onResume();
		MyApp app = (MyApp) getApplication();
		if (AD.KUGUO) {
			KuguoAdsManager.getInstance().receivePushMessage(app, true);
			if (AD.KUGUO_Zai) 
			/**
			 * 加入精品推荐的酷仔，传入0表示显示酷仔
			 */
			KuguoAdsManager.getInstance().showKuguoSprite(app,
					KuguoAdsManager.STYLE_KUZAI);
		}
			
		if (AD.GF)
			GFAgent.onResume(this);

		
		saveM(app);

		ListView listView = (ListView) findViewById(R.id.listView);
		try {
			listView.setAdapter(getListAdapter());
		} catch (IOException e) {
//			e.printStackTrace();
		}
		
		Warning.checkData(app, Warning.warns1);
		if (Status.getInstance().isbNeedVib()) {
			String tmp = "";
			for (Warning warn : Warning.warns1) {
				if (warn.isNeedWarn()) {
					tmp += (warn.getWarnInfo() + "\n");
					warn.setWarnInfo(true, new Date());
				}
			}
			MessageUtil.showInfo4Activity(this, "", tmp + "您可以选择关闭网络连接，节省费用！");
		}
	}

	private ListAdapter getListAdapter() throws IOException {
		MyApp app = (MyApp) getApplication();
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map;
		// 标题
		map = new HashMap<String, Object>();
		map.put(MyApp.STRING_DES, "今天流量");// 应该要是今天累计,直接读文件里面的数据
		map.put(MyApp.STRING_SIZE, "单位/M");
		list.add(map);
		//
		String[] des = new String[] { "2G/3G", "wifi", "总计" };
		for (int i = 0; i < des.length; i++) {
			map = new HashMap<String, Object>();
			map.put(MyApp.STRING_DES, des[i]);
			map.put(MyApp.STRING_SIZE, app.day.getData(i));
			list.add(map);
		}

		// 本月数据
		map = new HashMap<String, Object>();
		map.put(MyApp.STRING_DES, "本月流量");
		map.put(MyApp.STRING_SIZE, MyApp.STRING_EMPTY);
		list.add(map);

		app.month = getMM();
		for (int i = 0; i < des.length; i++) {
			map = new HashMap<String, Object>();
			map.put(MyApp.STRING_DES, des[i]);
			map.put(MyApp.STRING_SIZE, app.month.getData(i));
			list.add(map);
		}

		// 关于
		des = new String[] { "联系我们", "QQ", "Email" };
		String[] info = new String[] { "", "1577636387",
				"main_syh@yahoo.com.cn" };
		for (int i = 0; i < des.length; i++) {
			map = new HashMap<String, Object>();
			map.put(MyApp.STRING_DES, des[i]);
			map.put(MyApp.STRING_SIZE, info[i]);
			list.add(map);
		}

		int[] tt = { R.id.des, R.id.size };
		ListAdapter adapter = new SimpleAdapter(this, list, R.layout.list,
				new String[] { MyApp.STRING_DES, MyApp.STRING_SIZE }, tt);
		return adapter;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Intent intent = new Intent(GPRSService.class.getName());
		startService(intent);
		
		// 应用Id 应用密码 广告请求间隔(s) 测试模式
		if (AD.youmi) {
			AdManager.init(this, "198762f972d578e2", "119ca3646511d528", 30,
					false);
			LinearLayout adViewLayout = (LinearLayout) findViewById(R.id.adViewLayout1);
			adViewLayout.addView(new AdView(this), new LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
		}
	}

	/**
	 * 把现在的数据然后加上已经存在的数据
	 * 
	 * @param ctx
	 * @throws Exception
	 */
	static void saveM(Context ctx) {
		MyApp app = (MyApp) ctx.getApplicationContext();
		String curDate = MyApp.format.format(new Date());
		// 进入新的一天，记录凌晨的流量
		if (!curDate.equals(app.day.getDay())) {
			app.yesDayM = app.day.clone();
			app.day.clear();
			app.day.setDay(curDate);
		}
		double t1 = getGRPS();
		double t2 = getTotal();
		if ((app.gprs > 0 && t1 == 0) || (app.total > 0 && t2 == 0)) {// 为啥流量数据会清零？，不明原因
			if (AD.debug)
				if (ctx instanceof Activity)
				MessageUtil.showErr4Activity((Activity) ctx, "saveToSdCard",
						"流量变为0了");
				else
				MessageUtil.showInfo4Service((Service) ctx, "saveToSdCard",
						"流量变为0了");
		}
		if (t1 == 0) {// 清除为0了。

		} else if (app.gprs >= t1) {// 变小了

		} else {// 正常情况
			app.day.addGPRS(t1 - app.gprs);
		}

		if (t2 == 0) {// 清除为0了。

		} else if (app.total >= t2) {// 变小了

		} else {// 正常情况
			app.day.addTotal(t2 - app.total);
		}

		app.gprs = t1;
		app.total = t2;		
		
		try {
			saveToSdCard(app);
		} catch (Exception e) {
			if (AD.debug)
			if (ctx instanceof Activity)
				MessageUtil.showErr4Activity((Activity) ctx, "saveToSdCard", e);
			else
				MessageUtil.showInfo4Service((Service) ctx, "saveToSdCard", e);
		}
	}

	/**
	 * 保存当天的流量数据
	 * 
	 * @throws Exception
	 */
	private static void saveToSdCard(MyApp app) throws Exception {
		if (System.currentTimeMillis() - app.last < 10)// 最多10秒写一次
			return;
		app.last = System.currentTimeMillis();
		// 得到手机默认存储目录。并实例化
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "gprs" + File.separator,
				app.day.getSaveFileName());
		if (!file.exists()) {
			File parentFile = file.getParentFile();
			if (!parentFile.exists())
				parentFile.mkdirs();
		}

		saveMM(app, file, app.day);

	}

	/**
	 * 保存流量数据
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	private static void saveMM(MyApp app, File file, DayM dayM)
			throws IOException {
		if (dayM.getDay() == null)
			return;
		OutputStream fos = new FileOutputStream(file);
		fos.write(dayM.getSaveStr().getBytes());
		fos.close();
		
		if (AD.debug){
		RandomAccessFile rf = new RandomAccessFile(Environment.getExternalStorageDirectory()
				+ File.separator + "gprs" + File.separator + "debug.txt", "rw");
		// 定义一个类RandomAccessFile的对象，并实例化
		rf.seek(rf.length());// 将指针移动到文件末尾
		rf.writeBytes(new Date()+(app.yesDayM == null ? " yesDayM null " : " yesDayM "
				+ app.yesDayM.toString())
				+ " day "
				+ app.day.toString()
				+ " getGPRS "
				+ getGRPS()
				+ " getTotal "
				+ getTotal()
				+ " app.gprs "
				+ app.gprs
				+ " app.total "
				+ app.total+"\n");
		rf.close();// 关闭文件流
		}
	}

	public static DayM getCurDateM()  {
		return getM(Environment.getExternalStorageDirectory() + File.separator
				+ "gprs" + File.separator + MyApp.format.format(new Date()) + ".log");
	}

	/**
	 * 取得文件里面已经保存的流量数据
	 * 
	 * @param filename
	 * @return DayM
	 * @throws IOException
	 */
	private static DayM getM(String filename)  {
		try {
			return getM(new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return MyApp.zero;
	}

	/**
	 * 取得文件里面已经保存的流量数据
	 * 
	 * @param file
	 * @return DayM
	 * @throws IOException
	 */
	private static DayM getM(File file) throws IOException {
		DayM ret = MyApp.zero;
		if (file.exists()) {
			byte[] bytes = new byte[(int) file.length()];
			InputStream fin = new FileInputStream(file);
			fin.read(bytes);
			String content = new String(bytes);
			String[] s = content.split(" ");
			ret = new DayM(file.getName().substring(0, 10),
					Double.valueOf(s[0]), Double.valueOf(s[1]));
			fin.close();
		}

		return ret;
	}

	/**
	 * 取得当月累计流量数据
	 * 
	 * @return
	 * @throws IOException
	 */
	public static DayM getMM() throws IOException {
		DayM month = new DayM();
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "gprs");
		if (!file.exists())
			return MyApp.zero;
		File[] files = file.listFiles();
		Date d = new Date();
		String s = MyApp.format.format(d).substring(0, 7);
		for (File f : files) {
			if (!f.getName().startsWith(s))
				continue;
			DayM tmp = getM(f);
			month.add(tmp);
		}
		return month;
	}

	static double getGRPS() {
		return getData(0) + getData(1);
	}

	static double getTotal() {
		return getData(2) + getData(3);
	}

	/**
	 * @param type
	 * @return
	 */
	private static double getData(int type) {
		double size = 0;
		if (type == 0) {// 获取通过Mobile连接收到的字节总数，不包含WiFi
			size = TrafficStats.getMobileRxBytes() == TrafficStats.UNSUPPORTED ? 0
					: TrafficStats.getMobileRxBytes();
		} else if (type == 1) { // Mobile发送的总字节数
			size = TrafficStats.getMobileRxBytes() == TrafficStats.UNSUPPORTED ? 0
					: TrafficStats.getMobileTxBytes();
		} else if (type == 2) { // 获取总的接受字节数，包含Mobile和WiFi等
			size = TrafficStats.getMobileRxBytes() == TrafficStats.UNSUPPORTED ? 0
					: TrafficStats.getTotalRxBytes();
		} else if (type == 3) { // 发送的总数据包数，包含Mobile和WiFi等
			size = TrafficStats.getMobileRxBytes() == TrafficStats.UNSUPPORTED ? 0
					: TrafficStats.getTotalTxBytes();
		} else {
			throw new RuntimeException();
		}
		return size / 1024 / 1024;
	}
}