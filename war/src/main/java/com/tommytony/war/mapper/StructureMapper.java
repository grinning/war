package com.tommytony.war.mapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class StructureMapper {

	private String sep = System.getProperty("path.separator");
	
	public StructureMapper() {
		super();
	}
	
	public void save(String type, String name, Player p, Location pos1, Location pos2) {
		
		File file = new File("plugins" + sep + "war" + sep + "structures" + sep + type + sep + name + ".sdat"); //.sdat = structure data
	
		int xSize = (int)Math.abs(Math.floor(pos1.getBlockX() - (pos2.getBlockX())));
		int ySize = (int)Math.abs(Math.floor(pos1.getBlockY() - (pos2.getBlockY())));
		int zSize = (int)Math.abs(Math.floor(pos1.getBlockZ() - (pos2.getBlockZ())));
		World world = pos1.getWorld();
		
		int startx = pos1.getBlockX();
		int starty = pos1.getBlockY();
		int startz = pos1.getBlockZ();
		
	    double yaw = ((p.getLocation().getYaw() + 22.5) % 360);
	    if(yaw < 0)
	    	yaw += 360;
	    
	    int faceroni = (int) (yaw / 90);
	    String frontEnd = Integer.toString(xSize) + ';' + Integer.toString(ySize) + ';' + Integer.toString(zSize) + ';' + Integer.toString(faceroni) + ";";
	    String blocks = "";
	    
	    for(int x = 0; x < xSize; x++) {
	    	
	    	for(int y = 0; y < ySize; y++) {
	    		
	    		for(int z = 0; z < zSize; z++) {
	    		Location place = new Location(world, startx + x, starty + y, startz + z);
	    		blocks += Integer.toString(place.getBlock().getTypeId());
	    		blocks += ",";
	    		blocks += Byte.toString(place.getBlock().getData());
	    		blocks += ";";
	    		}
	    	}
	    }
		
	    try {
	    	file.createNewFile();
	    	PrintWriter pw = new PrintWriter(new FileWriter(file.getAbsolutePath()));
	    	pw.println((new StringBuilder().append(frontEnd)));
	    	pw.println((new StringBuilder().append(blocks)));
	    	pw.flush();
	    	pw.close();
	    } catch(IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	public void load(String type, String name, Player p) {
		File file = new File("plugins" + sep + "war" + sep + "structures" + sep + type + sep + name + ".sdat");
		
		if(!file.exists()) {
			p.sendMessage("YOU FAIL!!!!");
			return;
		}
		
		String[] frontEnd = null;
		String[] blocks = null;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
			frontEnd = reader.readLine().split(";");
			blocks = reader.readLine().split(";");
			reader.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		
		int xSize = Integer.valueOf(frontEnd[0]);
		int ySize = Integer.valueOf(frontEnd[1]);
		int zSize = Integer.valueOf(frontEnd[2]);
		int wasFacing = Integer.valueOf(frontEnd[3]);
		
		int px = p.getLocation().getBlockX();
		int py = p.getLocation().getBlockY();
		int pz = p.getLocation().getBlockZ();
		
		World world = p.getWorld();
		
		int i = 0;

		//West
		
		/*
		 * yaw: 0
		 * Forward: z + 1
		 * Right: x - 1
		 * Left: x + 1
		 * Back: z - 1
		 * 
		 * North 
		 * yaw: 90
		 * Forward: x - 1
		 * right: z - 1
		 * left: z + 1
		 * back: x + 1
		 *
		 */
		
		double yaw = ((p.getLocation().getYaw() + 22.5) % 360);
		if(yaw < 0)
			yaw += 360;
		
		int pDir = (int) (yaw / 90);
		
		int abs1 = Math.abs(pDir);
		int abs2 = Math.abs(wasFacing);
		
		byte transformation_mode = 0;
		
		int rotationmuch = Math.abs((abs1 - abs2));
		
		if(pDir == wasFacing){
			 transformation_mode = 0;
		} else if(rotationmuch == 90) {
			transformation_mode = 1;
		} else if(rotationmuch == 180) {
			transformation_mode = 2;
		} else if(rotationmuch == 270) {
			transformation_mode = 3;
		}
		
		for(int x = 0; x < xSize; x++) {
			
			for(int y = 0; y < ySize; y++) {
				
				for(int z = 0; z < zSize; z++) {
			
					int transformedx = 0;
					int transformedz = 0;
					
					if(transformation_mode == 0) {
						
					} else if(transformation_mode == 1) {
						transformedx = (int) (px * Math.cos(90) - pz * Math.sin(90));
						transformedz = (int) (px * Math.sin(90) + pz * Math.cos(90));
					} else if(transformation_mode == 2) {
						transformedx = (int) (px * Math.cos(180) - pz * Math.sin(180));
						transformedz = (int) (px * Math.sin(180) + pz * Math.cos(180));
					} else if(transformation_mode == 3) {
						transformedx = (int) (px * Math.cos(270) - pz * Math.sin(270));
						transformedz = (int) (px * Math.sin(270) + pz * Math.cos(270));
					}
					
					Location loc = new Location(world, transformedx + x, py + y, transformedz + z);
					Block b = loc.getBlock();
					String[] blockData = blocks[i].split(",");
					b.setTypeId(Integer.parseInt(blockData[0]));
					b.setData(Byte.parseByte(blockData[1]));
					i++;
				}
			}
		}
	}
}
