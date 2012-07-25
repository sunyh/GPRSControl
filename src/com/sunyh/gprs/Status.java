package com.sunyh.gprs;

public class Status {

	public static Status instance;
	/**
	 * 是否需要震动
	 */
	private boolean bNeedVib;

	public void setbNeedVib(boolean bNeedVib) {
		this.bNeedVib = bNeedVib;
	}

	public boolean isbNeedVib() {
		return bNeedVib;
	}

	private Status() {

	}

	public static Status getInstance() {
		if (instance == null) {
			instance = new Status();
		}
		return instance;
	}
}