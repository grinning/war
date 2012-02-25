package com.tommytony.war.job;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.tommytony.war.Warzone;
import java.util.Random;

public class CalculateRespawnPointJob implements Runnable {

	private Warzone warzone;
	public volatile boolean done;
	private volatile Location finalLoc;
	
	public CalculateRespawnPointJob(Warzone warzone) {
		this.warzone = warzone;
		this.done = false;
	}
	
	@Override
	public strictfp void run() {
		Random rand = new Random();
		int[] a = new int[3];
		a[0] = rand.nextInt(warzone.getVolume().getMaxX());
		a[1] = rand.nextInt(warzone.getVolume().getMaxY());
		a[2] = rand.nextInt(warzone.getVolume().getMaxZ());
		
		World world = warzone.getWorld();
		int[] b = this.normalizeNumbers(a);
		boolean calcdone = false;
		
		Block block = world.getBlockAt(b[0], b[1], b[2]);
		if(block.getType() == Material.AIR) {
			byte changefactor = 1;
			while(!calcdone) {
				b[1] -= changefactor;
				block = world.getBlockAt(b[0], b[1], b[2]);
				if(block.getType() != Material.AIR) {
					calcdone = true;
				} else {
					changefactor++;
				}
			}
		} else {
			this.setLocation(b[0], b[1], b[2]);
		}
	}
	
	
	public strictfp boolean checkLocation(int x, int y, int z) {
		if(this.warzone.getVolume().contains(new Location(this.warzone.getWorld(), x, y, z))) {
			return true;
		}
		return false;
	}
	
	public strictfp int[] normalizeNumbers(int[] a) { //lolz strictfp methods cause no Floating point operations!
		Random rand = new Random();
		boolean getout = false;
		
		while(!getout) {
		if(a[0] < warzone.getVolume().getMinX()) {
			getout = false;
			a[0] += rand.nextInt(warzone.getVolume().getSizeX());
		}
		if(a[1] < warzone.getVolume().getMinY()) {
			getout = false;
			a[1] += rand.nextInt(warzone.getVolume().getSizeY());
		}
		if(a[2] < warzone.getVolume().getMinZ()) {
			getout = false;
			a[2] += rand.nextInt(warzone.getVolume().getSizeZ());
		}
		if(a[1] < warzone.getVolume().getMaxY() && a[1] > warzone.getVolume().getMinY()) {
			if(a[0] < warzone.getVolume().getMaxX() && a[1] > warzone.getVolume().getMinX()) {
				if(a[2] < warzone.getVolume().getMaxZ() && a[2] > warzone.getVolume().getMinZ()) {
					getout = true;
				}
			}
		}
	   }
		return a;
	}
	
	public strictfp synchronized void setLocation(int x, int y, int z) {
		this.done = true;
		this.finalLoc = new Location(warzone.getWorld(), x, y, z);
	}
	
	public strictfp Location getFinalLoc() {
		return this.finalLoc;
	}
}
