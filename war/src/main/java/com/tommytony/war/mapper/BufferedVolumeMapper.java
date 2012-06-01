package com.tommytony.war.mapper;

import java.nio.ByteBuffer;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.tommytony.war.volume.Volume;

public class BufferedVolumeMapper {

	
	//This code is just a Buffer Safe Translation of com.tommytony.VolumeMapper.save;
	public static ByteBuffer save(Volume volume) {
		ByteBuffer buf = null;
		final char[] corner = {'c', 'o', 'r', 'n', 'e', 'r'};
		if(volume.hasTwoCorners()) {
		    //allocate a lot of space!
			//so typeid = 4 byte
			//data = 1 byte
			//+2 bytes for each thing in between them
			//so 4 bytes
			//9 bytes per block
			//So allocating space for each block + 2000 extra bytes
			
			buf = ByteBuffer.allocateDirect(volume.getSizeX() * volume.getSizeY() * 
					volume.getSizeZ() * 9 + 2000); 
			for(int i = 0; i < corner.length; i++) {
				buf.putChar(corner[i]);
			}
			buf.putChar('1');
			buf.putChar('\n');
			buf.putInt(volume.getCornerOne().getX());
			buf.putChar('\n');
			buf.putInt(volume.getCornerOne().getY());
			buf.putChar('\n');
			buf.putInt(volume.getCornerOne().getZ());
			buf.putChar('\n');
			for(int i = 0; i < corner.length; i++) {
				buf.putChar(corner[i]);
			}
			buf.putChar('2');
			buf.putChar('\n');
			buf.putInt(volume.getCornerTwo().getX());
			buf.putChar('\n');
			buf.putInt(volume.getCornerTwo().getY());
			buf.putChar('\n');
			buf.putInt(volume.getCornerTwo().getZ());
			buf.putChar('\n');
			
			for(int i = 0; i < volume.getSizeX(); i++) {
				for(int j = 0; j < volume.getSizeY(); j++) {
					for(int k = 0; k < volume.getSizeZ(); k++) {
						int typeid = volume.getBlockTypes()[i][j][k];
						byte data = volume.getBlockDatas()[i][j][k];
						buf.putInt(typeid);
						buf.putChar(',');
						buf.put(data);
						buf.putChar(',');
						if(typeid == Material.WALL_SIGN.getId() || typeid == Material.SIGN_POST.getId()) {
							String extra = "";
							String[] lines = volume.getSignLines().get(new StringBuilder("sign-").
									append(i).append("-").append(j).append("-").append(k).toString());
							if(lines != null) {
								for(String line : lines) {
									extra += new StringBuilder(line).append(";;").toString();
								}
								char[] chars = extra.toCharArray();
								for(int h = 0; h < chars.length; h++) {
									buf.putChar(chars[h]);
								}
							}
						} else if(typeid == Material.CHEST.getId()) {
							String extra = "";
							List<ItemStack> contents = volume.getInvBlockContents().get(new StringBuilder("chest-").
									append(i).append("-").append(j).append("-").append(k).toString());
							if(contents != null) {
								char[] chars = VolumeMapper.buildInventoryStringFromItemList(contents).toCharArray();
								for(int h = 0; h < chars.length; h++) {
									
								}
							}
						}
					}
				}
			}
			
			
			buf.flip();
		    	
		    
		}
		
		return null;
		
	}
}
