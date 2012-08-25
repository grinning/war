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
import com.tommytony.war.Warzone;
import com.tommytony.war.config.WarConfig;
import com.tommytony.war.config.WarConfigBag;
import com.tommytony.war.config.WarzoneConfig;
import com.tommytony.war.config.WarzoneConfigBag;

public class WarXMLMapper {

	public static void save() {
		XMLBinding binding = new XMLBinding();
		binding.setAlias(FastMap.class, "Map");
		binding.setAlias(Boolean.class, "Boolean");
		binding.setAlias(WarConfig.class, "WarConfig");
		binding.setAlias(Integer.class, "Integer");
		binding.setAlias(WarzoneConfig.class, "WarzoneConfig");
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
			e.printStackTrace();
		}
		(new File(War.war.getDataFolder().getPath())).mkdir();
		(new File(War.war.getDataFolder().getPath() + "/dat")).mkdir();
		List list = new ArrayList();
		Map<WarConfig, Object> war_config = new FastMap<WarConfig, Object>(6);
		WarConfigBag war_config_bag = War.war.getWarConfig();
		war_config.put(WarConfig.BUILDINZONESONLY, false);
		war_config.put(WarConfig.DISABLEBUILDMESSAGE, false);
		war_config.put(WarConfig.DISABLEPVPMESSAGE, false);
		war_config.put(WarConfig.MAXZONES, 12);
		war_config.put(WarConfig.PVPINZONESONLY, false);
		war_config.put(WarConfig.TNTINZONESONLY, false);
		list.add(war_config);
		Map<WarzoneConfig, Object> warzone_default_config = new FastMap<WarzoneConfig, Object>();
		WarzoneConfigBag warzone_bag = War.war.getWarzoneDefaultConfig();
		warzone_default_config.put(WarzoneConfig.AUTOASSIGN, false);
		warzone_default_config.put(WarzoneConfig.BLOCKHEADS, true);
		warzone_default_config.put(WarzoneConfig.DEATHMESSAGES, false);
		warzone_default_config.put(WarzoneConfig.DISABLED, false);
		warzone_default_config.put(WarzoneConfig.DOMENABLED, false);
		warzone_default_config.put(WarzoneConfig.DOMTIME, 15);
		warzone_default_config.put(WarzoneConfig.EASYCONFIG, false);
		warzone_default_config.put(WarzoneConfig.FRIENDLYFIRE, false);
		warzone_default_config.put(WarzoneConfig.GLASSWALLS, true);
		warzone_default_config.put(WarzoneConfig.HEALERS, false);
		warzone_default_config.put(WarzoneConfig.INFECTION, false);
		warzone_default_config.put(WarzoneConfig.INSTABREAK, false);
		warzone_default_config.put(WarzoneConfig.MAXTNT, 5);
		warzone_default_config.put(WarzoneConfig.MINPLAYERS, 1);
		warzone_default_config.put(WarzoneConfig.MINTEAMS, 1);
		warzone_default_config.put(WarzoneConfig.MONUMENTHEAL, 5);
		warzone_default_config.put(WarzoneConfig.NOCREATURES, false);
		warzone_default_config.put(WarzoneConfig.NODROPS, false);
		warzone_default_config.put(WarzoneConfig.PREPTIME, 0);
		try {
			writer.write(list, "config");
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} finally {
		try {
			writer.close();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		}
		
	}
}
