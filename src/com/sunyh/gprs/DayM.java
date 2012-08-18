package com.sunyh.gprs;

import java.text.DecimalFormat;

public class DayM {

	public final static DecimalFormat format = new DecimalFormat("#.##");;
	private String day;
	private double gprs;
	private double total;

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
	
	public void addGPRS(double gprs) {
		this.gprs += gprs;
	}
	
	public void addTotal(double total) {
		this.total += total;
	}
	
	public DayM(String day, double gprs, double total) {
		this.day = day;
		this.gprs = gprs;
		this.total = total;
	}

	public DayM(String day) {
		this.day = day;
	}
	
	public DayM() {
	}

	public double getGPRS() {
		return gprs;
	}

	public double getTotal() {
		return total;
	}

	public double getWifi() {
		return total - gprs;
	}

	public String getData(int type) {
		double size = 0;
		if (type == 0) {
			size = getGPRS();
		} else if (type == 1) {
			size = getWifi();
		} else if (type == 2) {
			size = getTotal();
		}
		return convert(size);
	}

	private static String convert(double d) {
		return format.format(d);
	}

	public void add(DayM d) {
		this.gprs += d.gprs;
		this.total += d.total;
	}
	
	public void subtract(DayM d) {
		if (d == null)
			return;
		this.gprs -= d.gprs;
		this.total -= d.total;
	}
	
	public void clear() {
		this.gprs = 0;
		this.total = 0;
	}

	public String getSaveFileName() {
		return day + ".log";
	}

	public String getSaveStr() {
		return convert(gprs) + " " + convert(total);
	}

	public DayM clone() {
		return new DayM(day, gprs, total);
	}

	public String toString() {
		return day + " "+getSaveStr();
	}
}