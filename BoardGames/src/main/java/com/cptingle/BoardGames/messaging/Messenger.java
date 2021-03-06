package com.cptingle.BoardGames.messaging;

/**
 * Messenger used to communicate with players 
 */
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Messenger {
	private final String prefix;

	public Messenger(String prefix) {
		if (prefix.contains("&")) {
			prefix = ChatColor.translateAlternateColorCodes('&', prefix);
		}
		this.prefix = prefix + " ";
	}

	public boolean tell(CommandSender p, String msg) {
		// If the input sender is null or the string is empty, return.
		if (p == null || msg == null || msg.equals("")) {
			return false;
		}

		// Otherwise, send the message with the [MobArena] tag.
		p.sendMessage(prefix + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', msg));
		return true;
	}

	public boolean tell(CommandSender p, Msg msg, String s) {
		return tell(p, msg.format(s));
	}

	public boolean tell(CommandSender p, Msg msg) {
		return tell(p, msg.toString());
	}
}
