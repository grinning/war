package com.tommytony.war.utility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.tommytony.war.Warzone;
import com.tommytony.war.structure.Monument;

public class WarNPC  {

	private transient HumanEntity npc;
	
	public WarNPC(HumanEntity npc) {
		this.npc = npc;
	}
	
	public void lookAt(Location place) {
		if(this.npc.getWorld() != place.getWorld()) {
			return;
		} 
		double xDiff = place.getX() - npc.getLocation().getBlockX();
		double yDiff = place.getY() - npc.getLocation().getBlockY();
		double zDiff = place.getZ() - npc.getLocation().getBlockZ();
		double disFormXZ = Math.sqrt((xDiff * xDiff) + (zDiff * zDiff));
		double disFormY = Math.sqrt((disFormXZ * disFormXZ) + (yDiff * yDiff));
		double yaw = Math.acos((xDiff / disFormXZ) * 180 / Math.PI);
		double pitch = Math.acos((yDiff / disFormY) * 180 / Math.PI - 90);
		
		if(zDiff < 0.0) {
			yaw += Math.abs(180 - yaw) * 2;
		}
		
		npc.getLocation().setYaw((float) yaw - 90);
		npc.getLocation().setPitch((float) pitch);
	}
	
	public void lookAt(Player player) {
		this.lookAt(player.getLocation());
	}
	
	public void fireArrow(Entity target) {
		Arrow arrow = npc.shootArrow();
		arrow.setShooter(this.npc);

	}
	
	public void turnAround() {
		npc.setVelocity(npc.getVelocity().multiply(-1));
	}
	
	public void hitWithSword() {
		for(Player player: Bukkit.getServer().getOnlinePlayers()) {
			if(this.checkLoc(player.getLocation())) {
				player.damage(5); //2.5 hearts
			}
		}
	}
	
	public Warzone getWarzone() {
		return Warzone.getZoneByLocation(npc.getLocation());
	}
	
	private boolean checkLoc(Location pLoc) {
		if(Math.abs(pLoc.getBlockX() - npc.getLocation().getBlockX()) < 2
				&&
			Math.abs(pLoc.getBlockY() - npc.getLocation().getBlockY()) < 2
			    &&
			Math.abs(pLoc.getBlockZ() - npc.getLocation().getBlockZ()) < 2
			&&
			Math.abs(pLoc.getYaw() - npc.getLocation().getYaw()) < 90) { //test for pointing at 
			return true;
		}
		return false;
	}
	
	public void toMonument(String name) {
		this.npc.setVelocity(this.getWarzone().getMonument(name).getLocation().getDirection().multiply((Math.sqrt(3) * 2) / 3)); //Our AI's are FAST!
	} //multiply by 2 root 3 over 3
	
	public void switchItemInHand(byte id) {
		switch(id) {
		case 0: 
			this.npc.setItemInHand(new ItemStack(Material.STONE_SWORD));
			break;
		case 1:
			this.npc.setItemInHand(new ItemStack(Material.IRON_SWORD));
			break;
		case 2:
			this.npc.setItemInHand(new ItemStack(Material.WOOD_SWORD));
			break;
		case 3:
			this.npc.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
			break;
		case 4:
			this.npc.setItemInHand(new ItemStack(Material.TNT, 20));
			break;
		case 5:
			this.npc.setItemInHand(new ItemStack(Material.REDSTONE_TORCH_ON));
			break;
		case 6:
			this.npc.setItemInHand(new ItemStack(Material.BOW));
			break;
		case 7:
			this.npc.setItemInHand(new ItemStack(Material.GOLD_SWORD));
			break;
		case 8:
			this.npc.setItemInHand(new ItemStack(Material.STONE_SPADE));
			break;
		case 9:
			this.npc.setItemInHand(new ItemStack(Material.WOOD_SPADE));
			break;
		case 10:
			this.npc.setItemInHand(new ItemStack(Material.IRON_SPADE));
			break;
		case 11:
			this.npc.setItemInHand(new ItemStack(Material.STONE_SPADE));
			break;
		case 12:
			this.npc.setItemInHand(new ItemStack(Material.DIAMOND_SPADE));
			break;
		case 13:
			this.npc.setItemInHand(new ItemStack(Material.WOOL));
			break;
		case 14:
			this.npc.setItemInHand(new ItemStack(Material.SNOW_BALL));
			break;
		default:
			break;
		} 
		
	}
	
	public void captureMonument(Monument mon, byte woolColor) {
		Block monCenter = mon.getLocation().getBlock();
		Location target = new Location(mon.getLocation().getWorld(), monCenter.getX(), monCenter.getY() + 1, monCenter.getZ());
		
		if(this.checkLoc(target)) {
			mon.getLocation().getWorld().getBlockAt(target).setType(Material.AIR);
			try {
				this.wait(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mon.getLocation().getWorld().getBlockAt(target).setType(Material.WOOL);
			mon.getLocation().getWorld().getBlockAt(target).setData(woolColor);
		} else {
			return;
		}
	}
	
	public HumanEntity getNpc() {
	    return this.npc;	
	}
}
