package com.faris.fakeadmin.helper;

import com.faris.fakeadmin.FakeAdmin;
import org.bukkit.command.*;
import org.bukkit.configuration.file.*;

import java.io.File;

public enum Lang {
	GENERAL_ERROR("General.Error", "&cAn internal error occurred whilst attempting to perform that command. Please contact a server administrator for help."),
	GENERAL_PLAYER_NOT_FOUND("General.Essentials player not found", "&6Error: &4Player not found"),
	GENERAL_NO_PERMISSION("General.No permission", "&4You do not have access to that command."),
	GENERAL_PLAYER("General.Player", "&cYou must be a player to use that command!"),
	GENERAL_PLAYER_UNKNOWN("General.Player unknown", "&cThat player could not be found."),
	CHAT_ADMIN("Chat.Admin chat", "&c<player>&r: <message>"),
	CHAT_PLAYER("Chat.Player chat", "<player>&r: <message>"),
	COMMAND_ADMIN_ADD("Command.Admin.Add", "&6You have promoted &c<player> &6to a fake administrator."),
	COMMAND_ADMIN_ADDED("Command.Admin.Added", "&aYou have been promoted to an administrator!"),
	COMMAND_ADMIN_EXEMPT("Command.Admin.Exempt", "&cYou cannot promote that player to a fake administrator."),
	COMMAND_ADMIN_FAILED("Command.Admin.Failed", "&cAnother plugin has prevented you from using that command."),
	COMMAND_ADMIN_REMOVE("Command.Admin.Remove", "&c<player> &6is no longer a fake administrator."),
	COMMAND_ADMIN_REMOVED("Command.Admin.Removed", "&cYou are no longer an administrator."),
	COMMAND_CLEAR("Command.Clear", "&6You have cleared all the fake administrators."),
	COMMAND_FAKE_UNKNOWN("Command.Fake unknown", "Unknown command. Type \"/help\" for help."),
	COMMAND_LIST("Command.List", "&6Fake admins (<amount>): &b<admins>"),
	COMMAND_SPY_FAKE_ADMIN("Command.Spy.Fake admin", "&6You cannot spy on fake administrators when you're one yourself!"),
	COMMAND_SPY_OFF("Command.Spy.Off", "&6You are no longer spying on fake administrators."),
	COMMAND_SPY_ON("Command.Spy.On", "&6You are now spying on fake administrators!"),
	COMMAND_UNKNOWN("Command.Unknown", "&cUnknown FakeAdmin command: &4<command>"),
	COMMAND_USAGE("Command.Usage", "&cUsage: &4/<usage>"),
	COMMAND_WHOIS("Command.Whois", "&c<nickname> &6is actually &c<player>&6."),
	COMMAND_WHOIS_UNKNOWN("Command.Whois unknown", "&c<nickname> &6could not be found."),
	SPY("Command spy", "&8[&4CommandSpy&8] &c<player> &6typed the command: &c/<command>"),
	TIME_DAY("Time.Day", "day"),
	TIME_DAYS("Time.Days", "days"),
	TIME_HOUR("Time.Hour", "hour"),
	TIME_HOURS("Time.Hours", "hours"),
	TIME_MINUTE("Time.Minute", "minute"),
	TIME_MINUTES("Time.Minutes", "minutes"),
	TIME_MONTH("Time.Month", "month"),
	TIME_MONTHS("Time.Months", "months"),
	TIME_NOW("Time.Now", "now"),
	TIME_SECOND("Time.Second", "second"),
	TIME_SECONDS("Time.Seconds", "seconds"),
	TIME_YEAR("Time.Year", "year"),
	TIME_YEARS("Time.Years", "years");

	private static FileConfiguration messagesConfig = null;

	private String key = null;
	private String defaultValue = null;

	private Lang(String key, String defaultValue) {
		this.key = key;
		this.defaultValue = defaultValue;
	}

	public String getRawMessage() {
		return messagesConfig.contains(this.key) ? messagesConfig.getString(this.key) : this.defaultValue;
	}

	public String getReplacedMessage(Object... objects) {
		String langMessage = this.getReplacedRawMessage(objects);
		return Utilities.replaceChatColoursAndFormats(langMessage);
	}

	public String getReplacedRawMessage(Object... objects) {
		String langMessage = this.getRawMessage();
		if (objects != null) {
			Object firstObject = "";
			for (int i = 0; i < objects.length; i++) {
				if (i == 0 || i % 2 == 0) {
					firstObject = objects[i] != null ? objects[i].toString() : "null";
				} else {
					if (firstObject != null)
						langMessage = langMessage.replace(firstObject.toString(), (objects[i] != null ? objects[i].toString() : "null"));
				}
			}
		}
		return langMessage;
	}

	public static void sendMessage(CommandSender sender, Lang lang, Object... messageFormat) {
		if (sender != null && lang != null) {
			String message = Utilities.replaceChatColoursAndFormats(String.format(lang.getRawMessage(), messageFormat));
			if (!message.isEmpty()) sender.sendMessage(message);
		}
	}

	public static void sendMessage(CommandSender sender, String message, Object... messageFormat) {
		if (sender != null && message != null && !message.isEmpty())
			sender.sendMessage(Utilities.replaceChatColoursAndFormats(String.format(message, messageFormat)));
	}

	public static void sendReplacedMessage(CommandSender sender, Lang lang, Object... replaceObjects) {
		if (sender != null && lang != null) {
			String message = lang.getReplacedMessage(replaceObjects);
			if (!message.isEmpty()) sender.sendMessage(message);
		}
	}

	public static void initialiseMessages() {
		File messagesFile = new File(FakeAdmin.getInstance().getDataFolder(), "messages.yml");
		messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

		for (Lang lang : values()) {
			if (!messagesConfig.isSet(lang.key)) messagesConfig.set(lang.key, lang.defaultValue);
		}
		try {
			messagesConfig.save(messagesFile);
		} catch (Exception ex) {
		}
	}

}
