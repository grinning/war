package com.tommytony.war.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.getspout.commons.ChatColor;

import com.tommytony.war.War;
import com.tommytony.war.utility.PlayerStat;


/**
 * Get stats
 * 
 * @author grinning
 */

public class GetStatsCommand extends AbstractWarCommand {

	
	
	public GetStatsCommand(WarCommandHandler handler, CommandSender sender, String[] args) {
		super(handler, sender, args);
	}

	@Override
	public boolean handle() {
		
		if(super.args.length != 1) {
			War.war.badMsg(super.getSender(), "Usage: /wstats <player>  or /warstats <player>");
			return true;
		} else if(super.args.length == 1) {
			String name = args[0];
			PlayerStat stats = War.war.getPlayerStats(name);
			
			if(stats != null) {
			    War.war.msg(super.getSender(), "Stats for player " + ChatColor.BLUE + name + "\n" + 
			    ChatColor.GOLD + "Kills: " + ChatColor.BLUE + stats.getKills() + "\n" +
			    ChatColor.DARK_RED + "Deaths: " + ChatColor.BLUE + stats.getDeaths());
			    //what is the point of reporting stats if you don't make them pretty? 
			return true;
			} else {
				//if the player is null
				int kills = 0;
				int deaths = 0;
				char sep = File.separatorChar;
				File file = new File("plugins" + sep + "war" + sep + "stats" + sep + name + ".stat");
				
				if(!file.exists()) {
					War.war.msg(super.getSender(), "Stats for player " + ChatColor.BLUE + name + "\n" +
				    ChatColor.GOLD + "Kills: " + ChatColor.BLUE + "0" + "\n" +
					ChatColor.DARK_RED + "Deaths: " + ChatColor.BLUE + "0");
					Bukkit.getServer().getLogger().log(Level.WARNING, "War> User " + super.getSender().getName() + " requested stats from a user file that didn't exist");
				} else { //if they don't exist we have to read them from files!
					try {
						Scanner read = new Scanner(file);
						while(read.hasNext()) {
							kills = read.nextInt();
							deaths = read.nextInt();
						}
						read.close();
						
					} catch(FileNotFoundException e) {
						Bukkit.getServer().getLogger().log(Level.WARNING, "War> Your computer is stupid");
						e.printStackTrace();
					}
					War.war.msg(super.getSender(), "Stats for player " + ChatColor.BLUE + name + "\n" +
					ChatColor.GOLD + "Kills: " + ChatColor.BLUE + kills + "\n" +
					ChatColor.DARK_RED + "Deaths: " + ChatColor.BLUE + deaths);
				}
				return true;
			}
		}
		return false;
	}

}
