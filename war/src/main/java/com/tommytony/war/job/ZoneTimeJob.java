package com.tommytony.war.job;

import java.util.logging.Level;

import com.tommytony.war.War;
import com.tommytony.war.Warzone;

public class ZoneTimeJob implements Runnable {

	private final int sec;
	private final Warzone zone;
	
	public ZoneTimeJob(int sec, Warzone zone) {
		this.sec = sec; //Time in SECONDS
		this.zone = zone;
	}
	
	@Override
	public void run() {
		try {
			zone.setPvpReady(false);
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			War.war.log("Failed to init timer for warzone", Level.SEVERE);
			e.printStackTrace();
		}
		zone.setPvpReady(true);
	}

	
}
