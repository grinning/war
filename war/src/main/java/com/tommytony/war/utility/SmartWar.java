package com.tommytony.war.utility;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

import com.tommytony.war.War;


public class SmartWar implements Runnable {

	private War war;
	private final double BlockingCoefficiant = 0.9;
	
    public SmartWar(War instance) {
    	this.war = instance;
    }
	
	public MemoryUsage getHeapMemoryUsage() {
	    return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
	}
	
	//number of threads = Number of availiable cores / (1 - Blocking Coefficiant)
	//where 0 <= Blocking Coefficiant <= 1
	//Blocking Coefficiant depends on the type of operation being performed
	
	public int getProcessors() {
		return ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
	}
	
	public int getAvailiableCores() {
		return Runtime.getRuntime().availableProcessors();
	}
	
	public int getNumberOfThreads() {
		return (int) (this.getAvailiableCores() / (1 - this.BlockingCoefficiant));
		
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
