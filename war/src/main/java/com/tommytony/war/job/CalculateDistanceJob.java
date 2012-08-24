package com.tommytony.war.job;

import java.util.concurrent.Callable;

import org.bukkit.entity.Player;

public class CalculateDistanceJob implements Callable<Integer> {

	private final Player a;
	private final Player d;
	
	public CalculateDistanceJob(Player a, Player d) {
		this.a = a;
		this.d = d;
	}
	
	@Override
	public Integer call() throws Exception {
		int aX = (int) a.getLocation().getX();
		int aY = (int) a.getLocation().getY();
		int aZ = (int) a.getLocation().getZ();
		int dX = (int) d.getLocation().getX();
		int dY = (int) d.getLocation().getY();
		int dZ = (int) d.getLocation().getZ();
		
		//dist form for 3d = SQRT((x1 - x2)^2 + (y1 - y2)^2 + (z1 - z2)^2)
		return new Integer((int) Math.sqrt(((aX - dX) * (aX - dX)) + ((aY - dY) * (aY - dY)) + ((aZ - dZ) * (aZ - dZ))));
	}

}
