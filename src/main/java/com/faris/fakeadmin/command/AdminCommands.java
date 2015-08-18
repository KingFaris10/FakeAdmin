package com.faris.fakeadmin.command;

import com.faris.fakeadmin.command.commands.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminCommands {
	private static Map<String, Class<? extends AdminCommand>> registeredCommands = null;
	private static Map<String, List<String>> registeredAliases = null;

	public static void initialiseCommands() {
		registeredCommands = new HashMap<>();
		registeredAliases = new HashMap<>();

		// Essentials
		registerCommand("ban", CommandBan.class, "eban");
		registerCommand("tempban", CommandTempban.class, "etempban");
		registerCommand("unban", CommandUnban.class, "pardon", "eunban", "epardon");
		registerCommand("kick", CommandKick.class, "ekick");
		registerCommand("mute", CommandMute.class, "emute");
		registerCommand("nick", CommandNick.class, "nickname", "enick", "enickname");
	}

	public static Class<? extends AdminCommand> getCommandClass(String command) {
		if (command != null) {
			command = command.toLowerCase();
			if (registeredCommands.containsKey(command)) return registeredCommands.get(command);
			for (Map.Entry<String, List<String>> aliasEntry : registeredAliases.entrySet()) {
				if (aliasEntry.getValue().contains(command)) return registeredCommands.get(aliasEntry.getKey());
			}
		}
		return null;
	}

	public static boolean registerCommand(String command, Class<? extends AdminCommand> commandClass, String... aliases) {
		if (command != null && !command.isEmpty() && commandClass != null) {
			registeredCommands.put(command.toLowerCase(), commandClass);
			registeredAliases.remove(command.toLowerCase());
			if (aliases != null && aliases.length > 0) {
				List<String> commandAliases = new ArrayList<>();
				for (String alias : aliases) {
					if (alias != null && !alias.isEmpty())
						commandAliases.add(alias.toLowerCase());
				}
				if (!commandAliases.isEmpty()) registeredAliases.put(command.toLowerCase(), commandAliases);
			}
			return true;
		}
		return false;
	}

	public static void unregisterCommand(String command) {
		if (command != null) {
			registeredCommands.remove(command.toLowerCase());
			registeredAliases.remove(command.toLowerCase());
		}
	}

	public static void unregisterCommands() {
		registeredCommands.clear();
		registeredAliases.clear();
	}
}
