package com.sunyh.gprs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Warning {

	/**
	 * activity对应的震动信息
	 */
	static List<Warning> warns1;

	/**
	 * service对应的震动信息
	 */
	static List<Warning> warns2;

	static {
		warns1 = initWarning();

		warns2 = initWarning();
	}

	final static String s1 = "当天GPRS流量已经超过阀值";

	final static String s2 = "当天wifi流量已经超过阀值";

	final static String s3 = "当月GPRS流量已经超过阀值";

	final static String s4 = "当月Wifi流量已经超过阀值";

	public static List<Warning> initWarning() {
		List<Warning> warns = new ArrayList<Warning>(4);
		warns.add(new Warning(null, 1, false, false, s1,2));
		warns.add(new Warning(null, 2, false, false, s2,1));
		warns.add(new Warning(null, 3, false, false, s3,2));
		warns.add(new Warning(null, 4, false, false, s4,1));
		return warns;
	}

	/**
	 * 警告的时间
	 */
	Date date;

	/**
	 * 警告类型(当日gprs,当日wifi,当月gprs,当月wifi)
	 */
	int type;
	
	/**
	 * 网络类型 1-wifi 2-gprs，3-other
	 */
	int netType;

	/**
	 * 是否需要警告
	 */
	private boolean needWarn;

	public boolean isNeedWarn() {
		return needWarn;
	}

	public void setNeedWarn(boolean needWarn) {
		this.needWarn = needWarn;
	}

	/**
	 * 是否已经警告过
	 */
	private boolean b;

	/**
	 * 警告的信息
	 */
	private String warnInfo;

	public String getWarnInfo() {
		return warnInfo;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * 是否已经警告过了
	 * 
	 * @return
	 */
	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	/**
	 * 设置警告的信息，已经警告和警告时间
	 * 
	 * @param b
	 * @param date
	 */
	public void setWarnInfo(boolean b, Date date) {
		this.b = b;
		this.date = date;
	}

	public Warning(Date date, int type, boolean needWarn, boolean b,
			String warnInfo,int netType) {
		this.date = date;
		this.type = type;
		this.needWarn = needWarn;
		this.b = b;
		this.warnInfo = warnInfo;
		this.netType = netType;
	}

	public String toString() {
		return "b " + this.b + " date " + this.date + " type " + this.type
				+ " needWarn " + this.needWarn + " warnInfo " + this.warnInfo+ " netType " + this.netType;
	}

	private static double convert(String s) {
		if (s == null || s.length() == 0) {
			return Double.MAX_VALUE;
		}
		return Double.valueOf(s);
	}

	static void checkData(List<Warning> warns) {

		Status.getInstance().setbNeedVib(false);
		if (MainActivity.day.getGPRS() >= convert(PropertiesUtil.prop
				.getProperty("dayGPRSMax"))) {
			Status.getInstance().setbNeedVib(true);
			warns.get(0).setNeedWarn(true);
		}
		if (MainActivity.day.getWifi() >= convert(PropertiesUtil.prop
				.getProperty("dayWifiMax"))) {
			Status.getInstance().setbNeedVib(true);
			warns.get(1).setNeedWarn(true);
		}
		if (MainActivity.month.getGPRS() >= convert(PropertiesUtil.prop
				.getProperty("monthGPRSMax"))) {
			Status.getInstance().setbNeedVib(true);
			warns.get(2).setNeedWarn(true);
		}
		if (MainActivity.month.getWifi() >= convert(PropertiesUtil.prop
				.getProperty("monthWifiMax"))) {
			Status.getInstance().setbNeedVib(true);
			warns.get(3).setNeedWarn(true);
		}
	}
}