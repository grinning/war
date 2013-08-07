package com.tommytony.war.mapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.tommytony.war.War;
import com.tommytony.war.structure.StructureType;
import com.tommytony.war.volume.Volume;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

/**
 * @author grinning
 * @since 1.8
 */

public class StructureVolumeMapper {

	public static int load(Volume volume, String name, StructureType type) {
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
		int[][][] types = new int[xSize][ySize][zSize];
		byte[][][] datas = new byte[xSize][ySize][zSize];
		JSONArray array = (JSONArray) obj.get("blockdata");
		for(int x = 0; x < xSize; x++) {
			for(int y = 0; y < ySize; y++) {
				for(int z = 0; z < zSize; z++) {
					types[x][y][z] = (Integer) array.get(2 * x * y * z);
					datas[x][y][z] = (Byte) array.get(2 * x * y * z + 1);
				}
			}
		}
		volume.setBlockTypes(types);
		volume.setBlockDatas(datas);
		return count;
	}
	
	public static int save(Volume volume, String name, StructureType type) {
		int count = 0;
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("xSize", new Integer(volume.getSizeX()));
		obj.put("ySize", new Integer(volume.getSizeY()));
		obj.put("zSize", new Integer(volume.getSizeZ()));
		JSONArray blocks = new JSONArray();
		for(int x = 0; x < volume.getSizeX(); x++) {
			for(int y = 0; y < volume.getSizeY(); y++) {
				for(int z = 0; z < volume.getSizeZ(); z++) {
					blocks.add(new Integer(volume.getBlockTypes()[x][y][z]));
					blocks.add(new Integer(volume.getBlockDatas()[x][y][z]));
					count += 2;
				}
			}
		}
		obj.put("blockdata", blocks);
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
