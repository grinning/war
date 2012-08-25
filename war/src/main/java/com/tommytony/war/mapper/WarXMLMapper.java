package com.tommytony.war.mapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;
import javolution.xml.XMLBinding;
import javolution.xml.XMLObjectWriter;
import javolution.xml.stream.XMLStreamException;

import com.tommytony.war.War;
import com.tommytony.war.config.WarConfig;
import com.tommytony.war.config.WarConfigBag;

public class WarXMLMapper {

	public static void save() {
		XMLBinding binding = new XMLBinding();
		binding.setAlias(FastMap.class, "Map");
		binding.setAlias(Boolean.class, "Boolean");
		binding.setAlias(WarConfig.class, "WarConfig");
		binding.setAlias(Integer.class, "Integer");
		OutputStream output = null;
		try {
			output = new FileOutputStream(War.war.getDataFolder().getPath() + "config.xml");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		XMLObjectWriter writer = null;
		try {
			writer = new XMLObjectWriter().setOutput(output).setBinding(binding);
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		(new File(War.war.getDataFolder().getPath())).mkdir();
		(new File(War.war.getDataFolder().getPath() + "/dat")).mkdir();
		List list = new ArrayList();
		Map<WarConfig, Object> war_config = new FastMap<WarConfig, Object>();
		WarConfigBag war_config_bag = War.war.getWarConfig();
		war_config.put(WarConfig.BUILDINZONESONLY, war_config_bag.getBoolean(WarConfig.BUILDINZONESONLY));
		war_config.put(WarConfig.DISABLEBUILDMESSAGE, war_config_bag.getBoolean(WarConfig.DISABLEBUILDMESSAGE));
		war_config.put(WarConfig.DISABLEPVPMESSAGE, war_config_bag.getBoolean(WarConfig.DISABLEPVPMESSAGE));
		war_config.put(WarConfig.MAXZONES, war_config_bag.getInt(WarConfig.MAXZONES));
		war_config.put(WarConfig.PVPINZONESONLY, war_config_bag.getBoolean(WarConfig.PVPINZONESONLY));
		war_config.put(WarConfig.TNTINZONESONLY, war_config_bag.getBoolean(WarConfig.TNTINZONESONLY));
		list.add(war_config);
		
		
	}
}
