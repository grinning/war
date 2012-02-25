package com.tommytony.war.job;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.tommytony.war.Warzone;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class CalculateRespawnPointJob implements Runnable {

	private Warzone warzone;
	private volatile Location finalLoc;
	public volatile AtomicBoolean done = new AtomicBoolean(false);
	
	
	public CalculateRespawnPointJob(Warzone warzone) {
		this.warzone = warzone;
		this.done.lazySet(false);
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
				if(b[1] <= warzone.getVolume().getMinY()) { //if we cannot find anything! in this place, we gonna go up some then change to next X
					b[1] = warzone.getVolume().getMaxY() - 1; //ohh ya!!!!!!!! (grinneroni + cheese)
					b[0]++; //There be dinosaurs
					calcdone = false;
				}
				if(block.getType() != Material.AIR) { //if we find a block lets do some more calculations!
					Block up1 = world.getBlockAt(b[0], b[1] + 1, b[2]);
					Block up2 = world.getBlockAt(b[0], b[1] + 2, b[2]);
					if(up1.getType() == Material.AIR && up2.getType() == Material.AIR) {
						calcdone = true; //our calculate is done, we found a spawnable position inside the warzone!
					}
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
	
	public strictfp void setLocation(int x, int y, int z) {
		this.done.lazySet(true); //I DO NOT WANT THE THREADS WRITING OVER EACH OTHER! CONCURRENCY SAFTEY IS MORE IMPORTANT
		this.finalLoc = new Location(warzone.getWorld(), x, y, z);
		//OHH YA, ATOMIC STLYE !!!!!! NO NEED FOR SYNCHROSIS!!!
	}
	
	public strictfp Location getFinalLoc() {
		return this.finalLoc;
	}
}
