package com.github.dusby.tsopen.utils;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeOut {

	private Timer timer;
	private TimerTask exitTask = null;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public TimeOut() {
		this.timer = new Timer();
		this.exitTask = new TimerTask() {
			@Override
			public void run() {
				System.exit(0);
			}
		};
	}

	public void trigger(int n) {
		int timeout = n != 0 ? n : 60;
		this.logger.info("Timeout : {} minutes", timeout);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, timeout);
		this.timer.schedule(this.exitTask, c.getTime());
	}

	public void cancel() {
		this.timer.cancel();
	}
}
