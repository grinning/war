package com.tommytony.war.command;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import com.tommytony.war.Team;
import com.tommytony.war.War;
import com.tommytony.war.Warzone;
import com.tommytony.war.config.WarConfig;
import com.tommytony.war.mapper.WarYmlMapper;
import com.tommytony.war.mapper.WarzoneYmlMapper;
import com.tommytony.war.structure.Monument;
import com.tommytony.war.structure.ZoneLobby;

/**
 * Deletes a warzone.
 *
 * @author Tim Düsterhus
 */
public class DeleteZoneCommand extends AbstractZoneMakerCommand {
	public DeleteZoneCommand(WarCommandHandler handler, CommandSender sender, String[] args) throws NotZoneMakerException {
		super(handler, sender, args);
	}

	@Override
	public boolean handle() {
		Warzone zone;
		boolean enoughArgsToHaveString = (this.args.length >= 1) ? true : false;
		boolean ifFirstZoneWasValidZone = false;
		if (this.args.length >= 1) {
			zone = Warzone.getZoneByName(this.args[0]);
			if(zone != null)
				ifFirstZoneWasValidZone = true;
		} else if (this.args.length == 0) {
			if (!(this.getSender() instanceof Player)) {
				return false;
			}
			zone = Warzone.getZoneByLocation((Player) this.getSender());
			if (zone == null) {
				ZoneLobby lobby = ZoneLobby.getLobbyByLocation((Player) this.getSender());
				if (lobby == null) {
					return false;
				}
				zone = lobby.getZone();
			}
		} else {
			return false;
		}

		if (zone == null) {
			return false;
		} else if (!this.isSenderAuthorOfZone(zone)) {
			return true;
		}

		for (Team t : zone.getTeams()) {
			if (t.getTeamFlag() != null) {
				t.getFlagVolume().resetBlocks();
			}
			t.getSpawnVolume().resetBlocks();

			// reset inventory
			for (Player p : t.getPlayers()) {
				zone.restorePlayerState(p);
			}
		}
		for (Monument m : zone.getMonuments()) {
			m.getVolume().resetBlocks();
		}
		if (zone.getLobby() != null) {
			zone.getLobby().getVolume().resetBlocks();
		}
		zone.getVolume().resetBlocks();
		War.war.getWarzones().remove(zone);
		WarYmlMapper.save();
		WarzoneYmlMapper.delete(zone.getName());
		if (War.war.getWarHub() != null) { // warhub has to change
			War.war.getWarHub().getVolume().resetBlocks();
			War.war.getWarHub().initialize();
		}
		this.msg("Warzone " + zone.getName() + " removed.");

		if(War.war.getWarConfig().getBoolean(WarConfig.LOGIMPORTANTCOMMANDS)) {
			DeleteZoneCommand.buildAndLogString(zone.getName(),
					this.getSender().getName(), this.args, enoughArgsToHaveString,
					ifFirstZoneWasValidZone);
		}
		return true;
	}
	
	public static void buildAndLogString(String zone, String player, String[] args, 
			boolean enoughArgsForString, boolean firstZoneValid) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		StringBuilder build = new StringBuilder();
		if(enoughArgsForString) {
		    for(int i = (firstZoneValid) ? 1 : 0; i < args.length; i++) {
		    	build.append(args[i]);
		    }
		}
		StringBuilder s = new StringBuilder();
		s.append('[').append(dateFormat.format(new Date())).append("] Event=DeletedZone Player=")
		.append(player).append(" Zone=").append(zone).append(" DeleteString=").append(build);
		War.war.logOut.println(s.toString());
	}
	
}
