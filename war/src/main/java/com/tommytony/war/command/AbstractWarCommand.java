package com.tommytony.war.command;

import org.bukkit.command.CommandSender;

import com.tommytony.war.War;


/**
 * Represents a war command
 *
 * @author Tim Düsterhus
 */
public abstract class AbstractWarCommand {

	/**
	 * The sender of this command
	 *
	 * @var	sender
	 */
	private CommandSender sender;

	/**
	 * The arguments of this command
	 *
	 * @var	args
	 */
	protected String[] args;

	/**
	 * Instance of WarCommandHandler
	 *
	 * @var	handler
	 */
	protected WarCommandHandler handler;

	public AbstractWarCommand(WarCommandHandler handler, CommandSender sender, String[] args) {
		this.handler = handler;
		this.setSender(sender);
		this.args = args;
	}

	/**
	 * Handles the command
	 *
	 * @return	true if command was used the right way
	 */
	abstract public boolean handle();

	/**
	 * Sends a success message
	 *
	 * @param 	message	message to send
	 */
	public final void msg(String message) {
		War.war.msg(this.getSender(), message);
	}

	/**
	 * Sends a failure message
	 *
	 * @param 	message	message to send
	 */
	public final void badMsg(String message) {
		War.war.badMsg(this.getSender(), message);
	}

	/**
	 * Changes the command-sender
	 *
	 * @param 	sender	new sender
	 */
	public final void setSender(CommandSender sender) {
		this.sender = sender;
	}

	/**
	 * Gets the command-sender
	 *
	 * @return	Command-Sender
	 */
	public final CommandSender getSender() {
		return this.sender;
	}
}
