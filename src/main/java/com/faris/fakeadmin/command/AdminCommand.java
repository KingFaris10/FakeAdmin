package com.faris.fakeadmin.command;

import com.faris.fakeadmin.FakeAdmin;
import org.bukkit.entity.*;

import java.util.List;

public abstract class AdminCommand {

	public abstract CommandReturnType onCommand(Player player, String command, String[] args) throws Exception;

	public abstract List<String> getUsage();

	protected FakeAdmin getPlugin() {
		return FakeAdmin.getInstance();
	}

	protected String getFinalArgs(String[] args, int startPosition) {
		if (args != null && startPosition >= 0 && startPosition < args.length) {
			StringBuilder sbFinalArgs = new StringBuilder();
			for (int i = startPosition; i < args.length; i++) {
				if (i == args.length - 1) sbFinalArgs.append(args[i]);
				else sbFinalArgs.append(args[i]).append(" ");
			}
			return sbFinalArgs.toString();
		}
		return "";
	}

	public enum CommandReturnType {
		VALID, INVALID_USAGE, DISABLED;
	}

}
