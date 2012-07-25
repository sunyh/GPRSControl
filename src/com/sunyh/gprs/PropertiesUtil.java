package com.sunyh.gprs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import android.os.Environment;

public class PropertiesUtil {
	static Properties prop = new Properties();

	static {
		prop = PropertiesUtil.loadConfig();
	}

	final static String propFile = Environment.getExternalStorageDirectory()
			+ File.separator + "gprs" + File.separator + "gprs.cfg";

	public static String getProp(String type) {
		return prop.getProperty(type);
	}

	public static void putProp(String type, Object value) {
		prop.put(type, String.valueOf(value));
	}

	public static Properties loadConfig() {
		Properties properties = new Properties();
		try {
			FileInputStream s = new FileInputStream(propFile);
			properties.load(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}

	public static void saveConfig(Properties properties) {
		try {
			FileOutputStream s = new FileOutputStream(propFile, false);
			properties.store(s, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
