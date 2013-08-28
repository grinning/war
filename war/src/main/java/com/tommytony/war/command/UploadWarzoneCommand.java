package com.tommytony.war.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tommytony.war.War;
import com.tommytony.war.Warzone;

/**
 * @author grinning
 *
 */

public class UploadWarzoneCommand extends AbstractZoneMakerCommand {

	public UploadWarzoneCommand(WarCommandHandler handler,
			CommandSender sender, String[] args) throws NotZoneMakerException {
		super(handler, sender, args);
	}

	@Override
	public boolean handle() {
		// they typed "/warget"
		if(args.length == 0) {
			return false;
		}
		
		if(args.length >= 1) {
			if(args[0] == "upload") {
				if(args.length == 2) {
					Warzone zone = Warzone.getZoneByName(args[1]);
					if(zone == null) {
						War.war.badMsg(getSender(), "Must type a valid Warzone to upload it");
						return true;
					}
					try {
						Warzone.uploadWarzoneAsTask(zone);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				} else if(args.length == 1) {
					if(((Player) getSender()) != null) {
						Warzone zone = Warzone.getZoneByLocation((Player) getSender());
						if(zone == null) {
							War.war.badMsg(getSender(), "Must be in Warzone to upload it");
							return true;
						}
						try {
							Warzone.uploadWarzoneAsTask(zone);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return true;
					}
					return false;
				} else {
					return false;
				}
			} else if(args[0] == "download") {
				
			}
		}
		return false;
	}

}
