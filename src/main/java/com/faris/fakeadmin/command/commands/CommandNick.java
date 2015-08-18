package com.faris.fakeadmin.command.commands;

import com.faris.fakeadmin.ConfigCommand;
import com.faris.fakeadmin.command.AdminCommand;
import com.faris.fakeadmin.helper.Utilities;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.List;
import java.util.UUID;

public class CommandNick extends AdminCommand {

	@Override
	public CommandReturnType onCommand(Player player, String command, String[] args) throws Exception {
		if (!ConfigCommand.NICK.isEnabled()) return CommandReturnType.DISABLED;
		if (args.length > 0) {
			if (args.length == 1) {
				String strNickname = args[0];
				if (!strNickname.equalsIgnoreCase("off")) {
					if (Utilities.isAlphanumerical(strNickname)) {
						if (strNickname.length() <= (ConfigCommand.NICK.hasSpecialAttribute("Max nickname length") ? ConfigCommand.NICK.getSpecialAttribute("Max nickname length").castValue(Integer.class) : 15)) {
							if (!this.getPlugin().getManager().getNicknameManager().doesConflict(strNickname)) {
								this.getPlugin().getManager().getNicknameManager().setNickname(player.getUniqueId(), strNickname);
								this.getPlugin().getManager().getConfigManager().getPlayersConfig().set("Nicknames." + player.getUniqueId().toString(), strNickname);
								this.getPlugin().getManager().getConfigManager().savePlayersConfig();

								if (ConfigCommand.NICK.hasSpecialAttribute("Nick message"))
									player.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.NICK.getSpecialAttribute("Nick message").castValue(String.class).replace("<nick>", Utilities.replaceChatColoursAndFormats(strNickname))));
							} else {
								if (ConfigCommand.NICK.hasSpecialAttribute("Nick in use message"))
									player.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.NICK.getSpecialAttribute("Nick in use message").castValue(String.class)));
							}
						} else {
							if (ConfigCommand.NICK.hasSpecialAttribute("Nick too long message"))
								player.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.NICK.getSpecialAttribute("Nick too long message").castValue(String.class)));
						}
					} else {
						if (ConfigCommand.NICK.hasSpecialAttribute("Invalid nickname"))
							player.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.NICK.getSpecialAttribute("Invalid nickname").castValue(String.class)));
					}
				} else {
					this.getPlugin().getManager().getNicknameManager().setNickname(player.getUniqueId(), null);
					this.getPlugin().getManager().getConfigManager().getPlayersConfig().set("Nicknames." + player.getUniqueId().toString(), null);
					this.getPlugin().getManager().getConfigManager().savePlayersConfig();

					if (ConfigCommand.NICK.hasSpecialAttribute("Nick off message"))
						player.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.NICK.getSpecialAttribute("Nick off message").castValue(String.class)));
				}
			} else {
				final String strNickname = args[0];
				final String strTarget = args[1];
				Player targetPlayer = player.getServer().getPlayer(strTarget);
				UUID targetPlayerUUID = targetPlayer != null ? targetPlayer.getUniqueId() : null;
				if (!strNickname.equalsIgnoreCase("off")) {
					if (Utilities.isAlphanumerical(strNickname)) {
						if (strNickname.length() <= (ConfigCommand.NICK.hasSpecialAttribute("Max nickname length") ? ConfigCommand.NICK.getSpecialAttribute("Max nickname length").castValue(Integer.class) : 15)) {
							if (!this.getPlugin().getManager().getNicknameManager().doesConflict(strNickname)) {
								boolean sendMessages = true;
								if (targetPlayerUUID != null) {
									this.getPlugin().getManager().getNicknameManager().setNickname(targetPlayerUUID, strNickname);

									this.getPlugin().getManager().getConfigManager().getPlayersConfig().set("Nicknames." + targetPlayerUUID.toString(), strNickname);
									this.getPlugin().getManager().getConfigManager().savePlayersConfig();
								} else {
									OfflinePlayer offlinePlayer = Utilities.getOfflinePlayer(strTarget);
									if (offlinePlayer != null) {
										targetPlayerUUID = offlinePlayer.getUniqueId();
										this.getPlugin().getManager().getNicknameManager().setNickname(targetPlayerUUID, strNickname);

										this.getPlugin().getManager().getConfigManager().getPlayersConfig().set("Nicknames." + targetPlayerUUID.toString(), strNickname);
										this.getPlugin().getManager().getConfigManager().savePlayersConfig();
									} else {
										if (ConfigCommand.NICK.hasSpecialAttribute("Player not found"))
											player.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.NICK.getSpecialAttribute("Player not found").castValue(String.class).replace("<target>", strTarget)));
										sendMessages = false;
									}
								}
								if (sendMessages) {
									if (ConfigCommand.NICK.hasSpecialAttribute("Nick message") && targetPlayer != null && this.getPlugin().getManager().getAdminManager().isFakeAdmin(targetPlayerUUID))
										targetPlayer.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.NICK.getSpecialAttribute("Nick message").castValue(String.class).replace("<nick>", Utilities.replaceChatColoursAndFormats(strNickname))));
									if (ConfigCommand.NICK.hasSpecialAttribute("Nick other message"))
										player.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.NICK.getSpecialAttribute("Nick other message").castValue(String.class).replace("<target>", targetPlayer != null ? targetPlayer.getName() : strTarget).replace("<nick>", Utilities.replaceChatColoursAndFormats(strNickname))));
								}
							} else {
								if (ConfigCommand.NICK.hasSpecialAttribute("Nick in use message"))
									player.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.NICK.getSpecialAttribute("Nick in use message").castValue(String.class)));
							}
						} else {
							if (ConfigCommand.NICK.hasSpecialAttribute("Nick too long message"))
								player.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.NICK.getSpecialAttribute("Nick too long message").castValue(String.class)));
						}
					} else {
						if (ConfigCommand.NICK.hasSpecialAttribute("Invalid nickname"))
							player.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.NICK.getSpecialAttribute("Invalid nickname").castValue(String.class)));
					}
				} else {
					if (targetPlayerUUID != null) {
						this.getPlugin().getManager().getNicknameManager().setNickname(targetPlayerUUID, null);
						this.getPlugin().getManager().getConfigManager().getPlayersConfig().set("Nicknames." + targetPlayerUUID.toString(), null);
						this.getPlugin().getManager().getConfigManager().savePlayersConfig();
					}
					if (ConfigCommand.NICK.hasSpecialAttribute("Nick off message"))
						player.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.NICK.getSpecialAttribute("Nick off message").castValue(String.class).replace("<target>", targetPlayer != null ? targetPlayer.getName() : strTarget)));
				}
			}
			return CommandReturnType.VALID;
		}
		return CommandReturnType.INVALID_USAGE;
	}

	@Override
	public List<String> getUsage() {
		return ConfigCommand.NICK.getUsageMessage("nick [player] <nickname:off>");
	}

}
