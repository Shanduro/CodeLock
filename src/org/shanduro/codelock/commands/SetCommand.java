package org.shanduro.codelock.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.shanduro.codelock.Code;
import org.shanduro.codelock.CodeLock;

/**
 * Handler for the /setcode sample command.
 * 
 * @author Shanduro
 */
public class SetCommand implements CommandExecutor {
	private final CodeLock plugin;

	public SetCommand(CodeLock plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] split) {

		if (plugin.ActiveButtons.get((Player) sender) != null) {
			if (split.length == 1) {
				Location loc = plugin.ActiveButtons.get((Player) sender);
				Code code = plugin.getDatabase().find(Code.class).where()
						.eq("worldName", loc.getWorld().getName())
						.eq("x", loc.getX()).eq("y", loc.getY())
						.eq("z", loc.getZ()).findUnique();
				if (code != null) {
					if (code.getPlayerName()
							.equals(((Player) sender).getName())) {
						code.setCode(split[0]);

						plugin.getDatabase().update(code);

						sender.sendMessage("Code was saved.");
						plugin.ActiveButtons.remove((Player) sender);
					} else
						sender.sendMessage("This is not your button.");
					plugin.ActiveButtons.remove((Player) sender);
				}
			} else {
				sender.sendMessage("Wrong /setcode syntax.");
				sender.sendMessage("Right syntax is: /setcode <code>");
			}
		} else {
			sender.sendMessage("No active button set.");
			sender.sendMessage("Place a new button to set it as active.");
		}
		return true;
	}
}