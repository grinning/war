package com.tommytony.war.structure;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.tommytony.war.Warzone;
import com.tommytony.war.volume.Volume;

/**
 * @author grinning
 * 
 */
public class Resupply {

	private final String name;
	private Warzone warzone;
	private Location location;
	private Volume volume;
	
	public Resupply(String name, Warzone warzone, Location location) {
		this.name = name;
		this.warzone = warzone;
		this.location = location;
		this.volume = new Volume(name, warzone.getWorld());
		this.setLocation(location);
	}
	
	public String getName() {
		return this.name;
	}
	
	public Warzone getWarzone() {
		return this.warzone;
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	public Volume getVolume() {
		return this.volume;
	}
	
	public void setLocation(Location location) {
		Block locationBlock = this.warzone.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		this.volume.setCornerOne(locationBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST, 2).getRelative(BlockFace.SOUTH, 2));
		this.volume.setCornerTwo(locationBlock.getRelative(BlockFace.UP, 2).getRelative(BlockFace.WEST, 2).getRelative(BlockFace.NORTH, 2));
		this.volume.saveBlocks();
		this.location = location;
		
		this.addResupplyBlocks();
	}
	
	public void addResupplyBlocks() {
		this.volume.setToMaterial(Material.AIR);
		
		int x = this.location.getBlockX();
		int y = this.location.getBlockY();
		int z = this.location.getBlockZ();
		
		this.warzone.getWorld().getBlockAt(x, y - 1, z).setType(Material.GLOWSTONE); //below chest, center
		this.warzone.getWorld().getBlockAt(x, y, z).setType(Material.CHEST);
		this.warzone.getWorld().getBlockAt(x + 1, y - 1, z).setType(Material.OBSIDIAN);
		this.warzone.getWorld().getBlockAt(x, y - 1, z + 1).setType(Material.OBSIDIAN);
		this.warzone.getWorld().getBlockAt(x - 1, y - z, z).setType(Material.OBSIDIAN);
		this.warzone.getWorld().getBlockAt(x, y - 1, z - 1).setType(Material.OBSIDIAN);
	}
	
	public boolean isNear(Location pLoc) {
		int x = this.location.getBlockX();
		int y = this.location.getBlockY();
		int z = this.location.getBlockZ();
		int playerX = pLoc.getBlockX();
		int playerY = pLoc.getBlockY();
		int playerZ = pLoc.getBlockZ();
		int diffX = Math.abs(playerX - x);
		int diffY = Math.abs(playerY - y);
		int diffZ = Math.abs(playerZ - z);
		if (diffX < 6 && diffY < 6 && diffZ < 6) {
			return true;
		}
		return false; //some methods are pasted
		//eclipse is going crazy... thinks this thing is filled with errors
	}
	
	@Override
	public void finalize() {
		this.warzone.finalize();
		this.warzone = null;
		this.location = null;
		this.volume.finalize();
		this.volume = null;
	}
}
