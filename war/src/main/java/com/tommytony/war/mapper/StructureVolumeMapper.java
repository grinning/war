package com.tommytony.war.mapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.tommytony.war.War;
import com.tommytony.war.structure.StructureType;
import com.tommytony.war.volume.Volume;

import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

/**
 * @author grinning
 * @since 1.8
 */

public class StructureVolumeMapper {

	public static StructureReturnType load(Volume volume, String name, StructureType type) {
		int count = 0;
		BufferedReader in = null;
		Object o = null;
		try {
			(new File(War.war.getDataFolder().getPath() + "/dat/structure/" + type.getString())).mkdir();
			in = new BufferedReader(new FileReader(new File(War.war.getDataFolder().getPath() + "/dat/structure/" + type.getString() + "/" + name + ".json")));
			o = JSONValue.parse(in);
			in.close();
		} catch(IOException e) {
			e.printStackTrace();
			System.out.println("read " + count + " blocks");
		}
		JSONObject obj = (JSONObject) o;
		volume.setName((String) obj.get("name"));
		int xSize = (Integer) obj.get("xSize");
		int ySize = (Integer) obj.get("ySize");
		int zSize = (Integer) obj.get("zSize");
		Vector specOffset = null;
		int[][][] types = new int[xSize][ySize][zSize];
		byte[][][] datas = new byte[xSize][ySize][zSize];
		JSONArray blocktype = (JSONArray) obj.get("blocktype");
		JSONArray blockdata = (JSONArray) obj.get("blockdata");
		int loopType; byte loopData;
		for(int x = 0; x < xSize; x++) {
			for(int y = 0; y < ySize; y++) {
				for(int z = 0; z < zSize; z++) {
					loopType = (Integer) blocktype.get((x * ySize * zSize) + (y * zSize) + (z));
					loopData = (Byte) blockdata.get((x * ySize * zSize) + (y * zSize) + (z));
					//special location checking
					if(loopType == 0 && loopData == Byte.MAX_VALUE) {
						//SPECIAL LOCATION :D
						specOffset = new Vector(x, y, z);
					}
				}
			}
		}
		count = xSize * ySize * zSize;
		volume.setBlockTypes(types);
		volume.setBlockDatas(datas);
		
		return new StructureVolumeMapper.StructureReturnType(count, specOffset);
	}
	
	static protected class StructureReturnType {
		public final int count;
		public final Vector vec;
		
		StructureReturnType(int count, Vector vec) {
			this.count = count;
			this.vec = vec;
		}
	}
	
	public static int save(Volume volume, String name, StructureType type, String specLoc) {
		int count = 0;
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("xSize", new Integer(volume.getSizeX()));
		obj.put("ySize", new Integer(volume.getSizeY()));
		obj.put("zSize", new Integer(volume.getSizeZ()));
		JSONArray types = new JSONArray();
		JSONArray datas = new JSONArray();
		for(int x = 0; x < volume.getSizeX(); x++) {
			for(int y = 0; y < volume.getSizeY(); y++) {
				for(int z = 0; z < volume.getSizeZ(); z++) {
					types.add(new Integer(volume.getBlockTypes()[x][y][z]));
					datas.add(new Integer(volume.getBlockDatas()[x][y][z]));
					count++;
				}
			}
		}
		obj.put("blocktype", types);
		obj.put("blockdata", datas);
		BufferedWriter out = null;
		try {
			(new File(War.war.getDataFolder().getPath() + "/dat/structure/" + type.getString())).mkdir();
			out = new BufferedWriter(new FileWriter(new File(War.war.getDataFolder().getPath() + "/dat/structure/" + type.getString() + "/" + name + ".json")));
			out.write(obj.toJSONString());
			out.flush();
			out.close();
		} catch(IOException e) {
			e.printStackTrace();
			System.out.println("wrote " + count + " blocks");
		}
		return count;
	}
}
