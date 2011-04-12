package org.shanduro.codelock.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.block.Action;
import org.shanduro.codelock.Code;
import org.shanduro.codelock.CodeLock;

/**
 * CodeLock player listener
 * 
 * @author CodeLock
 */
public class CodeLockPlayerListener extends PlayerListener {
	private final CodeLock plugin;

	public CodeLockPlayerListener(CodeLock instance) {
		plugin = instance;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		super.onPlayerInteract(event);

		if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK)
				&& event.getClickedBlock().getType()
						.equals(Material.STONE_BUTTON)) {

			Location loc = event.getClickedBlock().getLocation();
			Code code = plugin.getDatabase().find(Code.class).where()
					.eq("worldName", loc.getWorld().getName())
					.eq("x", loc.getX()).eq("y", loc.getY())
					.eq("z", loc.getZ()).findUnique();

			// when a code for this button was set
			if (code != null) {
				if (code.getCode() != null && !code.getCode().isEmpty()) {
					// event.getPlayer().sendMessage("block got code");
					Location activatableButton = plugin.ButtonsToActivate
							.get(event.getPlayer());
					// button not activatable
					if (activatableButton == null) {
						// event.getPlayer().sendMessage("no button activatable");
						// button needs code
						plugin.ActiveCodes.put(event.getPlayer(), loc);
						event.getPlayer().sendMessage("Access denied.");
						event.setCancelled(true);
					} else {
						// event.getPlayer().sendMessage("one button is activatable");
						// button not activatable
						if (!activatableButton.equals(loc)) {
							// event.getPlayer().sendMessage("clicked button not the activatable one");
							// button needs code
							plugin.ActiveCodes.put(event.getPlayer(), loc);
							event.getPlayer().sendMessage("Access denied.");
							event.setCancelled(true);
						} else {
							// button activated
							event.setCancelled(false);
						}
					}
				}
			}
		}
	}
}