package com.tommytony.war.utility;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import com.tommytony.war.War;


public class SmartWar implements Runnable {

	private War war;
	
    public SmartWar(War instance) {
    	this.war = instance;
    }
	
	public MemoryUsage getHeapMemoryUsage() {
	    return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
	}
	
	public int getProcessors() {
		return ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
	}
	
	public int getThreads() {
		return ManagementFactory.getThreadMXBean().getThreadCount();
	}
	
	public boolean getMemoryOk() {
		long totalMem = this.getHeapMemoryUsage().getCommitted();
		long usedMem = this.getHeapMemoryUsage().getUsed();
		
		if(totalMem - usedMem < 50000) { //if the JVM has less then 50,000 bytes of memory left
			return false;
		} else {
		return true;	
		}
	}

	public MemoryUsage getNonHeapMemoryUsage() {
		return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
	}

	public int getObjectPendingFinalizationCount() {
		return ManagementFactory.getMemoryMXBean().getObjectPendingFinalizationCount();
	}

    public void normalGc() {
    	System.gc();
    }
    
    public void smartGc() {
    	ManagementFactory.getMemoryMXBean().gc();
    }
    

	@Override
	public void run() {
		
		
	}
	
	@Override
	public void finalize() {
		this.war = null;
	}
    


}
