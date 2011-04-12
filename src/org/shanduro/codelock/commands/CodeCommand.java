package org.shanduro.codelock.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.shanduro.codelock.Code;
import org.shanduro.codelock.CodeLock;

/**
 * Handler for the /code command.
 * 
 * @author Shanduro
 */
public class CodeCommand implements CommandExecutor {
	private final CodeLock plugin;

	public CodeCommand(CodeLock plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] split) {

		if (plugin.ActiveCodes.get((Player) sender) != null) {
			if (split.length == 1) {
				Location loc = plugin.ActiveCodes.get((Player) sender);
				Code code = plugin.getDatabase().find(Code.class).where()
						.eq("worldName", loc.getWorld().getName())
						.eq("x", loc.getX()).eq("y", loc.getY())
						.eq("z", loc.getZ()).findUnique();
				if (!code.getCode().equals("")) {
					if (code.getCode().equals(split[0])) {
						sender.sendMessage("Access granted.");
						plugin.ButtonsToActivate.put((Player) sender, loc);
					} else {
						sender.sendMessage("Access denied.");
					}
				}
				return true;
			} else {
				sender.sendMessage("Wrong /code syntax.");
				sender.sendMessage("Right syntax is: /code <code>");
			}
		} else {
			sender.sendMessage("No button set to activate.");
			sender.sendMessage("Press a button that needs a code to set it as active.");
		}
		return true;
	}
}