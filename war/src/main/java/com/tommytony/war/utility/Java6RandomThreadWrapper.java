package com.tommytony.war.utility;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;

import com.tommytony.war.War;

public class Java6RandomThreadWrapper implements Cloneable {

	public int nextRandomInt() throws InterruptedException, ExecutionException {
		Future<Integer> result = Bukkit.getScheduler().callSyncMethod(War.war, new Callable<Integer>() {
			@Override
			public Integer call() {
				return Integer.valueOf(new Random().nextInt());
			}
		});
		return result.get().intValue();
	}
	
	public int nextRandomInt(final int max) throws InterruptedException, ExecutionException {
		Future<Integer> result = Bukkit.getScheduler().callSyncMethod(War.war, new Callable<Integer>() {
			@Override
			public Integer call() {
				return Integer.valueOf(new Random().nextInt(max));
			}
		});
		return result.get().intValue();
	}
	
	
	
	
	@Override
	public Java6RandomThreadWrapper clone() {
		return this;
	}
	
}
