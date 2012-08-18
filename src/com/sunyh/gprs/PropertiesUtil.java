package com.sunyh.gprs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import android.os.Environment;

public class PropertiesUtil {

	final static String propFile = Environment.getExternalStorageDirectory()
			+ File.separator + "gprs" + File.separator + "gprs.cfg";

	public static Properties loadConfig() {
		Properties properties = new Properties();
		FileInputStream s = null;
		try {
			s = new FileInputStream(propFile);
			properties.load(s);
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			try {
				if (s != null)
					s.close();
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}
		return properties;
	}

	public static void saveConfig(Properties properties) {
		FileOutputStream s = null;
		try {
			s = new FileOutputStream(propFile, false);
			properties.store(s, "");
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			try {
				if (s != null)
					s.close();
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}
	}
}
