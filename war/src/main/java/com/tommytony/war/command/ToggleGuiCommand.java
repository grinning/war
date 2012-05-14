package com.tommytony.war.command;

import org.bukkit.command.CommandSender;

import com.tommytony.war.War;

public class ToggleGuiCommand extends AbstractWarCommand {

	public ToggleGuiCommand(WarCommandHandler handler, CommandSender sender,
			String[] args) {
		super(handler, sender, args);
	}

	@Override
	public boolean handle() {
		if(args.length != 0) {
			War.war.badMsg(super.getSender(), "This command takes no arguments!");
			return true;
		} else if(args.length == 0) {
			if(!War.war.playerGuis.contains(super.getSender().getName())) {
				//the list no contains us so we toggle ourselves in
				War.war.playerGuis.add(super.getSender().getName());
				War.war.msg(super.getSender(), "Toggled War-GUI to true.");
			} else {
				//we must be removing ourselves
				War.war.playerGuis.remove(super.getSender().getName());
				War.war.msg(super.getSender(), "Toggled War-GUI to false.");
			}
			return true;
		}
		return false;
	}

}
