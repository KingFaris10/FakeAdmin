package com.faris.fakeadmin.api;

import com.faris.fakeadmin.FakeAdmin;
import com.faris.fakeadmin.command.AdminCommand;
import com.faris.fakeadmin.command.AdminCommands;
import com.faris.fakeadmin.helper.Utilities;
import com.faris.fakeadmin.hook.EssentialsAPI;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakeAdminAPI {

	/**
	 * Add a command handler for fake admins.
	 *
	 * @param command - The command
	 * @param commandClass - The command handler class
	 * @param aliases - The command's aliases (can be null or empty)
	 * @return Whether or not the command has been registered successfully.
	 */
	public static boolean addCommand(String command, Class<? extends AdminCommand> commandClass, String... aliases) {
		return AdminCommands.registerCommand(command, commandClass, aliases);
	}

	/**
	 * Get all the fake admins' UUIDs.
	 *
	 * @return The fake admins' UUIDs.
	 */
	public static List<UUID> getFakeAdmins() {
		return FakeAdmin.getInstance().getManager().getAdminManager().getFakeAdmins();
	}

	/**
	 * Get all the online fake admins.
	 *
	 * @return The online fake admins.
	 */
	public static List<Player> getOnlineFakeAdmins() {
		List<Player> onlineFakeAdmins = new ArrayList<>();
		for (UUID fakeAdminUUID : getFakeAdmins()) {
			Player fakeAdmin = Bukkit.getServer().getPlayer(fakeAdminUUID);
			if (fakeAdmin != null && fakeAdmin.isOnline()) onlineFakeAdmins.add(fakeAdmin);
		}
		return onlineFakeAdmins;
	}

	/**
	 * Check if a player is fake banned.
	 *
	 * @param checkUUID - The player's UUID
	 * @return Whether or not a player is fake banned.
	 */
	public static boolean isBanned(UUID checkUUID) {
		return FakeAdmin.getInstance().getManager().getBanManager().isBanned(checkUUID);
	}

	/**
	 * Check if a player is fake muted.
	 *
	 * @param checkUUID - The player's UUID
	 * @return Whether or not a player is fake muted.
	 */
	public static boolean isMuted(UUID checkUUID) {
		return FakeAdmin.getInstance().getManager().getMuteManager().isMuted(checkUUID);
	}

	/**
	 * Unregister a fake command.
	 *
	 * @param command - The command
	 */
	public static void removeCommand(String command) {
		AdminCommands.unregisterCommand(command);
	}

	/*
	 * Handlers
	 */

	private static Handler handlerMute = new Handler() {
		@Override
		public String getPluginName() {
			return FakeAdmin.getInstance().getName();
		}

		@Override
		public Object getValue(Object... parameters) {
			Player player = parameters[0] != null ? (Player) parameters[0] : null;
			return EssentialsAPI.hasEssentials() && EssentialsAPI.isMuted(player);
		}
	};

	public static Handler getMuteHandler() {
		return handlerMute;
	}

	/**
	 * Set the handler for checking whether a player is really muted or not.
	 *
	 * @param muteHandler - The mute handler
	 */
	public static void setMuteHandler(Handler muteHandler) {
		handlerMute = muteHandler;
	}

	private static Handler handlerVanish = new Handler() {
		@Override
		public String getPluginName() {
			return FakeAdmin.getInstance().getName();
		}

		@Override
		public Object getValue(Object... parameters) {
			Player viewer = parameters[0] != null ? (Player) parameters[0] : null;
			Player player = parameters[1] != null ? (Player) parameters[1] : null;
			return EssentialsAPI.hasEssentials() && viewer != null && !viewer.hasPermission("essentials.vanish.see") && EssentialsAPI.isVanished(player);
		}
	};

	public static Handler getVanishHandler() {
		return handlerVanish;
	}

	/**
	 * Set the handler for checking whether a player is really vanished or not.
	 *
	 * @param vanishHandler - The vanish handler
	 */
	public static void setVanishHandler(Handler vanishHandler) {
		handlerVanish = vanishHandler;
	}

	private static Handler handlerChat = new Handler() {
		@Override
		public String getPluginName() {
			return FakeAdmin.getInstance().getName();
		}

		@Override
		public Object getValue(Object... parameters) {
			Player player = parameters[0] != null ? (Player) parameters[0] : null;
			String message = parameters[1] != null ? parameters[1].toString() : null;
			if (player != null) {
				if (EssentialsAPI.hasEssentials()) {
					if (player.hasPermission("essentials.chat.color")) message = Utilities.replaceChatColours(message);
					if (player.hasPermission("essentials.chat.format")) message = Utilities.replaceChatFormats(message);
					if (player.hasPermission("essentials.chat.magic")) message = Utilities.replaceChatMagic(message);
				}
			}
			return message;
		}
	};

	public static Handler getChatHandler() {
		return handlerChat;
	}

	/**
	 * Set the handler for checking and changing the chat message of a fake administrator.
	 *
	 * @param chatHandler - The chat handler
	 */
	public static void setChatHandler(Handler chatHandler) {
		handlerChat = chatHandler;
	}

}
