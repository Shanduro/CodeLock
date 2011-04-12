package org.shanduro.codelock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.PersistenceException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.shanduro.codelock.commands.CodeCommand;
import org.shanduro.codelock.commands.SetCommand;
import org.shanduro.codelock.listeners.CodeLockBlockListener;
import org.shanduro.codelock.listeners.CodeLockPlayerListener;

/**
 * CodeLock plugin for Bukkit
 * 
 * @author Shanduro
 */
public class CodeLock extends JavaPlugin {
	private final CodeLockBlockListener blockListener = new CodeLockBlockListener(
			this);
	private final CodeLockPlayerListener playerListener = new CodeLockPlayerListener(
			this);
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	public final HashMap<Player, Location> ActiveButtons = new HashMap<Player, Location>();
	public final HashMap<Player, Location> ActiveCodes = new HashMap<Player, Location>();
	public final HashMap<Player, Location> ButtonsToActivate = new HashMap<Player, Location>();

	// NOTE: There should be no need to define a constructor any more for more
	// info on moving from
	// the old constructor see:
	// http://forums.bukkit.org/threads/too-long-constructor.5032/

	public void onDisable() {
		// TODO: Place any custom disable code here

		// NOTE: All registered events are automatically unregistered when a
		// plugin is disabled

		// EXAMPLE: Custom code, here we just output some info so we can check
		// all is well
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version "
				+ pdfFile.getVersion() + " disabled");
	}

	public void onEnable() {
		// TODO: Place any custom enable code here including the registration of
		// any events

		// Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener,
				Priority.Normal, this);

		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener,
				Priority.Normal, this);

		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener,
				Priority.Normal, this);

		// Register our commands
		getCommand("code").setExecutor(new CodeCommand(this));
		getCommand("setcode").setExecutor(new SetCommand(this));

		setupDatabase();

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version "
				+ pdfFile.getVersion() + " is enabled!");
	}

	private void setupDatabase() {
		try {
			System.out.println(getDatabase().find(Code.class).findRowCount()
					+ " activationcodes loaded.");
			System.out.println(getDatabase().find(Door.class).findRowCount()
					+ " playerprotected doors loaded.");
		} catch (PersistenceException ex) {
			System.out.println("Installing database for "
					+ getDescription().getName() + " due to first time usage");
			installDDL();
		}
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(Door.class);
		list.add(Code.class);
		return list;
	}

	public boolean isDebugging(final Player player) {
		if (debugees.containsKey(player)) {
			return debugees.get(player);
		} else {
			return false;
		}
	}

	public void setDebugging(final Player player, final boolean value) {
		debugees.put(player, value);
	}
}