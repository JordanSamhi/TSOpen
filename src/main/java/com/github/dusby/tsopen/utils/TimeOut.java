package com.github.dusby.tsopen.utils;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeOut {

	private Timer timer;
	private TimerTask exitTask = null;
	private int timeout;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public TimeOut(int n) {
		this.timer = new Timer();
		this.exitTask = new TimerTask() {
			@Override
			public void run() {
				System.exit(0);
			}
		};
		this.timeout = n != 0 ? n : 60;
	}

	public void trigger() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, this.timeout);
		this.timer.schedule(this.exitTask, c.getTime());
	}

	public void cancel() {
		this.timer.cancel();
	}

	public int getTimeout() {
		return this.timeout;
	}

}
