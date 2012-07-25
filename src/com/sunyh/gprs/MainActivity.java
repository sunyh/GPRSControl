package com.sunyh.gprs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import net.youmi.android.AdManager;
import net.youmi.android.AdView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.gfan.sdk.statitistics.GFAgent;
import com.sunyh.util.MessageUtil;

public class MainActivity extends Activity {
	final static DayM zero = new DayM();
	public final static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.CHINESE);
	private final static String STRING_EMPTY = "";
	private final static String STRING_DES = "des";
	private final static String STRING_SIZE = "size";

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("您确定要退出GPRS监控吗？");
		builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 退出程序
				Intent exitIntent = new Intent(Intent.ACTION_MAIN);
				exitIntent.addCategory(Intent.CATEGORY_HOME);
				startActivity(exitIntent);
				MainActivity.this.onDestroy();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
		return true;
	}

	protected void onPause() {
		super.onPause();

		if (Version.GF)
			GFAgent.onPause(this);
	}

	protected void onResume() {
		super.onResume();

		if (Version.GF)
			GFAgent.onResume(this);

		saveM(this);

		ListView listView = (ListView) findViewById(R.id.listView);
		try {
			listView.setAdapter(getListAdapter());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Warning.checkData(Warning.warns1);
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
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map;
		// 标题
		map = new HashMap<String, Object>();
		map.put(STRING_DES, "今天流量");// 应该要是今天累计,直接读文件里面的数据
		map.put(STRING_SIZE, "单位/M");
		list.add(map);
		//
		String[] des = new String[] { "2G/3G", "wifi", "总计" };
		for (int i = 0; i < des.length; i++) {
			map = new HashMap<String, Object>();
			map.put(STRING_DES, des[i]);
			map.put(STRING_SIZE, day.getData(i));
			list.add(map);
		}

		// 本月数据
		map = new HashMap<String, Object>();
		map.put(STRING_DES, "本月流量");
		map.put(STRING_SIZE, STRING_EMPTY);
		list.add(map);

		DayM data = getMM();
		for (int i = 0; i < des.length; i++) {
			map = new HashMap<String, Object>();
			map.put(STRING_DES, des[i]);
			map.put(STRING_SIZE, data.getData(i));
			list.add(map);
		}

		// 设置
		map = new HashMap<String, Object>();
		map.put(STRING_DES, "设置阀值");
		map.put(STRING_SIZE, STRING_EMPTY);
		list.add(map);

		// 关于
		des = new String[] { "联系我们", "QQ", "Email" };
		String[] info = new String[] { "", "1577636387",
				"main_syh@yahoo.com.cn" };
		for (int i = 0; i < des.length; i++) {
			map = new HashMap<String, Object>();
			map.put(STRING_DES, des[i]);
			map.put(STRING_SIZE, info[i]);
			list.add(map);
		}

		int[] tt = { R.id.des, R.id.size };
		ListAdapter adapter = new SimpleAdapter(this, list, R.layout.list,
				new String[] { STRING_DES, STRING_SIZE }, tt);
		return adapter;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Intent intent = new Intent(GPRSService.class.getName());
		startService(intent);

		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> map = (HashMap<String, Object>) arg0
						.getItemAtPosition(arg2);
				if (map.get(STRING_DES).equals("设置阀值")) {

					Intent intent = new Intent(MainActivity.this,
							SettingActivity.class);
					startActivity(intent);
				}
				Log.d(this.getClass().toString(), "3 " + new Date());
			}
		});

		// ActivityManager am = (ActivityManager) this
		// .getSystemService(Context.ACTIVITY_SERVICE);

		// List<RunningServiceInfo> tmp= am.getRunningServices(100);
		// for (RunningServiceInfo inf : tmp) {
		// Log.e("RunningServiceInfo",inf.toString());
		//
		// }
		// 应用Id 应用密码 广告请求间隔(s) 测试模式
		if (Version.youmi) {
			AdManager.init(this, "198762f972d578e2", "119ca3646511d528", 30,
					false);
			LinearLayout adViewLayout = (LinearLayout) findViewById(R.id.adViewLayout1);
			adViewLayout.addView(new AdView(this), new LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * 把现在的数据然后加上已经存在的数据
	 * 
	 * @param ctx
	 * @throws Exception
	 */
	static void saveM(Context ctx) {
		String curDate = format.format(new Date());
//		if (yesDayM == null) {// 第一次进入需要记录已经产生的流量，这个值不会被记录,因为不知道是多少时间产生的流量(开机的时候)
//			yesDayM = new DayM(curDate, getGRPS(), getTotal());
//		}
		// 进入新的一天，记录凌晨的流量
//		if (!curDate.equals(yesDayM.getDay())) {
//			yesDayM = new DayM(curDate, getGRPS(), getTotal());
//		}

		day = new DayM(curDate, getGRPS(), getTotal());
//		day.subtract(yesDayM);
		try {
			saveToSdCard();
		} catch (Exception e) {
			if (ctx instanceof Activity)
			MessageUtil.showErr4Activity((Activity) ctx, "getM", e);
		}
	}

	static long last;

	/**
	 * 今天凌晨的流量，今天产生的流量需要减去此值
	 */
//	static DayM yesDayM;
	/**
	 * 记录产生今天的流量
	 */
	static DayM day = new DayM();
	static DayM month = new DayM();

	/**
	 * 保存当天的流量数据
	 * 
	 * @throws Exception
	 */
	private static void saveToSdCard() throws Exception {
		if (System.currentTimeMillis() - last < 10)// 最多10秒写一次
			return;
		last = System.currentTimeMillis();
		// 得到手机默认存储目录。并实例化
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "gprs" + File.separator,
				day.getSaveFileName());
		if (!file.exists()) {
			File parentFile = file.getParentFile();
			if (!parentFile.exists())
				parentFile.mkdirs();
		}
		{
			// 如果文件已经存在了，取出今天已经保存的数据，然后累加，因为关机后数据会被清除
			if (day.getDay().equals(GPRSService.OldDay.getDay()))
				day.add(GPRSService.OldDay);// 加上老的数据
			saveMM(file, day);
		}
	}

	/**
	 * 保存流量数据
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	private static void saveMM(File file, DayM dayM) throws IOException {
		if (dayM.getDay() == null)
			return;
		OutputStream fos = new FileOutputStream(file);
		fos.write(dayM.getSaveStr().getBytes());
		fos.close();
	}

	public static DayM getCurDateM() throws IOException {
		return getM(Environment.getExternalStorageDirectory() + File.separator
				+ "gprs" + File.separator + format.format(new Date()) + ".log");
	}

	/**
	 * 取得文件里面已经保存的流量数据
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	private static DayM getM(String filename) throws IOException {
		return getM(new File(filename));
	}

	/**
	 * @throws IOException
	 *             取得文件里面已经保存的流量数据
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 * @throws
	 */
	private static DayM getM(File file) throws IOException {
		DayM ret = zero;
		if (file.exists()) {
			byte[] bytes = new byte[(int) file.length()];
			InputStream fin;

			fin = new FileInputStream(file);
			fin.read(bytes);
			String content = new String(bytes);
			String[] s = content.split(" ");
			ret = new DayM(file.getName().substring(0, 10),
					Double.valueOf(s[0]), Double.valueOf(s[1]));
		}

		return ret;
	}

	/**
	 * 取得当月累计流量数据
	 * 
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static DayM getMM() throws IOException {
		DayM month = new DayM();
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "gprs");
		if (!file.exists())
			return zero;
		File[] files = file.listFiles();
		Date d = new Date();
		String s = format.format(d).substring(0, 7);
		for (File f : files) {
			if (!f.getName().startsWith(s))
				continue;
			DayM tmp = getM(f);
			month.add(tmp);
		}
		return month;
	}

	private static double getGRPS() {
		return getData(0) + getData(1);
	}

	private static double getTotal() {
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