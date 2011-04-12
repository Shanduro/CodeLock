package org.shanduro.codelock.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.shanduro.codelock.Code;
import org.shanduro.codelock.CodeLock;
import org.shanduro.codelock.Door;

/**
 * CodeLock block listener
 * 
 * @author Shanduro
 */
public class CodeLockBlockListener extends BlockListener {
	private final CodeLock plugin;

	public CodeLockBlockListener(final CodeLock plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		super.onBlockPlace(event);

		if (event.getBlock().getType() == Material.STONE_BUTTON) {
			plugin.ActiveButtons.put(event.getPlayer(), event.getBlock()
					.getLocation());
			event.getPlayer().sendMessage("A button was placed.");
			event.getPlayer().sendMessage(
					"Use /setcode <code> to set an activation code.");
			Location loc = event.getBlock().getLocation();
			Code code = plugin.getDatabase().find(Code.class).where()
					.eq("worldName", loc.getWorld().getName())
					.eq("x", loc.getX()).eq("y", loc.getY())
					.eq("z", loc.getZ()).findUnique();

			if (code == null) {
				code = new Code();
			}

			code.setLocation(loc);
			code.setPlayerName(event.getPlayer().getName());

			plugin.getDatabase().save(code);
			// event.getPlayer().sendMessage("Block saved in database");
		} else if (event.getBlock().getType() == Material.IRON_DOOR_BLOCK) {
			// event.getPlayer().sendMessage("door placed");
			Location loc = event.getBlock().getLocation();
			Door door = plugin.getDatabase().find(Door.class).where()
					.eq("worldName", loc.getWorld().getName())
					.eq("x", loc.getX()).eq("y", loc.getY())
					.eq("z", loc.getZ()).findUnique();
			// event.getPlayer().sendMessage(loc.toString());
			// event.getPlayer().sendMessage("door loaded");
			if (door == null) {
				// event.getPlayer().sendMessage("creating door");
				door = new Door();
				door.setLocation(loc);
				door.setPlayerName(event.getPlayer().getName());
			}
			// event.getPlayer().sendMessage("saving door");
			plugin.getDatabase().save(door);
		}
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		super.onBlockBreak(event);

		// event.getPlayer().sendMessage(event.getBlock().getType().toString());

		if (event.getBlock().getType() == Material.STONE_BUTTON) {
			Location brokenButton = event.getBlock().getLocation();

			Code code = plugin.getDatabase().find(Code.class).where()
					.eq("worldName", brokenButton.getWorld().getName())
					.eq("x", brokenButton.getX()).eq("y", brokenButton.getY())
					.eq("z", brokenButton.getZ()).findUnique();

			// event.getPlayer().sendMessage("button was broken");
			if (code != null) {
				if (code.getCode() != null && !code.getCode().isEmpty()) {
					// event.getPlayer().sendMessage("button needs code");
					Location activatableButon = plugin.ButtonsToActivate
							.get(event.getPlayer());
					if (activatableButon != null) {
						if (activatableButon.getBlock().getLocation()
								.equals(brokenButton)) {
							plugin.getDatabase().delete(code);
							// event.getPlayer().sendMessage("Button removed from database.");
							plugin.ButtonsToActivate.remove(event.getPlayer());
						} else {
							// event.getPlayer().sendMessage("activatable button not broken one");
							event.getPlayer().sendMessage("Access denied.");
							event.setCancelled(true);
						}
					} else {
						// event.getPlayer().sendMessage("no activatable button");
						event.getPlayer().sendMessage("Access denied.");
						event.setCancelled(true);
					}
				} else {
					if (!code.getPlayerName().equals(
							event.getPlayer().getName())) {
						event.getPlayer().sendMessage(
								"This is not your button.");
						event.setCancelled(true);
					}
				}
			} else {
				// event.getPlayer().sendMessage("no code needed");
				event.setCancelled(false);
			}

			if (plugin.ActiveButtons.get(event.getPlayer()) != null
					&& plugin.ActiveButtons.get(event.getPlayer()) == event
							.getBlock().getLocation()) {
				plugin.ActiveButtons.remove(event.getPlayer());
				// event.getPlayer().sendMessage("Button removed as active button.");
			}
		} else if (event.getBlock().getType() == Material.IRON_DOOR_BLOCK) {
			// event.getPlayer().sendMessage("destroying door");
			Location loc = event.getBlock().getLocation();

			// event.getPlayer().sendMessage(loc.toString());

			Door door = plugin.getDatabase().find(Door.class).where()
					.eq("worldName", loc.getWorld().getName())
					.eq("x", loc.getX()).eq("y", loc.getY())
					.eq("z", loc.getZ()).findUnique();
			if (door != null) {
				// event.getPlayer().sendMessage(loc.toString());
				// event.getPlayer().sendMessage("door is protected");
				if (door.getPlayerName().equals(event.getPlayer().getName())) {
					// event.getPlayer().sendMessage("got permissions to destroy");
					plugin.getDatabase().delete(door);
					event.setCancelled(false);
				} else {
					event.getPlayer().sendMessage("This is not your door.");
					event.setCancelled(true);
				}
			} else {
				door = plugin.getDatabase().find(Door.class).where()
						.eq("worldName", loc.getWorld().getName())
						.eq("x", loc.getX()).eq("y", loc.getY() - 1)
						.eq("z", loc.getZ()).findUnique();
				if (door != null) {
					// event.getPlayer().sendMessage(loc.toString());
					// event.getPlayer().sendMessage("door is protected");
					if (door.getPlayerName()
							.equals(event.getPlayer().getName())) {
						// event.getPlayer().sendMessage("got permissions to destroy");
						plugin.getDatabase().delete(door);
						event.setCancelled(false);
					} else {
						// event.getPlayer().sendMessage("no permissions to destroy");
						event.setCancelled(true);
					}
				} else
					event.setCancelled(false);
			}
		} else if (CheckDoorAroundBlock(event.getBlock())
				|| CheckButtonOnBlock(event.getBlock())) {
			event.setCancelled(true);
		}
	}

	public boolean CheckDoorAroundBlock(Block block) {
		if (block.getFace(BlockFace.NORTH).getType() == Material.IRON_DOOR_BLOCK
				|| block.getFace(BlockFace.EAST).getType() == Material.IRON_DOOR_BLOCK
				|| block.getFace(BlockFace.SOUTH).getType() == Material.IRON_DOOR_BLOCK
				|| block.getFace(BlockFace.WEST).getType() == Material.IRON_DOOR_BLOCK
				|| block.getFace(BlockFace.UP).getType() == Material.IRON_DOOR_BLOCK
				|| block.getFace(BlockFace.DOWN).getType() == Material.IRON_DOOR_BLOCK)
			return true;
		else
			return false;
	}

	public boolean CheckButtonOnBlock(Block block) {
		if (block.getFace(BlockFace.NORTH).getType() == Material.STONE_BUTTON
				|| block.getFace(BlockFace.EAST).getType() == Material.STONE_BUTTON
				|| block.getFace(BlockFace.SOUTH).getType() == Material.STONE_BUTTON
				|| block.getFace(BlockFace.WEST).getType() == Material.STONE_BUTTON)
			return true;
		else
			return false;
	}
}