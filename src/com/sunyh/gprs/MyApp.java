package com.sunyh.gprs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.Timer;

import android.app.Application;

public class MyApp extends Application {
	
	public final static DayM zero = new DayM();
	public final static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.CHINESE);
	public final static String STRING_EMPTY = "";
	public final static String STRING_DES = "des";
	public final static String STRING_SIZE = "size";

	// 一些变量放到app下不会被gc

	long last;

	/**
	 * 上次getGPRS得到的数据，有时候突然变为0了。
	 */
	double gprs;

	/**
	 * 上次getTotal得到的数据，有时候突然变为0了。
	 */
	double total;

	/**
	 * 今天凌晨的流量，今天产生的流量需要减去此值
	 */
	DayM yesDayM;
	/**
	 * 记录产生今天的流量
	 */
	DayM day;
	DayM month;

	Timer timer = new Timer();

	Properties prop;

	public String getProp(String type) {
		return prop.getProperty(type);
	}

	public void putProp(String type, Object value) {
		prop.put(type, String.valueOf(value));
	}

	public MyApp() {
		super();

		gprs=MainActivity.getGRPS();
		total=MainActivity.getTotal();
		prop = PropertiesUtil.loadConfig();

		// 本次开机前当天的流量数据已经保存了，也可能没有
		day = MainActivity.getCurDateM();
		if (day == MyApp.zero) {
			day.setDay(MyApp.format.format(new Date()));
		}
	}
}